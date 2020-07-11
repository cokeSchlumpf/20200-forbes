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
package de.uni_mannheim.informatik.dws.winter.usecase.music;

import java.io.File;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.usecase.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseBlockingKeyByLongestTokenInNameGenerator;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseCountryComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseCountryComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseCountryComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseDateComparator10Years;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseDateComparator2Years;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseDurationComparator10Percent;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseDurationComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseNameComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseNameComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseTrackListComparatorOverlap;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseTrackListComparatorSize;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.ReleaseXMLReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Release_IdentityResolution_Main_Lastfm_ML {
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
	public static void main(String[] args) throws Exception {
		ReleaseXMLReader xmlReader = new ReleaseXMLReader();

		// Load MusicBrainz
		logger.info("Reading MusicBrainz file");
		DataSet<Release, Attribute> musicbrainz = new HashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/mapforce/musicbrainz_target_schema.xml"), "/releases/release",
				musicbrainz);

		// Load LastFM
		logger.info("Reading LastFM file");
		DataSet<Release, Attribute> lastfm = new HashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/mapforce/lastfm_target_schema.xml"), 
				"/releases/release", lastfm);

		// load the gold standard (training set)
		MatchingGoldStandard gsTrain = new MatchingGoldStandard();
		gsTrain.loadFromCSVFile(new File("usecase/music/goldstandard/musicbrainz_lastfm_train.csv"));

		// load the gold standard (test set)
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("usecase/music/goldstandard/musicbrainz_lastfm_test.csv"));

		// Matching Rule
		// Weka Machine Learning
		//TODO: change this value to use a different classification algorithm
		String classifier = "SimpleLogistic";
		//TODO: use this array to define the hyper-parameters of the classification algorithm
		String[] hyperparameters = new String[] {};
		WekaMatchingRule<Release, Attribute> matchingRule = new WekaMatchingRule<>(0.5, classifier, hyperparameters);
		// matchingRule.setForwardSelection(true);

		// activate logging
		// matchingRule.activateDebugReport("usecase/music/output/musicbrainz_2_lastfm_log.csv", 100000, gsTest);

		// Comparators for machine learning rule
		//TODO: un-comment any of the following lines to add comparators to the matching rule
		// matchingRule.addComparator(new ReleaseArtistComparatorEqual());
		matchingRule.addComparator(new ReleaseArtistComparatorJaccard());
		// matchingRule.addComparator(new ReleaseArtistComparatorLevenshtein());

		// matchingRule.addComparator(new ReleaseCountryComparatorEqual());
		// matchingRule.addComparator(new ReleaseCountryComparatorJaccard());
		// matchingRule.addComparator(new ReleaseCountryComparatorLevenshtein());

		matchingRule.addComparator(new ReleaseDateComparator2Years());
		matchingRule.addComparator(new ReleaseDateComparator10Years());

		// matchingRule.addComparator(new ReleaseDurationComparator10Percent());
		// matchingRule.addComparator(new ReleaseDurationComparatorEqual());

		matchingRule.addComparator(new ReleaseNameComparatorEqual());
		matchingRule.addComparator(new ReleaseNameComparatorJaccard());
		// matchingRule.addComparator(new ReleaseNameComparatorLevenshtein());

		matchingRule.addComparator(new ReleaseTrackListComparatorOverlap());
		// matchingRule.addComparator(new ReleaseTrackListComparatorSize());

		// train machine learning model
		RuleLearner<Release, Attribute> rl = new RuleLearner<>();
		Performance perfTrain = rl.learnMatchingRule(musicbrainz, lastfm, null, matchingRule, gsTrain);
		logger.info(matchingRule.getModelDescription());
		logger.info(String.format("Performance on training set: P: %.2f / R: %.2f / F1: %.2f", perfTrain.getPrecision(), perfTrain.getRecall(), perfTrain.getF1()));

		// Blocker
		// NoBlocker<Release, Attribute> blocker = new NoBlocker<>();
		StandardRecordBlocker<Release, Attribute> blocker = new StandardRecordBlocker<>(new ReleaseBlockingKeyByLongestTokenInNameGenerator());
		// SortedNeighbourhoodBlocker<Release, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new ReleaseBlockingKeyByLongestTokenInNameGenerator(), 30);

		// Initialize Matching Engine
		MatchingEngine<Release, Attribute> engine = new MatchingEngine<Release, Attribute>();

		// Execute the matching
		Processable<Correspondence<Release, Attribute>> correspondences = engine.runIdentityResolution(musicbrainz, lastfm, null, matchingRule, blocker);

		//TODO: un-comment this part to use top-1 global matching
		// run top-1 global matching
		// correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);
		// logger.info(correspondences.size()+" correspondences after global matching");

		//TODO: un-comment this part to use a maximum-weight, bipartite matching
		// Alternative: Create a maximum-weight, bipartite matching
		// MaximumBipartiteMatchingAlgorithm<Release,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		// maxWeight.run();
		// correspondences = maxWeight.getResult();
		// logger.info(correspondences.size()+" correspondences after global matching");

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("usecase/music/output/musicbrainz_2_lastfm_correspondences.csv"), correspondences);
		
		// evaluate your result
		MatchingEvaluator<Release, Attribute> evaluator = new MatchingEvaluator<Release, Attribute>();

		Performance perfTest = evaluator.evaluateMatching(correspondences.get(), gsTest);

		// print the evaluation result
		logger.info("MB <-> LastFM");
		logger.info(String.format("Precision: %.4f Recall: %.4f F1: %.4f", perfTest.getPrecision(),
				perfTest.getRecall(), perfTest.getF1()));
	}

}
