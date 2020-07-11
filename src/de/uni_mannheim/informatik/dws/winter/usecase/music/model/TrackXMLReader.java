package de.uni_mannheim.informatik.dws.winter.usecase.music.model;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

public class TrackXMLReader extends XMLMatchableReader<Track, Attribute> implements FusibleFactory<Release, Attribute>{

	public Release createInstanceForFusion(RecordGroup<Release, Attribute> cluster) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Track createModelFromElement(Node node, String provenanceInfo) {
		String name = null;
		String id = null;

		if (getValueFromChildElement(node, "name") != null
				&& getValueFromChildElement(node, "name").length() > 0) {
			name = getValueFromChildElement(node, "name");
			id = name.toLowerCase().replaceAll("[^a-z]","");
		}

		Track track = new Track(id, provenanceInfo);
		track.setName(name);

		if (getValueFromChildElement(node, "duration") != null
				&& getValueFromChildElement(node, "duration").length() > 0) 
			track.setDuration(Integer.parseInt(getValueFromChildElement(node, "duration")));
		if (getValueFromChildElement(node, "position") != null
				&& getValueFromChildElement(node, "position").length() > 0) 
			track.setPosition(Integer.parseInt(getValueFromChildElement(node, "position")));

		return track;
	}



}
