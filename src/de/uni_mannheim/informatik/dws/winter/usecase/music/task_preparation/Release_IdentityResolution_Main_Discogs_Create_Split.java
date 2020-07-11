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
package de.uni_mannheim.informatik.dws.winter.usecase.music.task_preparation;

import java.io.File;

import org.apache.logging.log4j.Logger;

import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.usecase.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.BlockingKeyIndexer;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.BlockingKeyIndexer.VectorCreationMethod;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.similarity.vectorspace.*;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.MatchableValue;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.similarity.vectorspace.VectorSpaceJaccardSimilarity;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorEqual;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorJaccard;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseArtistComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseBlockingKeyByLongestTokenInNameGenerator;
import de.uni_mannheim.informatik.dws.winter.usecase.music.identityresolution.ReleaseBlockingKeyTokeniserGenerator;
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
public class Release_IdentityResolution_Main_Discogs_Create_Split {
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
	 * The splits are created based on two assumptions: Perfect mapping and deduplicated datasets.
	 * In the 2020 version of the template some pairs in the GS test were manually corrected.
	 * More info in the 2020 WDI-Albstadt guide.
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
	public static void main(String[] args) throws Exception {

		// Load Discogs
		logger.info("Reading DISCOGS file");
		DataSet<Release, Attribute> discogs = new HashedDataSet<Release, Attribute>();
		ReleaseXMLReader xmlReader = new ReleaseXMLReader();
		xmlReader.loadFromXML(new File("usecase/music/input/discogs_target_schema.xml"), "/releases/release",
				discogs);

		// Load MusicBrainz
		logger.info("Reading MusicBrainz file");
		DataSet<Release, Attribute> musicbrainz = new HashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/input/musicbrainz_target_schema.xml"), "/releases/release",
				musicbrainz);

		// load the gold standard (complete)
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.setComplete(true);
		gsTest.loadFromCSVFile(new File("usecase/music/goldstandard/musicbrainz_discogs.csv"));

		// Matching Rule

		// Linear Combination
		LinearCombinationMatchingRule<Release, Attribute> matchingRule = new LinearCombinationMatchingRule<Release, Attribute>(0.0);

		// activate logging
		// matchingRule.activateDebugReport("usecase/music/output/musicbrainz_2_discogs_log.csv", -1, gsTest);

		// comparators for linear combination
		matchingRule.addComparator(new ReleaseNameComparatorJaccard(), 1.0);
		matchingRule.addComparator(new ReleaseArtistComparatorJaccard(), 1.0);
		matchingRule.addComparator(new ReleaseDateComparator2Years(), 1.0);
		matchingRule.addComparator(new ReleaseCountryComparatorJaccard(), 1.0);
		matchingRule.addComparator(new ReleaseDurationComparator10Percent(), 1.0);
		matchingRule.addComparator(new ReleaseTrackListComparatorOverlap(), 1.0);
		matchingRule.normalizeWeights();

		// Blocker
		StandardRecordBlocker<Release, Attribute> blocker = new StandardRecordBlocker<Release, Attribute>(new ReleaseBlockingKeyByLongestTokenInNameGenerator());

		// Initialize Matching Engine
		MatchingEngine<Release, Attribute> engine = new MatchingEngine<Release, Attribute>();

		// Execute the matching
		Processable<Correspondence<Release, Attribute>> correspondences = engine.runIdentityResolution(musicbrainz, discogs, null, matchingRule, blocker);

		// keep the 10 most similar candidates to generate negative examples
		correspondences = engine.getTopKInstanceCorrespondences(correspondences, 10, 0.0);

		// write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("usecase/music/output/musicbrainz_2_discogs_correspondences.csv"), correspondences);

		// evaluate your result
		MatchingEvaluator<Release, Attribute> evaluator = new MatchingEvaluator<Release, Attribute>();

		Performance perfTest = evaluator.evaluateMatching(correspondences.get(), gsTest);
		// evaluator.writeEvaluation(new File("usecase/music/output/musicbrainz_2_discogs_evaluated_correspondences.csv"), correspondences, gsTest);
		evaluator.createTrainTestSplit(correspondences, gsTest, 0.7, new File("usecase/music/goldstandard/musicbrainz_discogs_train.csv"), new File("usecase/music/goldstandard/musicbrainz_discogs_test.csv"));

		// print the evaluation result
		logger.info("MB <-> Discogs");
		logger.info(String.format("Precision: %.4f Recall: %.4f F1: %.4f", perfTest.getPrecision(),
				perfTest.getRecall(), perfTest.getF1()));
	}

}
