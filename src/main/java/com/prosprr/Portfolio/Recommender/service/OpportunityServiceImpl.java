package com.prosprr.Portfolio.Recommender.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prosprr.Portfolio.Recommender.exception.OpportunityCollectionException;
import com.prosprr.Portfolio.Recommender.model.OpportunityDTO;
import com.prosprr.Portfolio.Recommender.repository.OpportunityRepo;

@Service
public class OpportunityServiceImpl implements OpportunityService {

	class Pair {
		double rate;
		List<List<Integer>> freq;

		Pair(double rate, List<List<Integer>> freq) {
			this.rate = rate;
			this.freq = freq;
		}

		public double getRate() {
			return this.rate;
		}

		public List<List<Integer>> getFreq() {
			return this.freq;
		}
	}
	
	@Autowired
	private OpportunityRepo opportunityRepo;

	@Override
	public List<OpportunityDTO> getAllOpportunities() {
		List<OpportunityDTO> opportunities = opportunityRepo.findAll();
		if (opportunities.size() > 0) {
			return opportunities;
		} else {
			return new ArrayList<OpportunityDTO>();
		}
	}

	@Override
	public List<OpportunityDTO> getTypeOpportunities(Optional<String> type) {
		List<OpportunityDTO> opportunities = opportunityRepo.findByType(type);
		if (opportunities.size() > 0) {
			return opportunities;
		} else {
			return new ArrayList<OpportunityDTO>();
		}
	}

	@Override
	public void createOpportunity(OpportunityDTO opportunity) throws OpportunityCollectionException {
		Optional<OpportunityDTO> opportunityOptional = opportunityRepo.findByName(opportunity.getName());
		if (opportunityOptional.isPresent()) {
			throw new OpportunityCollectionException(OpportunityCollectionException.opportunityAlreadyExists());
		} else {
			opportunityRepo.save(opportunity);
		}
	}

	@Override
	public void updateOpportunity(String id, OpportunityDTO opportunity) throws OpportunityCollectionException {
		Optional<OpportunityDTO> opportunityWithId = opportunityRepo.findByOpportunityId(id);

		if (opportunityWithId.isPresent()) {
			OpportunityDTO opportunityToUpdate = opportunityWithId.get();

			if (opportunity.getName() != null && !opportunity.getName().trim().equals(opportunityToUpdate.getName())) {
				opportunityToUpdate.setName(opportunity.getName());
			}
			if (opportunity.getLastTwelveMonthReturns() != opportunityToUpdate.getLastTwelveMonthReturns()) {
				opportunityToUpdate.setLastTwelveMonthReturns(opportunity.getLastTwelveMonthReturns());
			}
			if (opportunity.getNextTwelveMonthReturnForecast() != opportunityToUpdate
					.getNextTwelveMonthReturnForecast()) {
				opportunityToUpdate.setNextTwelveMonthReturnForecast(opportunity.getNextTwelveMonthReturnForecast());
			}
			if (opportunity.getUnitPrice() != opportunityToUpdate.getUnitPrice()) {
				opportunityToUpdate.setUnitPrice(opportunity.getUnitPrice());
			}
			if (opportunity.getType() != null && !opportunity.getType().trim().equals(opportunityToUpdate.getType())) {
				opportunityToUpdate.setType(opportunity.getType());
			}
			opportunityRepo.save(opportunityToUpdate);
		} else {
			throw new OpportunityCollectionException(OpportunityCollectionException.NotFoundException(id));
		}
	}

	@Override
	public void deleteOpportunityById(String id) throws OpportunityCollectionException {
		Optional<OpportunityDTO> opportunityOptional = opportunityRepo.findById(id);
		if (!opportunityOptional.isPresent()) {
			throw new OpportunityCollectionException(OpportunityCollectionException.NotFoundException(id));
		} else {
			opportunityRepo.deleteById(id);
		}
	}

	

	@Override
	public List<HashMap<String, String>> getPortfolio(List<OpportunityDTO> opportunities, double totalInvestment,
			double expectedRate) {
		
		List<Integer> stk = new ArrayList<>();
		Queue<Pair> queue = new PriorityQueue<>(Comparator.comparing(Pair::getRate));
		
		int[][] tableSets = new int[opportunities.size() + 1][(int) totalInvestment + 1];

		for (int j = 0; j <= (int) totalInvestment; j++) {
			tableSets[0][j] = 0;
		}
		for (int i = 0; i <= opportunities.size(); i++) {
			tableSets[i][0] = 1;
		}

		for (int i = 1; i <= opportunities.size(); i++) {
			for (int j = 1; j <= (int) totalInvestment; j++) {
				if (j >= opportunities.get(i - 1).getUnitPrice()) {
					tableSets[i][j] = tableSets[i][j - (int) opportunities.get(i - 1).getUnitPrice()];
				}
				tableSets[i][j] += tableSets[i - 1][j];
			}
		}
		
		System.out.println(tableSets[opportunities.size()][(int) totalInvestment]);

		getSolutions(opportunities.size(), (int) totalInvestment, tableSets, opportunities, expectedRate,stk,queue);

		List<HashMap<String, String>> result = new ArrayList<>();
		
		if(queue.size()==0) return result;
		
		
		List<List<Integer>> ans = queue.peek().getFreq();
		
		double overallOneYearValue = 0.0;
		double overallTwoYearValue = 0.0;
		double overallThreeYearValue = 0.0;

		for (List<Integer> inv : ans) {
			HashMap<String, String> eachOpp = new HashMap<String, String>();
			String name = opportunities.get(inv.get(1)).getName();
			String quantity = inv.get(0) + "";
			String oneYearValue = "" + (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 1);
			String twoYearValue = "" + (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 2);
			String threeYearValue = "" + (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 3);
			eachOpp.put("name", name);
			eachOpp.put("quantity", quantity);
			eachOpp.put("oneYearValue", oneYearValue);
			eachOpp.put("twoYearValue", twoYearValue);
			eachOpp.put("threeYearValue", threeYearValue);
			overallOneYearValue += (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 1);
			overallTwoYearValue += (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 2);
			overallThreeYearValue += (opportunities.get(inv.get(1)).getUnitPrice() * inv.get(0))
					* (1 + (opportunities.get(inv.get(1)).getNextTwelveMonthReturnForecast() / 100) * 3);
			result.add(eachOpp);
		}
		double rate = 100 * ((overallOneYearValue - totalInvestment) / totalInvestment);
		HashMap<String, String> overall = new HashMap<String, String>();
		String OvoneYearValue = "" + overallOneYearValue;
		String OvtwoYearValue = "" + overallTwoYearValue;
		String OvthreeYearValue = "" + overallThreeYearValue;
		String Ovrate = "" + rate;
		overall.put("Rate", Ovrate);
		overall.put("Overall oneYearValue", OvoneYearValue);
		overall.put("Overall twoYearValue", OvtwoYearValue);
		overall.put("Overall threeYearValue", OvthreeYearValue);
		result.add(overall);

		return result;
	}

	public static int countUnique(List<Integer> stk2) {
		if (stk2.size() == 0) {
			return 0;
		}
		int count = 1;
		for (int i = 0; i < stk2.size() - 1; i++) {
			if (stk2.get(i) != stk2.get(i + 1)) {
				count++;
			}
		}
		return count;
	}

	public static List<List<Integer>> countFreq(List<Integer> stk2) {
		List<List<Integer>> freq = new ArrayList<>();
		
		Map<Integer, Integer> mp = new HashMap<>();
		for (int i = 0; i < stk2.size(); i++)
        {
            if (mp.containsKey(stk2.get(i)))
            {
                mp.put(stk2.get(i), mp.get(stk2.get(i)) + 1);
            }
            else
            {
                mp.put(stk2.get(i), 1);
            }
        }
		for (Map.Entry<Integer, Integer> entry : mp.entrySet())
        {
			List<Integer> temp = new ArrayList<>();
			temp.add(0, entry.getKey());
			temp.add(0, entry.getValue());
			freq.add(temp);
        }
		
		return freq;
	}

	private void getSolutions(int i, int j, int[][] tableSets, List<OpportunityDTO> opportunities,
			double expectedRate, List<Integer> stk, Queue<Pair> queue) {
		if (tableSets[i][j] == 0) {
			return;
		}
		if (j == 0) {
			if (countUnique(stk) <= 5) {
				double rate = 0.0;
				for (Integer idx : stk) {
					rate += (opportunities.get(idx).getNextTwelveMonthReturnForecast());
				}
				rate /= stk.size();
//				for(Integer x:stk)
//				{
//					System.out.print(x+" ");
//				}
//				System.out.println();
//				for(List<Integer> x: countFreq(stk))
//				{
//					System.out.print(x.get(0)+" "+x.get(1));
//				}
//				System.out.println();
				if (queue.size() == 5) {
					if (Math.abs(rate - expectedRate) < queue.peek().getRate()) {
						queue.poll();
						queue.add(new Pair(rate, countFreq(stk)));
					}
				} else {
					queue.add(new Pair(rate, countFreq(stk)));
				}
			}
			return;
		}
		if (tableSets[i][j] > tableSets[i - 1][j]) {
			stk.add(i - 1);
			getSolutions(i, j - (int) opportunities.get(i - 1).getUnitPrice(), tableSets, opportunities, expectedRate,stk,queue);
			stk.remove(stk.size() - 1);
		}
		getSolutions(i - 1, j, tableSets, opportunities, expectedRate,stk,queue);
	}
}