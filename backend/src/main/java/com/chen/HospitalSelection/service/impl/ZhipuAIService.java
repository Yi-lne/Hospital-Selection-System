package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 智谱AI服务（GLM-4）
 * 负责调用智谱AI API解析用户查询
 *
 * @author chen
 * @since 2025-02-22
 */
@Slf4j
@Service
public class ZhipuAIService {

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Value("${zhipu.api.url}")
    private String apiUrl;

    @Value("${zhipu.api.model:glm-4-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析用户查询，返回医院筛选条件
     */
    public HospitalFilterDTO analyzeQuery(String userQuery) {
        try {
            log.info("开始AI分析用户查询：{}", userQuery);

            // 1. 构建提示词
            String prompt = buildPrompt(userQuery);

            // 2. 调用智谱AI API
            String aiResponse = callZhipuAPI(prompt);

            // 3. 解析AI返回结果
            HospitalFilterDTO filter = parseAIResponse(aiResponse);

            log.info("AI分析完成，结果：{}", filter);
            return filter;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("AI分析失败", e);
            return new HospitalFilterDTO();
        }
    }

    /**
     * 构建AI提示词
     */
    private String buildPrompt(String userQuery) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的医院推荐助手，需要解析患者的查询需求，提取医院筛选条件。\n\n");
        prompt.append("【用户查询】\n");
        prompt.append(userQuery).append("\n\n");
        prompt.append("【任务要求】\n");
        prompt.append("请分析用户查询，提取以下信息，并以JSON格式返回：\n\n");
        prompt.append("{\n");
        prompt.append("  \"provinceCode\": \"省份6位代码（如110000表示北京，440000表示广东，没有则填null）\",\n");
        prompt.append("  \"cityCode\": \"城市6位代码（如110100表示北京市，440100表示广州市，没有则填null）\",\n");
        prompt.append("  \"areaCode\": \"区县6位代码（如110101表示东城区，没有则填null）\",\n");
        prompt.append("  \"hospitalLevel\": \"医院等级（grade3A=三甲、grade3B=三乙、grade2A=二甲、grade2B=二乙，没有则填null）\",\n");
        prompt.append("  \"deptName\": \"科室名称（如神经内科、心内科、骨科等，没有则填null）\",\n");
        prompt.append("  \"keyDepartments\": \"重点科室关键词（没有则填null）\",\n");
        prompt.append("  \"isValidQuery\": \"用户查询是否有效且地区在服务范围内（true/false）\",\n");
        prompt.append("  \"reasoning\": \"简要说明推理过程\"\n");
        prompt.append("}\n\n");
        prompt.append("【isValidQuery判断规则】\n");
        prompt.append("1. 用户查询是乱码、无意义、与医疗完全无关 → false\n");
        prompt.append("2. 用户提到具体地区，但地区不在中国大陆（如美国、日本、泰国曼谷、印度新德里、欧洲、月球等） → false\n");
        prompt.append("3. 用户未提及具体地区（如'推荐神经内科'、'头疼找什么医院'） → true（地区字段填null，表示不限制地区）\n");
        prompt.append("4. 用户提到的地区在中国大陆 → true（填写对应地区代码）\n\n");
        prompt.append("【重要规则】\n");
        prompt.append("1. 只返回JSON，不要其他任何文字说明\n");
        prompt.append("2. 地区代码使用中国大陆6位行政区划代码：\n");
        prompt.append("   - 北京：110000，上海市：310000，天津：120000，重庆：500000\n");
        prompt.append("   - 广东：440000，浙江：330000，江苏：320000，四川：510000，新疆：650000\n");
        prompt.append("   - 北京市：110100，上海市：310100，广州市：440100，深圳市：440300，杭州市：330100\n");
        prompt.append("   - 乌鲁木齐市：650100（新疆），成都市：510100（四川），南京市：320100（江苏）\n");
        prompt.append("3. 根据症状智能推断科室（如头痛→神经内科、胸痛→心内科、咳嗽→呼吸内科）\n");
        prompt.append("4. 如果用户提到'最好的'、'三甲'，hospitalLevel填grade3A\n");
        prompt.append("5. 如果信息不足或不确定，对应字段填null\n");
        prompt.append("6. isMedicalInsurance字段必须是布尔值true/false或null，不能是字符串\n");
        prompt.append("7. 【关键】如果isValidQuery为false，所有其他字段必须设为null\n\n");
        prompt.append("【常见科室推断规则】\n");
        prompt.append("- 神经系统：头痛、头晕、失眠、癫痫、中风、帕金森 → 神经内科\n");
        prompt.append("- 心血管：胸痛、心慌、高血压、心悸、心脏病 → 心内科\n");
        prompt.append("- 呼吸系统：咳嗽、哮喘、肺炎、呼吸困难、支气管炎 → 呼吸内科\n");
        prompt.append("- 消化系统：胃痛、腹泻、恶心、呕吐、肝炎、胃炎 → 消化内科\n");
        prompt.append("- 骨科：骨折、腰痛、关节炎、扭伤、骨质疏松 → 骨科\n");
        prompt.append("- 皮肤科：皮疹、过敏、皮炎、湿疹、荨麻疹 → 皮肤科\n");
        prompt.append("- 妇科：月经、怀孕、妇科炎症、白带异常 → 妇科\n");
        prompt.append("- 产科：怀孕、产检、生孩子 → 产科\n");
        prompt.append("- 儿科：儿童生病、小孩 → 儿科\n");
        prompt.append("- 眼科：眼睛、视力 → 眼科\n");
        prompt.append("- 耳鼻喉：耳朵、鼻子、喉咙 → 耳鼻喉科");

        return prompt.toString();
    }

    /**
     * 调用智谱AI API
     */
    private String callZhipuAPI(String prompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", model);

        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        request.put("messages", new Object[]{userMessage});
        request.put("temperature", 0.3);
        request.put("max_tokens", 1000);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode rootNode = objectMapper.readTree(response.getBody());

            if (rootNode.has("error")) {
                String errorMsg = rootNode.get("error").get("message").asText();
                throw new RuntimeException("智谱AI API错误: " + errorMsg);
            }

            String content = rootNode.get("choices").get(0).get("message").get("content").asText();
            log.info("智谱AI API调用成功");
            return content;

        } catch (Exception e) {
            log.error("智谱AI API调用失败", e);
            throw new RuntimeException("AI服务暂时不可用: " + e.getMessage(), e);
        }
    }

    /**
     * 解析AI返回的JSON
     */
    private HospitalFilterDTO parseAIResponse(String aiResponse) {
        try {
            int jsonStart = aiResponse.indexOf("{");
            int jsonEnd = aiResponse.lastIndexOf("}") + 1;

            if (jsonStart == -1 || jsonEnd == 0) {
                log.warn("AI响应中未找到JSON：{}", aiResponse);
                return new HospitalFilterDTO();
            }

            String jsonStr = aiResponse.substring(jsonStart, jsonEnd);
            JsonNode result = objectMapper.readTree(jsonStr);

            // 检查AI是否认为查询无效
            if (result.has("isValidQuery") && !result.get("isValidQuery").isNull()) {
                boolean isValid = result.get("isValidQuery").asBoolean(false);
                if (!isValid) {
                    throw new IllegalArgumentException("无法理解您的查询，请输入与医院、科室、症状或疾病相关的描述");
                }
            }

            HospitalFilterDTO filter = new HospitalFilterDTO();

            if (result.has("provinceCode") && !result.get("provinceCode").isNull()) {
                filter.setProvinceCode(result.get("provinceCode").asText());
            }

            if (result.has("cityCode") && !result.get("cityCode").isNull()) {
                filter.setCityCode(result.get("cityCode").asText());
            }

            if (result.has("areaCode") && !result.get("areaCode").isNull()) {
                filter.setAreaCode(result.get("areaCode").asText());
            }

            if (result.has("hospitalLevel") && !result.get("hospitalLevel").isNull()) {
                filter.setHospitalLevel(result.get("hospitalLevel").asText());
            }

            if (result.has("deptName") && !result.get("deptName").isNull()) {
                filter.setDeptName(result.get("deptName").asText());
            }

            if (result.has("keyDepartments") && !result.get("keyDepartments").isNull()) {
                filter.setKeyDepartments(result.get("keyDepartments").asText());
            }

            if (result.has("isMedicalInsurance") && !result.get("isMedicalInsurance").isNull()) {
                JsonNode insuranceNode = result.get("isMedicalInsurance");
                if (insuranceNode.isBoolean()) {
                    filter.setIsMedicalInsurance(insuranceNode.asBoolean() ? 1 : 0);
                } else if (insuranceNode.isTextual()) {
                    filter.setIsMedicalInsurance("true".equalsIgnoreCase(insuranceNode.asText()) ? 1 : 0);
                }
            }

            filter.setPage(1);
            filter.setPageSize(10);

            if (result.has("reasoning") && !result.get("reasoning").isNull()) {
                log.info("AI推理过程：{}", result.get("reasoning").asText());
            }

            return filter;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析AI响应失败：{}", aiResponse, e);
            return new HospitalFilterDTO();
        }
    }
}
