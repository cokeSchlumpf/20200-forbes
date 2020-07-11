/** 
 *
 * Copyright (C) 2019 Data and Web Science Group, University of Mannheim, Germany (code@dwslab.de)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.Company;

/**
 * {@link Comparator} for {@link Company}s based on the
 * {@link Company#getFounded()} value. With a maximal difference of 10 years.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyFoundedComparator10Years implements Comparator<Company, Attribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private YearSimilarity sim = new YearSimilarity(10);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Company entity1, Company entity2, Correspondence<Attribute, Matchable> corr) {
		double similarity = sim.calculate(entity1.getFounded(), entity2.getFounded());
		
		double postSimilarity = similarity * similarity;
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			if(entity1.getFounded()==null) {
				this.comparisonLog.setRecord1Value("");
			} else {
				this.comparisonLog.setRecord1Value(entity1.getFounded().toString());
			}
			if(entity2.getFounded()==null) {
				this.comparisonLog.setRecord2Value("");
			} else {
				this.comparisonLog.setRecord2Value(entity2.getFounded().toString());
			}
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
			this.comparisonLog.setPostprocessedSimilarity(Double.toString(postSimilarity));
		}
		
		return postSimilarity;
	}

	@Override
	public ComparatorLogger getComparisonLog() {
		return comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
