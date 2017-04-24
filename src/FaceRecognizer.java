
// FaceRecognizer.java
// Andrew Davison, April 2011, ad@fivedots.psu.ac.th

/* Show a sequence of images snapped from a webcam in a picture panel (FaceRecogPanel). 
   A face is highlighted with a yellow rectangle, which is updated as the face
   moves. The highlighted part of the image can be recognized by the user pressing
   the "Recognize Face" button.

   Usage:
      > java FaceRecognizer
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;

import org.bytedeco.javacv.*;
import org.bytedeco.javacpp.*;
import org.bytedeco.javacpp.videoInputLib.videoInput;

import java.net.URL;



public class FaceRecognizer extends JFrame 
{

	// GUI components
	private FaceRecogPanel facePanel;
	private JButton recogBut;
	private JTextField nameField;   // where the name (and distance info) appears
	
	//BAChanges - fields for searching in logs
	private JTextField searchField;
	private JButton searchButton;
	LogSearch logSearch = new LogSearch();

	public FaceRecognizer()
	{

		super("Face Recognizer");


		Container c = getContentPane();
		c.setLayout( new BorderLayout() );   

		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);

		facePanel = new FaceRecogPanel(this); // the sequence of pictures appear here
		c.add( facePanel, BorderLayout.CENTER);

		//BAChange - button to search for people in the log
		searchButton = new JButton("Search Person");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			searchButton.setEnabled(false);
			try {
				logSearch.setLogSearch(searchField.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			searchButton.setEnabled(true);
			}
		});

		// button for recognizing a highlighted face
		recogBut = new JButton("Recognize Face");
		recogBut.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{ nameField.setText("");
			recogBut.setEnabled(false);
			facePanel.setRecog();
			}
		});

		nameField = new JTextField(20);   // for the name of the recognized face
		nameField.setEditable(false);

		searchField = new JTextField(8);   // for the name of the recognized face
		searchField.setEditable(true);
		
		JPanel p = new JPanel();
		p.add(searchButton);
		p.add(searchField);
		p.add(recogBut);
		p.add( new JLabel("Name: "));
		p.add( nameField);
		c.add(p, BorderLayout.SOUTH);


		addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{ facePanel.closeDown();    // stop snapping pics
			System.exit(0);
			}
		});

		pack();  
		setResizable(false);
		setVisible(true);
	} // end of FaceRecognizer()



	public void setRecogName(final String faceName, final String dist)
	// update face name and its distance in the nameField; called from panel
	{ 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() 
			{  nameField.setText( faceName + " (" + dist + ")"); 
			recogBut.setEnabled(true);
			}
		});
	}  // end of setRecogName()


	// -------------------------------------------------------

	public static void main( String args[] )
	{ 
		new FaceRecognizer();  
		
		
//		//BAChanges - manuel udregning af afstand
//		BufferedImage img = null;
//		try {
//		    img = ImageIO.read(new File("03_img_resize.png"));
//		} catch (IOException e) {
//		}
//		
//		ManuelMatching ManuelCalc = new ManuelMatching();
//		try {
//			ManuelCalc.findMatch(img);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	

} // end of FaceRecognizer class
