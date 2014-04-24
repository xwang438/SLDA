package impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * k-means clustering algorithm
 * 
 */
public class KMeans {
	
	File dataset;
	int kClusters;
	List<Double[]> points;
	List<Double[]> clustersCentroids;
	List<Integer>[] clustersMapping;
	int dimension;

	@SuppressWarnings("unchecked")
	public KMeans(List<Double[]> descriptors) {
		int size = descriptors.size();
		this.kClusters = (int)Math.sqrt(size);
		this.points = descriptors;
		this.clustersCentroids = new ArrayList<Double[]>(kClusters);
		this.clustersMapping = (List<Integer>[]) new ArrayList[clustersCentroids.size()];
		this.dimension = descriptors.size() == 0? 0 : descriptors.get(0).length;
		for(int i = 0; i < points.size(); i++) {
			if(points.get(i).length < dimension) {
				Double[] d = new Double[dimension];
				for(int j = 0; j < points.get(i).length; j++)
					d[j] = points.get(i)[j];
				for(int j = points.get(i).length; j < dimension; j++)
					d[j] = 0.0;
				points.set(i, d);
			}
		}
	}

	// kmeans process
	public List<Double[]> run() throws IOException {
		setKRandomPoints();
		
		// until objective function does not converge... do
		clusterMapping();
		
		// loop while convergence of objective function not satisfied
		// calculate the new centroid points
		Double[] newCentroid = new Double[dimension];
		int pointsCounter = 0;
		double sum = 0.0;
		boolean isStopCondition = false;
		while (!isStopCondition) {
			for (int clusterID = 0; clusterID < clustersMapping.length; clusterID++) {
				List<Integer> indexList = clustersMapping[clusterID];
				for (int d = 0; d < dimension; d++) {
					for (Integer clusterPoint : indexList) {
						sum += points.get(clusterPoint)[d];
						pointsCounter++;
					}
					newCentroid[d] = sum / pointsCounter;
					sum = 0;
					pointsCounter = 0;
				}
				if (!compare(clustersCentroids.get(clusterID), newCentroid)) {
					clustersCentroids.set(clusterID, newCentroid);
					isStopCondition = false;
				} else {
					isStopCondition = true;
				}
				newCentroid = new Double[dimension];
			}
			clusterMapping();
		}
		BufferedWriter writer = null;
        try {
    	    writer = new BufferedWriter(new FileWriter("/Users/xwang/Documents/workspace/SLDA/dictionary.txt"));
    	    for(int i = 0; i < clustersCentroids.size(); i++) {
    	    	for(int j = 0; j < clustersCentroids.get(i).length; j++)
    		        writer.write(clustersCentroids.get(i)[j] + "\t");
    	    	writer.write("\n");
    	    }
        } catch(IOException e) {
    	    e.printStackTrace();
        } finally {
            writer.close();
        }
		return clustersCentroids;
	}

	protected boolean compare(Double[] p1, Double[] p2) {
		for (int i = 0; i < dimension; i++) {
			if(p1[i].doubleValue() != p2[i].doubleValue()) 
				return false;
		}
		return true;
	}

	/*
	 * euclidean distance between points: sqrt(sum(xi^2-cj^2))
	 */
	public static double euclideanDistance(Double[] x, Double[] c) {

		double sum = 0.0;
		double distance = 0.0;

		for (int n = 0; n < Math.min(x.length, c.length); n++) 
			sum += Math.pow(x[n] - c[n], 2);
		distance = Math.sqrt(Math.abs(sum));
        return distance;
	}
	
	/*
	 * match the points to the nearest cluster centroid
	 */
	@SuppressWarnings("unchecked")
	public void clusterMapping() {
		
		clustersMapping = (List<Integer>[]) new ArrayList[clustersCentroids.size()];
		for(int i = 0; i < clustersMapping.length; i++)
			clustersMapping[i] = new ArrayList<Integer> ();
		for(int i = 0; i < points.size(); i++) {
			double min = Double.MAX_VALUE;
			int clusterk = 0;
			for(int j = 0; j < clustersCentroids.size(); j++) {
			    double distance = euclideanDistance(points.get(i), clustersCentroids.get(j));
			    if(distance < min) {
			    	min = distance;
			    	clusterk = j;
			    }
			}
			clustersMapping[clusterk].add(i);
		}
	}

	/*
	 * randomly set k points as the initial centroids
	 */
	protected void setKRandomPoints() {
		
		int[] array = new int[points.size()];
		for(int i = 0; i < array.length; i++) 
			array[i] = i;
		Random ran = new Random();
		int count = points.size();
		for(int i = 0; i < kClusters; i++) {
			int n = ran.nextInt(count);
			clustersCentroids.add(points.get(array[i]));
			count--;
			int temp = array[count];
			array[count] = array[n];
			array[n] = temp;
		}
	}
}