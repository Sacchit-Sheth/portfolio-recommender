package com.prosprr.Portfolio.Recommender.exception;

public class OpportunityCollectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpportunityCollectionException(String message) {
		super(message);
	}
	
	public static String NotFoundException(String id) {
		return "Opportunity with "+id+" not found!";
	}
	
	public static String opportunityAlreadyExists() {
		return "Opportunity with given name already exists";
	}
}
