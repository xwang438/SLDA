package api;

import java.io.IOException;
import java.util.List;

public interface initialDocDesign {
	void initialArray(String imageFileLocation, List<Double[]> dict, int row, int co, List<Double[]> feature) throws IOException;
	List<Integer> [] getDesc();
	List<Double[]> [] getKeys();
}
