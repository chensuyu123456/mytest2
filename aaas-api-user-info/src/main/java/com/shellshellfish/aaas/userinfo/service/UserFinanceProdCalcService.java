package com.shellshellfish.aaas.userinfo.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;

public interface UserFinanceProdCalcService {


    BigDecimal calcTotalDailyAsset(String userUuid) throws Exception;

    BigDecimal calcDailyAsset(String userUuid, Long prodId, String fundCode, String date) throws Exception;

    void calcIntervalAmount(String userUuid, Long prodId, String fundCode, String startDate) throws Exception;

    void initDailyAmount(String userUuid, Long prodId, String date, String fundCode);

    BigDecimal calcYieldValue(String userUuid, Long prodId, String startDate, String endDate);

    BigDecimal calcYieldRate(String userUuid, Long prodId, String startDate, String endDate);

    BigDecimal calcYieldValue(String userUuid, String startDate, String endDate);

    BigDecimal calcYieldRate(String userUuid, String startDate, String endDate);

    void dailyCalculation() throws Exception;

    void dailyCalculation(String date) throws Exception;

	BigDecimal getAssert(String userUuid, Long prodId) throws Exception;
}
