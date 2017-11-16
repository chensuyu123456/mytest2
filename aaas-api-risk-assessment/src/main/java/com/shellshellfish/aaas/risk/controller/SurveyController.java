package com.shellshellfish.aaas.risk.controller;

import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.shellshellfish.aaas.risk.model.Question;
import com.shellshellfish.aaas.risk.model.SurveyResult;
import com.shellshellfish.aaas.risk.model.SurveyTemplate;
import com.shellshellfish.aaas.risk.model.dto.QuestionDTO;
import com.shellshellfish.aaas.risk.repository.SurveyResultRepository;
import com.shellshellfish.aaas.risk.repository.SurveyTemplateRepository;
import com.shellshellfish.aaas.risk.service.SurveyResultService;
import com.shellshellfish.aaas.risk.service.SurveyTemplateService;
import com.shellshellfish.aaas.risk.util.AnnotationHelper;
import com.shellshellfish.aaas.risk.util.Links;
import com.shellshellfish.aaas.risk.util.ResourceWrapper;

@RestController
@RequestMapping(value = "/api/risk-assessment")
public class SurveyController {

	private final Logger log = LoggerFactory.getLogger(SurveyController.class);
	
	@Autowired 
	private SurveyTemplateService surveyTemplateService;
	
	@Autowired
	private SurveyResultService surveyResultService;
	
	@RequestMapping(value = "/banks/{bankUuid}/survey-templates/latest", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResourceWrapper<SurveyTemplate>> getSurveyTemplate(@PathVariable String bankUuid) throws URISyntaxException {
		log.debug("REST request to get a survey template. bank uuid:{}", bankUuid);		
		
		//TODO: get survey template based on bankUuid
		
		SurveyTemplate surveyTemplate = surveyTemplateService.getSurveyTemplate("南京银行个人客户风险评估表", "1.0");
		ResourceWrapper<SurveyTemplate> resource = new ResourceWrapper<>(surveyTemplate);
		Links links = new Links();
		links.setSelf(String.format("/api/risk-assessment/banks/%s/survey-templates/latest", bankUuid));
		
		resource.setLinks(links);
		resource.setName("风险评估表");
		//AnnotationHelper.changeResourceAnnotion(resource, "surveyTemplate");
		 
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/banks/{bankUuid}/users/{userUuid}/survey-results", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SurveyResult> saveSurveyResult(@PathVariable String bankUuid,
														 @PathVariable String userUuid,
														 @RequestBody SurveyResult surveyResult) throws URISyntaxException, Exception{
		log.debug("REST request to Insert or Update a SurveyResult.");
		
		surveyResult.setUserId(userUuid);
		
		surveyResultService.save(surveyResult);	
		
		return ResponseEntity.ok().body(surveyResult);
	}
	
	
}
