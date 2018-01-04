package com.shellshellfish.aaas.finance.trade.pay.message;


import com.shellshellfish.aaas.common.constants.RabbitMQConstants;
import com.shellshellfish.aaas.common.message.order.TrdPayFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BroadcastMessageProducers {
    private static final Logger logger = LoggerFactory.getLogger(BroadcastMessageProducers.class);
    @Autowired
    RabbitTemplate rabbitTemplate;




    public void sendMessage(TrdPayFlow trdPayFlow) {
        logger.info("send message: " + trdPayFlow.getOrderDetailId());
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, RabbitMQConstants.ROUTING_KEY_ORDER,
            trdPayFlow);
        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGE_NAME, RabbitMQConstants.ROUTING_KEY_USERINFO,
            trdPayFlow);
    }

}
