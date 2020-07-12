package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.dbpedia;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.Persons;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.databind.NumberDeserializer;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.databind.YearDeserializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "apply")
public class Company {

   String identifier;

   @JacksonXmlProperty(localName = "name", namespace = "bar")
   String name;

   String countryName;

   @JacksonXmlProperty(localName = "fyear")
   @JsonDeserialize(using = YearDeserializer.class, as = Integer.class)
   Integer fYear;

   String cityName;

   @JsonDeserialize(using = NumberDeserializer.class, as = Double.class)
   Double revenue;

   @JsonDeserialize(using = NumberDeserializer.class, as = Double.class)
   Double assets;

   String industryName;

   String homepage;

   Persons founders;

}
