package com.shellshellfish.aaas.assetallocation.repository.dummy;

import com.shellshellfish.aaas.assetallocation.neo.mapper.FundGroupMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: hongming
 * Date: 2018/1/27
 * Desc:
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class FundGroupMapperTest {

    @Autowired
    private FundGroupMapper fundGroupMapper;

    @Test
    public void getFundGroupHistoryTimeTest() {
        Map<String, Object> query = new HashMap<>();
        query.put("fund_group_id", 5);
        query.put("subGroupId", 50059);
        String groupStartTime = fundGroupMapper.getFundGroupHistoryTime(query);
        System.out.println(groupStartTime);
    }

    @Test
    public void getFundGroupHistoryTimeByRiskLevelTest() {
        Map<String, Object> query = new HashMap<>();
        query.put("risk_level", "C1");
        String groupStartTime = fundGroupMapper.getFundGroupHistoryTimeByRiskLevel(query);
        System.out.println(groupStartTime);
    }

    @Test
    public void updateMaximumRetracementTest() {
        Map<String, Object> query = new HashMap<>();
        query.put("fund_group_id", "5");
        query.put("subGroupId", "50059");
        query.put("retracement", -0.014); //-0.014
        query.put("time", "2017-12-18");
        Integer effectRow = fundGroupMapper.updateMaximumRetracement(query);
        System.out.println(effectRow);
    }

    @Test
    public void updateMaximumRetracementByRiskLevelTest() {
        Map<String, Object> query = new HashMap<>();
        query.put("retracement", -0.0397); //-0.0397
        query.put("risk_level", "C1");
        query.put("time", "2018-01-15");
        Integer effectRow = fundGroupMapper.updateMaximumRetracementByRiskLevel(query);
        System.out.println(effectRow);
    }
}
