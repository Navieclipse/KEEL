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

package keel.Algorithms.RE_SL_Methods.P_FCS1;

/**
 * <p>
 * @author Written by Francisco Jos� Berlanga (University of Ja�n) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

public class Fuzzy {
/**	
 * <p>
 * It contains the definition for a gaussian fuzzy set
 * </p>
 */
 
    double center, width;

    /**
     * <p>       
     * Default constructor
     * </p>       
     */
    public Fuzzy() {
        center = 0.0;
        width = 0.0;
    }

    /**
     * <p>       
     * Creates a gaussian fuzzy set as a copy of another gaussian fuzzy set
     * </p>       
     * @param dif Fuzzy The gaussian fuzzy set used to create the new gaussian fuzzy set
     */
    public Fuzzy(Fuzzy dif) {
        center = dif.center;
        width = dif.width;
    }

}

