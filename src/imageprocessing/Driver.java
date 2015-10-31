package imageprocessing;

import edu.princeton.cs.introcs.StdOut;


public class Driver {
	
	public static void main(String[] args) {
		
		//create instance of ComponentImage using a chosen image
		ComponentImage componentImage = new ComponentImage("images/shapes.bmp");
		componentImage.getPicture().show();
		StdOut.println("Components: " + componentImage.countComponents());
		//componentImage.binaryComponentImage().show();
		//componentImage.colourComponentImage().show();
		//componentImage.highlightComponentImage().show();
		
	}
}
