package com.shellshellfish.aaas.userinfo.repositories.funds;

import com.shellshellfish.aaas.userinfo.model.dao.CoinFundYieldRate;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoCoinFundYieldRateRepository extends MongoRepository<CoinFundYieldRate, Long> {

	CoinFundYieldRate findFirstByCodeAndQueryDateBefore(String code, Long queryDate, Sort sort);

	List<CoinFundYieldRate> findAllByCodeAndQueryDateIsBetween(String fundCode, long sttime, long endtime, Sort sort);
}
