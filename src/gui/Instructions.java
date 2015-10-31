package gui;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.awt.Font;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Instructions extends JPanel {
	
	private JTextField title;
	private JTextArea body;
	private String mainContent; 

	/**
	 * Create the panel.
	 */
	public Instructions() {
		setPreferredSize(new Dimension(500, 500));
		setLayout(new BorderLayout(10, 10));
		setBorder(new EmptyBorder(10,20,10,20));
		
		mainContent = "\nTo count the number of objects in an image you must first convert\n"
				+ "the image to a binary image.\n"
				+ "When binarising an image the a threshold must be set. The threshold\n"
				+ "determines what colurs will be determined as white, and what colours\n"
				+ "will be determined as black.\n"
				+ "The default threshold is 128 but this can be changed using the slider\n"
				+ "on the left\n"
				+ "The number of objects in the image will be display on the right hand side\n"
				+ "of the menu bar\n"
				+ "To colour the objects, select the Colour option.\n"
				+ "To highlight the object, select the Highlight Option\n";
		
		title = new JTextField("Instructions");
		title.setAlignmentY(SwingConstants.CENTER);
		title.setBackground(new Color(240,240,240));
		title.setForeground(Color.RED);
		title.setFont(new Font("Tahoma", Font.PLAIN, 20));
		add(title, BorderLayout.NORTH);
		title.setEditable(false);
		title.setBorder(null);
		
		body = new JTextArea();
		body.setBackground(new Color(240,240,240));
		body.setAlignmentY(CENTER_ALIGNMENT);
		body.setText(mainContent);
		body.setEditable(false);
		add(body, BorderLayout.CENTER);
		
	}
}
