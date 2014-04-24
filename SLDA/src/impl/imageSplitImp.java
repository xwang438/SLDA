package impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import api.imageSplit;
/*
 * split a image into 8 * 8 chunks, get the centers for the documents
 */
public class imageSplitImp implements imageSplit {
	int rows;
	int cols;
	int chunks;
	int chunkWidth;
	int chunkHeight;
	BufferedImage image = null;
	public imageSplitImp() {
		rows = 0;
		cols = 0;
		chunks = 0;
		chunkWidth = 0;
		chunkHeight = 0;
	}
	public int getWidth() {
		return chunkWidth;
	}
	public int getHeight() {
		return chunkHeight;
	}
	
	/*
	 * input:
	 *     String: file name
	 *     int: segmented row number
	 *     int: segmented col number
	 * output: 
	 *     List<Double[]> : center positions for the image segments
	 */
	public List<Double[]> split(String name, int row, int col) throws IOException {
		File file = new File(name); 
        FileInputStream fis = new FileInputStream(file);  
        image = ImageIO.read(fis); //reading the image file
        List<Double[]> list = new ArrayList<Double[]> ();
        rows = row; 
        cols = col;  
        chunks = rows * cols;  
  
        chunkWidth = image.getWidth() / cols; // determines the chunk width and height  
        chunkHeight = image.getHeight() / rows;  

        int count = 0;  
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks  
        for (int x = 0; x < rows; x++) {  
            for (int y = 0; y < cols; y++) {  
                //Initialize the image array with image chunks  
                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());  
  
                // draws the image chunk  
                Graphics2D gr = imgs[count++].createGraphics();  
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x, chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);  
                gr.dispose();  
                Double[] pos = {(double)chunkWidth * ((double)(y) + 0.5), (double)chunkHeight * ((double)(x) + 0.5)};
                list.add(pos);
            }  
        }  
        System.out.println("Splitting done");  
      //writing mini images into image files  
        for (int i = 0; i < imgs.length; i++) {  
            ImageIO.write(imgs[i], "jpg", new File("/Users/xwang/Documents/workspace/SLDA/imagesplit/" + name.substring(name.length() - 7) + i + ".jpg"));  
        }  
        System.out.println("Mini images created"); 
        return list;
	}
	/*
	 * split the images in a image folder
	 */
	public List<Double[]> splitFolder(String imageFolderLocation, int row, int col) throws IOException {
		File folder = new File(imageFolderLocation);
		File[] files = folder.listFiles();
		List<Double[]> list = new ArrayList<Double[]> ();
		for (File file : files) {
	     	if (file.isFile() && file.getName().substring(file.getName().length() - 4).equals(".jpg")) {
	        	  String name = imageFolderLocation + file.getName();
			      List<Double[]> sublist = split(name, row, col);
			      list.addAll(sublist);
			}
	    }
		return list;
	}
}
