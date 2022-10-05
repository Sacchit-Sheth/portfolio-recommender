package com.prosprr.Portfolio.Recommender.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.prosprr.Portfolio.Recommender.exception.OpportunityCollectionException;
import com.prosprr.Portfolio.Recommender.model.OpportunityDTO;
import com.prosprr.Portfolio.Recommender.service.OpportunityService;

@RestController
public class OppController {

	@Autowired
	private OpportunityService opportunityService;

	@GetMapping("/opportunities")
	public ResponseEntity<?> getAllOpportunities(@RequestParam Optional<String> type) {
		try {
			List<OpportunityDTO> opportunities;
			if (!type.isPresent()) {
				opportunities = opportunityService.getAllOpportunities();
			} else {
				opportunities = opportunityService.getTypeOpportunities(type);
			}

			return new ResponseEntity<List<OpportunityDTO>>(opportunities, HttpStatus.OK);
		}

		catch (ConstraintViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

	@PostMapping("/opportunities")
	public ResponseEntity<?> createOpportunity(@RequestBody OpportunityDTO opportunity) {
		try {
			opportunityService.createOpportunity(opportunity);
			return new ResponseEntity<OpportunityDTO>(opportunity, HttpStatus.OK);
		} catch (ConstraintViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (OpportunityCollectionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}

	@PutMapping("/opportunities/{id}")
	public ResponseEntity<?> updateById(@PathVariable("id") String id, @RequestBody OpportunityDTO opportunity) {
		try {
			opportunityService.updateOpportunity(id, opportunity);
			return new ResponseEntity<>("Updated opportunity with id: " + id + "", HttpStatus.OK);
		} catch (ConstraintViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (OpportunityCollectionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/opportunities/{id}")
	public ResponseEntity<?> deleteById(@PathVariable("id") String id) throws OpportunityCollectionException {
		try {
			opportunityService.deleteOpportunityById(id);
			return new ResponseEntity<>("Successfully deleted Opportunity with id: " + id, HttpStatus.OK);
		} catch (OpportunityCollectionException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/portfolio")
	public ResponseEntity<?> getPortfolio(@RequestParam Optional<String> type, @RequestParam double totalInvestment,
			@RequestParam double expectedRate) {
		try {
			List<OpportunityDTO> opportunities;
			List<HashMap<String, String>> portfolio;
			if (!type.isPresent()) {
				opportunities = opportunityService.getAllOpportunities();
			} else {
				opportunities = opportunityService.getTypeOpportunities(type);
			}
			portfolio = opportunityService.getPortfolio(opportunities, totalInvestment, expectedRate);
			return new ResponseEntity<List<HashMap<String, String>>>(portfolio, HttpStatus.OK);
		} catch (ConstraintViolationException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}

}
