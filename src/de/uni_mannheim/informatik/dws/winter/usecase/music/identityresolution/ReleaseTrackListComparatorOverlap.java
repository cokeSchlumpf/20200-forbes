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

import java.util.ArrayList;
import java.util.List;

import de.uni_mannheim.informatik.dws.winter.matching.rules.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.list.OverlapSimilarity;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Track;

/**
 * {@link Comparator} for {@link Release}s based on the
 * {@link Release#getTracks()} value and their {@link OverlapSimilarity} value.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseTrackListComparatorOverlap implements Comparator<Release, Attribute> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ComparatorLogger comparisonLog;
	
	private OverlapSimilarity sim = new OverlapSimilarity();

	public double compare(Release entity1, Release entity2, Correspondence<Attribute, Matchable> corr) {
		
		double similarity = sim.calculate(getTrackList(entity1), getTrackList(entity2));
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(getTrackList(entity1).toString());
			this.comparisonLog.setRecord2Value(getTrackList(entity2).toString());
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		
		return similarity;
	}
	
	private List<String> getTrackList(Release release) {
		List<String> trackList = new ArrayList<String>();
		if(release.getTracks()!=null) {
			for (Track t: release.getTracks()) {
				trackList.add(t.getName());
			}
		}
		return trackList;
	}
	
	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
