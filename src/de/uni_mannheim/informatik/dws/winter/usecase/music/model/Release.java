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
package de.uni_mannheim.informatik.dws.winter.usecase.music.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Record;

/**
 * A {@link Record} representing a release.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Release extends AbstractRecord<Attribute> {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	private static final long serialVersionUID = 1L;

	public Release(String identifier, String provenance) {
		super(identifier, provenance);
		tracks = new LinkedList<Track>();
	}

	private String name;
	private String artist;
	private LocalDateTime date;
	private String country;
	private Integer duration;
	private String label;
	private String genre;
	private List<Track> tracks;

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
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}

	/**
	 * @param artist
	 *            the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
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
	 * @return the duration
	 */
	public Integer getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            the duration to set
	 */
	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre
	 *            the genre to set
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public static final Attribute NAME = new Attribute("Name");
	public static final Attribute ARTIST = new Attribute("artist");
	public static final Attribute DATE = new Attribute("Date");
	public static final Attribute COUNTRY = new Attribute("Country");
	public static final Attribute LABEL = new Attribute("Label");
	public static final Attribute GENRE = new Attribute("Genre");
	public static final Attribute DURATION = new Attribute("Duration");
	public static final Attribute TRACKS = new Attribute("Tracks");

	private Map<Attribute, Collection<String>> provenance = new HashMap<Attribute, Collection<String>>();
	private Collection<String> recordProvenance;

	public void setRecordProvenance(Collection<String> provenance) {
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
		return Arrays.asList(new String[] { NAME, ARTIST, DATE, COUNTRY, LABEL, GENRE, DURATION, TRACKS });
	} */
	
	@Override
	public boolean hasValue(Attribute attribute) {	
		if (attribute == NAME)
			return getName() != null && !getName().isEmpty();
		else if (attribute == ARTIST)
			return getArtist() != null && !getArtist().isEmpty();
		else if (attribute == DATE)
			return getDate() != null;
		else if (attribute == COUNTRY)
			return getCountry() != null && !getCountry().isEmpty();
		else if (attribute == LABEL)
			return getLabel() != null && !getLabel().isEmpty();
		else if (attribute == GENRE)
			return getGenre() != null && !getGenre().isEmpty();
		else if (attribute == DURATION)
			return getDuration() != null && getDuration() > 0;
		else if (attribute == TRACKS)
			return getTracks() != null && getTracks().size() > 0;
		else
			return false;
	}

	// Solely translates the actors into a human readable string (names of
	// actors)
	protected String tracksAsList() {
		String tracks = "";
		for (Track track : getTracks()) {
			if (tracks.length() > 1) {
				tracks += "|";
			}
			tracks += track.getName();
		}
		return tracks;
	}

	@Override
	public String toString() {
		
		String name = getName() == null ? "" : getName();
		String artist = getArtist() == null ? "" : getArtist();
		String country = getCountry() == null ? "" : getCountry();
		String date = getDate() == null ? "" : getDate().toString();
		String label = getLabel() == null ? "" : getLabel();
		String genre = getGenre() == null ? "" : getGenre();
		String duration = getDuration() == null ? "" : getDuration().toString();
		String tracks = getTracks() == null ? "" : tracksAsList();
		
		return String.format(
				"[Name: %s / Artist: %s / Country: %s / Date: %s / Label: %s / Genre: %s / Duration: %s / Track(s): %s]",
				name, artist, country, date, label, genre, duration, tracks);
	}

	public Object[] getValues() {
		String name = getName() == null ? "" : getName();
		String artist = getArtist() == null ? "" : getArtist();
		String country = getCountry() == null ? "" : getCountry();
		String date = getDate() == null ? "" : getDate().toString();
		String label = getLabel() == null ? "" : getLabel();
		String genre = getGenre() == null ? "" : getGenre();
		String duration = getDuration() == null ? "" : getDuration().toString();
		String tracks = getTracks() == null ? "" : tracksAsList();
		
		Object[] values = new Object[] {name,artist, country, date, label, genre, duration, tracks};
		return values;
	}

	public void setValues(Object[] values) {
		// TODO Auto-generated method stub
		
	}

	public Object getValue(Attribute attribute) {
		if (attribute == NAME)
			return getName();
		else if (attribute == ARTIST)
			return getArtist();
		else if (attribute == DATE)
			return getDate();
		else if (attribute == COUNTRY)
			return getCountry();
		else if (attribute == LABEL)
			return getLabel();
		else if (attribute == GENRE)
			return getGenre();
		else if (attribute == DURATION)
			return getDuration();
		else if (attribute == TRACKS)
			return getTracks();
		else
			return null;
	}
}
