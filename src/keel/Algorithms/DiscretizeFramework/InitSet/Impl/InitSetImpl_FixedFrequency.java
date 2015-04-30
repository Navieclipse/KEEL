package keel.Algorithms.DiscretizeFramework.InitSet.Impl;

import java.util.Vector;

import keel.Algorithms.DiscretizeFramework.InitSet.InitSet;
import keel.Algorithms.DiscretizeFramework.Utils.Common;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Rand;

public class InitSetImpl_FixedFrequency implements InitSet{
	
	private double [][]realValues;
	private double freqSize;

	@Override
	public void init(int attribute, int[] values, int begin, int end) {
		// TODO Auto-generated method stub
		
		double quota=freqSize;
		double dBound=0.0;
		int i;
		int oldBound=0;
		int numInt = (int)Math.ceil(((double)end-begin+1)/quota);

		Vector cp=new Vector();

		for(i=0;i<numInt-1;i++) {
			dBound+=quota;
			int iBound=(int)Math.round(dBound);
			if(iBound<=oldBound) continue;
			if(realValues[attribute][values[iBound-1]]!=realValues[attribute][values[iBound]]) {
				double cutPoint=(realValues[attribute][values[iBound-1]]+realValues[attribute][values[iBound]])/2.0;
				Common.cp.addElement(new Double(cutPoint));
				
			} else {
				double val=realValues[attribute][values[iBound]];
				int numFW=1;
				while(iBound+numFW<=end && realValues[attribute][values[iBound+numFW]]==val) numFW++;
				if(iBound+numFW>end) numFW=end-begin+2;
				int numBW=1;
				while(iBound-numBW>oldBound && realValues[attribute][values[iBound-numBW]]==val) numBW++;
				if(iBound-numBW==oldBound) numBW=end-begin+2;

				if(numFW<numBW) {
					iBound+=numFW;
				} else if(numBW<numFW) {
					iBound-=numBW;
				} else {
					if(numFW==end-begin+2) {
						return;
					}
					if(Rand.getReal()<0.5) {
						iBound+=numFW;
					} else {
						iBound-=numBW;
						iBound++;
					}
				}
				double cutPoint=(realValues[attribute][values[iBound-1]]+realValues[attribute][values[iBound]])/2.0;
				Common.cp.addElement(new Double(cutPoint));
			}
			oldBound=iBound;
		}

		Common.quanta = new int[Parameters.numClasses][Common.cp.size()+1];
		Common.sumaAbajo = new int[Common.cp.size()+1];
		Common.sumaDerecha = new int[Parameters.numClasses];
		Common.total = new int[1];
		
	}

	
}
