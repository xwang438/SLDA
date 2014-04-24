1, The test images are in SLDA/image. The jpg files are for test. The txt files are the result files. 
Each txt file contains the index of the dictionary words in the image. 

2, The main funciton is in SLDA/src/impl/app.java

3, imagesmall is a small image set contains only 60 images for test.
   image_200 is a image set contains 200 images for test and used for demo.
   image_500 contains 500 images for test. It will run for a very long time


SLDA steps:

Input: Massive unlabeled image sets
Output: Segment objects from the images, classify images into different categories(recognition).

1, From the images, get the codebook(dictionary)
      a, Run SIFT on the images and get the SIFT descriptors for all the images
      b, Run KMeans clustering on the descriptors. Get the centroids for the clusters.
      c, In each image, decide which cluster id the SIFT descriptors belong to by euclidean distance. 
      d, Finally, each image is represented by an array of cluster ids.

2, Design of documents
      a, Each image is splited into 8 * 8 equal pieces. Each piece is a document. 
      b, Get the center points of the pieces to represent the documents.
      c, Initially assign the descriptors to the documents based on their location in the image.
      
* A key difference between SLDA and LDA is that in SLDA, the word-document assignment is a  hidden random variable.
  There is a generative procedure to assign words to documents. 

3, Gibbs Sampling process
      a, Assign the words to documents
      b, Label the topic for the words
      
