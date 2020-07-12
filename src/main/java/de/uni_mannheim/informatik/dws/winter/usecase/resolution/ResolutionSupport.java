package de.uni_mannheim.informatik.dws.winter.usecase.resolution;

import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.*;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;
import de.uni_mannheim.informatik.dws.winter.usecase.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import de.uni_mannheim.informatik.dws.winter.util.identityresolution.CompanyBlockingKeyByFirstCharGenerator;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.function.Function;

@AllArgsConstructor(staticName = "apply")
public final class ResolutionSupport {

   private final Logger logger;

   public static ResolutionSupport apply(String logLevel) {
      var logger = WinterLogManager.activateLogger(logLevel);
      return apply(logger);
   }

   public static ResolutionSupport apply() {
      return apply("trace");
   }

   public DataSet<Company, Attribute> getDataset(Path path) {
      return Operators.suppressExceptions(() -> {
         logger.info("Loading " + path + " ...");
         DataSet<Company, Attribute> dataset = new HashedDataSet<>();
         CompanyXMLReader xmlReader = new CompanyXMLReader();
         xmlReader.loadFromXML(path.toFile(), "/companies/company", dataset);
         return dataset;
      });
   }


   public MatchingGoldStandard getGoldStandard(Path goldStandardPath) {
      return Operators.suppressExceptions(() -> {
         MatchingGoldStandard gsTest = new MatchingGoldStandard();
         gsTest.loadFromCSVFile(goldStandardPath.toFile());

         return gsTest;
      });
   }

   public Blocker<Company, Attribute, Company, Attribute> getNoBlocker() {
      return new NoBlocker<>();
   }


   public Blocker<Company, Attribute, Company, Attribute> getStandardBlocker() {
      return new StandardRecordBlocker<>(new CompanyBlockingKeyByFirstCharGenerator());
   }

   public Blocker<Company, Attribute, Company, Attribute> getSortedNeighbourhoodBlocker() {
      return new SortedNeighbourhoodBlocker<>(new CompanyBlockingKeyByFirstCharGenerator(), 30);
   }

   public Logger getLogger() {
      return logger;
   }

   public Function<Processable<Correspondence<Company, Attribute>>, Processable<Correspondence<Company, Attribute>>> getTopKTransformer(int k, double threshold) {
      return correspondences -> {
         var engine = new MatchingEngine<Company, Attribute>();
         return engine.getTopKInstanceCorrespondences(correspondences, k, threshold);
      };
   }

   public Function<Processable<Correspondence<Company, Attribute>>, Processable<Correspondence<Company, Attribute>>> getTopKTransformer() {
      return getTopKTransformer(1, 0.0);
   }

   public Function<Processable<Correspondence<Company, Attribute>>, Processable<Correspondence<Company, Attribute>>> getMaximumBipartiteMatchingAlgorithm() {
      return c -> {
         var maxWeight = new MaximumBipartiteMatchingAlgorithm<Company,Attribute>(c);
         maxWeight.run();
         return maxWeight.getResult();
      };
   }

   public void run(
      String name1, String name2,
      DataSet<Company, Attribute> ds1, DataSet<Company, Attribute> ds2,
      MatchingRule<Company, Attribute> matchingRule, Blocker<Company, Attribute, Company, Attribute> blocker,
      Path correspondencesPath, MatchingGoldStandard goldStandardTest,
      Function<Processable<Correspondence<Company, Attribute>>, Processable<Correspondence<Company, Attribute>>> transformers) {

      Operators.suppressExceptions(() -> {
         var engine = new MatchingEngine<Company, Attribute>();
         var correspondences = engine.runIdentityResolution(ds1, ds2, null, matchingRule, blocker);

         if (transformers != null) {
            correspondences = transformers.apply(correspondences);
         }

         new CSVCorrespondenceFormatter().writeCSV(correspondencesPath.toFile(), correspondences);

         // evaluate your result
         var evaluator = new MatchingEvaluator<Company, Attribute>();
         var perfTest = evaluator.evaluateMatching(correspondences.get(), goldStandardTest);
         logger.info(name1 + " <-> " + name2);
         logger.info(String.format(
            "Precision: %.4f\nRecall: %.4f\nF1:  %.4f",
            perfTest.getPrecision(),
            perfTest.getRecall(),
            perfTest.getF1()));
      });
   }

   public void runML(
      String name1, String name2,
      DataSet<Company, Attribute> ds1, DataSet<Company, Attribute> ds2,
      WekaMatchingRule<Company, Attribute> matchingRule, Blocker<Company, Attribute, Company, Attribute> blocker,
      Path correspondencesPath, MatchingGoldStandard goldStandardTest, MatchingGoldStandard goldStandardTrain,
      Function<Processable<Correspondence<Company, Attribute>>, Processable<Correspondence<Company, Attribute>>> transformers) {

      Operators.suppressExceptions(() -> {
         var rl = new RuleLearner<Company, Attribute>();
         var perfTrain = rl.learnMatchingRule(ds1, ds2, null, matchingRule, goldStandardTrain);

         logger.info(matchingRule.getModelDescription());
         logger.info(String.format("Performance on training set: P: %.2f / R: %.2f / F1: %.2f", perfTrain.getPrecision(), perfTrain.getRecall(), perfTrain.getF1()));

         run(name1, name2, ds1, ds2, matchingRule, blocker, correspondencesPath, goldStandardTest, transformers);
      });
   }

}
