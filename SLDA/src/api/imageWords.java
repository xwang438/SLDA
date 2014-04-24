package api;
import java.io.IOException;
import java.util.List;

/*
 * Given the image folder location and centers of the clusters, match the dictionary words for the images
 */
public interface imageWords {

	List<Integer[]> matchWords(String imageFolderLocation, List<Double[]> centroids, List<List<Double[]>> features) throws IOException;
	Integer[] match(List<Double[]> centroids, List<Double[]> sublist) throws IOException;
	List<String> getNames(String imageFolderLocation, List<Double[]> centroids, List<List<Double[]>> features) throws IOException;
}
