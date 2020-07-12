package de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.forbes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor(staticName = "apply")
@JsonPropertyOrder({ "company", "sector", "industry", "continent", "country", "marketValue", "sales", "profits", "assets", "rank", "identifier" })
public class Company {

   @JsonProperty("Company")
   String company;

   @JsonProperty("Sector")
   String sector;

   @JsonProperty("Industry")
   String industry;

   @JsonProperty("Continent")
   String continent;

   @JsonProperty("Country")
   String country;

   @JsonProperty("Market Value")
   double marketValue;

   @JsonProperty("Sales")
   double sales;

   @JsonProperty("Profits")
   double profits;

   @JsonProperty("Assets")
   double assets;

   @JsonProperty("Rank")
   int rank;

   @JsonProperty("Identifier")
   String identifier;

}
