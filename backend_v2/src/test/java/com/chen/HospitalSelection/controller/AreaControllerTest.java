package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.AreaService;
import com.chen.HospitalSelection.vo.AreaVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 地区控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AreaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AreaService areaService;

    private AreaVO provinceVO;
    private AreaVO cityVO;
    private AreaVO areaVO;

    @BeforeEach
    public void setUp() {
        // 省份数据
        provinceVO = new AreaVO();
        provinceVO.setId(1L);
        provinceVO.setCode("440000");
        provinceVO.setName("广东省");
        provinceVO.setLevel(1);
        provinceVO.setParentCode("0");

        // 城市数据
        cityVO = new AreaVO();
        cityVO.setId(2L);
        cityVO.setCode("440100");
        cityVO.setName("广州市");
        cityVO.setLevel(2);
        cityVO.setParentCode("440000");

        // 区县数据
        areaVO = new AreaVO();
        areaVO.setId(3L);
        areaVO.setCode("440104");
        areaVO.setName("越秀区");
        areaVO.setLevel(3);
        areaVO.setParentCode("440100");
    }

    @Test
    @DisplayName("测试省市区树 - 成功")
    public void testGetAreaTree_Success() throws Exception {
        // Arrange
        provinceVO.setChildren(Arrays.asList(cityVO));
        cityVO.setChildren(Arrays.asList(areaVO));

        when(areaService.getAreaTree()).thenReturn(Arrays.asList(provinceVO));

        // Act & Assert
        mockMvc.perform(get("/area/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("广东省"))
                .andExpect(jsonPath("$.data[0].children[0].name").value("广州市"))
                .andExpect(jsonPath("$.data[0].children[0].children[0].name").value("越秀区"));
    }

    @Test
    @DisplayName("测试省市区树 - 空结果")
    public void testGetAreaTree_Empty() throws Exception {
        // Arrange
        when(areaService.getAreaTree()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/area/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试省份列表 - 成功")
    public void testGetProvinces_Success() throws Exception {
        // Arrange
        AreaVO province2 = new AreaVO();
        province2.setId(4L);
        province2.setCode("450000");
        province2.setName("广西壮族自治区");
        province2.setLevel(1);
        province2.setParentCode("0");

        when(areaService.getProvinces()).thenReturn(Arrays.asList(provinceVO, province2));

        // Act & Assert
        mockMvc.perform(get("/area/province"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("广东省"))
                .andExpect(jsonPath("$.data[1].name").value("广西壮族自治区"));
    }

    @Test
    @DisplayName("测试省份列表 - 空结果")
    public void testGetProvinces_Empty() throws Exception {
        // Arrange
        when(areaService.getProvinces()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/area/province"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试城市列表 - 成功")
    public void testGetCities_Success() throws Exception {
        // Arrange
        AreaVO city2 = new AreaVO();
        city2.setId(5L);
        city2.setCode("440300");
        city2.setName("深圳市");
        city2.setLevel(2);
        city2.setParentCode("440000");

        when(areaService.getCitiesByProvince("440000")).thenReturn(Arrays.asList(cityVO, city2));

        // Act & Assert
        mockMvc.perform(get("/area/city/440000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("广州市"))
                .andExpect(jsonPath("$.data[1].name").value("深圳市"));
    }

    @Test
    @DisplayName("测试城市列表 - 空结果")
    public void testGetCities_Empty() throws Exception {
        // Arrange
        when(areaService.getCitiesByProvince(anyString())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/area/city/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("测试区县列表 - 成功")
    public void testGetAreas_Success() throws Exception {
        // Arrange
        AreaVO area2 = new AreaVO();
        area2.setId(6L);
        area2.setCode("440103");
        area2.setName("荔湾区");
        area2.setLevel(3);
        area2.setParentCode("440100");

        when(areaService.getAreasByCity("440100")).thenReturn(Arrays.asList(areaVO, area2));

        // Act & Assert
        mockMvc.perform(get("/area/area/440100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("越秀区"))
                .andExpect(jsonPath("$.data[1].name").value("荔湾区"));
    }

    @Test
    @DisplayName("测试区县列表 - 空结果")
    public void testGetAreas_Empty() throws Exception {
        // Arrange
        when(areaService.getAreasByCity(anyString())).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/area/area/999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
