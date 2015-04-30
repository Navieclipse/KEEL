package keel.Algorithms.DiscretizeFramework.Measure.Impl;

import keel.Algorithms.DiscretizeFramework.Measure.Measure;
import keel.Algorithms.DiscretizeFramework.Utils.Common;

public class MeasureImplCaim_Bayesian implements Measure{

	@Override
	public double compute() {
		int i, j;
		int mejorCount;
		double suma = 0.0;
		double temp;
		
		for (i=0; i<Common.quanta[0].length; i++) {
			mejorCount = Common.quanta[0][i];
			for (j=1; j<Common.quanta.length; j++) {
				if (Common.quanta[j][i] > mejorCount) {
					mejorCount = Common.quanta[j][i];
				}
			}
			temp = (double)mejorCount / (double)Common.sumaAbajo[i];
			temp *= (double)mejorCount;
			suma += temp;
		}
		
		return suma / (double)Common.quanta[0].length;
	}

}
