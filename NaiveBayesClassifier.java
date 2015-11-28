/*
 * Charles Mingchao Liao
 * 
 * Naive Bayes Classifier
 * 
 * This program is used to test the performance of Naive-Byes Algorithm
 * 
 * */

import java.io.*;
import java.util.*;

public class NaiveBayesClassifier {
	
	// number of attributes
	public static int attrNums;
	
	//number of samples
	public static int totalDataNums = 0;
	
	//all samples, including class label
	public static ArrayList<String[]> data = new ArrayList<String[]>();
	
	//number of each existing <xi,class label> pair
	public static Map<String,Map<String,Integer>> attrCounter = new HashMap();
	
	//number of each class label
	public static Map<String,Integer> classLabelCounter = new HashMap();
	
	public static void main(String[] args) {
		if(args.length == 0) {
			System.err.println("Usage: java NaiveBayesClassifier <data file 1> <data file 2> <data file3> ......");
			System.exit(-1);
		}
		
		controller(args);
	}
	
	//this method is used to read file 
	//and count each <xi, class label> pair, 
	//and count each class label
	public static void processFile(String fileName) throws FileNotFoundException {
		//initialize all global variables
		init();

		Scanner sc = new Scanner(new File(fileName));
		
		while(sc.hasNextLine()) {
			
			//get every attributes from each sample, the last one is class label
			String[] tmp = sc.nextLine().split("\\s+");
			
			totalDataNums++;
			
			Object o;
			
			//if the class label doesn't exist, put to map, counter is 1
			//else increase corresponding counter
			if((o = classLabelCounter.get(tmp[tmp.length-1])) != null) {
				classLabelCounter.put(tmp[tmp.length-1], ((int) o)+1);
			} else {
				classLabelCounter.put(tmp[tmp.length-1], 1);
			}
			
			attrNums = tmp.length - 1;
			
			data.add(tmp);
			
			for(int i = 0; i < tmp.length - 1; i++) {
				
				Map<String, Integer> first;
				
				//if the attribute doesn't exist, put to map
				//else get another map
				if((first = attrCounter.get(tmp[i])) != null) {
					
					//if the class label doesn't exist, put to map, counter is 1
					//else increase corresponding counter
					if((o = first.get(tmp[tmp.length-1])) != null) {
						first.put(tmp[tmp.length-1], ((int) o)+1);
					} else {
						first.put(tmp[tmp.length-1], 1);
					}
				} else {
					Map<String,Integer> second = new HashMap();
					second.put(tmp[tmp.length-1], 1);
					attrCounter.put(tmp[i], second);
				}
			}
		}
		
		sc.close();
	}
	
	//accept a string array contains all file need to process
	//process each file, print its performance and time
	//catch file not found exception
	public static void controller(String[] name) {
		System.out.println("Test Result:");
		for(String s : name) {
			try {
				long start = System.currentTimeMillis();
				processFile(s);
				double p = computePerformance(data);
				long end = System.currentTimeMillis();
				System.out.printf("\nPerformance for %s is: %f\t(time: %dms)\n",s,p,end-start);
			} catch(Exception e) {
				System.err.printf("\nError when processing %s (ie. File Not Found)\n",s);
			}
		}
	}
	
	//computer performance for each sample in the data
	public static double computePerformance(ArrayList<String[]> data) {
		int count = 0;
		for(String[] sample : data) {
			if(isCorrect(sample)) count++;
		}
		return ((double) count) / totalDataNums;
	}
	
	//judge whether the prediction of class label for giving sample is correct or not
	public static boolean isCorrect(String[] sample) {
		return makePrediction(sample).equals(sample[sample.length-1]);
	}
	
	//make prediction of class label for given sample
	//the class label with the highest "probability value" will be chose
	public static String makePrediction(String[] sample) {
		double p = Double.MIN_VALUE;
		String c = "";
		
		for(String s : classLabelCounter.keySet()) {
			double tmp = p(sample,s);
			if(tmp > p) {
				p = tmp;
				c = s;
			}
		}
		
		return c;
	}
	
	//compute the probability of each class
	public static double p(String[] sample, String c) {
		double rtn = 1;
		
		//probability of class label
		rtn *= p(c);
		
		for(int i = 0; i < sample.length-1; i++) {
			
			//probability of attribute,class label pair
			rtn *= p(sample[i], c);
		}
		
		return rtn;
	}
	
	//compute probability of given class label
	public static double p(String c) {
		Object o;
		if((o = classLabelCounter.get(c)) == null) return 0;
		return ((int) o) / ((double) totalDataNums);
	} 
	
	//compute probability of given attribute,class label pair
	public static double p(String attr, String c) {
		Object count;
		Map tmp;
		
		if((tmp = attrCounter.get(attr)) == null) return 0;
		if((count = tmp.get(c)) == null) return 0;
		
		return ((int) count) / ((double) p(c));
	}

	//do initialization for all global variables
	public static void init() {
		attrNums = 0;
		totalDataNums = 0;
		data = new ArrayList<String[]>();
		attrCounter = new HashMap();
		classLabelCounter = new HashMap();
	}
}
