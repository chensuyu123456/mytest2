package com.shellshellfish.aaas.userinfo.message;


import com.rabbitmq.client.Channel;
import com.shellshellfish.aaas.common.constants.RabbitMQConstants;
import com.shellshellfish.aaas.common.message.order.OrderStatusChangeDTO;
import com.shellshellfish.aaas.common.message.order.TrdPayFlow;
import com.shellshellfish.aaas.common.utils.TradeUtil;
import com.shellshellfish.aaas.userinfo.model.dao.MongoUiTrdLog;
import com.shellshellfish.aaas.userinfo.repositories.mongo.MongoUserTrdLogMsgRepo;
import com.shellshellfish.aaas.userinfo.repositories.mysql.UiProductDetailRepo;
import com.shellshellfish.aaas.userinfo.repositories.mysql.UiProductRepo;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BroadcastMessageConsumers {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastMessageConsumers.class);

    private final CountDownLatch latch = new CountDownLatch(1);

    @Autowired
    UiProductDetailRepo uiProductDetailRepo;

    @Autowired
    UiProductRepo uiProductRepo;

    @Autowired
    MongoUserTrdLogMsgRepo mongoUserTrdLogMsgRepo;

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = RabbitMQConstants.QUEUE_USERINFO_BASE + RabbitMQConstants.OPERATION_TYPE_UPDATE_UIPROD,
            durable = "false"),
        exchange =  @Exchange(value = RabbitMQConstants.EXCHANGE_NAME, type = "topic",
            durable = "true"),  key = RabbitMQConstants.ROUTING_KEY_USERINFO)
    )
    public void receiveMessage(TrdPayFlow trdPayFlow, Channel channel, @Header(AmqpHeaders
        .DELIVERY_TAG) long tag) throws Exception {
        logger.info("Received fanout 1 message: " + trdPayFlow);
        //update ui_products 和 ui_product_details
        try{
            uiProductDetailRepo.updateByParam(trdPayFlow.getFundSum(), TradeUtil.getUTCTime(),
                trdPayFlow.getUserId(),trdPayFlow.getUserProdId() ,trdPayFlow.getFundCode(),
                trdPayFlow.getTrdStatus());
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        try {
            channel.basicAck(tag, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        latch.countDown();
        
    }


    @Transactional
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = RabbitMQConstants.QUEUE_USERINFO_BASE + RabbitMQConstants
            .OPERATION_TYPE_UPDATE_UITRDLOG, durable = "false"),
        exchange =  @Exchange(value = RabbitMQConstants.EXCHANGE_NAME, type = "topic",
            durable = "true"),  key = RabbitMQConstants.ROUTING_KEY_USERINFO)
    )
    public void receiveTradeMessage(TrdPayFlow trdPayFlow, Channel channel, @Header(AmqpHeaders
        .DELIVERY_TAG) long tag) throws Exception {
        logger.info("Received fanout 1 message: " + trdPayFlow);
        //update ui_products 和 ui_product_details
        MongoUiTrdLog  mongoUiTrdLog = new MongoUiTrdLog();
        try{
            if(null == trdPayFlow.getTrdMoneyAmount()){
                mongoUiTrdLog.setAmount(BigDecimal.valueOf(0));
            }else {
                mongoUiTrdLog.setAmount(
                    TradeUtil.getBigDecimalNumWithDiv100(trdPayFlow.getTrdMoneyAmount()));
            }
            mongoUiTrdLog.setOperations(trdPayFlow.getTrdType());
            mongoUiTrdLog.setUserProdId(trdPayFlow.getUserProdId());
            mongoUiTrdLog.setUserId(trdPayFlow.getUserId());
            mongoUiTrdLog.setTradeStatus(trdPayFlow.getTrdStatus());
            mongoUiTrdLog.setLastModifiedDate(TradeUtil.getUTCTime());
            mongoUiTrdLog.setFundCode(trdPayFlow.getFundCode());
            mongoUiTrdLog.setTradeDate(trdPayFlow.getTrdDate());
            mongoUserTrdLogMsgRepo.save(mongoUiTrdLog);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        try {
            channel.basicAck(tag, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        latch.countDown();

    }

    @Transactional
    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = RabbitMQConstants.QUEUE_USERINFO_BASE + RabbitMQConstants
            .OPERATION_TYPE_UPDATE_UITRDLOG, durable = "false"),
        exchange =  @Exchange(value = RabbitMQConstants.EXCHANGE_NAME, type = "topic",
            durable = "true"),  key = RabbitMQConstants.ROUTING_KEY_USERINFO)
    )
    public void receiveOrderStatusChangeMessage(OrderStatusChangeDTO orderStatusChangeDTO, Channel channel, @Header
        (AmqpHeaders
        .DELIVERY_TAG) long tag) throws Exception {
        logger.info("Received fanout 1 message: " + orderStatusChangeDTO);
        //update ui_products 和 ui_product_details
        MongoUiTrdLog  mongoUiTrdLog = new MongoUiTrdLog();
        try{
            mongoUiTrdLog.setOperations(orderStatusChangeDTO.getOrderType());
            mongoUiTrdLog.setUserProdId(orderStatusChangeDTO.getUserProdId());
            mongoUiTrdLog.setUserId(orderStatusChangeDTO.getUserId());
            mongoUiTrdLog.setTradeStatus(orderStatusChangeDTO.getOrderStatus());
            mongoUiTrdLog.setLastModifiedDate(TradeUtil.getUTCTime());
            mongoUiTrdLog.setTradeDate(orderStatusChangeDTO.getOrderDate());
            mongoUserTrdLogMsgRepo.save(mongoUiTrdLog);
        }catch (Exception ex){
            ex.printStackTrace();
            logger.error(ex.getMessage());
        }
        try {
            channel.basicAck(tag, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        latch.countDown();

    }
}
