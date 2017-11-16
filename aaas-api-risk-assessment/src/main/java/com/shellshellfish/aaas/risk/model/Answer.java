package com.shellshellfish.aaas.risk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Answer {

	private Integer questionOrdinal;
	@JsonProperty("selectedOption")
	private OptionItem selectedOption;
	
	public Answer() {
		
	}
	
	public Answer(Integer questionOrdinal, OptionItem selectedOption) {
		super();
		this.questionOrdinal = questionOrdinal;
		this.selectedOption = selectedOption;
	}
	
	public Integer getQuestionOrdinal() {
		return questionOrdinal;
	}
	public void setQuestionOrdinal(Integer questionOrdinal) {
		this.questionOrdinal = questionOrdinal;
	}
	public OptionItem getSelectedOption() {
		return selectedOption;
	}
	public void setSelectedOption(OptionItem selectedOption) {
		this.selectedOption = selectedOption;
	}
	

}
