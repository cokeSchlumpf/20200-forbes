package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public final class NumberDeserializer extends JsonDeserializer<Double> {

   @Override
   public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      var s = p.readValueAs(String.class).replace(",", "");

      if (s.trim().length() > 0) {
         return Double.parseDouble(s);
      } else {
         return null;
      }
   }

}
