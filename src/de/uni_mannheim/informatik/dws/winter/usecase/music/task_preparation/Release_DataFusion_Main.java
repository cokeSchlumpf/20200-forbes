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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

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
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.NameFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseArtistEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseCountryEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseDateEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseDurationEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseLabelEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseNameEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.evaluation.ReleaseTracksEvaluationRule;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.ArtistFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.ArtistFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.ArtistFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.ArtistFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.CountryFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.CountryFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DateFuserFavourSource;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DateFuserMostRecent;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DateFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DurationFuserAverage;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DurationFuserMax;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.DurationFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.LabelFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.LabelFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.NameFuserLongestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.NameFuserShortestString;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.NameFuserVoting;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.TrackFuserIntersection;
import de.uni_mannheim.informatik.dws.winter.usecase.music.datafusion.fusers.TrackFuserUnion;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.Release;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.ReleaseXMLFormatter;
import de.uni_mannheim.informatik.dws.winter.usecase.music.model.ReleaseXMLReader;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

/**
 * @author Robert Meusel (robert@dwslab.de)
 *
 */
public class Release_DataFusion_Main {
	
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
		
		// Load discogs dataset
		logger.info("Reading DISCOGS file");
		FusibleDataSet<Release, Attribute> discogs = new FusibleHashedDataSet<Release, Attribute>();
		ReleaseXMLReader xmlReader = new ReleaseXMLReader();
		xmlReader.loadFromXML(new File("usecase/music/input/discogs_target_schema.xml"),
				"/releases/release", discogs);
		discogs.printDataSetDensityReport();

		// load musicbrainz dataset
		logger.info("Reading MusicBrainz file");
		FusibleDataSet<Release, Attribute> musicbrainz = new FusibleHashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/input/musicbrainz_target_schema.xml"),
				"/releases/release", musicbrainz);
		musicbrainz.printDataSetDensityReport();

		// load last.fm dataset
		logger.info("Reading LastFM file");
		FusibleDataSet<Release, Attribute> lastfm = new FusibleHashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/input/lastfm_target_schema.xml"),
				"/releases/release", lastfm);
		lastfm.printDataSetDensityReport();

		// assign dataset trust scores
		discogs.setScore(2.0);
		musicbrainz.setScore(1.0);
		lastfm.setScore(2.0);

		// specify dataset timestamps (e.g. last update)
		DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd")
		        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		        .toFormatter(Locale.ENGLISH);
		discogs.setDate(LocalDateTime.parse("2016-03-31",dateFormatter));
		musicbrainz.setDate(LocalDateTime.parse("2016-06-03", dateFormatter));
		lastfm.setDate(LocalDateTime.parse("2016-05-02", dateFormatter));

		// load correspondences
		CorrespondenceSet<Release, Attribute> correspondences = new CorrespondenceSet<Release, Attribute>();

		// NOTE: we're loading the gold standard here instead of correspondences created by a matcher, so we get the correct record groups
		// - these will then be used to generate the fusion gold standard!
		correspondences.loadCorrespondences(new File("usecase/music/goldstandard/musicbrainz_discogs.csv"),
				musicbrainz, discogs);
		correspondences.loadCorrespondences(new File("usecase/music/goldstandard/musicbrainz_lastfm.csv"), musicbrainz,
				lastfm);
		
		correspondences.printGroupSizeDistribution();

		// load the gold standard
		DataSet<Release, Attribute> gs = new FusibleHashedDataSet<Release, Attribute>();
		xmlReader.loadFromXML(new File("usecase/music/goldstandard/gs_fusion.xml"), "/releases/release", gs);

		// Define data fusion strategy
		DataFusionStrategy<Release, Attribute> strategy = new DataFusionStrategy<Release, Attribute>(new ReleaseXMLReader());

		// activate logging
		strategy.activateDebugReport("usecase/music/output/fusion_log.csv", -1, gs);

		// add strategies and evaluators

		// strategy.addAttributeFuser(Release.NAME, new NameFuserFavourSource(), new ReleaseNameEvaluationRule());
		// strategy.addAttributeFuser(Release.NAME, new NameFuserLongestString(), new ReleaseNameEvaluationRule());
		strategy.addAttributeFuser(Release.NAME, new NameFuserShortestString(), new ReleaseNameEvaluationRule());
		// strategy.addAttributeFuser(Release.NAME, new NameFuserVoting(), new ReleaseNameEvaluationRule());

		// strategy.addAttributeFuser(Release.ARTIST, new ArtistFuserFavourSource(), new ReleaseArtistEvaluationRule());
		strategy.addAttributeFuser(Release.ARTIST, new ArtistFuserLongestString(), new ReleaseArtistEvaluationRule());
		// strategy.addAttributeFuser(Release.ARTIST, new ArtistFuserShortestString(), new ReleaseArtistEvaluationRule());
		// strategy.addAttributeFuser(Release.ARTIST, new ArtistFuserVoting(), new ReleaseArtistEvaluationRule());

		// strategy.addAttributeFuser(Release.DATE, new DateFuserFavourSource(), new ReleaseDateEvaluationRule());
		// strategy.addAttributeFuser(Release.DATE, new DateFuserMostRecent(), new ReleaseDateEvaluationRule());
		strategy.addAttributeFuser(Release.DATE, new DateFuserVoting(), new ReleaseDateEvaluationRule());

		strategy.addAttributeFuser(Release.COUNTRY, new CountryFuserLongestString(), new ReleaseCountryEvaluationRule());
		// strategy.addAttributeFuser(Release.COUNTRY, new CountryFuserShortestString(), new ReleaseCountryEvaluationRule());

		// strategy.addAttributeFuser(Release.DURATION, new DurationFuserAverage(), new ReleaseDurationEvaluationRule());
		strategy.addAttributeFuser(Release.DURATION, new DurationFuserMax(), new ReleaseDurationEvaluationRule());
		// strategy.addAttributeFuser(Release.DURATION, new DurationFuserVoting(), new ReleaseDurationEvaluationRule());

		strategy.addAttributeFuser(Release.LABEL, new LabelFuserLongestString(), new ReleaseLabelEvaluationRule());
		// strategy.addAttributeFuser(Release.LABEL, new LabelFuserShortestString(), new ReleaseLabelEvaluationRule());

		// strategy.addAttributeFuser(Release.TRACKS, new TrackFuserIntersection(), new ReleaseTracksEvaluationRule());
		strategy.addAttributeFuser(Release.TRACKS, new TrackFuserUnion(), new ReleaseTracksEvaluationRule());
		
		// create the fusion engine
		DataFusionEngine<Release, Attribute> engine = new DataFusionEngine<Release, Attribute>(strategy);

		// calculate cluster consistency
		engine.printClusterConsistencyReport(correspondences, null);

		// run the fusion
		FusibleDataSet<Release, Attribute> fusedDataSet = engine.run(correspondences, null);

		// write the result
		new ReleaseXMLFormatter().writeXML(new File("usecase/music/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Release, Attribute> evaluator = new DataFusionEvaluator<Release, Attribute>(strategy, new RecordGroupFactory<Release, Attribute>());
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("Accuracy: %.2f", accuracy));
	}

}
