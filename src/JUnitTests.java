import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class JUnitTests {

	//	String message = "Hello World";	
	//	MessageUtil messageUtil = new MessageUtil(message);
	//
	//	@Test
	//	public void testPrintMessage() {	  
	//		assertEquals(message,messageUtil.printMessage());
	//	}


	//	-------------------- ManuelMatching Class Tests --------------------
	ManuelMatching ManuelMatchingTest = new ManuelMatching();

	
	@Test
	public void testSaveImt() throws IOException {	
		String title = "testPNG";
		BufferedImage img = ImageIO.read(new File("03_img_resize.png"));
		ManuelMatchingTest.saveImt(title, img);
		
		File file = new File("testPNG.png");
		assertTrue(file.exists());
	}

	@Test
	public void testSaveArray() throws IOException {	
		String title = "testArrayCSV";
		double[] array = new double[2];
		ManuelMatchingTest.saveArray(title, array);
		
		File file = new File("testArrayCSV.csv");
		
//		assertEquals(2,file.length());
		assertTrue(file.exists());
	}
	
	@Test
	public void testSaveMatrix() throws IOException {	
		String title = "testMatrixCSV";
		Matrix2D matrix = new Matrix2D(2, 2);
		
		ManuelMatchingTest.saveMatrix(title, matrix);
		
		File file = new File("testMatrixCSV.csv");
//		Matrix2D imMat = new Matrix2D(imArr, 1);
		
		assertTrue(file.exists());
	}
	
	@Test
	public void testFindMatch() throws IOException{
		BufferedImage img = ImageIO.read(new File("JUnitFace.png"));
		ManuelMatchingTest.findMatch(img);
		assertTrue(new File("01_imgArray.csv").exists());
		assertTrue(new File("02_1D_Matrix.csv").exists());
		assertTrue(new File("03_Normalised_1D_Matrix.csv").exists());
		assertTrue(new File("04_Sub_Mean_1D_Matrix.csv").exists());
		assertTrue(new File("05_Weights_Matrix.csv").exists());
		assertTrue(new File("06_Distance_Array.csv").exists());
	}
	
}
