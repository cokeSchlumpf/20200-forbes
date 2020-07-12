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
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;

/**
 * {@link EvaluationRule} for the names of {@link Company}s. The rule simply
 * compares the country of two {@link Company}s and returns true,in case they
 * are equal.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CountryEvaluationRule extends EvaluationRule<Company, Attribute> {

	@Override
	public boolean isEqual(Company record1, Company record2, Attribute attr) {
		// the title is correct if all tokens are there, but the order does not
		// matter
		return record1.getCountry().equals(record2.getCountry());
	}

	@Override
	public boolean isEqual(Company record1, Company record2, Correspondence<Attribute, Matchable> arg2) {
		// TODO Auto-generated method stub
		return record1.getCountry().equals(record2.getCountry());
	}

}
