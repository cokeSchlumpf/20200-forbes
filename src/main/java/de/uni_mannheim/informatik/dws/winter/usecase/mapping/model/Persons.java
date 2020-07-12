package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class Persons {

   private Persons() {
      this(Lists.newArrayList());
   }

   @JacksonXmlElementWrapper(useWrapping = false)
   List<String> name;

}
