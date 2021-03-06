package com.shellshellfish.aaas.finance.caculate;

import io.grpc.Server;
import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("com.shellshellfish.aaas.finance.caculate.repositories")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class FinanceCaculateServiceApplication {

	private static Server server;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run
				(FinanceCaculateServiceApplication.class, args);
		server = (Server) configurableApplicationContext.getBean("server");
		server.start();
		blockUntilShutdown();
	}

	private static void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}

	}


}
