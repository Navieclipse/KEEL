package keel.Algorithms.DiscretizeFramework.Utils;

import java.util.Vector;

import keel.Algorithms.DiscretizeFramework.ChangeResult.ChangeResult;
import keel.Algorithms.DiscretizeFramework.InitSet.InitSet;
import keel.Algorithms.DiscretizeFramework.Measure.Measure;

public class Common {
	public static double [][]cutPoints;
	public static double [][]realValues;
	public static boolean []realAttributes;
	public static int []classOfInstances;
	public static int iClassIndex;
	
	public static Vector <Double> cp = new Vector <Double>();
	public static InitSet initSet;
	public static ChangeResult changeResult;
	public static Measure measure;
	
	//CADD
	public static double BIGX = 20.0;
	public static double confidenceThreshold;
	public static int numIntervals;
	public static int sumaAbajo[], sumaDerecha[], total[], quanta[][];
	public static int ordenados[];
}
