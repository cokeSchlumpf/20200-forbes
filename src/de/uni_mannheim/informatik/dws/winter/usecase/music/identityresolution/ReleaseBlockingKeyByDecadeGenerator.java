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

package de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution;

import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.BlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;

/**
 * {@link BlockingKeyGenerator} for {@link Release}s, which generates a blocking
 * key based on the decade. E.g. 1999-->199, 2014 --> 201
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseBlockingKeyByDecadeGenerator extends RecordBlockingKeyGenerator<Release, Attribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void generateBlockingKeys(Release record, Processable<Correspondence<Attribute, Matchable>> correspondences,
			DataIterator<Pair<String, Release>> resultCollector) {
		if (record.getDate() != null) {
			resultCollector.next(new Pair<String, Release>(Integer.toString(record.getDate().getYear() / 10), record));
		}
	}

}
