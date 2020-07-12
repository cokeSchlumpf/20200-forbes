package de.uni_mannheim.informatik.dws.winter.usecase.mapping;

import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.Persons;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.fullcontact.Companies;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.fullcontact.Person;
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
public final class FullContactMapping implements Runnable {

   private final Path target;

   @Override
   public void run() {
      Operators.suppressExceptions(() -> {
         var om = ObjectMapperFactory.apply().createXml();
         var companies = om.readValue(Paths.get("data/input/fullcontact.xml").toFile(), Companies.class);

         var mapped = companies
            .getCompanies()
            .stream()
            .map(company -> {
               var id = company.getId();
               var name = company.getName();

               LocalDate founded = null;
               if (company.getFounded() != null && company.getFounded() > 0) {
                  founded = LocalDate.of(company.getFounded(), 1, 1);
               } else if (company.getOnlinesince() != null && company.getOnlinesince() > 0) {
                  founded = LocalDate.of(company.getOnlinesince(), 1, 1);
               }

               var country = company.getCountry();
               var city = company.getLocality();
               var keyPeople = Persons.apply(
                  company
                     .getKeypersons()
                     .stream()
                     .filter(p -> p.getTitle().toLowerCase().contains("found"))
                     .map(Person::getName)
                     .collect(Collectors.toList()));

               return Company.apply(id, name, null, founded, country, city, null, null, null, keyPeople);
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
