package keel.Algorithms.DiscretizeFramework.Utils;

import keel.Algorithms.Genetic_Rule_Learning.Globals.FileManagement;
import keel.Algorithms.Genetic_Rule_Learning.Globals.LogManager;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

public class ApplyDiscretization {
	public static void applyDiscretization(String in,String out) {
		boolean bHit;
		
		InstanceSet is=new InstanceSet();
		try {
			is.readSet(in,false);
                } catch(Exception e) {
                        LogManager.printErr(e.toString());
                        System.exit(1);
                }

		FileManagement fm = new FileManagement();
		Instance []instances=is.getInstances();
		Attribute []att=Attributes.getInputAttributes();

		try {
			fm.initWrite(out);
			fm.writeLine("@relation "+Attributes.getRelationName()+"\n");
			bHit = false;
			for(int i=0;i<Parameters.numAttributes;i++) {
				if (i == Common.iClassIndex){
					fm.writeLine(Attributes.getOutputAttributes()[0].toString()+"\n");
					bHit = true;
				}
				if(Common.realAttributes[i]) {
					String def="@attribute "+att[i].getName()+" {";
					if(Common.cutPoints[i]!=null) {
						for(int j=0;j<Common.cutPoints[i].length+1;j++) {
							def+=j;
							if(j<Common.cutPoints[i].length) def+=",";
						}
					} else {
						def+=0;
					}
					def+="}\n";
					fm.writeLine(def);
				} else {
					fm.writeLine(att[i].toString()+"\n");
				}
			}
			if (bHit == false){
				fm.writeLine(Attributes.getOutputAttributes()[0].toString()+"\n");
			}
			
			fm.writeLine("@inputs ");
			for (int i = 0; i < Parameters.numAttributes-1;i++){
				fm.writeLine(att[i].getName()+",");
			}
			fm.writeLine(att[Parameters.numAttributes-1].getName()+"\n");
			
			fm.writeLine("@outputs "+ Attributes.getOutputAttributes()[0].getName()+"\n");
			
			fm.writeLine("@data\n");

			bHit = false;
			for(int i=0;i<instances.length;i++) {
				boolean []missing=instances[i].getInputMissingValues();
				String newInstance="";
				for(int j=0;j<Parameters.numAttributes;j++) {
					if (j == Common.iClassIndex){
						String className=instances[i].getOutputNominalValues(0);
						newInstance+=className+",";
						bHit = true;
					}
					
					if(missing[j]) {
						newInstance+="?";
					} else {
						if(Common.realAttributes[j]) {
							double val=instances[i].getInputRealValues(j);
							int interv=Util.discretize(j,val);
							newInstance+=interv;
						} else {
							newInstance+=instances[i].getInputNominalValues(j);
						}
					}
					
					if (bHit == true && j == (Parameters.numAttributes -1)){
						newInstance += "\n";
					} else {
						newInstance +=",";
					} 
				}
				if (bHit == false){
					String className=instances[i].getOutputNominalValues(0);
					newInstance+=className+"\n";					
				}

				fm.writeLine(newInstance);
			}
			fm.closeWrite();
		} catch(Exception e) {
			LogManager.printErr("Exception in doDiscretize");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
