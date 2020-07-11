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
package de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers;

import java.time.LocalDateTime;

import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.date.EarliestDate;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.Company;

/**
 * {@link AttributeValueFuser} for the founded of {@link Company}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class FoundedFuserOldest extends AttributeValueFuser<LocalDateTime, Company, Attribute> {

	public FoundedFuserOldest() {
		super(new EarliestDate<Company, Attribute>());
	}

	@Override
	public boolean hasValue(Company record, Correspondence<Attribute, Matchable> corr) {
		return record.hasValue(Company.FOUNDED);
	}

	@Override
	public LocalDateTime getValue(Company record, Correspondence<Attribute, Matchable> corr) {
		return record.getFounded();
	}

	@Override
	public void fuse(RecordGroup<Company, Attribute> group, Company fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<LocalDateTime, Company, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setFounded(fused.getValue());
		fusedRecord.setAttributeProvenance(Company.FOUNDED, fused.getOriginalIds());
	}

}
