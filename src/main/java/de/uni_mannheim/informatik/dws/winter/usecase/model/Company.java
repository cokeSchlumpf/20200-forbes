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
package de.uni_mannheim.informatik.dws.winter.usecase.model;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * A {@link Record} representing a company.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Company extends AbstractRecord<Attribute> {

	/*
	 * example entry <company> <id>random1</id> <name>SAP</name>
	 * <type>IPO</type> <website>http://www.sap.com</website>
	 * <founded>1972-01-01</founded> <country>DE</country> <city>Walldorf</city>
	 * <industry>Healthcare</industry> <assets>3730000123</assets>
	 * <revenue>312344221</revenue> <employees>10</employees> <keypeople>
	 * <name>Hasso Plattner</name> <name>Hans-Werner Hector</name> <name>Klaus
	 * Tschira</name> </keypeople> </company>
	 */

	private static final long serialVersionUID = 1L;

	public Company(String identifier, String provenance) {
		super(identifier, provenance);
		keyPersons = new LinkedList<String>();
	}

	private String name;
	private LocalDateTime founded;
	private String country;
	private String city;
	private String industry;
	private Double assets;
	private Double revenue;
	private List<String> keyPersons;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the founded
	 */
	public LocalDateTime getFounded() {
		return founded;
	}

	/**
	 * @param founded
	 *            the founded to set
	 */
	public void setFounded(LocalDateTime founded) {
		this.founded = founded;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * @param industry
	 *            the industry to set
	 */
	public void setIndustry(String industry) {
		this.industry = industry;
	}

	/**
	 * @return the assets
	 */
	public Double getAssets() {
		return assets;
	}

	/**
	 * @param assets
	 *            the assets to set
	 */
	public void setAssets(Double assets) {
		this.assets = assets;
	}

	/**
	 * @return the revenue
	 */
	public Double getRevenue() {
		return revenue;
	}

	/**
	 * @param revenue
	 *            the revenue to set
	 */
	public void setRevenue(Double revenue) {
		this.revenue = revenue;
	}

	/**
	 * @return the keyPersons
	 */
	public List<String> getKeyPersons() {
		return keyPersons;
	}

	/**
	 * @param keyPersons
	 *            the keyPersons to set
	 */
	public void setKeyPersons(List<String> keyPersons) {
		this.keyPersons = keyPersons;
	}

	/*
	public static final String NAME = "Name";
	public static final String COUNTRY = "Country";
	public static final String CITY = "City";
	public static final String INDUSTRY = "Industry";
	public static final String ASSETS = "Assets";
	public static final String REVENUE = "Revenue";
	public static final String FOUNDED = "Founded";
	public static final String KEYPERSONS = "Keypersons";*/
	
	public static final Attribute NAME = new Attribute("Name");
	public static final Attribute COUNTRY = new Attribute("Country");
	public static final Attribute CITY = new Attribute("City");
	public static final Attribute INDUSTRY = new Attribute("Industry");
	public static final Attribute ASSETS = new Attribute("Assets");
	public static final Attribute REVENUE = new Attribute("Revenue");
	public static final Attribute FOUNDED = new Attribute("Founded");
	public static final Attribute KEYPERSONS = new Attribute("Keypersons");

	private Map<Attribute, Collection<String>> provenance = new HashMap<>();
	private Collection<String> recordProvenance;
	
	public void setRecordProvenance(Collection<String> provenance) {
		//this.provenance.put("RECORD", provenance);
		recordProvenance = provenance;
	}

	public Collection<String> getRecordProvenance() {
		return recordProvenance;
	}

	public void setAttributeProvenance(Attribute attribute, Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(Attribute attribute) {
		return provenance.get(attribute);
	}

	public String getMergedAttributeProvenance(Attribute name2) {
		Collection<String> prov = provenance.get(name2);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}

	/*public Collection<String> getAttributeNames() {
		return Arrays.asList(new String[] { NAME, COUNTRY, CITY, INDUSTRY, ASSETS, REVENUE, FOUNDED, KEYPERSONS });
	} */

	@Override
	public boolean hasValue(Attribute attribute) {
		if (attribute == NAME)
			return getName() != null && !getName().isEmpty();
		else if (attribute == COUNTRY)
			return getCountry() != null && !getCountry().isEmpty();
		else if (attribute == CITY)
			return getCity() != null && !getCity().isEmpty();
		else if (attribute == INDUSTRY)
			return getIndustry() != null && !getIndustry().isEmpty();
		else if (attribute == ASSETS)
			return getAssets() != null && getAssets() > 0;
		else if (attribute == REVENUE)
			return getRevenue() != null && getRevenue() > 0;
		else if (attribute == FOUNDED)
			return getFounded() != null;
		else if (attribute == KEYPERSONS)
			return getKeyPersons() != null && getKeyPersons().size() > 0;
		else
			return false;
	}

	// Solely translates the actors into a human readable string (names of
	// actors)
	protected String keyPersonsLists() {
		String keyP = "";
		if (getKeyPersons() != null) {
			for (String s : getKeyPersons()) {
				if (keyP.length() > 1) {
					keyP += "|";
				}
				keyP += s;
			}
		}
		return keyP;
	}

	@Override
	public String toString() {
		String name = getName() == null ? "": getName();
		String country = getCountry() == null ? "": getCountry();
		String city = getCity() == null ? "": getCity();
		String industry = getIndustry() == null ? "": getIndustry();
		String assets = getAssets() == null ? "": getAssets().toString();
		String founded = getFounded() == null ? "": getFounded().toString();
		String revenue = getRevenue() == null ? "": getRevenue().toString();
		return String.format(
				"[Name: %s / Country: %s / City: %s / Industry: %s / Assets: %s / Revenue: %s / Founded: %s / Keyperson(s): %s]",
				name, country, city, industry, assets, revenue, founded.toString(),
				keyPersonsLists());
	}

	public Object[] getValues() {
		String name = getName() == null ? "": getName();
		String country = getCountry() == null ? "": getCountry();
		String city = getCity() == null ? "": getCity();
		String industry = getIndustry() == null ? "": getIndustry();
		String assets = getAssets() == null ? "": getAssets().toString();
		String founded = getFounded() == null ? "": getFounded().toString();
		String revenue = getRevenue() == null ? "": getRevenue().toString();
		
		String[] values = new String[] {name, country, city, industry, assets, founded, revenue, keyPersonsLists()};
		return values;
	}

	public void setValues(Object[] values) {
		setName((String)values[0]);
		setCountry((String)values[1]);
		setCity((String)values[2]);
		setIndustry((String)values[3]);
		setAssets((Double)(values[4]));
		setFounded((LocalDateTime)(values[5]));
		setRevenue((Double)(values[6]));
		List<String> keyPersonas = new LinkedList<String>();
		for(String value : ((String)values[7]).split("|")){
			keyPersonas.add(value);
		}
		setKeyPersons(keyPersonas);
	}
}
