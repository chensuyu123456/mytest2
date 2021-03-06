package com.shellshellfish.aaas.risk.repositories.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.shellshellfish.aaas.risk.model.dao.SurveyTemplate;

public interface SurveyTemplateRepository extends MongoRepository<SurveyTemplate, String> {
	SurveyTemplate findOneByTitleAndVersion(String title, String version);

	Page<SurveyTemplate> findByTitleAndVersion(String title, String version, Pageable pageable);
}
