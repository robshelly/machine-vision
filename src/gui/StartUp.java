package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import edu.princeton.cs.introcs.Picture;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StartUp {

	private JFrame frame;
	private JTextField title;
	private JTextField selectFile;
	private JButton browse;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartUp window = new StartUp();
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
	public StartUp() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		title = new JTextField("Computer Vision");
		title.setEditable(false);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBackground(new Color(240,240,240));
		title.setForeground(Color.BLUE);
		title.setFont(new Font("Tahoma", Font.PLAIN, 30));
		frame.getContentPane().add(title, BorderLayout.NORTH);
		
		JPanel instructions = new Instructions();
		frame.getContentPane().add(instructions, BorderLayout.CENTER);
		
		selectFile = new JTextField("Select an image to get started");
		selectFile.setBackground(new Color(240,240,240));
		selectFile.setBorder(null);
		selectFile.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				MainInterface.main();
				frame.dispose();
			}
		});
		
		JPanel south = new JPanel();
		south.setLayout(new BorderLayout(0, 0));
		south.setBorder(new EmptyBorder(10, 50, 20, 50));
		south.add(selectFile, BorderLayout.WEST);
		south.add(browse, BorderLayout.EAST);
		
		frame.getContentPane().add(south, BorderLayout.SOUTH);
		
		
	}

}
