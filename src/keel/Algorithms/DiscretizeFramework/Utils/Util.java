package keel.Algorithms.DiscretizeFramework.Utils;

import java.util.Vector;

public class Util {
	public static int discretize(int attribute,double value) {
		if(Common.cutPoints[attribute]==null) return 0;
		for(int i=0;i<Common.cutPoints[attribute].length;i++)
			if(value<Common.cutPoints[attribute][i]) return i;
		return Common.cutPoints[attribute].length;
	}
	
	public static int getNumIntervals(int attribute) {
		return Common.cutPoints[attribute].length+1;
	}

	public static double getCutPoint(int attribute,int cp) {
		return Common.cutPoints[attribute][cp];
	}
	
	public static void sortValues(int attribute,int []values,int begin,int end) {
		double pivot;
		int temp;
		int i,j;

		i=begin;j=end;
		pivot=Common.realValues[attribute][values[(i+j)/2]];
		do {
			while(Common.realValues[attribute][values[i]]<pivot) i++;
			while(Common.realValues[attribute][values[j]]>pivot) j--;
			if(i<=j) {
				if(i<j) {
					temp=values[i];
					values[i]=values[j];
					values[j]=temp;
				}
				i++; j--;
			}
		} while(i<=j);
		if(begin<j) sortValues(attribute,values,begin,j);
		if(i<end) sortValues(attribute,values,i,end);
	}
	
	public static void construyeQuanta (int quanta[][], int sumaAbajo[], int sumaDerecha[], int total[], Vector <Double> cutPoints, int ordenados[], int attribute) {
			
			int i, j;
			int intervalo = 0;
			
			for (i=0; i<quanta.length; i++) {
				for (j=0; j<quanta[i].length; j++) {
					quanta[i][j] = 0;
					sumaAbajo[j] = 0;
				}
				sumaDerecha[i] = 0;
			}
			total[0] = 0;
			
			for (i=0; i<ordenados.length; i++) {
				if (intervalo < cutPoints.size()) {
					if (Common.realValues[attribute][ordenados[i]] >= cutPoints.elementAt(intervalo)) {
						intervalo++;
					}
				} else {
					intervalo = cutPoints.size();
				}
				quanta[Common.classOfInstances[ordenados[i]]][intervalo]++;
			}
			
			for (i=0; i<quanta.length; i++) {
				for (j=0; j<quanta[i].length; j++) {
					sumaAbajo[j] += quanta[i][j];
					sumaDerecha[i] += quanta[i][j];
					total[0] += quanta[i][j];
				}
			}		
		}	
}
