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
package de.uni_mannheim.informatik.dws.winter.util.identityresolution;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.EqualsSimilarity;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;

/**
 * {@link Comparator} for {@link Company}s based on the
 * {@link Company#getName()} value and their {@link EqualsSimilarity} value
 * based on the longest token within the name. E.g. SAP SE --> SAP, SAP SE -->
 * SAP : SAP == SAP --> 1.0
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyNameComparatorLongestTokenEqual implements Comparator<Company, Attribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private EqualsSimilarity<String> sim = new EqualsSimilarity<String>();
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(Company entity1, Company entity2, Correspondence<Attribute, Matchable> corr) {
		double similarity = sim.calculate(getLongestString(entity1.getName()), getLongestString(entity2.getName()));
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(entity1.getName());
			this.comparisonLog.setRecord2Value(entity2.getName());
			
			this.comparisonLog.setRecord1PreprocessedValue(getLongestString(entity1.getName()));
			this.comparisonLog.setRecord2PreprocessedValue(getLongestString(entity2.getName()));
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		
		return similarity;
	}

	private static String getLongestString(String instanceString) {
		String[] tokens = instanceString.split("[^\\w']+");
		int maxLength = 0;
		String longestString = null;
		for (String s : tokens) {
			if (s.length() > maxLength) {
				maxLength = s.length();
				longestString = s;
			}
		}
		return longestString;
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