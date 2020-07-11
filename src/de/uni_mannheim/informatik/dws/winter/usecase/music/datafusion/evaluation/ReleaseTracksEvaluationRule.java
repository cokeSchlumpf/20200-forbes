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

import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Track;

/**
 * {@link EvaluationRule} for the tracks of {@link Release}s. The rule simply
 * compares the full set of tracks of two {@link Release}s and returns true, in
 * case they are identical. The methods depends on the compare() function of
 * {@link Track}
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseTracksEvaluationRule extends EvaluationRule<Release, Attribute> {

	public boolean isEqual(Release record1, Release record2) {
		Set<String> tracks1 = new HashSet<String>();

		for (Track a : record1.getTracks()) {
			tracks1.add(a.getName());
		}

		Set<String> tracks2 = new HashSet<String>();
		for (Track a : record2.getTracks()) {
			tracks2.add(a.getName());
		}

		return tracks1.containsAll(tracks2) && tracks2.containsAll(tracks1);
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
