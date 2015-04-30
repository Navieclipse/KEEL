package keel.Algorithms.DiscretizeFramework.InitSet.Impl;

import java.util.Vector;

import keel.Algorithms.DiscretizeFramework.InitSet.InitSet;
import keel.Algorithms.DiscretizeFramework.Utils.Common;
import keel.Algorithms.Genetic_Rule_Learning.BioHEL.Parameters;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Rand;

public class InitSetImpl_CADD implements InitSet{
	public void init(int attribute,int[]values,int begin,int end){
		int numInt;
		numInt = (end - begin + 1) / (3 * Parameters.numClasses);
		if (Common.numIntervals > 0) {
			if (numInt < Common.numIntervals && Common.numIntervals <= (end - begin + 1)) {
				numInt = Common.numIntervals;
			}
		}
		double quota = (end - begin + 1) / (double) numInt;
		double dBound = 0.0;
		int i, j;
		int oldBound = 0;
		boolean saCabo = false;
		
		/*First step: Uniform Frequency discretizer with fixed num. intervals*/

		for (i=0; i<numInt - 1 && !saCabo; i++) {
			dBound += quota;
			int iBound = (int) Math.round(dBound);
			if (iBound <= oldBound)
				continue;
			if (Common.realValues[attribute][values[iBound-1]] != Common.realValues[attribute][values[iBound]]) {
				double cutPoint=Common.realValues[attribute][values[iBound-1]];
				Common.cp.addElement(new Double(cutPoint));
			} else {
				double val = Common.realValues[attribute][values[iBound]];
				int numFW = 1;
				while (iBound + numFW <= end && Common.realValues[attribute][values[iBound + numFW]] == val) numFW++;
				if (iBound + numFW > end) numFW = end - begin + 2;
				int numBW = 1;
				while (iBound - numBW > oldBound && Common.realValues[attribute][values[iBound - numBW]] == val) numBW++;
				if (iBound - numBW == oldBound) numBW = end - begin + 2;

				if (numFW < numBW) {
					iBound += numFW;
				} else if (numBW < numFW) {
					iBound -= numBW;
				} else {
					if (numFW == end - begin + 2) {
						saCabo = true;
					}
					if (Rand.getReal() < 0.5) {
						iBound += numFW;
					} else {
						iBound -= numBW;
						iBound++;
					}
				}
				if (!saCabo) {
					double cutPoint = Common.realValues[attribute][values[iBound-1]];
					Common.cp.addElement(new Double(cutPoint));
				}
			}
			oldBound=iBound;
		}
		Common.quanta = new int[Parameters.numClasses][Common.cp.size()+1];
		Common.sumaAbajo = new int[Common.cp.size()+1];
		Common.sumaDerecha = new int[Parameters.numClasses];
		Common.total = new int[1];
	}
	
}
