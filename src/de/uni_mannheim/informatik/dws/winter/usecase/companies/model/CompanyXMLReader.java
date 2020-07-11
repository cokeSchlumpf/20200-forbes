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
package de.uni_mannheim.informatik.dws.winter.usecase.companies.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

/**
 * A {@link MatchableFactory} for {@link Company}s.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class CompanyXMLReader extends XMLMatchableReader<Company, Attribute> implements FusibleFactory<Company, Attribute> {

	public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd['T'HH:mm:ss.SSS]")
            .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
            .toFormatter(Locale.ENGLISH);

	
	
	@Override
	protected void initialiseDataset(DataSet<Company, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
		// the schema is defined in the Company class and not interpreted from the file, so we have to set the attributes manually
		dataset.addAttribute(Company.ASSETS);
		dataset.addAttribute(Company.CITY);
		dataset.addAttribute(Company.COUNTRY);
		dataset.addAttribute(Company.FOUNDED);
		dataset.addAttribute(Company.INDUSTRY);
		dataset.addAttribute(Company.KEYPERSONS);
		dataset.addAttribute(Company.NAME);
		dataset.addAttribute(Company.REVENUE);
	}
	
	@Override
	public Company createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Company company = new Company(id, provenanceInfo);

		// fill the attributes
		company.setName(getValueFromChildElement(node, "name"));
		company.setCountry(getValueFromChildElement(node, "country"));
		company.setCity(getValueFromChildElement(node, "city"));
		company.setIndustry(getValueFromChildElement(node, "industry"));

		try {
			String assetString = getValueFromChildElement(node, "assets");
			if (assetString != null && assetString.length() > 0) {
				company.setAssets(Double.parseDouble(assetString));
			}

		} catch (NumberFormatException ne) {
			ne.printStackTrace();
		}
		try {
			String revenueString = getValueFromChildElement(node, "revenue");
			if (revenueString != null && revenueString.length() > 0) {
				company.setRevenue(Double.parseDouble(revenueString));
			}
		} catch (NumberFormatException ne) {
			ne.printStackTrace();
		}
		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "founded");
			if (date != null && !date.isEmpty()) {
				LocalDateTime dt = LocalDateTime.parse(date, CompanyXMLReader.DATE_FORMATTER);
				company.setFounded(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// load the list of actors
		List<String> keyPersons = getListFromChildElement(node, "keypeople");
		company.setKeyPersons(keyPersons);

		return company;
	}


	public Company createInstanceForFusion(RecordGroup<Company, Attribute> cluster) {

		List<String> ids = new LinkedList<String>();

		for (Company m : cluster.getRecords()) {
			ids.add(m.getIdentifier());
		}

		Collections.sort(ids);

		String mergedId = StringUtils.join(ids, '+');

		return new Company(mergedId, "fused");
	}

}
