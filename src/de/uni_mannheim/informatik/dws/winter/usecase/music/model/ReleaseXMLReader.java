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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * A {@link MatchableFactory} for {@link Release}s.
 * 
 *  @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class ReleaseXMLReader extends XMLMatchableReader<Release, Attribute> implements FusibleFactory<Release, Attribute> {

	private static final Logger logger = WinterLogManager.getLogger();
	
	
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
	protected void initialiseDataset(DataSet<Release, Attribute> dataset) {
		super.initialiseDataset(dataset);
		
		// the schema is defined in the Release class and not interpreted from the file, so we have to set the attributes manually
		dataset.addAttribute(Release.ARTIST);
		dataset.addAttribute(Release.COUNTRY);
		dataset.addAttribute(Release.DATE);
		dataset.addAttribute(Release.DURATION);
		dataset.addAttribute(Release.GENRE);
		dataset.addAttribute(Release.LABEL);
		dataset.addAttribute(Release.NAME);
		dataset.addAttribute(Release.TRACKS);
		
	}
	@Override
	public Release createModelFromElement(Node node, String provenanceInfo) {
		String id = getValueFromChildElement(node, "id");

		// create the object with id and provenance information
		Release release = new Release(id, provenanceInfo);

		// fill the attributes
		release.setName(getValueFromChildElement(node, "name").intern());
		release.setArtist(getValueFromChildElement(node, "artist").intern());

		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "release-date");
			if (date != null && !date.isEmpty()) {
				LocalDateTime dt = LocalDateTime.parse(date, DATE_FORMATTER);
				release.setDate(dt);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			logger.error(e.getMessage());
		}
		if (getValueFromChildElement(node, "release-country") != null
				&& getValueFromChildElement(node, "release-country").length() > 0) {
			release.setCountry(getValueFromChildElement(node, "release-country").intern());
		}
		if (getValueFromChildElement(node, "duration") != null
				&& getValueFromChildElement(node, "duration").length() > 0) {
			try {
				release.setDuration(Integer.parseInt(getValueFromChildElement(node, "duration")));
			} catch (Exception e) {
				logger.info("Could not parse " + getValueFromChildElement(node, "duration")
						+ " to integer from release " + id);
			}
		}
		if (getValueFromChildElement(node, "label") != null && getValueFromChildElement(node, "label").length() > 0) {
			release.setLabel(getValueFromChildElement(node, "label").intern());
		}
		if (getValueFromChildElement(node, "genre") != null && getValueFromChildElement(node, "genre").length() > 0) {
			release.setGenre(getValueFromChildElement(node, "genre").intern());
		}
		// TODO Provenance Info
		release.setTracks( getObjectListFromChildElement(node, "tracks", "track", new TrackXMLReader(), "") );

		return release;
	}


	public Release createInstanceForFusion(RecordGroup<Release, Attribute> cluster) {

		List<String> ids = new LinkedList<String>();

		for (Release m : cluster.getRecords()) {
			ids.add(m.getIdentifier());
		}

		Collections.sort(ids);

		String mergedId = StringUtils.join(ids, '+');

		return new Release(mergedId, "fused");
	}

}
