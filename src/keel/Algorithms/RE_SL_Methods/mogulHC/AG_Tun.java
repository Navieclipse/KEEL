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

package keel.Algorithms.RE_SL_Methods.mogulHC;

import org.core.*;
import java.util.*;
import java.lang.Math;

class AG_Tun {
  public double prob_cruce, prob_mutacion, a, b;
  public int Mu_next, Trials;
  public double Best_current_perf;
  public int Best_guy;
  public int long_poblacion, n_genes, n_reglas;

  public int[] sample;
  public int last;

  public Structure[] Old;
  public Structure[] New;
  public Structure[] C;
  public Structure[] temp;
  public TipoIntervalo[] intervalos;
  public Adap_Tun fun_adap;

  public AG_Tun(int n_poblacion, double cruce, double mutacion, double valor_a,
            double valor_b, Adap_Tun funcion) {
    int i;

    this.long_poblacion = n_poblacion;
    this.prob_cruce = cruce;
    prob_mutacion = mutacion;
    a = valor_a;
    b = valor_b;
    fun_adap = funcion;
	this.last = (int) (this.long_poblacion * this.prob_cruce);

    sample = new int[long_poblacion];
  }

  /** Reads the RB of a input file */
  private void leer_BR(String fichero, int n_variables) {
    int i;
    String cadena;

    cadena = Fichero.leeFichero(fichero);
    StringTokenizer sT = new StringTokenizer(cadena, "\n\r\t ", false);
    sT.nextToken();
    sT.nextToken();
    sT.nextToken();

    n_reglas = Integer.parseInt(sT.nextToken());
    n_genes = 3 * n_variables * n_reglas;

    Old = new Structure[long_poblacion];
    New = new Structure[long_poblacion];

    for (i = 0; i < long_poblacion; i++) {
      Old[i] = new Structure(n_genes);
      New[i] = new Structure(n_genes);
    }

    for (i = 0; i < n_genes; i++) {
      New[0].Gene[i] = Double.parseDouble(sT.nextToken());
    }

    New[0].n_e = 1;
  }

  public void Intercambio() {
    temp = Old;
    Old = New;
    New = temp;
  }

  /** Inicialization of the population */
  public void Initialize(String fichero, int n_variables) {
    int i, j;

    leer_BR(fichero, n_variables);

    prob_mutacion = prob_mutacion / (double) n_genes;

    C = new Structure[4];
    for (i = 0; i < 4; i++) {
      C[i] = new Structure(n_genes);
    }

    intervalos = new TipoIntervalo[n_genes];
    for (i = 0; i < n_genes; i++) {
      intervalos[i] = new TipoIntervalo();
    }

    /* we generate the variation intervals for each gene */
    for (i = 0; i < n_genes; i += 3) {
      intervalos[i].min = New[0].Gene[i] - (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
      intervalos[i].max = New[0].Gene[i] + (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;

      intervalos[i + 1].min = New[0].Gene[i + 1] - (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
      intervalos[i + 1].max = New[0].Gene[i + 1] + (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;

      intervalos[i + 2].min = New[0].Gene[i + 2] - (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
      intervalos[i + 2].max = New[0].Gene[i + 2] + (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
    }

    /* the remainder of the population is randomly generated out of a the intervals */
    for (i = 1; i < long_poblacion; i++) {
      for (j = 0; j < n_genes; j++) {
        New[i].Gene[j] = intervalos[j].min + (intervalos[j].max - intervalos[j].min) * Randomize.Rand();
      }

      New[i].n_e = 1;
    }
  }

  /* Selection based on the Baker's Estocastic Universal Sampling */
  void Select() {
    double expected, factor, perf, ptr, sum, rank_max, rank_min;
    int i, j, k, best, temp;

    rank_min = 0.75;

    /* we assign the ranking to each element:
         The best: ranking = long_poblacion-1
      The worse: ranking = 0 */
    for (i = 0; i < long_poblacion; i++) {
      Old[i].n_e = 0;
    }

    /* we look for the best ordered non element */
    for (i = 0; i < long_poblacion - 1; i++) {
      best = -1;
      perf = 0.0;
      for (j = 0; j < long_poblacion; j++) {
        if ( (Old[j].n_e == 0) && (best == -1 || Old[j].Perf < perf)) {
          perf = Old[j].Perf;
          best = j;
        }
      }

      /* we assign the ranking */
      Old[best].n_e = long_poblacion - 1 - i;
    }

    /* we normalize the ranking */
    rank_max = 2.0 - rank_min;
    factor = (rank_max - rank_min) / (double) (long_poblacion - 1);

    /* we assign the number of replicas of each chormosome according to the select probability */
    k = 0;
    ptr = Randomize.Rand();
    for (sum = i = 0; i < long_poblacion; i++) {
      expected = rank_min + Old[i].n_e * factor;
      for (sum += expected; sum >= ptr; ptr++) {
        sample[k++] = i;
      }
    }

    /* we complete the population if necessary */
    if (k != long_poblacion) {
      for (; k < long_poblacion; k++) {
        sample[k] = Randomize.RandintClosed(0, long_poblacion - 1);
      }
    }

    /* we shuffle the selected chromosomes */
    for (i = 0; i < long_poblacion; i++) {
      j = Randomize.RandintClosed(i, long_poblacion - 1);
      temp = sample[j];
      sample[j] = sample[i];
      sample[i] = temp;
    }

    /* we create the new population */
    for (i = 0; i < long_poblacion; i++) {
      k = sample[i];
      for (j = 0; j < n_genes; j++) {
        New[i].Gene[j] = Old[k].Gene[j];
      }

      New[i].Perf = Old[k].Perf;
      New[i].n_e = 0;
    }
  }

  private double T_producto_logico(double x, double y) {
    if (x < y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  private double S_suma_logica(double x, double y) {
    if (x > y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  private double Promedio1(double x, double y, double p) {
    return (p * x + (1 - p) * y);
  }

  /** Max-Min-Aritmetical Crossover */
  public void Max_Min_Crossover() {
    int mom, dad, i, j, temp;
    int[] indice = new int[4];
    double temp1, temp2;

    for (mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      for (i = 0; i < n_genes; i++) {

        temp1 = New[mom].Gene[i];
        temp2 = New[dad].Gene[i];

        /* we obtain 4 offsprings: appling the t-norma, the
             t-conorma and 2 the average function */
        C[0].Gene[i] = T_producto_logico(temp1, temp2);
        C[1].Gene[i] = S_suma_logica(temp1, temp2);
        C[2].Gene[i] = Promedio1(temp1, temp2, a);
        C[3].Gene[i] = Promedio1(temp1, temp2, 1.0 - a);
      }

      /* Evaluation of the 4 offsprings */
      C[0].Perf = fun_adap.eval(C[0].Gene);
      C[1].Perf = fun_adap.eval(C[1].Gene);
      C[2].Perf = fun_adap.eval(C[2].Gene);
      C[3].Perf = fun_adap.eval(C[3].Gene);

      /* we order the offsprings by means of the bubble method */
      for (i = 0; i < 4; i++) {
        indice[i] = i;
      }

      for (i = 0; i < 4; i++) {
        for (j = 0; j < 3 - i; j++) {
          if (C[indice[j + 1]].Perf < C[indice[j]].Perf) {
            temp = indice[j];
            indice[j] = indice[j + 1];
            indice[j + 1] = temp;
          }
        }
      }

      for (i = 0; i < n_genes; i++) {
        New[mom].Gene[i] = C[indice[0]].Gene[i];
        New[dad].Gene[i] = C[indice[1]].Gene[i];
      }

      /* we update the fitness of the offsprings */
      New[mom].Perf = C[indice[0]].Perf;
      New[dad].Perf = C[indice[1]].Perf;
      New[mom].n_e = 0;
      New[dad].n_e = 0;

      Trials += 2;
    }
  }

  private double delta(long t, double y, long n_generaciones) {
    double r, potencia, subtotal, sub;

    r = Randomize.Rand();
    sub = 1.0 - (double) t / (double) n_generaciones;
    potencia = Math.pow(sub, (double) b);
    subtotal = Math.pow(r, potencia);
    return (y * (1.0 - subtotal));
  }

  /** Mutation Non Uniform */
  public void Mutacion_No_Uniforme(long Gen, long n_generaciones) {
    int posiciones, i, j;
    double nval, m;

    posiciones = n_genes * long_poblacion;

    if (prob_mutacion > 0) {
      while (Mu_next < posiciones) {

        /* we determinate the chromosome and the gene */
        i = Mu_next / n_genes;
        j = Mu_next % n_genes;

        /* we mutate the gene */
        if (Randomize.Rand() < 0.5) {
          nval = New[i].Gene[j] +
              delta(Gen, intervalos[j].max - New[i].Gene[j], n_generaciones);
        }
        else {
          nval = New[i].Gene[j] -
              delta(Gen, New[i].Gene[j] - intervalos[j].min, n_generaciones);
        }

        New[i].Gene[j] = nval;
        New[i].n_e = 1;

        /* we calculate the next position */
        if (prob_mutacion < 1) {
          m = Randomize.Rand();
          Mu_next += (int) (Math.log(m) / Math.log(1.0 - prob_mutacion));
          Mu_next++;
        }
        else {
          Mu_next += 1;
        }
      }

      Mu_next -= posiciones;
    }
  }

  /** Fitness Function */
  void Evaluate() {
    double performance;
    int i, j;

    for (i = 0; i < long_poblacion; i++) {
      /* if the chromosome aren't evaluated, it's evaluate */
      if (New[i].n_e == 1) {
        New[i].Perf = fun_adap.eval(New[i].Gene);
        performance = New[i].Perf;
        New[i].n_e = 0;
        Trials++; /* we increment the number of evaluated chromosomes */

      }
      else {
        performance = New[i].Perf;
      }

      /* we calculate the position of the best individual */
      if (i == 0) {
        Best_current_perf = performance;
        Best_guy = 0;
      }
      else if (performance < Best_current_perf) {
        Best_current_perf = performance;
        Best_guy = i;
      }
    }
  }

  /* Elitist selection */
  void Elitist() {
    int i, k, found;

    /* if the best individual of the old population aren't in the new population, we remplace the last individual for this */
    for (i = 0, found = 0; i < long_poblacion && (found == 0); i++) {
      for (k = 0, found = 1; (k < n_genes) && (found == 1); k++) {
        if (New[i].Gene[k] != Old[Best_guy].Gene[k]) {
          found = 0;
        }
      }
    }

    if (found == 0) {
      for (k = 0; k < n_genes; k++) {
        New[long_poblacion - 1].Gene[k] = Old[Best_guy].Gene[k];
      }

      New[long_poblacion - 1].Perf = Old[Best_guy].Perf;
      New[long_poblacion - 1].n_e = 0;
    }
  }

  /** Returns the best solution*/
  public double[] solucion() {
    return (New[Best_guy].Gene);
  }

  /** Returns the fitness of the best solution */
  public double solucion_ec() {
    return (New[Best_guy].Perf);
  }
}

