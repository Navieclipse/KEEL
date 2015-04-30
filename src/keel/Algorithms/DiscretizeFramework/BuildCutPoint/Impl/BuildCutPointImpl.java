package keel.Algorithms.DiscretizeFramework.BuildCutPoint.Impl;

import keel.Algorithms.DiscretizeFramework.BuildCutPoint.BuildCutPoint;
import keel.Algorithms.DiscretizeFramework.DiscretizeAttribute.DiscretizeAttribute;
import keel.Algorithms.DiscretizeFramework.DiscretizeAttribute.impl.DiscretizeAttributeImpl;
import keel.Algorithms.DiscretizeFramework.Utils.Common;
import keel.Algorithms.DiscretizeFramework.Utils.Util;
import keel.Algorithms.Genetic_Rule_Learning.Globals.LogManager;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

public class BuildCutPointImpl implements BuildCutPoint{
	public void buildCutPoints(InstanceSet is) {
		int i;
		boolean bHit;
		
		Instance []instances=is.getInstances();

		Common.classOfInstances= new int[instances.length];
		for(i=0;i<instances.length;i++) 
			Common.classOfInstances[i]=instances[i].getOutputNominalValuesInt(0);
		
		Common.cutPoints=new double[Parameters.numAttributes][];
		Common.realAttributes = new boolean[Parameters.numAttributes];
		Common.realValues = new double[Parameters.numAttributes][];

		i = 0;
		bHit = false;
		for (int a = 0; i < Parameters.numAttributes; a++){
			Attribute at=Attributes.getAttribute(a);
			if (at.getDirectionAttribute() == Attribute.INPUT){
				if(at.getType()==Attribute.REAL || at.getType()==Attribute.INTEGER) {
					Common.realAttributes[i]=true;
	
					Common.realValues[i] = new double[instances.length];
					int []points= new int[instances.length];
					int numPoints=0;
					for(int j=0;j<instances.length;j++) {
						if(!instances[j].getInputMissingValues(i)) {
							points[numPoints++]=j;
							Common.realValues[i][j]=instances[j].getInputRealValues(i);
						}
					}
	
					Util.sortValues(i,points,0,numPoints-1);
	
					DiscretizeAttribute da = new DiscretizeAttributeImpl();
					
					da.discretizeAttribute(i,points,0,numPoints-1);
					
					if(Common.cp.size()>0) {
						Common.cutPoints[i]=new double[Common.cp.size()];
						for(int j=0;j<Common.cutPoints[i].length;j++) {
							Common.cutPoints[i][j]=((Double)Common.cp.elementAt(j)).doubleValue();
							LogManager.println("Cut point "+j+" of attribute "+i+" : "+Common.cutPoints[i][j]);
						}
					} else {
						Common.cutPoints[i]=null;
					}
					LogManager.println("Number of cut points of attribute "+i+" : "+Common.cp.size());
				} else {
					Common.realAttributes[i]=false;
				}
				i++;	
			} else {
				Common.iClassIndex = a;
				bHit = true;
			}
		}
		
		if (bHit == false){
			Common.iClassIndex = Parameters.numAttributes;
		}
	}
}
