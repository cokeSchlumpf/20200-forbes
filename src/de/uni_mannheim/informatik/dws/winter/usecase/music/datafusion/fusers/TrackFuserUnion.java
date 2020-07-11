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
package de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers;

import java.util.List;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.list.Union;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Track;

/**
 * {@link AttributeValueFuser} for the tracks of {@link Release}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class TrackFuserUnion extends AttributeValueFuser<List<Track>, Release, Attribute> {

	public TrackFuserUnion() {
		super(new Union<Track, Release, Attribute>());
	}

	@Override
	public boolean hasValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.hasValue(Release.TRACKS);
	}

	@Override
	public List<Track> getValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.getTracks();
	}

	@Override
	public void fuse(RecordGroup<Release, Attribute> group, Release fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<List<Track>, Release, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setTracks(fused.getValue());
		fusedRecord.setAttributeProvenance(Release.TRACKS, fused.getOriginalIds());
	}

}
