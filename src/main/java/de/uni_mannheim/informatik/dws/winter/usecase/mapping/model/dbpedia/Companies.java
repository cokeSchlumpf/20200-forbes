package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.dbpedia;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@JacksonXmlRootElement(localName = "companies")
public class Companies {

   @JacksonXmlProperty(localName = "company")
   @JacksonXmlElementWrapper(useWrapping = false)
   List<Company> companies;

}
