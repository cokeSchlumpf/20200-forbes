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

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Release}s.
 * 
 *  @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseXMLFormatter extends XMLFormatter<Release> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("releases");
	}

	public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("yyyy[-MM[-dd['T'HH:mm:ss.SSS]]]")
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
            .toFormatter(Locale.ENGLISH);
	

	@Override
	public Element createElementFromRecord(Release record, Document doc) {
		Element release = doc.createElement("release");

		release.appendChild(createTextElement("id", record.getIdentifier(), doc));

		release.appendChild(createTextElementWithProvenance("name", record.getName(),
				record.getMergedAttributeProvenance(Release.NAME), doc));
		release.appendChild(createTextElementWithProvenance("artist", record.getArtist(),
				record.getMergedAttributeProvenance(Release.ARTIST), doc));
		if (record.getDate() != null) {
			release.appendChild(createTextElementWithProvenance("release-date", DATE_FORMATTER.format(record.getDate()),
					record.getMergedAttributeProvenance(Release.DATE), doc));
		}
		release.appendChild(createTextElementWithProvenance("release-country", record.getCountry(),
				record.getMergedAttributeProvenance(Release.COUNTRY), doc));
		if (record.getDuration() != null) {
			release.appendChild(createTextElementWithProvenance("duration", "" + record.getDuration(),
					record.getMergedAttributeProvenance(Release.DURATION), doc));
		}
		release.appendChild(createTextElementWithProvenance("label", record.getLabel(),
				record.getMergedAttributeProvenance(Release.LABEL), doc));
		release.appendChild(createTextElementWithProvenance("genre", record.getGenre(),
				record.getMergedAttributeProvenance(Release.GENRE), doc));
		if (record.getTracks() != null && record.getTracks().size() > 0) {
			release.appendChild(createTrackList(record, doc));
		}

		return release;
	}

	protected Element createTextElementWithProvenance(String name, String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createTrackList(Release record, Document doc) {
		Element tracksRoot = doc.createElement("tracks");
		tracksRoot.setAttribute("provenance", record.getMergedAttributeProvenance(Release.TRACKS));
		// sort tracks
		List<Track> sortTrack = record.getTracks();
		Collections.sort(sortTrack, new Comparator<Track>() {

			public int compare(Track record1, Track record2) {
				if (record1.getPosition() == null && record1.getPosition() == null) {
					return 0;
				} else if (record1.getPosition() == null) {
					return 1;
				} else if (record2.getPosition() == null) {
					return -1;
				} else {
					return Integer.compare(record1.getPosition(), record2.getPosition());
				}
			}
		});

		for (Track track : sortTrack) {
			Element trackRoot = doc.createElement("track");
			trackRoot.appendChild(createTextElement("name", track.getName(), doc));
			if (track.getDuration() != null) {
				trackRoot.appendChild(createTextElement("duration", "" + track.getDuration(), doc));
			}
			if (track.getPosition() != null) {
				trackRoot.appendChild(createTextElement("position", "" + track.getPosition(), doc));
			}
			tracksRoot.appendChild(trackRoot);
		}
		return tracksRoot;
	}

}
