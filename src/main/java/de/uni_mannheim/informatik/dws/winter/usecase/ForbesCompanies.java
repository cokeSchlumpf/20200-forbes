package de.uni_mannheim.informatik.dws.winter.usecase;

import de.uni_mannheim.informatik.dws.winter.usecase.fusion.CompanyFusion;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.DbpediaMapping;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.ForbesMapping;
import de.uni_mannheim.informatik.dws.winter.usecase.mapping.FullContactMapping;
import de.uni_mannheim.informatik.dws.winter.usecase.resolution.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ForbesCompanies {

   public static void main(String ...args) throws IOException {
      Path dbpediaDataPath = Paths.get("data/output/dbpedia_target_schema.xml");
      Path fullContactDataPath = Paths.get("data/output/fullcontact_target_schema.xml");
      Path forbesDataPath = Paths.get("data/output/forbes_target_schema.xml");

      Path fullContactGoldStandardTestPath = Paths.get("data/goldstandard/forbes_fullcontact_test.csv");
      Path fullContactGoldStandardTrainPath = Paths.get("data/goldstandard/forbes_fullcontact_train.csv");
      Path dbpediaGoldStandardTestPath = Paths.get("data/goldstandard/forbes_dbpedia_test.csv");
      Path dbpediaGoldStandardTrainPath = Paths.get("data/goldstandard/forbes_dbpedia_train.csv");
      Path fusionGoldStandardPath = Paths.get("data/goldstandard/gs_fusion.xml");

      Path fullContactCorrespondencesPath = Paths.get("data/output/fullcontact_correspondences.csv");
      Path dbpediaCorrespondencesPath = Paths.get("data/output/dbpedia_correspondences.csv");

      Path fullContactMatchingLogsPath = Paths.get("data/output/fullcontact_matching_logs.csv");
      Path dbpediaMatchingLogsPath = Paths.get("data/output/dbpedia_matching_logs.csv");
      Path fusionLogsPath = Paths.get("data/output/fusion_logs.txt");

      Path resultPath = Paths.get("data/output/fused.xml");

      var dbpediaMapping = DbpediaMapping.apply(dbpediaDataPath);
      var fullcontactMapping = FullContactMapping.apply(fullContactDataPath);
      var forbesMapping = ForbesMapping.apply(forbesDataPath);

      var resolutionSupport = ResolutionSupport.apply();

      var fullContactResolution = FullContactResolution.apply(
         resolutionSupport, forbesDataPath, fullContactDataPath,
         fullContactGoldStandardTestPath, fullContactCorrespondencesPath, fullContactMatchingLogsPath);

      var fullContactResolutionML = FullContactResolutionML.apply(
         resolutionSupport, forbesDataPath, fullContactDataPath,
         fullContactGoldStandardTestPath, fullContactGoldStandardTrainPath,
         fullContactCorrespondencesPath, fullContactMatchingLogsPath);

      var dbpediaResolution = DbpediaResolution.apply(
         resolutionSupport, forbesDataPath, dbpediaDataPath,
         dbpediaGoldStandardTestPath, dbpediaCorrespondencesPath, dbpediaMatchingLogsPath);

      var dbpediaResolutionML = DbpediaResolutionML.apply(
         resolutionSupport, forbesDataPath, dbpediaDataPath,
         dbpediaGoldStandardTestPath, dbpediaGoldStandardTrainPath,
         dbpediaCorrespondencesPath, dbpediaMatchingLogsPath);

      var fusion = CompanyFusion.apply(
         forbesDataPath, fullContactDataPath, dbpediaDataPath,
         fullContactCorrespondencesPath, dbpediaCorrespondencesPath, fusionGoldStandardPath,
         fusionLogsPath, resultPath);

      /*
      dbpediaMapping.run();
      fullcontactMapping.run();
      forbesMapping.run();

      fullContactResolution.run();
      dbpediaResolution.run();
      */
      fusion.run();
   }

}
