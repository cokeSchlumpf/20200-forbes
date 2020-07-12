package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.regex.Pattern;

public final class YearDeserializer extends JsonDeserializer<Integer> {

   private static final Pattern pattern = Pattern.compile("([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])");

   @Override
   public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      var s = p.readValueAs(String.class);
      var m = pattern.matcher(s);

      if (m.matches()) {
         return Integer.valueOf(m.group(1));
      } else {
         return 0;
      }
   }

}
