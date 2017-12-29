package com.shellshellfish.aaas.finance.trade.order.config;

import com.shellshellfish.aaas.finance.trade.order.service.OrderService;
import com.shellshellfish.aaas.finance.trade.order.service.impl.OrderServiceImpl;
import io.grpc.BindableService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcFinanceProdClientConfig {




  @Value("${grpc.finance_prod_client.host}")
  String finHost;

  @Value("${grpc.finance_prod_client.port}")
  int finPort;

  @Value("${grpc.pay_client.host}")
  String payHost;

  @Value("${grpc.pay_client.port}")
  int payPort;

  @Value("${grpc.datamanager_client.host}")
  String dmHost;

  @Value("${grpc.datamanager_client.port}")
  int dmPort;

  @Bean
  ManagedChannelBuilder<?> grpcFINChannelBuilder(){
    return ManagedChannelBuilder.forAddress(finHost, finPort);
  }

  @Bean
  ManagedChannelBuilder<?> grpcPayChannelBuilder(){
    return ManagedChannelBuilder.forAddress(payHost, payPort);
  }

  @Bean
  ManagedChannelBuilder<?> grpcDMChannelBuilder(){
    return ManagedChannelBuilder.forAddress(dmHost, dmPort);
  }

  @Value("${grpc.userinfo_client.host}")
  String uiHost;

  @Value("${grpc.userinfo_client.port}")
  int uiPort;

  @Bean
  ManagedChannelBuilder<?> grpcUIChannelBuilder(){
    return ManagedChannelBuilder.forAddress(uiHost, uiPort);
  }

  @Bean
  @PostConstruct
  ManagedChannel managedFINChannel(){
    ManagedChannel managedChannel = grpcFINChannelBuilder().usePlaintext(true).build();
    return managedChannel;
  }

  @Bean
  @PostConstruct
  ManagedChannel managedUIChannel(){
    ManagedChannel managedChannel = grpcUIChannelBuilder().usePlaintext(true).build();
    return managedChannel;
  }

  @Bean
  @PostConstruct
  ManagedChannel managedPayChannel(){
    ManagedChannel managedChannel = grpcPayChannelBuilder().usePlaintext(true).build();
    return managedChannel;
  }

  @Bean
  @PostConstruct
  ManagedChannel managedDMChannel(){
    ManagedChannel managedChannel = grpcDMChannelBuilder().usePlaintext(true).build();
    return managedChannel;
  }

  @Value("${grpc.order_server.port}")
  int orderServerPort;

  @Bean
  ServerBuilder serverBuilder(){
    return ServerBuilder.forPort(orderServerPort);
  }

  @Bean
  OrderService orderService(){return new OrderServiceImpl();}

  @Bean
  Server server(){
    return serverBuilder().addService((BindableService) orderService()).build();
  }

}
