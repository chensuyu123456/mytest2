package com.shellshellfish.aaas.risk.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shellshellfish.aaas.risk.model.Question;
import com.shellshellfish.aaas.risk.model.SurveyTemplate;
import com.shellshellfish.aaas.risk.model.dto.QuestionDTO;
import com.shellshellfish.aaas.risk.service.QuestionService;
import com.shellshellfish.aaas.risk.service.SurveyTemplateService;
import com.shellshellfish.aaas.risk.util.CollectionResourceWrapper;
import com.shellshellfish.aaas.risk.util.Links;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/riskassessments")
public class QuestionController {

	private final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Autowired
	private SurveyTemplateService surveyTemplateService;
	
	@Autowired
	private QuestionService questionService;
	
	
	@ApiOperation("风险测评")
	@ApiResponses({
		@ApiResponse(code=200,message="OK"),
        @ApiResponse(code=400,message="请求参数没填好"),
        @ApiResponse(code=401,message="未授权用户"),        				
		@ApiResponse(code=403,message="服务器已经理解请求，但是拒绝执行它"),
		@ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")   
    })
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="path",name="bankUuid",dataType="String",required=true,value="银行卡的Uuid",defaultValue=""),
		@ApiImplicitParam(paramType="query",name="page",dataType="int",required=false,value="当前显示页",defaultValue=""),
		@ApiImplicitParam(paramType="query",name="size",dataType="int",required=false,value="每页显示数",defaultValue="")
	})
	@RequestMapping(value = "/banks/{bankUuid}/questions", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CollectionResourceWrapper<List<QuestionDTO>>> getAllQuestions(
			@PathVariable String bankUuid,
			@RequestParam(required=false)Integer page,
			@RequestParam(required=false)Integer size) throws Exception {
		log.debug("REST request to get questions based on page id and bank uuid. bank uuid:{}, page:{}", bankUuid, page);		
		if (size == null) {
			size = 20;
		}
		//TODO: get survey template based on bankUuid
						
		SurveyTemplate surveyTemplate = surveyTemplateService.getSurveyTemplate("南京银行个人客户风险评估表", "1.0");		
		
		List<Question> questions;
		if (page != null) {
			questions = questionService.getQuestionsByPage(page, size, surveyTemplate.getQuestions());
			
		} else {
			questions = surveyTemplate.getQuestions();
		}
		
		List<QuestionDTO> dtoList = questionService.convertToQuestionDTOs(questions, surveyTemplate.getId());
		
		CollectionResourceWrapper<List<QuestionDTO>> resource = new CollectionResourceWrapper<>(dtoList);
		Links links = new Links();
		if (page != null) {
			links.setSelf(String.format("/api/risk-assessment/banks/%s/questions?page=%d&size=%d", bankUuid, page, size));
			links.setNext(String.format("/api/risk-assessment/banks/%s/questions?page=%d&size=%d", bankUuid, page + 1, size));
			if (page > 0) {
				links.setPrev(String.format("/api/risk-assessment/banks/%s/questions?page=%d&size=%d", bankUuid, page - 1, size));
			}
		} else {
			links.setSelf(String.format("/api/risk-assessment/banks/%s/questions", bankUuid));
		}		
		
		resource.setLinks(links);	
		resource.setTotal(surveyTemplate.getQuestions().size());
		resource.setName("风险评估题目"); 
	//    AnnotationHelper.changeResourceAnnotion(resource, "_items"); 
	    
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}
	
}
