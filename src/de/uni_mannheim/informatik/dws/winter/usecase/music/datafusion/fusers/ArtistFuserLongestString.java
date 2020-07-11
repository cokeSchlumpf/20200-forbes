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

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.LongestString;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;

/**
 * {@link AttributeValueFuser} for the ARTIST of {@link Release}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ArtistFuserLongestString extends AttributeValueFuser<String, Release, Attribute> {

	public ArtistFuserLongestString() {
		super(new LongestString<Release, Attribute>());
	}

	@Override
	public void fuse(RecordGroup<Release, Attribute> group, Release fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {

		// get the fused value
		FusedValue<String, Release, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);

		// set the value for the fused record
		fusedRecord.setArtist(fused.getValue());

		// add provenance info
		fusedRecord.setAttributeProvenance(Release.ARTIST, fused.getOriginalIds());
	}

	@Override
	public boolean hasValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.hasValue(Release.ARTIST);
	}

	@Override
	public String getValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.getArtist();
	}

}
