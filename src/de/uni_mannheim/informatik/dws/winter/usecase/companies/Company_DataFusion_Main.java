/**
* 
* Copyright (C) 2019 Data and Web Science Group, University of Mannheim (code@dwslab.de)
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* 		http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
package de.uni_mannheim.informatik.dws.winter.usecase.companies;

import java.io.File;
import java.time.LocalDateTime;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.AssetsEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.CityEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.CountryEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.FoundedEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.KeyPersonEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.NameEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.evaluation.RevenueEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.AssetsFuserAverage;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.AssetsFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.AssetsFuserMax;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.AssetsFuserMostRecent;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CityFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CityFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CityFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CountryFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CountryFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.CountryFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.FoundedFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.FoundedFuserMostRecent;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.FoundedFuserNewest;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.FoundedFuserOldest;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.KeyPersonFuserIntersection;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.KeyPersonFuserUnion;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.NameFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.NameFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.NameFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.NameFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.RevenueFuserAverage;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.RevenueFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.RevenueFuserMax;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.datafusion.fusers.RevenueFuserMostRecent;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.Company;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.CompanyXMLFormatter;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Company_DataFusion_Main {
	
	/*
	 * Trace Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 * 		
	 */
	private static final Logger logger = WinterLogManager.activateLogger("trace");

	public static void main(String[] args) throws Exception {
		// Forbes
		logger.info("FORBES");
		FusibleDataSet<Company, Attribute> forbes = new FusibleHashedDataSet<>();
		CompanyXMLReader xmlReader = new CompanyXMLReader();
		xmlReader.loadFromXML(new File("usecase/company/mapforce/forbes_target_schema.xml"), "/companies/company", forbes);
		forbes.printDataSetDensityReport();

		// DBpedia
		logger.info("DBPEDIA");
		FusibleDataSet<Company, Attribute> dbpedia = new FusibleHashedDataSet<>();
		xmlReader.loadFromXML(new File("usecase/company/mapforce/dbpedia_target_schema.xml"),
				"/companies/company", dbpedia);
		dbpedia.printDataSetDensityReport();

		// Fullcontact
		logger.info("FULLCONTACT");
		FusibleDataSet<Company, Attribute> fullcontact = new FusibleHashedDataSet<>();
		xmlReader.loadFromXML(new File("usecase/company/mapforce/fullcontact_target_schema.xml"),
				"/companies/company", fullcontact);
		fullcontact.printDataSetDensityReport();

		// Maintain Provenance
		//TODO: adjust these scores if you use one of the FavourSource fusers
		forbes.setScore(1.0);
		dbpedia.setScore(2.0);
		fullcontact.setScore(3.0);

		// Date (e.g. last update)
		forbes.setDate(LocalDateTime.parse("2013-01-01", CompanyXMLReader.DATE_FORMATTER));
		dbpedia.setDate(LocalDateTime.parse("2015-01-01", CompanyXMLReader.DATE_FORMATTER));
		fullcontact.setDate(LocalDateTime.parse("2016-06-01", CompanyXMLReader.DATE_FORMATTER));

		// load correspondences
		CorrespondenceSet<Company, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("usecase/company/output/forbes_2_dbpedia_correspondences.csv"),
				forbes, dbpedia);
		correspondences.loadCorrespondences(
				new File("usecase/company/output/forbes_2_fullcontact_correspondences.csv"), forbes, fullcontact);
		correspondences.printGroupSizeDistribution();

		// load the gold standard
		DataSet<Company, Attribute> gs = new FusibleHashedDataSet<>();
		xmlReader.loadFromXML(new File("usecase/company/goldstandard/gs_fusion.xml"), "/companies/company", gs);

		// Define data fusion strategy
		DataFusionStrategy<Company, Attribute> strategy = new DataFusionStrategy<>(new CompanyXMLReader());

		// activate logging
		// strategy.activateDebugReport("usecase/company/output/fusion_log.csv", -1, gs);

		//TODO: un-comment one of the following lines for each attribute to define the fusion strategy
		// strategy.addAttributeFuser(Company.NAME, new NameFuserFavourSource(), new NameEvaluationRule());
		// strategy.addAttributeFuser(Company.NAME, new NameFuserLongestString(), new NameEvaluationRule());
		// strategy.addAttributeFuser(Company.NAME, new NameFuserShortestString(), new NameEvaluationRule());
		// strategy.addAttributeFuser(Company.NAME, new NameFuserVoting(), new NameEvaluationRule());

		// strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserFavourSource(), new AssetsEvaluationRule());
		// strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserMax(), new AssetsEvaluationRule());
		// strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserAverage(), new AssetsEvaluationRule());
		// strategy.addAttributeFuser(Company.ASSETS, new AssetsFuserMostRecent(), new AssetsEvaluationRule());

		// strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserFavourSource(), new RevenueEvaluationRule());
		// strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserMax(), new RevenueEvaluationRule());
		// strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserAverage(), new RevenueEvaluationRule());
		// strategy.addAttributeFuser(Company.REVENUE, new RevenueFuserMostRecent(), new RevenueEvaluationRule());

		// strategy.addAttributeFuser(Company.KEYPERSONS, new KeyPersonFuserIntersection(), new KeyPersonEvaluationRule());
		// strategy.addAttributeFuser(Company.KEYPERSONS, new KeyPersonFuserUnion(), new KeyPersonEvaluationRule());

		// strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserFavourSource(), new FoundedEvaluationRule());
		// strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserMostRecent(), new FoundedEvaluationRule());
		// strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserNewest(), new FoundedEvaluationRule());
		// strategy.addAttributeFuser(Company.FOUNDED, new FoundedFuserOldest(), new FoundedEvaluationRule());

		// strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserLongestString(), new CountryEvaluationRule());
		// strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserShortestString(), new CountryEvaluationRule());
		// strategy.addAttributeFuser(Company.COUNTRY, new CountryFuserVoting(), new CountryEvaluationRule());

		// strategy.addAttributeFuser(Company.CITY, new CityFuserLongestString(), new CityEvaluationRule());
		// strategy.addAttributeFuser(Company.CITY, new CityFuserShortestString(), new CityEvaluationRule());
		// strategy.addAttributeFuser(Company.CITY, new CityFuserVoting(), new CityEvaluationRule());

		// create the fusion engine
		DataFusionEngine<Company, Attribute> engine = new DataFusionEngine<>(strategy);

		// calculate cluster consistency
		engine.printClusterConsistencyReport(correspondences, null);
		
		// run the fusion
		FusibleDataSet<Company, Attribute> fusedDataSet = engine.run(correspondences, null);
		fusedDataSet.printDataSetDensityReport();

		// write the result
		CompanyXMLFormatter formatter = new CompanyXMLFormatter();
		formatter.writeXML(new File("usecase/company/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Company, Attribute> evaluator = new DataFusionEvaluator<>(strategy, new RecordGroupFactory<Company, Attribute>());
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);
		logger.info("Accuracy: " + accuracy);
	}

}
