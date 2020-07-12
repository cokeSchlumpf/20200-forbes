package de.uni_mannheim.informatik.dws.winter.usecase.mapping;

import com.google.common.collect.Lists;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.Persons;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.target.Company;
import de.uni_mannheim.informatik.dws.winter.util.ObjectMapperFactory;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import lombok.AllArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@AllArgsConstructor(staticName = "apply")
public final class ForbesMapping implements Runnable {

   private final Path target;

   @Override
   public void run() {
      Operators.suppressExceptions(() -> {
         var om = ObjectMapperFactory.apply().createCsv();
         var schema = om
            .schemaFor(de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.forbes.Company.class)
            .withHeader()
            .withColumnSeparator('\t')
            .withLineSeparator("\r\n");

         var mapped = om
            .readerFor(de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.forbes.Company.class)
            .with(schema)
            .readValues(Paths.get("data/input/forbes.csv").toFile())
            .readAll()
            .stream()
            .map(obj -> (de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.forbes.Company) obj)
            .map(company -> {
               var id = company.getIdentifier();
               var name = company.getCompany();
               var website = company.getIdentifier();
               var country = company.getCountry();
               var industry = company.getIndustry();
               var assets = company.getAssets() * 10_000_000;
               var revenue = company.getProfits();
               var keyPeople = Persons.apply(Lists.newArrayList());

               return Company.apply(id, name, website, null, country, null, industry, assets, revenue, keyPeople);
            })
            .collect(Collectors.toList());

         if (!Files.exists(target.getParent())) {
            Files.createDirectories(target.getParent());
         }

         ObjectMapperFactory.apply().createXml().writeValue(
            target.toFile(),
            de.uni_mannheim.informatik.dws.winter.usecase.mapping.model.target.Companies.apply(mapped));
      });
   }

}
