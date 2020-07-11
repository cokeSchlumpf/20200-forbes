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

import java.time.LocalDateTime;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.MostRecent;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;

/**
 * {@link AttributeValueFuser} for the date of {@link Release}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class DateFuserMostRecent extends AttributeValueFuser<LocalDateTime, Release, Attribute> {

	public DateFuserMostRecent() {
		super(new MostRecent<LocalDateTime, Release, Attribute>());
	}

	@Override
	public boolean hasValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.hasValue(Release.DATE);
	}

	@Override
	public LocalDateTime getValue(Release record, Correspondence<Attribute, Matchable> corr) {
		return record.getDate();
	}

	@Override
	public void fuse(RecordGroup<Release, Attribute> group, Release fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<LocalDateTime, Release, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setDate(fused.getValue());
		fusedRecord.setAttributeProvenance(Release.DATE, fused.getOriginalIds());
	}

}
