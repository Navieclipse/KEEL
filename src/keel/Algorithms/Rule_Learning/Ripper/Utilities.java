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

/**
 * <p>
 * @author Written by Alberto Fern�ndez (University of Granada)  15/10/2008
 * @author Modified by Xavi Sol� (La Salle, Ram�n Llull University - Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Ripper;

import java.util.Arrays;



public class Utilities {
/**
 * Collection of auxiliar methods.
 */
	
	
  /** The natural logarithm of 2. */
  public static double log2 = Math.log(2);

  /**
   * Mergesort algorithm for an array of long integers.
   * @param theArray long[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(long[] theArray,int nElems){
    // provides workspace
    long[] workSpace = new long[nElems];
    recMergeSort(theArray, workSpace, 0, nElems-1);
  }

  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(long[] theArray,long[] workSpace, int lowerBound,int upperBound){
    if(lowerBound == upperBound)            // if range is 1,
      return;                              // no use sorting
    else
    {                                    // find midpoint
      int mid = (lowerBound+upperBound) / 2;
      // sort low half
      recMergeSort(theArray,workSpace, lowerBound, mid);
      // sort high half
      recMergeSort(theArray,workSpace, mid+1, upperBound);
      // merge them
      merge(theArray,workSpace, lowerBound, mid+1, upperBound);
    }  // end else
  }  // end recMergeSort()
  //-----------------------------------------------------------
  static private void merge(long[] theArray,long[] workSpace, int lowPtr,int highPtr, int upperBound){
    int j = 0;                             // workspace index
    int lowerBound = lowPtr;
    int mid = highPtr-1;
    int n = upperBound-lowerBound+1;       // # of items

    while(lowPtr <= mid && highPtr <= upperBound)
      if( theArray[lowPtr] < theArray[highPtr] )
        workSpace[j++] = theArray[lowPtr++];
      else
        workSpace[j++] = theArray[highPtr++];

    while(lowPtr <= mid)
      workSpace[j++] = theArray[lowPtr++];

    while(highPtr <= upperBound)
      workSpace[j++] = theArray[highPtr++];

    for(j=0; j<n; j++)
      theArray[lowerBound+j] = workSpace[j];
  }
  /*************************END OF THE FIRST METHOD*******************************/

  /**
   * Mergesort algorithm for an array of long integers.
   * @param theArray double[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(double[] theArray,int nElems){
    // provides workspace
    double[] workSpace = new double[nElems];
//    recMergeSort(theArray, workSpace, 0, nElems-1);
    Arrays.sort(theArray);
  }

  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(double[] theArray,double[] workSpace, int lowerBound,int upperBound){
    if(lowerBound == upperBound)            // if range is 1,
      return;                              // no use sorting
    else
    {                                    // find midpoint
      int mid = (lowerBound+upperBound) / 2;
      // sort low half
      recMergeSort(theArray,workSpace, lowerBound, mid);
      // sort high half
      recMergeSort(theArray,workSpace, mid+1, upperBound);
      // merge them
      merge(theArray,workSpace, lowerBound, mid+1, upperBound);
    }  // end else
  }  // end recMergeSort()
  //-----------------------------------------------------------
  static private void merge(double[] theArray,double[] workSpace, int lowPtr,int highPtr, int upperBound){
    int j = 0;                             // workspace index
    int lowerBound = lowPtr;
    int mid = highPtr-1;
    int n = upperBound-lowerBound+1;       // # of items

    while(lowPtr <= mid && highPtr <= upperBound)
      if( theArray[lowPtr] < theArray[highPtr] )
        workSpace[j++] = theArray[lowPtr++];
      else
        workSpace[j++] = theArray[highPtr++];

    while(lowPtr <= mid)
      workSpace[j++] = theArray[lowPtr++];

    while(highPtr <= upperBound)
      workSpace[j++] = theArray[highPtr++];

    for(j=0; j<n; j++)
      theArray[lowerBound+j] = workSpace[j];
  }
  /*************************END OF THE SECOND METHOD*******************************/

  /**
   * Mergesort algorithm for an array of Pairs.
   * @param theArray Pair[] the Array to sort
   * @param nElems int size of theArray
   */
  public static void mergeSort(Pair[] theArray,int nElems){
    // provides workspace
//    Pair[] workSpace = new Pair[nElems];
//    recMergeSort(theArray, workSpace, 0, nElems-1);
	Arrays.sort(theArray);
  }
  //------------------------------PRIVATE METHODS--------------------------------------------------/
  static private void recMergeSort(Pair[] theArray,Pair[] workSpace, int lowerBound,int upperBound){
    if(lowerBound == upperBound)            // if range is 1,
      return;                              // no use sorting
    else
    {                                    // find midpoint
      int mid = (lowerBound+upperBound) / 2;
      // sort low half
      recMergeSort(theArray,workSpace, lowerBound, mid);
      // sort high half
      recMergeSort(theArray,workSpace, mid+1, upperBound);
      // merge them
      merge(theArray,workSpace, lowerBound, mid+1, upperBound);
    }  // end else
  }  // end recMergeSort()
  //-----------------------------------------------------------
  static private void merge(Pair[] theArray,Pair[] workSpace, int lowPtr,int highPtr, int upperBound){
      int j = 0;                             // workspace index
      int lowerBound = lowPtr;
      int mid = highPtr-1;
      int n = upperBound-lowerBound+1;       // # of items

      while(lowPtr <= mid && highPtr <= upperBound)
        if( theArray[lowPtr].value < theArray[highPtr].value )
          workSpace[j++] = theArray[lowPtr++];
        else
          workSpace[j++] = theArray[highPtr++];

      while(lowPtr <= mid)
        workSpace[j++] = theArray[lowPtr++];

      while(highPtr <= upperBound)
        workSpace[j++] = theArray[highPtr++];

      for(j=0; j<n; j++)
        theArray[lowerBound+j] = workSpace[j];
    }

    /*************************END OF THE THIRD METHOD*******************************/

    /**
     * Mergesort algorithm for a vector of Trio.
     * @param theArray Vector the Array to sort
     * @param nElems int size of theArray
     */
    public static void mergeSort(Trio[] theArray,int nElems){
      // provides workspace
//      Trio[] workSpace = new Trio[nElems];
//      recMergeSort(theArray, workSpace, 0, nElems-1);
    	Arrays.sort(theArray);
    }
    //------------------------------PRIVATE METHODS--------------------------------------------------/
    static private void recMergeSort(Trio[] theArray,Trio[] workSpace, int lowerBound,int upperBound){
      if(lowerBound == upperBound)            // if range is 1,
        return;                              // no use sorting
      else
      {                                    // find midpoint
        int mid = (lowerBound+upperBound) / 2;
        // sort low half
        recMergeSort(theArray,workSpace, lowerBound, mid);
        // sort high half
        recMergeSort(theArray,workSpace, mid+1, upperBound);
        // merge them
        merge(theArray,workSpace, lowerBound, mid+1, upperBound);
      }  // end else
    }  // end recMergeSort()
    //-----------------------------------------------------------
    static private void merge(Trio[] theArray,Trio[] workSpace, int lowPtr,int highPtr, int upperBound){
        int j = 0;                             // workspace index
        int lowerBound = lowPtr;
        int mid = highPtr-1;
        int n = upperBound-lowerBound+1;       // # of items

        while(lowPtr <= mid && highPtr <= upperBound)
          if( theArray[lowPtr].getKey() < theArray[highPtr].getKey() )
            workSpace[j++] = theArray[lowPtr++];
          else
            workSpace[j++] = theArray[highPtr++];

        while(lowPtr <= mid)
          workSpace[j++] = theArray[lowPtr++];

        while(highPtr <= upperBound)
          workSpace[j++] = theArray[highPtr++];

        for(j=0; j<n; j++)
          theArray[lowerBound+j] = workSpace[j];
      }

      /*************************END OF THE FOURTH METHOD*******************************/


      /**
       * Returns the logarithm of a for base 2.
       *
       * @param a 	a double
       * @return	the logarithm for base 2
       */
      public static double log2(double a) {

        return Math.log(a) / log2;
      }

}

