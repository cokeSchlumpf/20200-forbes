package de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.numeric;

import java.util.Collection;

import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.ConflictResolutionFunction;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Fusible;
import de.uni_mannheim.informatik.dws.winter.model.FusibleValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

public class Maximum<RecordType extends Matchable & Fusible<SchemaElementType>, SchemaElementType extends Matchable> extends ConflictResolutionFunction<Double, RecordType, SchemaElementType>{

	@Override
	public FusedValue<Double, RecordType, SchemaElementType> resolveConflict(
			Collection<FusibleValue<Double, RecordType, SchemaElementType>> values) {
		FusibleValue<Double, RecordType, SchemaElementType> maximum = null;
		for(FusibleValue<Double, RecordType, SchemaElementType> value : values) {
			if(maximum == null || value.getValue()>maximum.getValue()) {
				maximum = value;
			}
		}
		return new FusedValue<>(maximum);
	}

}
