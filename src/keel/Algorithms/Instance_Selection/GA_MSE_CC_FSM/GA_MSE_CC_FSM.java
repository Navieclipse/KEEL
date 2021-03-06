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

//
//  GA_MSE_CC_FSM.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 25-6-2008.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.GA_MSE_CC_FSM;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;

public class GA_MSE_CC_FSM extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private int tamPoblacion;
  private int nGen;
  private int kNeigh;

  public GA_MSE_CC_FSM (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l;
    int nClases;
    int clusters[];
    int nClus;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    Cromosoma poblacion[];
    int gen = 0;
    Cromosoma newPob[];

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Random inicialization of the population*/
    Randomize.setSeed (semilla);
    poblacion = new Cromosoma[tamPoblacion];
    for (i=0; i<tamPoblacion; i++)
      poblacion[i] = new Cromosoma (datosTrain.length, nClases, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, kNeigh, distanceEu);    

    while (gen < nGen) {
    	for (i=0; i<tamPoblacion; i++)
    		poblacion[i].evalua(clasesTrain, kNeigh, nClases);
    
    	nClus = Randomize.Randint(2, datosTrain.length-1);
    	clusters = kMeans (datosTrain, nClus);
    	
    	for (i=0; i<tamPoblacion; i++) {
    		poblacion[i].mutacion(nClus, clusters, datosTrain.length, nClases, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, kNeigh, distanceEu);
    	}

    	if (gen % 10 == 9) {
            Arrays.sort(poblacion);    		
            newPob = new Cromosoma[tamPoblacion];
            cruceCC (poblacion, newPob, nClus, clusters, datosTrain.length);
            poblacion = newPob;
            /*Evaluation of the population*/
            for (i=0; i<tamPoblacion; i++) {
            	poblacion[i].prepare(datosTrain.length, nClases, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, kNeigh, distanceEu);
        		poblacion[i].evalua(clasesTrain, kNeigh, nClases);
            }
    	}
    	gen++;
    }

    Arrays.sort(poblacion);
    nSel = poblacion[0].genesActivos();

    /*Building of S set from the best cromosome obtained*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (poblacion[0].getGen(i)) { //the instance must be copied to the solution
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("GA_MSE_CC_FSM "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, this.kNeigh);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, this.kNeigh);
	}
            
     KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);

  }
  
  public int [] kMeans (double datos[][], int nClus) {
	
	  int clusters[];
	  int orden[];
	  int i, j;
	  int tmp, pos;
	  double centers[][];
	  boolean parar = false;
	  double dist, minDist;
	  int conta[];
	  
	  conta = new int[nClus];
	  clusters = new int[datos.length];
	  orden = new int[datos.length];
	  for (i=0; i<orden.length; i++) {
		  orden[i] = i;
	  }
	  
	  for (i=0; i<orden.length; i++) {
		  pos = Randomize.Randint(i, orden.length-1);
		  tmp = orden[i];
		  orden[i] = orden[pos];
		  orden[pos] = tmp;
	  }
	  
	  centers = new double[nClus][datos[0].length];
	  for (i=0; i<nClus; i++) {
		  for (j=0; j<datos[0].length; j++) {
			  centers[i][j] = datos[orden[i]][j];
		  }
	  }
	  
	  for (i=0; i<datos.length; i++) {
		  pos = 0;
		  minDist = KNN.distancia(datos[i], centers[0]);
		  for (j=1; j<nClus; j++) {
			  dist = KNN.distancia(datos[i], centers[j]);
			  if (dist < minDist) {
				  minDist = dist;
				  pos = j;
			  }
		  }
		  clusters[i] = pos;
	  }	  
	  
	  while (!parar) {
		  parar = true;
		  
		  for (i=0; i<nClus; i++) {
			Arrays.fill(centers[i], 0);  
		  }
		  Arrays.fill(conta, 0);
		  for (i=0; i<clusters.length; i++) {
			  for (j=0; j<datos[0].length; j++) {
				  centers[clusters[i]][j] += datos[i][j];
			  }
			  conta[clusters[i]]++;
		  }
		  for (i=0; i<centers.length; i++) {
			  for (j=0; j<centers[i].length; j++) {
				  centers[i][j] /= (double) conta[i];
			  }
		  }
		  
		  for (i=0; i<datos.length; i++) {
			  pos = 0;
			  minDist = KNN.distancia(datos[i], centers[0]);
			  for (j=1; j<nClus; j++) {
				  dist = KNN.distancia(datos[i], centers[j]);
				  if (dist < minDist) {
					  minDist = dist;
					  pos = j;
				  }
			  }
			  if(pos != clusters[i]) {
				  parar = false;
				  clusters[i] = pos;
			  }
		  }
	  }
	  
	  return clusters;
  }

  public void cruceCC (Cromosoma poblacion[], Cromosoma newPob[], int nClus, int clusters[], int size) {
	  
	  int i, j, k;
	  int pos1, pos2;
	  
	  for (k=0; k<newPob.length; k++)
		  newPob[k] = new Cromosoma(size);
	  
	  for (k=0; k<poblacion.length; k+=2) {
		  pos1 = Randomize.Randint(0, (poblacion.length-1)/10);
		  do {
			  pos2 = Randomize.Randint(0, (poblacion.length-1)/10);			  
		  } while (pos1 == pos2);
		  
		  for (i=0; i<nClus; i++) {
			  if (Randomize.Rand() < 0.5) {
				  for (j=0; j<size; j++) {
					  if (clusters[j] == i) {
						  newPob[k].setGen(j, poblacion[k+1].getGen(j));
						  newPob[k+1].setGen(j, poblacion[k].getGen(j));
					  }
				  }
			  } else {
				  for (j=0; j<size; j++) {
					  if (clusters[j] == i) {
						  newPob[k].setGen(j, poblacion[k].getGen(j));
						  newPob[k+1].setGen(j, poblacion[k+1].getGen(j));
					  }
				  }				  
			  }
		  }
	  }	  

  }

  public void leerConfiguracion (String ficheroScript) {

    String fichero, linea, token;
    StringTokenizer lineasFichero, tokens;
    byte line[];
    int i, j;

    ficheroSalida = new String[2];

    fichero = Fichero.leeFichero (ficheroScript);
    lineasFichero = new StringTokenizer (fichero,"\n\r");

    lineasFichero.nextToken();
    linea = lineasFichero.nextToken();

    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the name of the training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    
	for (i=j+1; line[i]!='\"'; i++);
	i++;
	for (j=i; line[j]!='\"'; j++);
	ficheroValidation = new String (line,i,j-i);
	
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Getting the path and base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of the output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);

    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

    /*Getting the size of the population and number of evaluations*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nGen = Integer.parseInt(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kNeigh = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}
