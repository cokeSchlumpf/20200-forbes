package de.uni_mannheim.informatik.dws.winter.usecase.resolution;

import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import de.uni_mannheim.informatik.dws.winter.util.identityresolution.CompanyNameComparatorJaccard;
import lombok.AllArgsConstructor;

import java.nio.file.Path;

@AllArgsConstructor(staticName = "apply")
public final class FullContactResolution implements Runnable {

   private final ResolutionSupport resolutionSupport;

   private final Path forbesDataPath;

   private final Path fullContactDataPath;

   private final Path goldStandardTestPath;

   private final Path correspondencesPath;

   private final Path matchingRuleLogsPath;

   public void run() {
      Operators.suppressExceptions(() -> {
			var forbes = resolutionSupport.getDataset(forbesDataPath);
			var fullContact = resolutionSupport.getDataset(fullContactDataPath);
			var goldStandard = resolutionSupport.getGoldStandard(goldStandardTestPath);
         var blocker = resolutionSupport.getNoBlocker();
         var transformer = resolutionSupport.getTopKTransformer();

         // Linear combination
         LinearCombinationMatchingRule<Company, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.5);

         if (matchingRuleLogsPath != null) {
            matchingRule.activateDebugReport(matchingRuleLogsPath.toFile().toString(), 100000, goldStandard);
         }

         // matchingRule.addComparator(new CompanyNameComparatorEqual(), 1.0);
         matchingRule.addComparator(new CompanyNameComparatorJaccard(), 1.0);
         // matchingRule.addComparator(new CompanyNameComparatorLevenshtein(), 1.0);
         // matchingRule.addComparator(new CompanyNameComparatorLongestTokenEqual(), 1.0);

         // matchingRule.addComparator(new CompanyCountryComparatorEqual(), 1.0);
         // matchingRule.addComparator(new CompanyCountryComparatorJaccard(), 1.0);

         // matchingRule.addComparator(new CompanyFoundedComparator2Years(), 1.0);
         // matchingRule.addComparator(new CompanyFoundedComparator10Years(), 1.0);

         // matchingRule.addComparator(new CompanyCityComparatorEqual(), 1.0);
         // matchingRule.addComparator(new CompanyCityComparatorJaccard(), 1.0);
         // matchingRule.addComparator(new CompanyCityComparatorLevenshtein(), 1.0);

         // matchingRule.addComparator(new CompanyIndustryComparatorEqual(), 1.0);
         // matchingRule.addComparator(new CompanyIndustryComparatorJaccard(), 1.0);
         // matchingRule.addComparator(new CompanyIndustryComparatorLevenshtein(), 1.0);

         matchingRule.normalizeWeights();

         resolutionSupport.run(
            "forbes", "fullcontact",
            forbes, fullContact, matchingRule, blocker, correspondencesPath, goldStandard, transformer);
      });
   }
}
