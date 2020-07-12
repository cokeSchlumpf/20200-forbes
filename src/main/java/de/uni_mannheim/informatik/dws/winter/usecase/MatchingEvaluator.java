/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.uni_mannheim.informatik.dws.winter.usecase;

import de.uni_mannheim.informatik.dws.winter.model.*;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Group;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.processing.RecordKeyValueMapper;
import de.uni_mannheim.informatik.dws.winter.utils.StringUtils;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Evaluates a set of {@link Correspondence}s against a
 * {@link MatchingGoldStandard}.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 * @param <RecordType> the type of records between which the correspondences
 *        exist
 * @param <SchemaElementType> the type of the causal correspondences
 */
public class MatchingEvaluator<RecordType extends Matchable, SchemaElementType extends Matchable> {

	private static final Logger logger = WinterLogManager.getLogger();

	public MatchingEvaluator() {
	}

	/**
	 * Evaluates the given correspondences against the gold standard
	 * 
	 * @param correspondences the correspondences to evaluate
	 * @param goldStandard    the gold standard
	 * @return the result of the evaluation
	 */
	public Performance evaluateMatching(Processable<Correspondence<RecordType, SchemaElementType>> correspondences,
                                       MatchingGoldStandard goldStandard) {
		return evaluateMatching(correspondences.get(), goldStandard);
	}

	/**
	 * Evaluates the given correspondences against the gold standard
	 * 
	 * @param correspondences the correspondences to evaluate
	 * @param goldStandard    the gold standard
	 * @return the result of the evaluation
	 */
	public Performance evaluateMatching(Collection<Correspondence<RecordType, SchemaElementType>> correspondences,
                                       MatchingGoldStandard goldStandard) {
		int correct = 0;
		int matched = 0;
		int correct_max = goldStandard.getPositiveExamples().size();

		// keep a list of all unmatched positives for later output
		List<Pair<String, String>> positives = new ArrayList<>(goldStandard.getPositiveExamples());

		for (Correspondence<RecordType, SchemaElementType> correspondence : correspondences) {
			if (goldStandard.containsPositive(correspondence.getFirstRecord(), correspondence.getSecondRecord())) {
				correct++;
				matched++;

				logger.trace(String.format("[correct] %s,%s,%s", correspondence.getFirstRecord().getIdentifier(),
						correspondence.getSecondRecord().getIdentifier(),
						Double.toString(correspondence.getSimilarityScore())));

				// remove pair from positives
				Iterator<Pair<String, String>> it = positives.iterator();
				while (it.hasNext()) {
					Pair<String, String> p = it.next();
					String id1 = correspondence.getFirstRecord().getIdentifier();
					String id2 = correspondence.getSecondRecord().getIdentifier();

					if (p.getFirst().equals(id1) && p.getSecond().equals(id2)
							|| p.getFirst().equals(id2) && p.getSecond().equals(id1)) {
						it.remove();
					}
				}
			} else if (goldStandard.isComplete() || goldStandard.containsNegative(correspondence.getFirstRecord(),
					correspondence.getSecondRecord())) {
				matched++;

				logger.trace(String.format("[wrong] %s,%s,%s", correspondence.getFirstRecord().getIdentifier(),
						correspondence.getSecondRecord().getIdentifier(),
						Double.toString(correspondence.getSimilarityScore())));

			}
		}

		// print all missing positive examples
		for (Pair<String, String> p : positives) {
			logger.trace(String.format("[missing] %s,%s", p.getFirst(), p.getSecond()));
		}

		return new Performance(correct, matched, correct_max);
	}

	public void writeEvaluation(File f, Processable<Correspondence<RecordType, SchemaElementType>> correspondences,
			MatchingGoldStandard goldStandard) throws IOException {
		writeEvaluation(f, correspondences.get(), goldStandard);
	}

	public void writeEvaluation(File f, Collection<Correspondence<RecordType, SchemaElementType>> correspondences,
			MatchingGoldStandard goldStandard) throws IOException {
		BufferedWriter w = new BufferedWriter(new FileWriter(f));

		// keep a list of all unmatched positives for later output
		List<Pair<String, String>> positives = new ArrayList<>(goldStandard.getPositiveExamples());

		for (Correspondence<RecordType, SchemaElementType> correspondence : correspondences) {
			if (goldStandard.containsPositive(correspondence.getFirstRecord(), correspondence.getSecondRecord())) {
				w.write(String.format("%s,%s,%s,%b\n", correspondence.getFirstRecord().getIdentifier(),
						correspondence.getSecondRecord().getIdentifier(),
						Double.toString(correspondence.getSimilarityScore()), true));

				// remove pair from positives
				Iterator<Pair<String, String>> it = positives.iterator();
				while (it.hasNext()) {
					Pair<String, String> p = it.next();
					String id1 = correspondence.getFirstRecord().getIdentifier();
					String id2 = correspondence.getSecondRecord().getIdentifier();

					if (p.getFirst().equals(id1) && p.getSecond().equals(id2)
							|| p.getFirst().equals(id2) && p.getSecond().equals(id1)) {
						it.remove();
					}
				}
			} else if (goldStandard.isComplete() || goldStandard.containsNegative(correspondence.getFirstRecord(),
					correspondence.getSecondRecord())) {
				w.write(String.format("%s,%s,%s,%b\n", correspondence.getFirstRecord().getIdentifier(),
						correspondence.getSecondRecord().getIdentifier(),
						Double.toString(correspondence.getSimilarityScore()), false));

			} else {
				w.write(String.format("%s,%s,%s,unknown\n", correspondence.getFirstRecord().getIdentifier(),
					correspondence.getSecondRecord().getIdentifier(),
					Double.toString(correspondence.getSimilarityScore())));
			}
		}

		// print all missing positive examples
		for (Pair<String, String> p : positives) {
			w.write(String.format("%s,%s,0.0,missing\n", p.getFirst(), p.getSecond()));
		}

		w.close();
	}

	public void createTrainTestSplit(Processable<Correspondence<RecordType, SchemaElementType>> correspondences,
                                    MatchingGoldStandard goldStandard, double trainingPercentage, File training, File test) throws IOException {

		// group the correspondences by their LHS
		Processable<Group<RecordType, Correspondence<RecordType, SchemaElementType>>> groups = correspondences.group(
				new RecordKeyValueMapper<RecordType, Correspondence<RecordType, SchemaElementType>, Correspondence<RecordType, SchemaElementType>>() {

					@Override
					public void mapRecordToKey(Correspondence<RecordType, SchemaElementType> record,
                                          DataIterator<Pair<RecordType, Correspondence<RecordType, SchemaElementType>>> resultCollector) {
						resultCollector.next(new Pair<>(record.getFirstRecord(), record));
					}
				});

		BufferedWriter wTrain = new BufferedWriter(new FileWriter(training));
		BufferedWriter wTest = new BufferedWriter(new FileWriter(test));
		Random r = new Random();

		// create a random split of the groups
		for(Group<RecordType, Correspondence<RecordType, SchemaElementType>> g : groups.get()) {
			StringBuilder sb = new StringBuilder();
			// boolean hasCorrect = false;
			boolean isTraining = r.nextDouble()<trainingPercentage;

			Processable<Correspondence<RecordType, SchemaElementType>> cors = g.getRecords();

			if(isTraining) {
				// limit training set to 10 pairs per record (balancing)
				cors = cors.sort((c)->c.getSimilarityScore(), false).take(10);
			}

			for(Correspondence<RecordType, SchemaElementType> cor : cors.get()) {
				boolean isPositive = goldStandard.containsPositive(cor.getFirstRecord(), cor.getSecondRecord());
				boolean isNegative = goldStandard.containsNegative(cor.getFirstRecord(), cor.getSecondRecord());
				Boolean isCorrect = null;
				if(isPositive) {
					isCorrect = true;
					// hasCorrect = true;
				} else if(isNegative || goldStandard.isComplete()) {
					isCorrect = false;
				}
				if(isCorrect!=null) {
					sb.append(String.format("%s\n", StringUtils.join(new String[] {
						cor.getFirstRecord().getIdentifier(),
						cor.getSecondRecord().getIdentifier(),
						isCorrect.toString()
					}, ",")));
				}
			}

			// if(hasCorrect) {
				if(isTraining) {
					wTrain.write(sb.toString());
				} else {
					wTest.write(sb.toString());
				}
			// }
		}

		wTrain.close();
		wTest.close();
	}

}
