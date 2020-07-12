package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.fullcontact;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "apply")
@JacksonXmlRootElement(localName = "Person")
public class Person {

   String name;

   String title;

}
