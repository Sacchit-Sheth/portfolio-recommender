package com.prosprr.Portfolio.Recommender.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.prosprr.Portfolio.Recommender.model.OpportunityDTO;

@Repository
public interface OpportunityRepo extends MongoRepository<OpportunityDTO, String> {

	@Query("{'type': ?0}")
	List<OpportunityDTO> findByType(Optional<String> type);

	@Query("{'name': ?0}")
	Optional<OpportunityDTO> findByName(String name);

	@Query("{'id': ?0}")
	Optional<OpportunityDTO> findByOpportunityId(String id);

}
