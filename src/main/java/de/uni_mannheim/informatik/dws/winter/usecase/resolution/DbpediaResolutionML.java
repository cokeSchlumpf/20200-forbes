package de.uni_mannheim.informatik.dws.winter.usecase.resolution;

import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import de.uni_mannheim.informatik.dws.winter.util.identityresolution.CompanyCityComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.util.identityresolution.CompanyNameComparatorJaccard;
import lombok.AllArgsConstructor;

import java.nio.file.Path;

@AllArgsConstructor(staticName = "apply")
public final class DbpediaResolutionML implements Runnable {

   private final ResolutionSupport resolutionSupport;

   private final Path forbesDataPath;

   private final Path dbpediaContactDataPath;

   private final Path goldStandardTestPath;

   private final Path goldStandardTrainPath;

   private final Path correspondencesPath;

   private final Path matchingRuleLogsPath;

   public void run() {
      Operators.suppressExceptions(() -> {
         var forbes = resolutionSupport.getDataset(forbesDataPath);
         var dbpedia = resolutionSupport.getDataset(dbpediaContactDataPath);
         var goldStandardTest = resolutionSupport.getGoldStandard(goldStandardTestPath);
         var goldStandardTrain = resolutionSupport.getGoldStandard(goldStandardTrainPath);
         var blocker = resolutionSupport.getNoBlocker();
         var transformer = resolutionSupport.getTopKTransformer();

         // Linear combination
         String classifier = "SimpleLogistic";
         String[] hyperparameters = new String[] {};
         WekaMatchingRule<Company, Attribute> matchingRule = new WekaMatchingRule<>(0.5, classifier, hyperparameters);
         // matchingRule.setForwardSelection(true);

         if (matchingRuleLogsPath != null) {
            matchingRule.activateDebugReport(matchingRuleLogsPath.toFile().toString(), 100000, goldStandardTest);
         }

         // matchingRule.addComparator(new CompanyCityComparatorEqual());
         matchingRule.addComparator(new CompanyCityComparatorJaccard());
         // matchingRule.addComparator(new CompanyCityComparatorLevenshtein());

         // matchingRule.addComparator(new CompanyCountryComparatorEqual());
         // matchingRule.addComparator(new CompanyCountryComparatorJaccard());

         // matchingRule.addComparator(new CompanyFoundedComparator10Years());
         // matchingRule.addComparator(new CompanyFoundedComparator2Years());

         // matchingRule.addComparator(new CompanyIndustryComparatorEqual());
         // matchingRule.addComparator(new CompanyIndustryComparatorJaccard());
         // matchingRule.addComparator(new CompanyIndustryComparatorLevenshtein());

         // matchingRule.addComparator(new CompanyNameComparatorEqual());
         // matchingRule.addComparator(new CompanyNameComparatorJaccard());
         // matchingRule.addComparator(new CompanyNameComparatorLevenshtein());
         // matchingRule.addComparator(new CompanyNameComparatorLongestTokenEqual());

         resolutionSupport.runML(
            "dbpedia", "fullcontact",
            forbes, dbpedia, matchingRule, blocker, correspondencesPath, goldStandardTest, goldStandardTrain, transformer);
      });
   }
}
