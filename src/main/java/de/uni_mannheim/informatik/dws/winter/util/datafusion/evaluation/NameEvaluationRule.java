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
package de.uni_mannheim.informatik.dws.winter.util.datafusion.evaluation;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.EqualsSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;

/**
 * {@link EvaluationRule} for the names of {@link Company}s. The rule simply
 * compares the names of two {@link Company}s and returns true, in case their
 * similarity based on {@link EqualsSimilarity} is 1.0.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class NameEvaluationRule extends EvaluationRule<Company, Attribute> {

	SimilarityMeasure<String> sim = new EqualsSimilarity<>();

	@Override
	public boolean isEqual(Company record1, Company record2, Attribute attr) {
		// the title is correct if all tokens are there, but the order does not
		// matter
		return sim.calculate(record1.getName(), record2.getName()) == 1.0;
	}

	@Override
	public boolean isEqual(Company record1, Company record2, Correspondence<Attribute, Matchable> arg2) {
		// TODO Auto-generated method stub
		return sim.calculate(record1.getName(), record2.getName()) == 1.0;
	}

}
