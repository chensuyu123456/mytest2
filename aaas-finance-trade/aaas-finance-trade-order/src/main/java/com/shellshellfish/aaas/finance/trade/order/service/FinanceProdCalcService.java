package com.shellshellfish.aaas.finance.trade.order.service;

import com.shellshellfish.aaas.common.grpc.finance.product.ProductMakeUpInfo;
import com.shellshellfish.aaas.finance.trade.order.model.DistributionResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface FinanceProdCalcService {
    BigDecimal getMinBuyAmount(List<ProductMakeUpInfo> productMakeUpInfoList) throws Exception;

    BigDecimal getMaxBuyAmount(List<ProductMakeUpInfo> productMakeUpInfoList) throws Exception;

    DistributionResult getPoundageOfBuyFund(BigDecimal amount, List<ProductMakeUpInfo> productMakeUpInfoList) throws Exception;

    DistributionResult getPoundageOfSellFund(BigDecimal totalAmount, List<ProductMakeUpInfo> productMakeUpInfoList) throws Exception;
}
