package com.prosprr.Portfolio.Recommender.model;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="opportunities")
public class OpportunityDTO {
	@Id
	private String id;
	
	@NotNull(message="name is required.")
	private String name;
	
	@NotNull(message="LastTwelveMonthReturns is required.")
	private double lastTwelveMonthReturns ;
	
	@NotNull(message="NextTwelveMonthReturnForecast is required.")
	private double nextTwelveMonthReturnForecast ;
	
	@NotNull(message="UnitPrice is required.")
	private double unitPrice ;
	
	@NotNull(message="Type is required.")
	private String type;

}
