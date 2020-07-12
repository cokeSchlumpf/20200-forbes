package de.uni_mannheim.informatik.dws.winter.usecase.mapping;

import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.dbpedia.Companies;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.target.Company;
import de.uni_mannheim.informatik.dws.winter.util.ObjectMapperFactory;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import lombok.AllArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class DbpediaMapping implements Runnable {

   private final Path target;

   @Override
   public void run() {
      Operators.suppressExceptions(() -> {
         var om = ObjectMapperFactory.apply().createXml();
         var companies = om.readValue(Paths.get("data/input/dbpedia.xml").toFile(), Companies.class);

         var mapped = companies
            .getCompanies()
            .stream()
            .map(company -> {
               var id = company.getIdentifier();
               var name = company.getName();
               var website = company.getHomepage();

               LocalDate founded = null;
               if (company.getFYear() != null && company.getFYear() > 0) {
                  founded = LocalDate.of(company.getFYear(), 1, 1);
               }

               var country = company.getCountryName();
               var city = company.getCityName();
               var industry = company.getIndustryName();
               var assets = company.getAssets();
               var revenue = company.getRevenue();
               var keyPeople = company.getFounders();

               return Company.apply(id, name, website, founded, country, city, industry, assets, revenue, keyPeople);
            })
            .collect(Collectors.toList());

         if (!Files.exists(target.getParent())) {
            Files.createDirectories(target.getParent());
         }

         om.writeValue(
            target.toFile(),
            de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.target.Companies.apply(mapped));
      });
   }

}
