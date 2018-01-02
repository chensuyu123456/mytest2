package com.shellshellfish.aaas.finance.trade.order.message;

import com.shellshellfish.aaas.common.message.order.PayDto;
import com.shellshellfish.aaas.common.message.order.TrdOrderDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles="dev")
public class BroadcastMessageProducerTest {

  @Autowired
  BroadcastMessageProducer broadcastMessageProducer;

  @Test
  public void sendMessages() throws Exception {
    PayDto payDto = new PayDto();
    broadcastMessageProducer.sendPayMessages(payDto);
  }

}