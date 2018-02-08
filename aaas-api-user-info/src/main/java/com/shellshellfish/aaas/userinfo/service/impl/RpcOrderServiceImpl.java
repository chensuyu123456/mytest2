package com.shellshellfish.aaas.userinfo.service.impl;

import com.shellshellfish.aaas.finance.trade.order.OrderDetail;
import com.shellshellfish.aaas.finance.trade.order.OrderDetailQueryInfo;
import com.shellshellfish.aaas.finance.trade.order.OrderDetailResult;
import com.shellshellfish.aaas.finance.trade.order.OrderQueryInfo;
import com.shellshellfish.aaas.finance.trade.order.OrderResult;
import com.shellshellfish.aaas.finance.trade.order.OrderRpcServiceGrpc.OrderRpcServiceBlockingStub;
import com.shellshellfish.aaas.userinfo.service.RpcOrderService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author pierre 18-1-31
 */
@Service
public class RpcOrderServiceImpl implements RpcOrderService {


	private static  final Logger logger = LoggerFactory.getLogger(RpcOrderServiceImpl.class);

	@Autowired
	OrderRpcServiceBlockingStub orderRpcServiceBlockingStub;


	@Override
	public OrderResult getOrderInfoByProdIdAndOrderStatus(Long userProdId, Integer orderStatus) {
		OrderQueryInfo.Builder builder = OrderQueryInfo.newBuilder();
		builder.setOrderStatus(orderStatus);
		builder.setUserProdId(userProdId);

		OrderResult orderResult = orderRpcServiceBlockingStub.getOrderInfo(builder.build());


		return orderResult;
	}

	@Override
	public List<OrderDetail> getOrderDetails(Long userProdId, Integer orderDetailStatus) {
		OrderDetailQueryInfo.Builder builder = OrderDetailQueryInfo.newBuilder();
		builder.setOrderDetailStatus(orderDetailStatus);
		builder.setUserProdId(userProdId);

		OrderDetailResult orderDetailResult = orderRpcServiceBlockingStub
				.getOrderDetail(builder.build());
		return orderDetailResult.getOrderDetailResultList();

	}
}
