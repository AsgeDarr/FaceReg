import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class ExtraMatch {
	private static String EF_CACHE = "";
	public boolean buildImageCache = true; 
	public boolean buildImageManipulations = false; 
	public static String TRAINING_DIR = "trainingImages";
	private static final String EIGENFACES_PREFIX = "eigen_";
	private static final String FILE_EXT = ".png";

	public ExtraMatch(){

	}

	public void buildEigenFacesPatterns() throws IOException{



		//Get images
		ArrayList<String> images = getTrainingFnms(); 
		if(buildImageManipulations){
			imageManipulation(images);
		}

		//Name cache
		//build facebundles
		if(buildImageCache){
			createCache("topLeft");
			createCache("topRight");
			createCache("top");
			createCache("bottom");
			createCache("left");
			createCache("right");
		}


		//make function to run faceRecogMulti med hvert bundle
	}


	public void createCache(String name){
		EF_CACHE = "EF_CACHE_images" + name;
		TRAINING_DIR = "multiBundle/" + name + "/";
		ArrayList<String> imagesTopLeft = getTrainingFnms(); 
		build(imagesTopLeft, 22, "multiBundle/eigen/" + name + "/");


	}

	public void imageManipulation(ArrayList<String> imgDirs) throws IOException{
		BufferedImage[] ims = FileUtils.loadTrainingIms(imgDirs);

		for(int i = 0; i < ims.length; i++){

			BufferedImage imgTopLeft = ims[i].getSubimage(0, 0, ims[i].getWidth()/2, ims[i].getHeight()/2);
			BufferedImage imgTopRight = ims[i].getSubimage(ims[i].getWidth()/2, 0, ims[i].getWidth()/2, ims[i].getHeight()/2);
			BufferedImage imgTop = ims[i].getSubimage(0, 0, ims[i].getWidth(), ims[i].getHeight()/2);
			BufferedImage imgBottom = ims[i].getSubimage(0,  ims[i].getHeight()/2, ims[i].getWidth(), ims[i].getHeight()/2);
			BufferedImage imgLeft = ims[i].getSubimage(0, 0, ims[i].getWidth()/2, ims[i].getHeight());
			BufferedImage imgRight= ims[i].getSubimage(ims[i].getWidth()/2, 0, ims[i].getWidth()/2, ims[i].getHeight());





			ImageIO.write(imgTopLeft, "png", new File("multiBundle/topLeft/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
			ImageIO.write(imgTopRight, "png", new File("multiBundle/topRight/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
			ImageIO.write(imgTop, "png", new File("multiBundle/top/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
			ImageIO.write(imgBottom, "png", new File("multiBundle/bottom/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
			ImageIO.write(imgLeft, "png", new File("multiBundle/left/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
			ImageIO.write(imgRight, "png", new File("multiBundle/right/" + imgDirs.get(i).substring(imgDirs.get(i).lastIndexOf("/") + 1)));
		}

		System.out.println("DONE");

	}

	public static void build(ArrayList<String> fnms, int numEFs, String EIGENFACES_DIR)
	// create a FaceBundle for the specified number of eigenfaces, and store it
	{
		int numIms = fnms.size();
		if ((numEFs < 1) || (numEFs >= numIms)) {
			System.out.println("Number of eigenfaces must be in range (1-" + (numIms-1) + ")" +
					"; using " + (numIms-1));
			numEFs = numIms-1;
		}
		else
			System.out.println("Number of eigenfaces: " + numEFs);

		FaceBundle bundle = makeBundle(fnms, EIGENFACES_DIR);
		writeCache(bundle);
		reconstructIms(numEFs, bundle);  // optional: rebuild the original images from the bundle
	}


	public static void writeCache(FaceBundle bundle)
	// save the FaceBundle object in a file called EF_CACHE
	{
		System.out.println("Saving eigenfaces to: " + EF_CACHE + " ...");
		try {
			ObjectOutputStream oos = new ObjectOutputStream( 
					new FileOutputStream("multiBundle/multiCache/" + EF_CACHE) );
			oos.writeObject(bundle);
			System.out.println("Cache save succeeded");
			oos.close();
		}
		catch (Exception e) {
			System.out.println("Cache save failed");
			System.out.println(e);
		}
	} // end of writeCache()


	//--------------- GET IMAGES -----------------
	public static ArrayList<String> getTrainingFnms()
	// return all the names of the training image files + their paths
	{
		File dirF = new File(TRAINING_DIR);
		String[] fnms = dirF.list( new FilenameFilter() {
			public boolean accept(File f, String name)
			{  return name.endsWith(".png"); }
		});

		if (fnms == null) {
			System.out.println(TRAINING_DIR + " not found");
			return null;
		}
		else if (fnms.length == 0) {
			System.out.println(TRAINING_DIR + " contains no " + " " + ".png" + " files");
			return null;
		}
		else
			return getPathNms(fnms);
	}  // end of getTrainingFnms()

	private static ArrayList<String> getPathNms(String[] fnms)
	{
		ArrayList<String> imFnms = new ArrayList<String>();
		for (String fnm : fnms)
			imFnms.add(TRAINING_DIR + File.separator + fnm);

		Collections.sort(imFnms);
		return imFnms;
	}  // end of getPathNms()








	// -------------- NEEDED FUNCTIONS ---------- 
	private static FaceBundle makeBundle(ArrayList<String> fnms, String EIGENFACES_DIR)
	// create eigenvectors/eigenvalue bundle for the specified training image filenames;
	// also save each eigenface (eigenvector) as an image file
	{
		BufferedImage[] ims = FileUtils.loadTrainingIms(fnms);


		Matrix2D imsMat = convertToNormMat(ims);   // each row is a normalized image
		double[] avgImage = imsMat.getAverageOfEachColumn();

		//CHANGING


		imsMat.subtractMean();   // subtract mean face from each image (row)
		// each row now contains only distinguishing features from a training image 

		// calculate covariance matrix
		Matrix2D imsDataTr = imsMat.transpose();
		Matrix2D covarMat = imsMat.multiply(imsDataTr);

		// calculate Eigenvalues and Eigenvectors for covariance matrix
		EigenvalueDecomp egValDecomp = covarMat.getEigenvalueDecomp();
		double[] egVals = egValDecomp.getEigenValues();
		double[][] egVecs = egValDecomp.getEigenVectors();

		sortEigenInfo(egVals, egVecs);   // sort Eigenvectos and Eigenvariables

		Matrix2D egFaces = getNormEgFaces(imsMat, new Matrix2D(egVecs));

		System.out.println("\nSaving Eigenfaces as images...");
		saveEFIms(egFaces, ims[0].getWidth(), EIGENFACES_DIR );
		System.out.println("Saving done\n");

		return new FaceBundle(fnms, imsMat.toArray(), avgImage,
				egFaces.toArray(), egVals, ims[0].getWidth(), ims[0].getHeight());
	}  

	private static Matrix2D convertToNormMat(BufferedImage[] ims)
	/* convert array of  images into a matrix; each row is an image
     and the number of columns is the number of pixels in the image.
     The array is normalized.
	 */
	{
		int imWidth = ims[0].getWidth();
		int imHeight = ims[0].getHeight();

		int numRows = ims.length;
		int numCols = imWidth * imHeight;
		double[][] data = new double[numRows][numCols];
		for (int i = 0; i < numRows; i++)
			ims[i].getData().getPixels(0, 0, imWidth, imHeight, data[i]);    // one image per row

		Matrix2D imsMat = new Matrix2D(data);
		imsMat.normalise();
		return imsMat;
	}		// end of convertToNormMat()




	private static Matrix2D getNormEgFaces(Matrix2D imsMat, Matrix2D egVecs)
	/* calculate normalized Eigenfaces for the training images by multiplying the 
     eigenvectors to the training images matrix */

	{
		Matrix2D egVecsTr = egVecs.transpose();
		Matrix2D egFaces = egVecsTr.multiply(imsMat);
		double[][] egFacesData = egFaces.toArray();

		for (int row = 0; row < egFacesData.length; row++) {
			double norm = Matrix2D.norm(egFacesData[row]);   // get normal
			for (int col = 0; col < egFacesData[row].length; col++)
				egFacesData[row][col] = egFacesData[row][col]/norm;
		}
		return new Matrix2D(egFacesData);
	}  // end of getNormEgFaces()

	private static void sortEigenInfo(double[] egVals, double[][] egVecs)
	/* sort the Eigenvalues and Eigenvectors arrays into descending order
     by eigenvalue. Add them to a table so the sorting of the values adjusts the
     corresponding vectors
	 */
	{
		Double[] egDvals = getEgValsAsDoubles(egVals);

		// create table whose key == eigenvalue; value == eigenvector
		Hashtable<Double, double[]> table = new Hashtable<Double, double[]>();
		for (int i = 0; i < egDvals.length; i++)
			table.put( egDvals[i], getColumn(egVecs, i) );     

		ArrayList<Double> sortedKeyList = sortKeysDescending(table);
		updateEgVecs(egVecs, table, egDvals, sortedKeyList);
		// use the sorted key list to update the Eigenvectors array

		// convert the sorted key list into an array
		Double[] sortedKeys = new Double[sortedKeyList.size()];
		sortedKeyList.toArray(sortedKeys); 

		// use the sorted keys array to update the Eigenvalues array
		for (int i = 0; i < sortedKeys.length; i++)
			egVals[i] = sortedKeys[i].doubleValue();

	}  // end of sortEigenInfo()



	private static Double[] getEgValsAsDoubles(double[] egVals)
	// convert double Eigenvalues to Double objects, suitable for Hashtable keys
	{  
		Double[] egDvals = new Double[egVals.length];
		for (int i = 0; i < egVals.length; i++)
			egDvals[i] = new Double(egVals[i]);
		return egDvals;
	}  // end of getEgValsAsDoubles()



	private static double[] getColumn(double[][] vecs, int col)
	/* the Eigenvectors array is in column order (one vector per column);
     return the vector in column col */
	{
		double[] res = new double[vecs.length];
		for (int i = 0; i < vecs.length; i++)
			res[i] = vecs[i][col];
		return res;
	}  // end of getColumn()



	private static ArrayList<Double> sortKeysDescending(
			Hashtable<Double,double[]> table)
	// sort the keylist part of the hashtable into descending order
	{
		ArrayList<Double> keyList = Collections.list( table.keys() );
		Collections.sort(keyList, Collections.reverseOrder()); // largest first
		return keyList;
	}  // end of sortKeysDescending()



	private static void updateEgVecs(double[][] egVecs,
			Hashtable<Double, double[]> table, 
			Double[] egDvals, ArrayList<Double> sortedKeyList)
	/* get vectors from the table in descending order of sorted key,
     and update the original vectors array */
	{ 
		for (int col = 0; col < egDvals.length; col++) {
			double[] egVec = table.get(sortedKeyList.get(col));
			for (int row = 0; row < egVec.length; row++) 
				egVecs[row][col] = egVec[row];
		}
	}  // end of updateEgVecs()

	// ---------- reconstruction of images from eigenfaces ------------------


	private static void reconstructIms(int numEFs, FaceBundle bundle)
	{
		System.out.println("\nReconstructing training images...");

		Matrix2D egFacesMat = new Matrix2D( bundle.getEigenFaces() );
		Matrix2D egFacesSubMat = egFacesMat.getSubMatrix(numEFs);

		Matrix2D egValsMat = new Matrix2D(bundle.getEigenValues(), 1);
		Matrix2D egValsSubMat = egValsMat.transpose().getSubMatrix(numEFs);

		double[][] weights = bundle.calcWeights(numEFs);
		double[][] normImgs = getNormImages(weights, egFacesSubMat, egValsSubMat);
		// the mean-subtracted (normalized) training images
		double[][] origImages = addAvgImage(normImgs, bundle.getAvgImage() );  
		// original training images = normalized images + average image

		FileUtils.saveReconIms2(origImages, bundle.getImageWidth()); 
		System.out.println("Reconstruction done\n");
	}  // end of reconstructIms()



	private static double[][] getNormImages(double[][] weights, 
			Matrix2D egFacesSubMat, Matrix2D egValsSubMat)
	/* calculate weights x eigenfaces, which generates mean-normalized traimning images;
	     there is one image per row in the returned array
	 */
	{
		double[] egDValsSub = egValsSubMat.flatten();
		Matrix2D tempEvalsMat = new Matrix2D(weights.length, egDValsSub.length);
		tempEvalsMat.replaceRowsWithArray(egDValsSub);

		Matrix2D tempMat = new Matrix2D(weights);
		tempMat.multiplyElementWise(tempEvalsMat);

		Matrix2D normImgsMat = tempMat.multiply(egFacesSubMat);
		return normImgsMat.toArray();
	}  // end of getNormImages()



	private static double[][] addAvgImage(double[][] normImgs, double[] avgImage)
	// add the average image to each normalized image (each row) and store in a new array;
	// the result are the original training images; one per row
	{
		double[][] origImages = new double[normImgs.length][normImgs[0].length];
		for (int i = 0; i < normImgs.length; i++) {
			for (int j = 0; j < normImgs[i].length; j++)
				origImages[i][j] = normImgs[i][j] + avgImage[j];
		}
		return origImages;
	}  // end of addAvgImage()

	// ------------------ save EigenFaces as images -------------------


	public static void saveEFIms(Matrix2D egfaces, int imWidth, String EIGENFACES_DIR)
	/* save each row of the eigenfaces matrix as an image in EIGENFACES_DIR, 
	     whose pixel width is imWidth */
	{
		double[][] egFacesArr = egfaces.toArray();
		makeDirectory(EIGENFACES_DIR);

		for (int row = 0; row < egFacesArr.length; row++) {
			String fnm = EIGENFACES_DIR + File.separator + EIGENFACES_PREFIX + row + FILE_EXT;

			saveArrAsImage(fnm, egFacesArr[row], imWidth);
		}
	}  // end of saveEFIms()

	private static void makeDirectory(String dir)
	// create a new directory or delete the contents of an existing one
	{
		File dirF = new File(dir);
		if (dirF.isDirectory()) {
			System.out.println("Directory: " + dir + " already exists; deleting its contents");
			for (File f : dirF.listFiles())
				deleteFile(f);
		}
		else {
			dirF.mkdir();
			System.out.println("Created new directory: " + dir);
		}
	}  // end of makeDirectory()

	private static void saveArrAsImage(String fnm, double[] imData, int width)
	// save a ID array as an image
	{
		BufferedImage im = ImageUtils.createImFromArr(imData, width);
		if (im != null) {
			try {
				ImageIO.write(im, "png", new File(fnm));
				System.out.println("  " + fnm);    // saving 
			}
			catch (Exception e) {
				System.out.println("Could not save image to " + fnm);
			}
		}
	}  // end of saveArrAsImage()

	private static void deleteFile(File f)
	{
		if (f.isFile()) {
			boolean deleted = f.delete();
			/* if(deleted)
	        System.out.println("  deleted: "+ f.getName() );
			 */
		}
	}	 // end of deleteFile()



}
