package gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.princeton.cs.introcs.Picture;

public class FileSelector {
	
	private Picture pic;

	public Picture selectFile(JFrame frame) {
		
		JFileChooser fc = new JFileChooser();
		boolean validChoice = false;
		while (!validChoice) {
			//fc.showOpenDialog(frame);
	
			if (fc.showOpenDialog(frame) != JFileChooser.CANCEL_OPTION) {
				File file = fc.getSelectedFile();
				try {
					pic = new Picture(file);
					validChoice = true;
				} catch (Exception e) {
					//TODO not a picture dialog
					JOptionPane.showMessageDialog(frame, "Invalid File Type!"
							+ "\nPlease select an image file type", "Error", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				//This code will execute if the user clicks cancel
				// in the open file dialog box
				//In this case, avoid getting stuck in while loop
				// and avoid throwing an exception
				validChoice = true;
			}
		}
		return pic;	
	}
	
	
	
}
