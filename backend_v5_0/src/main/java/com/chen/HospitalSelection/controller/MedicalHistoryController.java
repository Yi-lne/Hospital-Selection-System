package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.MedicalHistoryDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.MedicalHistoryService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.MedicalHistoryVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 病史接口
 * 基础路径：/api/medical-history
 *
 * @author chen
 */
@RestController
@RequestMapping("/medical-history")
@Api(tags = "病史管理")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    /**
     * 病史列表
     * 接口路径：GET /api/medical-history/list
     * 是否需要登录：是
     *
     * @return 用户的所有病史记录
     */
    @GetMapping("/list")
    @ApiOperation("病史列表")
    public Result<List<MedicalHistoryVO>> getMedicalHistoryList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        List<MedicalHistoryVO> list = medicalHistoryService.getMedicalHistoryList(userId);
        return Result.success(list);
    }

    /**
     * 添加病史
     * 接口路径：POST /api/medical-history/add
     * 是否需要登录：是
     *
     * @param dto 病史信息（疾病名称、诊断日期、状态等）
     * @return 添加的病史信息
     */
    @PostMapping("/add")
    @ApiOperation("添加病史")
    public Result<MedicalHistoryVO> addMedicalHistory(@RequestBody @Valid MedicalHistoryDTO dto,
                                                      HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        Long historyId = medicalHistoryService.addMedicalHistory(userId, dto);
        // Get the created history to return
        MedicalHistoryVO medicalHistoryVO = medicalHistoryService.getMedicalHistoryDetail(historyId, userId);
        return Result.success(medicalHistoryVO, "添加成功");
    }

    /**
     * 修改病史
     * 接口路径：PUT /api/medical-history/update/{id}
     * 是否需要登录：是
     *
     * @param id  病史ID
     * @param dto 修改的病史信息
     * @return 修改后的病史信息
     */
    @PutMapping("/update/{id}")
    @ApiOperation("修改病史")
    public Result<MedicalHistoryVO> updateMedicalHistory(@PathVariable Long id,
                                                          @RequestBody @Valid MedicalHistoryDTO dto,
                                                          HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        medicalHistoryService.updateMedicalHistory(id, userId, dto);
        // Get the updated history to return
        MedicalHistoryVO medicalHistoryVO = medicalHistoryService.getMedicalHistoryDetail(id, userId);
        return Result.success(medicalHistoryVO, "修改成功");
    }

    /**
     * 删除病史
     * 接口路径：DELETE /api/medical-history/{id}
     * 是否需要登录：是
     *
     * @param id 病史ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除病史")
    public Result<Void> deleteMedicalHistory(@PathVariable Long id,
                                            HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        medicalHistoryService.deleteMedicalHistory(id, userId);
        return Result.success(null, "删除成功");
    }

    /**
     * 病史详情
     * 接口路径：GET /api/medical-history/{id}
     * 是否需要登录：是
     *
     * @param id 病史ID
     * @return 病史详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation("病史详情")
    public Result<MedicalHistoryVO> getMedicalHistoryDetail(@PathVariable Long id,
                                                           HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        MedicalHistoryVO medicalHistoryVO = medicalHistoryService.getMedicalHistoryDetail(id, userId);
        return Result.success(medicalHistoryVO);
    }

    /**
     * 获取当前登录用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        // 检查 token 是否存在
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("用户未登录：缺少认证令牌");
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录：令牌无效");
            }
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录: " + e.getMessage(), e);
        }
    }
}
