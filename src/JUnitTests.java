import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.imageio.ImageIO;

public class JUnitTests {

	//	String message = "Hello World";	
	//	MessageUtil messageUtil = new MessageUtil(message);
	//
	//	@Test
	//	public void testPrintMessage() {	  
	//		assertEquals(message,messageUtil.printMessage());
	//	}
	
//	syso
//	@Before
//	    System.setOut(new PrintStream(outMessage));
//	    System.setErr(new PrintStream(errMessage));
//
//		assertEquals("hello", outMessage.toString());
	
//	@After
//	    System.setOut(null);
//	    System.setErr(null);
//	
//		assertEquals("hello again", errMessage.toString());
	
	

	private final ByteArrayOutputStream outMessage = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errMessage = new ByteArrayOutputStream();
	
//		-------------------- ManuelMatching Class Tests --------------------
	ManuelMatching ManuelMatchingTest = new ManuelMatching(22);

	
	@Test
	public void testSaveImt() throws IOException {	
		String title = "testPNG";
		BufferedImage img = ImageIO.read(new File("manuelMatchFiles/03_img_resize.png"));
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
		assertTrue(new File("manuelMatchFiles/01_imgArray.csv").exists());
		assertTrue(new File("manuelMatchFiles/02_1D_Matrix.csv").exists());
		assertTrue(new File("manuelMatchFiles/03_Normalised_1D_Matrix.csv").exists());
		assertTrue(new File("manuelMatchFiles/04_Sub_Mean_1D_Matrix.csv").exists());
		assertTrue(new File("manuelMatchFiles/05_Weights_Matrix.csv").exists());
		assertTrue(new File("manuelMatchFiles/06_Distance_Array.csv").exists());
		
	}
	
	
//	@Test
//	public void testZeroNumEigenFaces() throws IOException{
//		
//		System.setErr(new PrintStream(errMessage));
//		ManuelMatching ManuelMatchingTestError = new ManuelMatching(0);
//		String errString = ("Number of matching eigenfaces must be in the range (1-" 
//		+ (0-1) + ")" + "; using " + 0);
//		assertEquals(errString,errMessage.toString());
//		System.setErr(null);
//	}
	
//	-------------------- Logging --------------------
	@Test
	public void testLogValues(){
		LogValues logValuesTest = new LogValues("TestTime", "TestMatch", 10.0);
		assertEquals("TestTime",logValuesTest.getTime());
		assertEquals("TestMatch",logValuesTest.getMatch());
		assertTrue(10.0 == logValuesTest.getDistance());
		logValuesTest.setDistance(20.0);
		logValuesTest.setMatch("NewTestMatch");
		logValuesTest.setTime("NewTestTime");
		assertEquals("NewTestTime",logValuesTest.getTime());
		assertEquals("NewTestMatch",logValuesTest.getMatch());
		assertTrue(20.0 == logValuesTest.getDistance());
		assertEquals("TimeStamp; NewTestTime; Match; NewTestMatch;Distance; 20.0;\n",
				logValuesTest.toString());
	}
	
	
	
	
	
	
	
	
	
	
}
