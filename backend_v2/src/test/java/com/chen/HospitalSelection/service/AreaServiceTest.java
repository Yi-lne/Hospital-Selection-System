package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.mapper.AreaMapper;
import com.chen.HospitalSelection.model.Area;
import com.chen.HospitalSelection.vo.AreaVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 地区服务测试类
 *
 * @author chen
 */
public class AreaServiceTest {

    @Mock
    private AreaMapper areaMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.AreaServiceImpl areaService;

    private Area testProvince;
    private Area testCity;
    private Area testArea;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试省份
        testProvince = new Area();
        testProvince.setId(1L);
        testProvince.setCode("440000");
        testProvince.setName("广东省");
        testProvince.setParentCode("0");
        testProvince.setLevel(1);

        // 创建测试城市
        testCity = new Area();
        testCity.setId(2L);
        testCity.setCode("440100");
        testCity.setName("广州市");
        testCity.setParentCode("440000");
        testCity.setLevel(2);

        // 创建测试区县
        testArea = new Area();
        testArea.setId(3L);
        testArea.setCode("440103");
        testArea.setName("越秀区");
        testArea.setParentCode("440100");
        testArea.setLevel(3);
    }

    @Test
    @DisplayName("测试获取省市区树 - 成功")
    public void testGetAreaTree_Success() {
        // Arrange
        when(areaMapper.selectProvinces()).thenReturn(Arrays.asList(testProvince));
        when(areaMapper.selectCitiesByProvince("440000")).thenReturn(Arrays.asList(testCity));
        when(areaMapper.selectAreasByCity("440100")).thenReturn(Arrays.asList(testArea));

        // Act
        List<AreaVO> result = areaService.getAreaTree();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("广东省", result.get(0).getName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("广州市", result.get(0).getChildren().get(0).getName());
        assertEquals("越秀区", result.get(0).getChildren().get(0).getChildren().get(0).getName());

        verify(areaMapper, times(1)).selectProvinces();
        verify(areaMapper, times(1)).selectCitiesByProvince("440000");
        verify(areaMapper, times(1)).selectAreasByCity("440100");
    }

    @Test
    @DisplayName("测试获取省份列表 - 成功")
    public void testGetProvinces_Success() {
        // Arrange
        Area province2 = new Area();
        province2.setId(4L);
        province2.setCode("450000");
        province2.setName("广西壮族自治区");
        province2.setParentCode("0");
        province2.setLevel(1);

        List<Area> provinceList = Arrays.asList(testProvince, province2);
        when(areaMapper.selectProvinces()).thenReturn(provinceList);

        // Act
        List<AreaVO> result = areaService.getProvinces();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("广东省", result.get(0).getName());
        assertEquals("广西壮族自治区", result.get(1).getName());

        verify(areaMapper, times(1)).selectProvinces();
    }

    @Test
    @DisplayName("测试获取城市列表 - 成功")
    public void testGetCitiesByProvince_Success() {
        // Arrange
        Area city2 = new Area();
        city2.setId(5L);
        city2.setCode("440300");
        city2.setName("深圳市");
        city2.setParentCode("440000");
        city2.setLevel(2);

        List<Area> cityList = Arrays.asList(testCity, city2);
        when(areaMapper.selectCitiesByProvince("440000")).thenReturn(cityList);

        // Act
        List<AreaVO> result = areaService.getCitiesByProvince("440000");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("广州市", result.get(0).getName());
        assertEquals("深圳市", result.get(1).getName());

        verify(areaMapper, times(1)).selectCitiesByProvince("440000");
    }

    @Test
    @DisplayName("测试获取区县列表 - 成功")
    public void testGetAreasByCity_Success() {
        // Arrange
        Area area2 = new Area();
        area2.setId(6L);
        area2.setCode("440106");
        area2.setName("天河区");
        area2.setParentCode("440100");
        area2.setLevel(3);

        List<Area> areaList = Arrays.asList(testArea, area2);
        when(areaMapper.selectAreasByCity("440100")).thenReturn(areaList);

        // Act
        List<AreaVO> result = areaService.getAreasByCity("440100");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("越秀区", result.get(0).getName());
        assertEquals("天河区", result.get(1).getName());

        verify(areaMapper, times(1)).selectAreasByCity("440100");
    }

    @Test
    @DisplayName("测试根据编码查询地区 - 成功")
    public void testGetAreaByCode_Success() {
        // Arrange
        when(areaMapper.selectByCode("440100")).thenReturn(testCity);

        // Act
        AreaVO result = areaService.getAreaByCode("440100");

        // Assert
        assertNotNull(result);
        assertEquals("广州市", result.getName());
        assertEquals("440100", result.getCode());

        verify(areaMapper, times(1)).selectByCode("440100");
    }

    @Test
    @DisplayName("测试搜索地区 - 成功")
    public void testSearchAreasByName_Success() {
        // Arrange
        when(areaMapper.searchByName("广州")).thenReturn(Arrays.asList(testCity));

        // Act
        List<AreaVO> result = areaService.searchAreasByName("广州");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("广州市", result.get(0).getName());

        verify(areaMapper, times(1)).searchByName("广州");
    }

    @Test
    @DisplayName("测试获取省市区树 - 空列表")
    public void testGetAreaTree_Empty() {
        // Arrange
        when(areaMapper.selectProvinces()).thenReturn(Collections.emptyList());

        // Act
        List<AreaVO> result = areaService.getAreaTree();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试获取地区路径 - 成功")
    public void testGetAreaPath_Success() {
        // Arrange
        when(areaMapper.selectByCode("440103")).thenReturn(testArea);
        when(areaMapper.selectByCode("440100")).thenReturn(testCity);
        when(areaMapper.selectByCode("440000")).thenReturn(testProvince);

        // Act
        String result = areaService.getAreaPath("440103");

        // Assert
        assertNotNull(result);
        assertEquals("广东省 > 广州市 > 越秀区", result);
    }
}
