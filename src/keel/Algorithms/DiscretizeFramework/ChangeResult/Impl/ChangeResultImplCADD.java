package keel.Algorithms.DiscretizeFramework.ChangeResult.Impl;

import java.util.Vector;

import keel.Algorithms.DiscretizeFramework.ChangeResult.ChangeResult;
import keel.Algorithms.DiscretizeFramework.Utils.Common;
import keel.Algorithms.DiscretizeFramework.Utils.Util;
import keel.Algorithms.Genetic_Rule_Learning.Globals.Parameters;

public class ChangeResultImplCADD implements ChangeResult{
	public void changeResult(int attribute,int []values,int begin,int end)
	{
		Vector <Double> cpTmp;
		Vector <Double> mejorCP;
		
		double fitness, mejorFitness;
		boolean parar = false;
		double partialRCA;
		double test;
        mejorCP = new Vector <Double>(Common.cp);
		
		/*Second step: Local Search changing the cut points*/
		while (!parar) {
			Util.construyeQuanta(Common.quanta, Common.sumaAbajo, Common.sumaDerecha, Common.total, Common.cp, Common.ordenados, attribute);
			
			mejorFitness = Common.measure.compute();
			parar = true;
			for (int i=0; i<Common.cp.size(); i++) {
				cpTmp = cambiaIntervalo (Common.cp,Common.ordenados, attribute, i, false);
				Util.construyeQuanta(Common.quanta, Common.sumaAbajo, Common.sumaDerecha, Common.total, cpTmp, Common.ordenados, attribute);
				fitness =Common.measure.compute();
				if (fitness > mejorFitness) {
					mejorFitness = fitness;
					mejorCP = new Vector <Double>(cpTmp);
					parar = false;
				}

				cpTmp = cambiaIntervalo (Common.cp, Common.ordenados, attribute, i, true);
				Util.construyeQuanta(Common.quanta, Common.sumaAbajo, Common.sumaDerecha, Common.total, cpTmp, Common.ordenados, attribute);
				fitness = Common.measure.compute();
				if (fitness > mejorFitness) {
					mejorFitness = fitness;
					mejorCP = new Vector <Double>(cpTmp);
					parar = false;
				}
			}
			Common.cp = new Vector <Double>(mejorCP);
		}
		
		for (int i=1; i<Common.cp.size(); i++) {
			if (Common.cp.elementAt(i-1).doubleValue() >= Common.cp.elementAt(i).doubleValue()) {
				Common.cp.remove(i);
				i--;
			}
		}
		
		/*Third step: remove intervals which are statistically independent*/
		parar = false;
		while (!parar && Common.cp.size() > (Common.numIntervals-1)) {
			parar = true;
			Util.construyeQuanta(Common.quanta, Common.sumaAbajo, Common.sumaDerecha, Common.total, Common.cp, Common.ordenados, attribute);
			for (int i=0; i<Common.cp.size() && parar; i++) {
				 partialRCA = computeRCA(Common.quanta, Common.sumaAbajo, i);
				 test = computeTest(Common.quanta, Common.sumaAbajo, i);
				 if (partialRCA >= test) {					 
					 parar = false;
					 Common.cp.remove(i);
				 }
			}
		}
	}
	private Vector <Double> cambiaIntervalo (Vector <Double> cp, int ordenados[], int attribute, int intervalo, boolean sentido) {
			
			Vector <Double> res = new Vector <Double>();
			int i, j;
			double v;
			
			for (i=0; i<cp.size(); i++) {
				if (i == intervalo) {
					v = cp.elementAt(i);
					for (j=0; j<ordenados.length && Common.realValues[attribute][ordenados[j]] < v; j++);
					if (sentido) {
						for ( ;j<ordenados.length && Common.realValues[attribute][ordenados[j]] == v; j++);
						if (j == ordenados.length) {
							j--;
						}
					} else {
						if (j>0) {
							j--;
						}
					}
					res.addElement(Common.realValues[attribute][ordenados[j]]);
				} else {
					res.addElement(cp.elementAt(i));
				}
			}
			
			return res;
		}
	private double computeRCA (int quanta[][], int sumaAbajo[], int intervalo) {
		
		int i, j;
		double ICA = 0;
		double HCA = 0;
		int total;
		int sumaDerecha[] = new int[Parameters.numClasses];
		
		total = sumaAbajo[intervalo] + sumaAbajo[intervalo+1];
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				sumaDerecha[i] += quanta[i][j];
			}
		}		
	
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					ICA += (double)quanta[i][j]/(double)total * log2(((double)quanta[i][j] / (double)total) / (((double)sumaDerecha[i] / (double)total) * ((double)sumaAbajo[j] / (double)total)));
			}
		}
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					HCA += (double)quanta[i][j]/(double)total * log2((double)quanta[i][j]/(double)total);
			}
		}
		
		HCA = -1.0 * HCA;
		
		return ICA / HCA;		
	}
	private double computeTest (int quanta[][], int sumaAbajo[], int intervalo) {
		
		int i, j;
		double HCA = 0;
		int total;
		int sumaDerecha[] = new int[Parameters.numClasses];
		
		total = sumaAbajo[intervalo] + sumaAbajo[intervalo+1];
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				sumaDerecha[i] += quanta[i][j];
			}
		}		
		
		for (i=0; i<quanta.length; i++) {
			for (j=intervalo; j<=intervalo+1; j++) {
				if (quanta[i][j] > 0)
					HCA += (double)quanta[i][j]/(double)total * log2((double)quanta[i][j]/(double)total);
			}
		}
		
		HCA = -1.0 * HCA;
		
		return critchi(Common.confidenceThreshold, Parameters.numClasses-1) / (2 * total * HCA);		
	}
	private double log2 (double x) {		
		return Math.log(x) / Math.log(2);
	}
	private double critchi(double p, double df) {
	    double CHI_EPSILON = 0.000001;   /* Accuracy of critchi approximation */
	    double CHI_MAX = 99999.0;        /* Maximum chi-square value */
	    double minchisq = 0.0;
	    double maxchisq = CHI_MAX;
	    double chisqval;
	    
	    if (p <= 0.0) {
	        return maxchisq;
	    } else {
	        if (p >= 1.0) {
	            return 0.0;
	        }
	    }
	    
	    chisqval = df / Math.sqrt(p);    /* fair first value */
	    while ((maxchisq - minchisq) > CHI_EPSILON) {
	        if (pochisq(chisqval, df) < p) {
	            maxchisq = chisqval;
	        } else {
	            minchisq = chisqval;
	        }
	        chisqval = (maxchisq + minchisq) * 0.5;
	    }
	    return chisqval;
	}
	
	private double pochisq(double x, double df) {
	    double a, y=0.0, s;
	    double e, c, z;
	    boolean even;                     /* True if df is an even number */
	
	    double LOG_SQRT_PI = 0.5723649429247000870717135; /* log(sqrt(pi)) */
	    double I_SQRT_PI = 0.5641895835477562869480795;   /* 1 / sqrt(pi) */
	    
	    if (x <= 0.0 || df < 1) {
	        return 1.0;
	    }
	    
	    a = 0.5 * x;
	    even = !(df % 1 == 1);
	    if (df > 1) {
	        y = ex(-a);
	    }
	    s = (even ? y : (2.0 * poz(-Math.sqrt(x))));
	    if (df > 2) {
	        x = 0.5 * (df - 1.0);
	        z = (even ? 1.0 : 0.5);
	        if (a > Common.BIGX) {
	            e = (even ? 0.0 : LOG_SQRT_PI);
	            c = Math.log(a);
	            while (z <= x) {
	                e = Math.log(z) + e;
	                s += ex(c * z - a - e);
	                z += 1.0;
	            }
	            return s;
	        } else {
	            e = (even ? 1.0 : (I_SQRT_PI / Math.sqrt(a)));
	            c = 0.0;
	            while (z <= x) {
	                e = e * (a / z);
	                c = c + e;
	                z += 1.0;
	            }
	            return c * y + s;
	        }
	    } else {
	        return s;
	    }
	}

	private double ex(double x) {
	    return (x < -Common.BIGX) ? 0.0 : Math.exp(x);
	}   
	
	private double poz(double z) {
	    double y, x, w;
	    double Z_MAX = 6.0;              /* Maximum meaningful z value */
	    
	    if (z == 0.0) {
	        x = 0.0;
	    } else {
	        y = 0.5 * Math.abs(z);
	        if (y >= (Z_MAX * 0.5)) {
	            x = 1.0;
	        } else if (y < 1.0) {
	            w = y * y;
	            x = ((((((((0.000124818987 * w
	                     - 0.001075204047) * w + 0.005198775019) * w
	                     - 0.019198292004) * w + 0.059054035642) * w
	                     - 0.151968751364) * w + 0.319152932694) * w
	                     - 0.531923007300) * w + 0.797884560593) * y * 2.0;
	        } else {
	            y -= 2.0;
	            x = (((((((((((((-0.000045255659 * y
	                           + 0.000152529290) * y - 0.000019538132) * y
	                           - 0.000676904986) * y + 0.001390604284) * y
	                           - 0.000794620820) * y - 0.002034254874) * y
	                           + 0.006549791214) * y - 0.010557625006) * y
	                           + 0.011630447319) * y - 0.009279453341) * y
	                           + 0.005353579108) * y - 0.002141268741) * y
	                           + 0.000535310849) * y + 0.999936657524;
	        }
	    }
	    return z > 0.0 ? ((x + 1.0) * 0.5) : ((1.0 - x) * 0.5);
	}

}
