package com.prosprr.Portfolio.Recommender.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Investment {

	private String name;
	
	private int quantity;
	
	private double oneYearValue;
	
	private double twoYearValue;
	
	private double threeYearValue;
}
