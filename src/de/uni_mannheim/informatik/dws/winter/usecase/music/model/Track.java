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

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * An object represents a track
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class Track extends AbstractRecord<Attribute> {

	/*
	 * example entry <actor> <name>Janet Gaynor</name>
	 * <birthday>1906-01-01</birthday> <birthplace>Pennsylvania</birthplace>
	 * </actor>
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer duration;
	private Integer position;
	
	public static final Attribute NAME = new Attribute("name");
	public static final Attribute DURATION = new Attribute("duration");
	public static final Attribute POSITION = new Attribute("position");
	
	public Track(String id, String provenancInfo) {
		super(id, provenancInfo);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

	// /*
	//  * (non-Javadoc)
	//  * 
	//  * @see java.lang.Object#hashCode()
	//  */
	// @Override
	// public int hashCode() {
	// 	int result = 31 + ((name == null) ? 0 : name.hashCode());
	// 	return result;
	// }

	// /*
	//  * (non-Javadoc)
	//  * 
	//  * @see java.lang.Object#equals(java.lang.Object)
	//  */
	// @Override
	// public boolean equals(Object obj) {
	// 	if (this == obj)
	// 		return true;
	// 	if (obj == null)
	// 		return false;
	// 	if (getClass() != obj.getClass())
	// 		return false;
	// 	Track other = (Track) obj;
	// 	if (name == null) {
	// 		if (other.name != null)
	// 			return false;
	// 	} else if (!name.equals(other.name))
	// 		return false;
	// 	return true;
	// }

	@Override
	public boolean hasValue(Attribute attribute) {
		if (attribute == NAME) {
			return getName() != null && !getName().isEmpty();
		} else if (attribute == DURATION) {
			return getDuration() != null && getDuration() > 0;
		} else if (attribute == POSITION) {
			return getPosition() != null;
		}
		return false;
	}

	public Object[] getValues() {
		
		Object[] values = new Object[] {getName(), getDuration(), getPosition()};
		return values;
	}

	public void setValues(Object[] values) {
		// TODO Auto-generated method stub
		
	}

}
