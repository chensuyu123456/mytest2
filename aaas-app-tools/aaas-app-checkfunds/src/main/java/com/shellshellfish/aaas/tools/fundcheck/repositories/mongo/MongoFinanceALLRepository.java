package com.shellshellfish.aaas.tools.fundcheck.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.shellshellfish.aaas.tools.fundcheck.model.MongoFinanceAll;

public interface MongoFinanceALLRepository extends MongoRepository<MongoFinanceAll, Long> {
	MongoFinanceAll findAllByDate(String date);
	void deleteAllByDate(String date);
}
