package impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GibbsSampling {
	
	List<Integer> doc[];//word index array
	int V, K, M;//vocabulary size, topic number, document number
	int [][] z;//topic label array
	float alpha; //doc-topic dirichlet prior parameter 
	float beta; //topic-word dirichlet prior parameter
	int [][] nmk;//given document m, count times of topic k. M*K
	int [][] nkt;//given topic k, count times of term t. K*V
	int [] nmkSum;//Sum for each row in nmk
	int [] nktSum;//Sum for each row in nkt
	double [][] phi;//Parameters for topic-word distribution K*V
	double [][] theta;//Parameters for doc-topic distribution M*K
	int iterations;//Times of iterations
	int saveStep;//The number of iterations between two saving
	int beginSaveIters;//Begin save model at this iteration
	int splitNum = 64;

	public GibbsSampling() {
		alpha = 0.5f;
		beta = 0.1f;
		iterations = 100;
		K = 15;
		saveStep = 10;
		beginSaveIters = 50;
	}

	@SuppressWarnings("unchecked")
	public void initializeModel(String imageFolderLocation, int row, int col) throws IOException {
		imageSplitImp is = new imageSplitImp();
		List<Double[]> split = is.splitFolder(imageFolderLocation, row, col);
		M = split.size();
	    codebook cb = new codebook();
	    List<Double[]> descriptors = cb.getSIFT(imageFolderLocation) ;
	    List<Double[]> dict = cb.kmeans(descriptors);
	    List<List<Double[]>> features = cb.getFeature();
	    V = dict.size();
		nmk = new int [M][K];
		nkt = new int[K][V];
		nmkSum = new int[M];
		nktSum = new int[K];
		phi = new double[K][V];
		theta = new double[M][K];

		//initialize documents index array
		doc = new ArrayList [M];
		File folder = new File(imageFolderLocation);
	    File[] listOfFiles = folder.listFiles();
	    imageWordsimp iw = new imageWordsimp();
	    List<Integer[]> words = iw.matchWords(imageFolderLocation, dict, features);
		for(int m = 0; m < M; m++){
			doc[m] = new ArrayList<Integer> ();
			File f = listOfFiles[m / splitNum];
			int splitCount = m % splitNum;
			double rowleft = splitCount / row * is.getHeight();
			double colleft = splitCount % col * is.getWidth();
			SIFTfeatureImp sift = new SIFTfeatureImp();
			List<Double[]> keypoints = sift.siftKeyPoints(imageFolderLocation + f.getName());
			for(int i = 0; i < keypoints.size(); i++) {
				if(keypoints.get(i)[0] >= rowleft && keypoints.get(i)[0] <= rowleft + is.getHeight() && keypoints.get(i)[1] >= colleft && keypoints.get(i)[1] <= colleft + is.getWidth())
					doc[m].add(words.get(m)[i]);
			}
		}

		//initialize topic label z for each word
		z = new int[M][];
		for(int m = 0; m < M; m++){
			int N = doc[m].size();
			z[m] = new int[N];
			for(int n = 0; n < N; n++){
				int initTopic = (int)(Math.random() * K);// From 0 to K - 1
				z[m][n] = initTopic;
				//number of words in doc m assigned to topic initTopic add 1
				nmk[m][initTopic]++;
				//number of terms doc[m][n] assigned to topic initTopic add 1
				nkt[initTopic][doc[m].get(n)]++;
					// total number of words assigned to topic initTopic add 1
					nktSum[initTopic]++;
			}
			 // total number of words in document m is N
			nmkSum[m] = N;
		}
	}
	
	public void inferenceModel(String savePath) throws IOException {
		if(iterations < saveStep + beginSaveIters){
			System.err.println("Error: the number of iterations should be larger than " + (saveStep + beginSaveIters));
			System.exit(0);
		}
		for(int i = 0; i < iterations; i++){
			System.out.println("Iteration " + i);
			if((i >= beginSaveIters) && (((i - beginSaveIters) % saveStep) == 0)){
				//Saving the model
				System.out.println("Saving model at iteration " + i +" ... ");
				//Firstly update parameters
				updateEstimatedParameters();
				//Secondly print model variables
				saveIteratedModel(i, savePath);
			}

			//Use Gibbs Sampling to update z[][]
			for(int m = 0; m < M; m++){
				int N = doc[m].size();
				for(int n = 0; n < N; n++){
					// Sample from p(z_i|z_-i, w)
					int newTopic = sampleTopicZ(m, n);
					z[m][n] = newTopic;
				}
			}
		}
	}

	private void updateEstimatedParameters() {
		for(int k = 0; k < K; k++){
			for(int t = 0; t < V; t++){
				phi[k][t] = (nkt[k][t] + beta) / (nktSum[k] + V * beta);
			}
		}

		for(int m = 0; m < M; m++){
			for(int k = 0; k < K; k++){
				theta[m][k] = (nmk[m][k] + alpha) / (nmkSum[m] + K * alpha);
			}
		}
	}

	private int sampleTopicZ(int m, int n) {
		// Sample from p(z_i|z_-i, w) using Gibbs update rule
		//Remove topic label for w_{m,n}
		int oldTopic = z[m][n];
		nmk[m][oldTopic]--;
		nkt[oldTopic][doc[m].get(n)]--;
		nmkSum[m]--;
		nktSum[oldTopic]--;

		//Compute p(z_i = k|z_-i, w)
		double [] p = new double[K];
		for(int k = 0; k < K; k++){
			p[k] = (nkt[k][doc[m].get(n)] + beta) / (nktSum[k] + V * beta) * (nmk[m][k] + alpha) / (nmkSum[m] + K * alpha);
		}

		//Sample a new topic label for w_{m, n} like roulette
		//Compute cumulated probability for p
		for(int k = 1; k < K; k++){
			p[k] += p[k - 1];
		}
		double u = Math.random() * p[K - 1]; //p[] is unnormalised
		int newTopic;
		for(newTopic = 0; newTopic < K; newTopic++){
			if(u < p[newTopic]){
				break;
			}
		}

		//Add new topic label for w_{m, n}
		nmk[m][newTopic]++;
		nkt[newTopic][doc[m].get(n)]++;
		nmkSum[m]++;
		nktSum[newTopic]++;
		return newTopic;
	}

	public void saveIteratedModel(int iters, String savePath) throws IOException {
		//lda.phi K*V
		BufferedWriter writer = new BufferedWriter(new FileWriter(savePath + ".phi"));		
		for (int i = 0; i < K; i++){
			for (int j = 0; j < V; j++){
				writer.write(phi[i][j] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		//lda.theta M*K
		writer = new BufferedWriter(new FileWriter(savePath + ".theta"));
		for(int i = 0; i < M; i++){
			for(int j = 0; j < K; j++){
				writer.write(theta[i][j] + "\t");
			}
			writer.write("\n");
		}
		writer.close();

		//lda.tassign
		writer = new BufferedWriter(new FileWriter(savePath + ".tassign"));
		for(int m = 0; m < M; m++){
			for(int n = 0; n < doc[m].size(); n++){
				writer.write(doc[m].get(n) + ":" + z[m][n] + "\t");
			}
				writer.write("\n");
		}
		writer.close();
	}
}
