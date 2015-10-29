package gui;

import imageprocessing.ComponentImage;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javafx.scene.layout.Border;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import edu.princeton.cs.introcs.Picture;
import edu.princeton.cs.introcs.StdOut;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import java.awt.BorderLayout;

public class Interface implements ChangeListener{

	private JFrame frame;
	private JFileChooser fc;
	//private Picture pic;
	private JPanel imgPanel;
	private JMenuBar menuBar;
	private JMenuItem selectImage;
	private JMenuItem binarise;
	private JMenuItem colour;
	private JMenuItem highlight;
	private JLabel count;
	private JSlider slider;
	
	private ComponentImage ci;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface window = new Interface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//TODO check id (0,0) is necessary
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		//Set maximum size to current screen resolution
		//To avoid displaying windows larger than screen
		//frame.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setResizable(true);
		
		imgPanel = new JPanel();
		imgPanel.setLayout(new BorderLayout());
//		JScrollPane scroll = new JScrollPane(imgPanel, 
//				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
//				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		frame.getContentPane().add(scroll, BorderLayout.CENTER);
		frame.getContentPane().add(imgPanel, BorderLayout.CENTER);
		
		slider = new JSlider(JSlider.VERTICAL, 	0, 255, 128);
		slider.setMinorTickSpacing(8);
		slider.setMajorTickSpacing(32);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		frame.getContentPane().add(slider, BorderLayout.LINE_START);
		slider.addChangeListener(this);
		slider.setVisible(false);
		
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		fc = new JFileChooser();
		
		selectImage = new JMenuItem("Choose an Image");
		menuBar.add(selectImage);
		selectImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				fc.showOpenDialog(frame);
				
				if (fc.getSelectedFile() != null) {
					
					File file = fc.getSelectedFile();
					try {
						ci = new ComponentImage(new Picture(file));
						displayImage(ci.getPicture());
						showOptions();
						frame.pack();
						//adjustWindowSize();
					} catch (Exception e1) {
						StdOut.println("Invalid file type");
					}
				}
			}	
		});
		
		
		binarise = new JMenuItem("Binarise Image");
		menuBar.add(binarise);
		binarise.setVisible(false);
		binarise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayImage(ci.binaryComponentImage());
				count.setText("Objects: " + ci.countComponents());
			}
		});
		
		colour = new JMenuItem("Colour Object");
		menuBar.add(colour);
		colour.setVisible(false);
		colour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayImage(ci.colourComponentImage());
			}
		});
		
		highlight = new JMenuItem("Highlight Objects");
		menuBar.add(highlight);
		highlight.setVisible(false);
		highlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayImage(ci.highlightComponentImage());
			}
		});
		
		count = new JLabel("Objects");
		menuBar.add(count);
		count.setVisible(false);
		menuBar.add(Box.createHorizontalGlue());
	}
	
	
	/**
	 * Displays an image in the interface window
	 * @param picture The Picture object to be displayed
	 */
	private void displayImage(Picture picture) {
//		imgPanel.removeAll();
//		imgPanel.repaint();
//		//Maybe add layout to centre picture
//		imgPanel.add(picture.getJLabel());
//		imgPanel.validate();
		
		imgPanel.removeAll();
		imgPanel.repaint();
		JLabel imgLabel = picture.getJLabel();
		imgPanel.add(imgLabel);
		
		//Add scroll to accomodate large images
		JScrollPane scroll = new JScrollPane(imgLabel,  
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		adjustWindowSize();
		
		imgPanel.add(scroll, BorderLayout.CENTER);
		imgPanel.validate();
		
		
	}

	
	/* 
	 * Change Listener for Slider that controls threshold
	 * 
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		
		
		//For smaller images (65536 pixels or less eg. 256x256 for square pic)
		//Adjust binary image and count objects as slider moves
		if (ci.getPicture().height() * ci.getPicture().width() <= 65536) {
			ci.setThreshold(source.getValue());
			StdOut.println("Threshold is " + ci.getThreshold());
			displayImage(ci.binaryComponentImage());
			count.setText("Objects: " + ci.countComponents());
		//For large images binarising and counting takes longer
		//Therefore, only binarise and count after slider has settled
		} else {
			if(!source.getValueIsAdjusting()) {
				ci.setThreshold(source.getValue());
				StdOut.println("Threshold is " + ci.getThreshold());
				displayImage(ci.binaryComponentImage());
				count.setText("Objects: " + ci.countComponents());
			}
		}
		
	}

	/**
	 * Method maximises he window in the screen without overlapping the taskbar
	 * 
	 * This method was created using information leanred from the following sources
	 * 
	 * http://stackoverflow.com/questions/479523/java-swing-maximize-window
	 * http://stackoverflow.com/questions/10123735/get-effective-screen-size-from-java
	 * http://stackoverflow.com/questions/9536804/setsize-doesnt-work-for-jframe
	 */
	private void adjustWindowSize() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		//Only maximise the window if the picture is larger than the screen
		if ( ci.getPicture().height() >= screenSize.height 
				|| ci.getPicture().width() >= screenSize.width) {
		
			GraphicsConfiguration config = frame.getGraphicsConfiguration();
			Insets insets  = Toolkit.getDefaultToolkit().getScreenInsets(config);
			
			screenSize.height = screenSize.height - insets.top - insets.bottom;
			screenSize.width = screenSize.width - insets.left - insets.right;
			
			frame.setLocation(insets.left, insets.top);
			frame.setPreferredSize(screenSize);
			
		//If a smaller image is chosen reset the frame to default
		//This ensures that if a small image is selected after a large image
		//the frame will pack again and not stay at the maximised size
		} else {
			frame.setLocation(100, 100);
			frame.setPreferredSize(null);
		}
	}
	
	/**
	 * Sets the options and controls visible
	 * Options and controls should only be visible
	 * after a file has been selected
	 */
	private void showOptions() {
		binarise.setVisible(true);
		colour.setVisible(true);
		highlight.setVisible(true);
		count.setVisible(true);
		slider.setVisible(true);
	}
}
