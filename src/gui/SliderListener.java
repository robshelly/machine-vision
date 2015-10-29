package gui;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.princeton.cs.introcs.StdOut;

public class SliderListener implements ChangeListener{

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider source = (JSlider)e.getSource();
		int threshold = source.getValue();
		StdOut.println("Threshold : " + threshold);
		
		
	}

}
