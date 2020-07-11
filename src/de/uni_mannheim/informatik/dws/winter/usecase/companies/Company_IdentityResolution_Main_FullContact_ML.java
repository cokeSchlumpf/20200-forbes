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

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyBlockingKeyByFirstCharGenerator;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyCityComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyCityComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyCityComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyCountryComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyCountryComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyFoundedComparator10Years;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyFoundedComparator2Years;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyIndustryComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyIndustryComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyIndustryComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyNameComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.identityresolution.CompanyNameComparatorLongestTokenEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.Company;
import de.uni_mannheim.informatik.dws.winter.usecase.companies.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * @author Robert
 *
 */
public class Company_IdentityResolution_Main_FullContact_ML {
	
	/*
	 * Trace Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 * 		
	 */
	private static final Logger logger = WinterLogManager.activateLogger("default");

	public static void main(String[] args) throws Exception {
		// Load Forbes
		logger.info("Loading Forbes");
		DataSet<Company, Attribute> forbes = new HashedDataSet<>();
		CompanyXMLReader xmlReader = new CompanyXMLReader();
		xmlReader.loadFromXML(new File("usecase/company/mapforce/forbes_target_schema.xml"), "/companies/company", forbes);

		// Load fullcontact
		DataSet<Company, Attribute> fullcontact = new HashedDataSet<>();
		xmlReader.loadFromXML(new File("usecase/company/mapforce/fullcontact_target_schema.xml"),
				"/companies/company", fullcontact);

		// load the gold standard (train set)
		MatchingGoldStandard gsTrain = new MatchingGoldStandard();
		gsTrain.loadFromCSVFile(new File("usecase/company/goldstandard/forbes_fullcontact_train.csv"));
		
		// load the gold standard (test set)
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("usecase/company/goldstandard/forbes_fullcontact_test.csv"));

		// Matching Rule
		// Weka Machine Learning
		//TODO: change this value to use a different classification algorithm
		String classifier = "SimpleLogistic";
		//TODO: use this array to define the hyper-parameters of the classification algorithm
		String[] hyperparameters = new String[] {};
		WekaMatchingRule<Company, Attribute> matchingRule = new WekaMatchingRule<>(0.5, classifier, hyperparameters);
		// matchingRule.setForwardSelection(true);

		// activate logging
		// matchingRule.activateDebugReport("usecase/company/output/forbes_2_fullcontact_log.csv", -1, gsTest);

		// Comparators for machine learning rule
		//TODO: un-comment any of the following lines to add comparators to the matching rule
		// matchingRule.addComparator(new CompanyCityComparatorEqual());
		// matchingRule.addComparator(new CompanyCityComparatorJaccard());
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

		// train machine learning model
		RuleLearner<Company, Attribute> rl = new RuleLearner<>();
		Performance perfTrain = rl.learnMatchingRule(forbes, fullcontact, null, matchingRule, gsTrain);
		logger.info(matchingRule.getModelDescription());
		logger.info(String.format("Performance on training set: P: %.2f / R: %.2f / F1: %.2f", perfTrain.getPrecision(), perfTrain.getRecall(), perfTrain.getF1()));

		// TODO: (un-)comment the following lines to use different blockers
		// NoBlocker<Company, Attribute> blocker = new NoBlocker<>();
		StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<>(new CompanyBlockingKeyByFirstCharGenerator());
		// SortedNeighbourhoodBlocker<Company, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new CompanyBlockingKeyByFirstCharGenerator(), 30);

		// Initialize Matching Engine
		MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(forbes, fullcontact, null, matchingRule, blocker);

		//TODO: un-comment this part to use top-1 global matching
		// run top-1 global matching
		// correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);

		//TODO: un-comment this part to use a maximum-weight, bipartite matching
		// Alternative: Create a maximum-weight, bipartite matching
		// MaximumBipartiteMatchingAlgorithm<Company,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		// maxWeight.run();
		// correspondences = maxWeight.getResult();

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("usecase/company/output/forbes_2_fullcontact_correspondences.csv"), correspondences);

		// evaluate your result
		MatchingEvaluator<Company, Attribute> evaluator = new MatchingEvaluator<>();

		// // evaluate your result
		Performance perfTest = evaluator.evaluateMatching(correspondences.get(), gsTest);

		// print the evaluation result
		logger.info("Forbes <-> fullcontact");
		logger.info(String.format("Precision: %.4f\nRecall: %.4f\nF1:  %.4f", perfTest.getPrecision(),
				perfTest.getRecall(), perfTest.getF1()));

	}

}
