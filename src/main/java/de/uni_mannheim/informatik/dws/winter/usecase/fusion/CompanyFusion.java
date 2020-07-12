package de.uni_mannheim.informatik.dws.winter.usecase.fusion;

import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.usecase.model.Company;
import de.uni_mannheim.informatik.dws.winter.usecase.model.CompanyXMLFormatter;
import de.uni_mannheim.informatik.dws.winter.usecase.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.util.Operators;
import de.uni_mannheim.informatik.dws.winter.util.datafusion.evaluation.*;
import de.uni_mannheim.informatik.dws.winter.util.datafusion.fusers.*;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.time.LocalDateTime;

@AllArgsConstructor(staticName = "apply")
public class CompanyFusion implements Runnable {

   private static final Logger logger = WinterLogManager.activateLogger("trace");

   private final Path forbesDataPath;

   private final Path fullcontactDataPath;

   private final Path dbpediaDataPath;

   private final Path fullcontactCorrespondencesPath;

   private final Path dbpediaCorrespondencesPath;

   private final Path fusionGoldStandardPath;

   private final Path fusionLogPath;

   private final Path resultPath; // "usecase/company/output/fused.xml"

   private FusibleDataSet<Company, Attribute> getDataSet(Path path) {
      return Operators.suppressExceptions(() -> {
         FusibleDataSet<Company, Attribute> ds = new FusibleHashedDataSet<>();
         CompanyXMLReader xmlReader = new CompanyXMLReader();
         xmlReader.loadFromXML(path.toFile(), "/companies/company", ds);

         System.out.println(path);
         ds.printDataSetDensityReport();

         return ds;
      });
   }

   private DataSet<Company, Attribute> getHashedDataset(Path path) {
      return Operators.suppressExceptions(() -> {
         CompanyXMLReader xmlReader = new CompanyXMLReader();
         DataSet<Company, Attribute> ds = new FusibleHashedDataSet<>();
         xmlReader.loadFromXML(path.toFile(), "/companies/company", ds);
         return ds;
      });
   }

   @Override
   public void run() {
      Operators.suppressExceptions(() -> {
         var forbes = getDataSet(forbesDataPath);
         var dbpedia = getDataSet(dbpediaDataPath);
         var fullcontact = getDataSet(fullcontactDataPath);

         // Maintain Provenance
         // TODO: adjust these scores if you use one of the FavourSource fusers
         forbes.setScore(1.0);
         dbpedia.setScore(2.0);
         fullcontact.setScore(3.0);

         // Date (e.g. last update)
         forbes.setDate(LocalDateTime.parse("2013-01-01", CompanyXMLReader.DATE_FORMATTER));
         dbpedia.setDate(LocalDateTime.parse("2015-01-01", CompanyXMLReader.DATE_FORMATTER));
         fullcontact.setDate(LocalDateTime.parse("2016-06-01", CompanyXMLReader.DATE_FORMATTER));

         // load correspondences
         var correspondences = new CorrespondenceSet<Company, Attribute>();
         correspondences.loadCorrespondences(dbpediaCorrespondencesPath.toFile(), forbes, dbpedia);
         correspondences.loadCorrespondences(fullcontactCorrespondencesPath.toFile(), forbes, fullcontact);
         correspondences.printGroupSizeDistribution();

         // fusion gold standard
         var fusionGoldStandard = getHashedDataset(fusionGoldStandardPath);
         DataFusionStrategy<Company, Attribute> strategy = new DataFusionStrategy<>(new CompanyXMLReader());
         strategy.activateDebugReport(fusionLogPath.toFile().toString(), -1, fusionGoldStandard);

         strategy.addAttributeFuser(Company.NAME, new NameFuserFavourSource(), new NameEvaluationRule());
         // strategy.addAttributeFuser(Company.NAME, new NameFuserLongestString(), new NameEvaluationRule());
         // strategy.addAttributeFuser(Company.NAME, new NameFuserShortestString(), new NameEvaluationRule());
         // strategy.addAttributeFuser(Company.NAME, new NameFuserVoting(), new NameEvaluationRule());

         // strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserFavourSource(), new AssetsEvaluationRule());
         // strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserMax(), new AssetsEvaluationRule());
         strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserAverage(), new AssetsEvaluationRule());
         // strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserMostRecent(), new AssetsEvaluationRule());

         // strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserFavourSource(), new RevenueEvaluationRule());
         // strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserMax(), new RevenueEvaluationRule());
         strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserAverage(), new RevenueEvaluationRule());
         // strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserMostRecent(), new RevenueEvaluationRule());

         // strategy.addAttributeFuser(Company.KEYPERSONS, new KeyPersonFuserIntersection(), new KeyPersonEvaluationRule());
         strategy.addAttributeFuser(Company.KEYPERSONS, new KeyPersonFuserUnion(), new KeyPersonEvaluationRule());

         strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserFavourSource(), new FoundedEvaluationRule());
         // strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserMostRecent(), new FoundedEvaluationRule());
         // strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserNewest(), new FoundedEvaluationRule());
         // strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserOldest(), new FoundedEvaluationRule());

         strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserLongestString(), new CountryEvaluationRule());
         // strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserShortestString(), new CountryEvaluationRule());
         // strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserVoting(), new CountryEvaluationRule());

         // strategy.addAttributeFuser(Company.CITY, new CityFuserLongestString(), new CityEvaluationRule());
         // strategy.addAttributeFuser(Company.CITY, new CityFuserShortestString(), new CityEvaluationRule());
         strategy.addAttributeFuser(Company.CITY, new CityFuserVoting(), new CityEvaluationRule());

         var engine = new DataFusionEngine<>(strategy);
         engine.printClusterConsistencyReport(correspondences, null);

         var fusedDataSet = engine.run(correspondences, null);
         fusedDataSet.printDataSetDensityReport();

         var formatter = new CompanyXMLFormatter();
         formatter.writeXML(resultPath.toFile(), fusedDataSet);

         // evaluate
         var evaluator = new DataFusionEvaluator<>(strategy, new RecordGroupFactory<>());
         double accuracy = evaluator.evaluate(forbes, fusionGoldStandard, null);
         logger.info("Accuracy: " + accuracy);
      });
   }

}
