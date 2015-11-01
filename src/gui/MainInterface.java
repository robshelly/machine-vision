package gui;

import imageprocessing.ComponentImage;
import imageprocessing.Luminance;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import edu.princeton.cs.introcs.Picture;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.BorderLayout;

public class MainInterface implements ActionListener, ChangeListener {

	private JFrame frame;
	private JPanel imgPanel;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem selectImage;
	private JMenuItem resetImage;
	private JMenuItem help;
	private JMenuItem exit;
	private JLabel count;
	private JSlider slider;
	private ButtonGroup group;
	private JMenu process;
	private JMenu highlightMenu;
	private JRadioButtonMenuItem oriImg;
	private JRadioButtonMenuItem binImg;
	private JRadioButtonMenuItem colImg;
	private JCheckBoxMenuItem checkHighlight;

	private String currentMode;
	private String objectsInfo;
	private ComponentImage ci;

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainInterface window = new MainInterface();
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
	public MainInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 700, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(10, 0));
		frame.setResizable(true);

		imgPanel = new JPanel();
		imgPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(imgPanel, BorderLayout.CENTER);

		slider = new JSlider(JSlider.VERTICAL, 0, 256, 128);
		slider.setMinorTickSpacing(8);
		slider.setMajorTickSpacing(32);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		frame.getContentPane().add(slider, BorderLayout.LINE_START);
		slider.addChangeListener(this);
		slider.setEnabled(false);

		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		// Menu Submenu
		menu = new JMenu("Menu");
		menuBar.add(menu);

		selectImage = new JMenuItem("Choose an Image");
		menu.add(selectImage);
		selectImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});

		resetImage = new JMenuItem("Reset Image");
		menu.add(resetImage);
		resetImage.setEnabled(false);
		resetImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});

		help = new JMenuItem("Help");
		menu.add(help);
		help.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel instructions = new Instructions();
				JOptionPane.showMessageDialog(frame, instructions,
						"Instructions", JOptionPane.INFORMATION_MESSAGE);

			}
		});

		menu.addSeparator();

		exit = new JMenuItem("Exit");
		menu.add(exit);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Image Processing Submenu
		process = new JMenu("Process Image");
		process.setEnabled(false);
		menuBar.add(process);

		group = new ButtonGroup();

		oriImg = new JRadioButtonMenuItem("Original Image");
		oriImg.setSelected(true);
		oriImg.setActionCommand("original");
		oriImg.addActionListener(this);
		group.add(oriImg);
		process.add(oriImg);

		binImg = new JRadioButtonMenuItem("Binary Image");
		binImg.setActionCommand("binary");
		binImg.addActionListener(this);
		group.add(binImg);
		process.add(binImg);

		colImg = new JRadioButtonMenuItem("Coloured Image");
		colImg.setActionCommand("colour");
		colImg.addActionListener(this);
		group.add(colImg);
		process.add(colImg);

		// Highlight Submenu
		highlightMenu = new JMenu("Highlight Objects");
		highlightMenu.setEnabled(false);
		menuBar.add(highlightMenu);

		checkHighlight = new JCheckBoxMenuItem("Highlight");
		checkHighlight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				displayImage();
			}
		});
		highlightMenu.add(checkHighlight);

		//Object count label on menubar
		menuBar.add(Box.createHorizontalGlue());
		count = new JLabel("Objects:     ", SwingConstants.RIGHT);
		//Set the width of the label to avoid it changing when
		//the number of digits in the count changes
		count.setPreferredSize(new Dimension(100, 10));
		count.setBorder(new EmptyBorder(0, 10, 0, 10));
		menuBar.add(count);
		
		

		// Set currentMode to original image
		currentMode = "original";
		// Set the object info to say no image has been processed yet
		objectsInfo = "No Image Processed";
		count.setToolTipText(objectsInfo);
		
		// Always select a file on initialisation
		selectFile();

	}

	/**
	 * Displays an image in the interface window
	 */
	private void displayImage() {

		imgPanel.removeAll();
		imgPanel.repaint();

		JLabel imgLabel;
		// Check which mode the user has selected i.e. original, binary or
		// colour
		// And display the appropriate image
		// Also check is the user has selected highlight object
		// and display image accordingly
		if (currentMode == "binary") {
			if (!checkHighlight.isSelected()) {
				imgLabel = ci.binaryComponentImage().getJLabel();
			} else {
				imgLabel = ci
						.highlightComponentImage(ci.binaryComponentImage())
						.getJLabel();
			}

		} else if (currentMode == "colour") {
			if (!checkHighlight.isSelected()) {
				imgLabel = ci.colourComponentImage().getJLabel();
			} else {
				imgLabel = ci
						.highlightComponentImage(ci.colourComponentImage())
						.getJLabel();
			}

		} else {
			if (!checkHighlight.isSelected()) {
				imgLabel = ci.getPicture().getJLabel();
			} else {
				imgLabel = ci.highlightComponentImage(ci.getPicture())
						.getJLabel();
			}
		}

		imgPanel.add(imgLabel);

		// Add scroll to accommodate large images
		JScrollPane scroll = new JScrollPane(imgLabel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		imgPanel.add(scroll, BorderLayout.CENTER);
		imgPanel.validate();
		
		//display the object count
		displayObjectCount();
	}

	/*
	 * Change Listener for Slider that controls threshold
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
	 * )
	 */
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();

		// For smaller images (65536 pixels or less eg. 256x256 for square pic)
		// Adjust binary image and count objects as slider moves
		if (ci.getPicture().height() * ci.getPicture().width() <= 65536) {
			ci.setThreshold(source.getValue());
			displayImage();

			// For large images binarising and counting takes longer
			// Therefore, only binarise and count after slider has settled
		} else {
			if (!source.getValueIsAdjusting()) {
				ci.setThreshold(source.getValue());
				displayImage();
			}
		}
	}

	/**
	 * Method maximises he window in the screen without overlapping the taskbar
	 * 
	 * This method was created using information learned from the following
	 * sources
	 * 
	 * http://stackoverflow.com/questions/479523/java-swing-maximize-window
	 * http:
	 * //stackoverflow.com/questions/10123735/get-effective-screen-size-from
	 * -java http://stackoverflow.com/questions/9536804/setsize-doesnt-work-for-
	 * jframe
	 */
	private void adjustWindowSize() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// Only maximise the window if the picture is larger than the screen
		if (ci.getPicture().height() >= screenSize.height
				|| ci.getPicture().width() >= screenSize.width) {

			GraphicsConfiguration config = frame.getGraphicsConfiguration();
			Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

			screenSize.height = screenSize.height - insets.top - insets.bottom;
			screenSize.width = screenSize.width - insets.left - insets.right;

			frame.setLocation(insets.left, insets.top);
			frame.setPreferredSize(screenSize);

			// If a smaller image is chosen reset the frame to default
			// This ensures that if a small image is selected after a large
			// image
			// the frame will pack again and not stay at the maximised size
		} else {
			frame.setLocation(100, 100);
			frame.setPreferredSize(null);
		}
	}

	/**
	 * Sets the options and controls visible Options and controls should only be
	 * visible after a file has been selected
	 */
	private void showOptions() {
		resetImage.setEnabled(true);
		process.setEnabled(true);
		highlightMenu.setEnabled(true);
		slider.setEnabled(true);
	}

	/**
	 * Selects an image using FileChooser Uses the image to create an instance
	 * of ComponentImage
	 */
	private void selectFile() {
		FileSelector fs = new FileSelector();
		Picture pic = fs.selectFile(frame);
		if (pic != null) {
			ci = new ComponentImage(pic, slider.getValue());
			// New image uploaded so reset to default modes
			reset();
			showOptions();
			frame.pack();
		}
	}

	/**
	 * Displays the number of objects in the image
	 * 
	 * Also sets the ToolTipText to explain the nature of objects ie dark object
	 * on bright background or bright objects on dark background
	 */
	private void displayObjectCount() {

		int n = ci.countComponents();
		count.setText("Objects: " + n);

		if (Luminance.lum(ci.binaryComponentImage().get(0, 0)) == 0) {
			objectsInfo = "There are " + n
					+ " bright objects on a dark background";
		} else {
			objectsInfo = "There are " + n
					+ " dark objects on a bright background";
		}
		count.setToolTipText(objectsInfo);
	}

	/**
	 * Resets operation to default modes i.e. Show original image, highlight
	 * unchecked and object count cleared
	 */
	private void reset() {
		checkHighlight.setSelected(false);
		group.setSelected(oriImg.getModel(), true);
		currentMode = "original";
		count.setText("Objects:    ");
		count.setToolTipText("No Image Processed");
		displayImage();
		adjustWindowSize();
	}

	/*
	 * Action Listener for Image Processing radio buttons group (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		currentMode = e.getActionCommand();
		displayImage();
	}
}
