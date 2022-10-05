package com.prosprr.Portfolio.Recommender.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.prosprr.Portfolio.Recommender.exception.OpportunityCollectionException;
import com.prosprr.Portfolio.Recommender.model.OpportunityDTO;

public interface OpportunityService {

	public List<OpportunityDTO> getTypeOpportunities(Optional<String> type);

	public void createOpportunity(OpportunityDTO opportunity) throws OpportunityCollectionException;

	List<OpportunityDTO> getAllOpportunities();

	public void updateOpportunity(String id, OpportunityDTO opportunity) throws OpportunityCollectionException;

	public void deleteOpportunityById(String id) throws OpportunityCollectionException;

	public List<HashMap<String, String>> getPortfolio(List<OpportunityDTO> opportunities, double totalInvestment,
			double expectedRate);
}
