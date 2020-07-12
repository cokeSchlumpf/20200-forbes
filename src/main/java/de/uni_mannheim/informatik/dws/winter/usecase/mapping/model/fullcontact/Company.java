package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.fullcontact;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.collect.Lists;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.databind.YearDeserializer;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Company {

   private Company() {
      this(null, null, null, null, null, null, null, Lists.newArrayList());
   }

   String id;

   String name;

   @JsonDeserialize(using = YearDeserializer.class, as = Integer.class)
   Integer onlinesince;

   Integer founded;

   String country;

   String locality;

   String address;

   @JacksonXmlElementWrapper(localName = "keypersons")
   @JacksonXmlProperty(localName = "person")
   List<Person> keypersons;

}
