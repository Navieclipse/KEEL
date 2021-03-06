/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fern�ndez
 * @version 1.0
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.core.*;
import keel.Dataset.*;


public class EARMGA {

    myDataset trans;
    String assoc_rules_fname;
    String sup_rules_fname;
    EARMGAProcess ap;
    DataB dataBase;
	ArrayList<AssociationRule> assoc_rules;

    //We may declare here the algorithm's parameters
	private int popsize;
	private int nGen;
	private double alpha;
    private int nIntervals;
	private int kItemsets;
	private double ps;
	private double pc;
	private double pm;
	private double minConfidence;
	private double minSupport;

    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public EARMGA() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public EARMGA(parseParameters parameters) {       
        this.trans = new myDataset();
        try {
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
            trans.readDataSet( parameters.getTransactionsInputFile() );
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasNumericalAttributes();
		this.somethingWrong = this.somethingWrong || this.trans.hasMissingAttributes();
		
		this.assoc_rules_fname = parameters.getAssociationRulesFile();
        this.sup_rules_fname = parameters.getOutputFile(0);

		long seed = Long.parseLong(parameters.getParameter(0));

        this.kItemsets = Integer.parseInt( parameters.getParameter(1) );
        this.popsize = Integer.parseInt( parameters.getParameter(2) );
        this.nGen = Integer.parseInt( parameters.getParameter(3) );
        this.alpha = Double.parseDouble( parameters.getParameter(4) );
        this.ps = Double.parseDouble( parameters.getParameter(5) );
        this.pc = Double.parseDouble( parameters.getParameter(6) );
        this.pm = Double.parseDouble( parameters.getParameter(7) );
        this.nIntervals = Integer.parseInt( parameters.getParameter(8) );
        this.minSupport = Double.parseDouble( parameters.getParameter(9) );
		this.minConfidence = Double.parseDouble( parameters.getParameter(10) );

		if (this.kItemsets > this.trans.getnVars())  this.kItemsets = this.trans.getnVars();

        Randomize.setSeed(seed);
	}

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } 
		else {
			this.dataBase = new DataB(this.nIntervals, this.trans);

        	this.ap = new EARMGAProcess(this.trans, this.dataBase, this.nGen, this.popsize, this.kItemsets, this.ps, this.pc, this.pm, this.alpha);
			this.ap.run();
			this.ap.printReport(this.minConfidence, this.minSupport);
			this.assoc_rules = this.ap.getSetRules (this.minConfidence, this.minSupport);
        	        	
			try {
				PrintWriter rule_writer = new PrintWriter(assoc_rules_fname);
				PrintWriter sup_writer = new PrintWriter(sup_rules_fname);
				
				rule_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rule_writer.println("<association_rules>");
				
				sup_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				sup_writer.println("<values>");
				
				
				double avg_sup=0.0, avg_conf=0.0;
				
				for (int i=0; i < assoc_rules.size(); i++) {
					AssociationRule a_r = assoc_rules.get(i);
					
					ArrayList<Gene> ant = a_r.getAntecedent();
					ArrayList<Gene> cons = a_r.getConsequent();
					
					rule_writer.println("<rule id=\"" + i + "\">");
					sup_writer.println("<rule id=\"" + i + "\" support=\"" + a_r.getSupport() + "\" supportRule=\"" + a_r.getAll_support() + "\" confidence=\"" + a_r.getConfidence() + "\" />");
					
					rule_writer.println("<antecedents>");
					for (int j=0; j < ant.size(); j++) {
						Gene g_ant = ant.get(j);
						createRule(g_ant, rule_writer);
					}
					rule_writer.println("</antecedents>");
					
					rule_writer.println("<consequents>");
					for (int j=0; j < cons.size(); j++) {
						Gene g_cons = cons.get(j);
						createRule(g_cons, rule_writer);
					}
					rule_writer.println("</consequents>");
					
					rule_writer.println("</rule>");
					
					avg_sup += a_r.getAll_support();
					avg_conf += a_r.getConfidence();
				}
				
				rule_writer.println("</association_rules>");
				sup_writer.println("</values>");
				
				rule_writer.close();
				sup_writer.close();
				
				System.out.println("Average SupportRules: " + ( avg_sup / assoc_rules.size() ) );
				System.out.println("Average Confidence: " + ( avg_conf / assoc_rules.size() ) );
				
				System.out.println("Algorithm Finished");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
        }
    }
    
    private void createRule(Gene g, PrintWriter w) {
		int i;
    	int attr = g.getAttr();
		Intervals inter;
		ArrayList<Integer> value = g.getValue();
    	
    	w.print("<attribute name=\"" + Attributes.getAttribute(attr).getName() + "\" value=\"");
    	
		if ( g.getType() == myDataset.NOMINAL ) {
			for (i=0; i < value.size()-1; i++) {
				w.print(Attributes.getAttribute(attr).getNominalValue(value.get(i).intValue()));
			}
			w.print(Attributes.getAttribute(attr).getNominalValue(value.get(value.size()-1).intValue()));
		}
		else {
			for (i=0; i < value.size()-1; i++) {
				inter = this.dataBase.getInterval(attr, value.get(i).intValue());
				w.print("[" + inter.getLeft() + ", " + inter.getRight() + "]");		
			}
			inter = this.dataBase.getInterval(attr, value.get(value.size()-1).intValue());
			w.print("[" + inter.getLeft() + ", " + inter.getRight() + "]");		
		}
		
		w.println("\" />");
    }    

}
