package com.shellshellfish.aaas.finance.trade.order.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.shellshellfish.aaas.finance.trade.order", mongoTemplateRef = "mongoTemplate")
public class MongoConfiguration {

	// @Bean
	// public Mongo mongo(MongoProperties mongoProperties) throws Exception {
	// return new MongoClient(mongoProperties.getHost(), mongoProperties.getPort());
	// }
	//
	// @Bean
	// public MongoTemplate mongoTemplate(MongoProperties mongoProperties) throws
	// Exception {
	// return new MongoTemplate(mongo(mongoProperties),
	// mongoProperties.getDatabase());
	// }

	@Value("${spring.data.mongodb.host}")
	private String host;

	@Value("${spring.data.mongodb.port}")
	private int port;

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Bean
	public Mongo mongo() throws Exception {
		// return new MongoClient(mongoProperties.getHost(), mongoProperties.getPort());
		return new MongoClient(host, port);
	}

	@Bean
	@Primary
	@Qualifier("mongoTemplate")
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(new MongoClient(host, port), database);
	}
}