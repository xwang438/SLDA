package impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;
public class visualPage extends JPanel {
	
	/**
	 * draw images based on cluster id
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<JButton> buttons = new ArrayList<JButton> ();
	public List<String>[] list;
	private JLabel[] imageicon;
	DefaultListModel model; 
    JFrame frame=new JFrame("Cluster");
    JScrollPane p2scroller = null;
    JScrollPane p1scroller = null;
    
	/**
	 *This is a constructor which handles the layout of the page.
	 */
	public visualPage(String imageFolderLocation) throws IOException {
		list = visual(imageFolderLocation);		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1000,1000));

		JPanel p2=new JPanel();
		p2.setLayout(new GridLayout(4,4));
		p2.setPreferredSize(new Dimension(300,300));
		for(int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setSize(4, 4);
			p2.add(buttons.get(i));
		    buttons.get(i).addActionListener(new listenPress());
		}
		p2scroller = new JScrollPane(p2);  
		p2scroller.setPreferredSize(new Dimension(100, 100));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(p2scroller,BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		for(int i = 0; i < list[0].size(); i++)
		    System.out.println(list[0].get(i));
	}

	/**
	 * Given the image folder location, get the list of the image names for each cluster id.
	 * For each cluster id, get the images that contain a descriptor that can match to the cluster
	 */
	@SuppressWarnings("unchecked")
	public List<String>[] visual(String imageFolderLocation) throws IOException {
		codebook cb = new codebook();
		List<Double[]> sift = cb.getSIFT(imageFolderLocation);
	    System.out.println("sift is done!");
	    List<Double[]> dict = cb.kmeans(sift);
	    System.out.println("kmeans is done!");
	    imageWordsimp iw = new imageWordsimp();
		List<Integer[]> words = iw.matchWords(imageFolderLocation, dict, cb.getFeature());
	    System.out.println("match words is done!");
		List<String> filename = iw.getNames(imageFolderLocation, dict, cb.getFeature());
		List<String>[] images = new ArrayList [dict.size()];
		for(int i = 0; i < dict.size(); i++) {
			images[i] = new ArrayList<String> ();
			for(int j = 0; j < words.size(); j++) {
				for(int k = 0; k < words.get(j).length; k++) {
					if(words.get(j)[k] == i) {
						images[i].add(filename.get(j));
						break;
					}
				}
			}
			JButton button = new JButton(String.valueOf(i));
			buttons.add(button);
		}
		return images;
	}
	
	private class listenPress implements ActionListener
	{
	    public void actionPerformed(ActionEvent event)
	    {
	        for(int i = 0; i < buttons.size(); i++) {
	        	if(event.getSource() == buttons.get(i)) {
	        		frame.getContentPane().removeAll();
	        		imageicon = new JLabel[list[i].size()];
	        		
	        		JPanel p1=new JPanel();
	        		p1.setPreferredSize(new Dimension(1500,10000)); 
	        		for(int j = 0; j < list[i].size(); j++) {
	        		    String IMG_FILE_PATH = list[i].get(j);
	        		    BufferedImage myPicture = null;
						try {
							myPicture = ImageIO.read(new File(IMG_FILE_PATH));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						imageicon[j] = new JLabel(new ImageIcon(myPicture));
	        		    Dimension size = new Dimension(myPicture.getWidth(), myPicture.getHeight());
	        		    imageicon[j].setSize(size);
	        		    p1.add(imageicon[j]);
	        		}
	        		JScrollPane p1scroller = new JScrollPane(p1,  
	                        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,  
	                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS); 
	        		p1scroller.setPreferredSize(new Dimension(1000, 600));
	        		frame.add(p1scroller,BorderLayout.NORTH);
	        		frame.add(p2scroller,BorderLayout.SOUTH);
	        		frame.pack();
	        		frame.setVisible(true);
	        		frame.validate();
	        		frame.repaint();
	        	}
	        }
	    }
	}
}
