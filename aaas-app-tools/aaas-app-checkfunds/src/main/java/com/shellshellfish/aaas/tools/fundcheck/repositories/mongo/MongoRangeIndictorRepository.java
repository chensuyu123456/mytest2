package com.shellshellfish.aaas.tools.fundcheck.repositories.mongo;



import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import com.shellshellfish.aaas.tools.fundcheck.model.RangeIndicator;

@Repository
public interface MongoRangeIndictorRepository extends
    
    org.springframework.data.mongodb.repository.MongoRepository<RangeIndicator, Long> {

	@Query("{\"code\":?0}")
    List<RangeIndicator> findByCode(String code);


}
