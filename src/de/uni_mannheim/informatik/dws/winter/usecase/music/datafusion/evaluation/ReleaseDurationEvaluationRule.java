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
package de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.numeric.PercentageSimilarity;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;

/**
 * {@link EvaluationRule} for the duration of {@link Releases}s. The rule simply
 * compares the duration of two {@link Release}s and returns true, in case their
 * similarity based on {@link PercentageSimilarity} does not exceed a difference of 5%.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseDurationEvaluationRule extends EvaluationRule<Release, Attribute> {

	PercentageSimilarity sim = new PercentageSimilarity(0.05);

	public boolean isEqual(Release record1, Release record2) {
		if(record1.getDuration()==null && record2.getDuration()==null) {
			return true;
		} else if (record1.getDuration()==null || record2.getDuration()==null) {
			return false;
		} else {
			return sim.calculate((double) record1.getDuration(), (double) record2.getDuration()) > 0.0;
		}
	}

	@Override
	public boolean isEqual(Release arg0, Release arg1, Attribute arg2) {
		// TODO Auto-generated method stub
		return isEqual(arg0, arg1);
	}

	@Override
	public boolean isEqual(Release arg0, Release arg1, Correspondence<Attribute, Matchable> arg2) {
		// TODO Auto-generated method stub
		return isEqual(arg0, arg1);
	}

}
