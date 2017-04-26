import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;


//Function createde to save images and make calculations in a manuel recreation of match calculation 

public class ManuelMatching {
	private static final float FACES_FRAC = 0.75f;   
	// default fraction of eigenfaces used in a match

	private FaceBundle bundle = null;
	private double[][] weights = null;    // training image weights
	private int numEFs = 0;     // number of eigenfaces to be used in the recognition



	public void saveImt(String title, BufferedImage img){
		File outputfile = new File(title + ".png");
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveArray(String title, double[] array) throws IOException{

		FileWriter writer = new FileWriter(title + ".csv");
		for(int i = 0; i < array.length; i++)
		{
			writer.append(array[i]+"");
			writer.append(",");
			
		}
		writer.close();
	}

	public void saveMatrix(String title, Matrix2D Matrix) throws IOException{
		FileWriter writer = new FileWriter(title + ".csv");
		for(int i = 0; i <=Matrix.columns()-1; i++)
		{
			for(int j = 0;j<=Matrix.rows()-1;j++){
				writer.append(Matrix.getQuick(j, i)+" ");
				writer.append(",");
			}
			writer.append("\n");

		}
		writer.close();
	}

	// ----------------- Pieced together from from FaceRecognition in JavaFaces 2 -----------------

	public ManuelMatching()
	{  this(22); }


	public ManuelMatching(int numEigenFaces)
	{
		bundle = FileUtils.readCache();
		if (bundle == null) {
			System.out.println("You must build an Eigenfaces cache before any matching");
			System.exit(1);
		}

		int numFaces = bundle.getNumEigenFaces();
		// System.out.println("No of eigenFaces: " + numFaces);

		numEFs = numEigenFaces;
		if ((numEFs < 1) || (numEFs > numFaces-1)) {
			numEFs = Math.round((numFaces-1)*FACES_FRAC);     // set to less than max
			System.out.println("Number of matching eigenfaces must be in the range (1-" + 
					(numFaces-1) + ")" + "; using " + numEFs);
		}
		else
			System.out.println("Number of eigenfaces: " + numEFs);

		weights = bundle.calcWeights(numEFs);
	}  // end of FaceRecognition()



	public MatchResult findMatch(BufferedImage im) throws IOException
	{
		double[] imArr = ImageUtils.createArrFromIm(im);    // change image into an array
		saveArray("01_imgArray", imArr);

		// convert array to normalized 1D matrix
		Matrix2D imMat = new Matrix2D(imArr, 1);
		saveMatrix("02_1D_Matrix",imMat);


		imMat.normalise();
		saveMatrix("03_Normalised_1D_Matrix",imMat);
		
		imMat.subtract(new Matrix2D(bundle.getAvgImage(), 1));  // subtract mean image
		saveMatrix("04_Sub_Mean_1D_Matrix",imMat);
		
		Matrix2D imWeights = getImageWeights(numEFs, imMat); 
		saveMatrix("05_Weights_Matrix",imMat);
		// map image into eigenspace, returning its coordinates (weights);
		// limit mapping to use only numEFs eigenfaces

		double[] dists = getDists(imWeights);
		saveArray("06_Distance_Array", dists);
		
		ImageDistanceInfo distInfo = getMinDistInfo(dists);
		System.out.println("min Distinfo " + distInfo.getValue());
		// find smallest Euclidian distance between image and training image

		ArrayList<String> imageFNms = bundle.getImageFnms();
		String matchingFNm = imageFNms.get( distInfo.getIndex() );
		System.out.println("matchingFNm : " + matchingFNm);
		// get the training image filename that is closest 

		double minDist = Math.sqrt( distInfo.getValue() );
		System.out.println("minDist " + minDist);

		return new MatchResult(matchingFNm, minDist);
	} // end of findMatch()


	private Matrix2D getImageWeights(int numEFs, Matrix2D imMat)
	/* map image onto numEFs eigenfaces returning its weights 
	     (i.e. its coordinates in eigenspace)
	 */
	{
		Matrix2D egFacesMat = new Matrix2D( bundle.getEigenFaces() );
		Matrix2D egFacesMatPart = egFacesMat.getSubMatrix(numEFs);
		Matrix2D egFacesMatPartTr = egFacesMatPart.transpose();

		return imMat.multiply(egFacesMatPartTr);
	}  // end of getImageWeights()



	private double[] getDists(Matrix2D imWeights)
	/* return an array of the sum of the squared Euclidian distance
	     between the input image weights and all the training image weights */
	{
		Matrix2D tempWt = new Matrix2D(weights);   // training image weights
		double[] wts = imWeights.flatten();

		tempWt.subtractFromEachRow(wts);
		tempWt.multiplyElementWise(tempWt);
		double[][] sqrWDiffs = tempWt.toArray();
		double[] dists = new double[sqrWDiffs.length];

		for (int row = 0; row < sqrWDiffs.length; row++) {
			double sum = 0.0;
			for (int col = 0; col < sqrWDiffs[0].length; col++)
				sum += sqrWDiffs[row][col];
			dists[row] = sum;
		}
		return dists;
	}  // end of getDists()



	private ImageDistanceInfo getMinDistInfo(double[] dists)
	{
		double minDist = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < dists.length; i++)
			if (dists[i] < minDist) {
				minDist = dists[i];
				index = i;
			}
		return new ImageDistanceInfo(dists[index], index);
	}	  // end of getMinDistInfo()



}



