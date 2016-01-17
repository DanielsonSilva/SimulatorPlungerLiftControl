package view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import processing.Requisition;

public class ButtonsListener implements MouseListener {
	
	private Requisition requisition; // Object for points requisition
	private Thread t; // Thread for manipulate the Requisition

	public ButtonsListener() {
		requisition = new Requisition();
		t = new Thread(requisition);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JButton o = (JButton) e.getSource();
		int id = (int) o.getClientProperty("id");
		switch (id) {
			//Play
			case 1:
				play();
				break;
			//Pause
			case 2:
				pause();
				break;
			//Stop
			case 3:
				stop();
				break;
			//First Cycle
			case 4:
				firstCycle();
				break;
			//Previous Cycle
			case 5:
				previousCycle();
				break;
			//Next Cycle
			case 6:
				nextCycle();
				break;
			//Last Cycle
			case 7:
				lastCycle();
				break;
			//Configuration
			case 8:
				configuration();
				break;
			//Error
			default:
				JOptionPane.showMessageDialog(null, "ButtonError");
				break;				
		}
	}

	private void configuration() {
		// TODO Auto-generated method stub
		
	}

	private void lastCycle() {
		// TODO Auto-generated method stub
		
	}

	private void nextCycle() {
		// TODO Auto-generated method stub
		
	}

	private void previousCycle() {
		// TODO Auto-generated method stub
		
	}

	private void firstCycle() {
		// TODO Auto-generated method stub
		
	}

	private void stop() {
		// TODO Auto-generated method stub
		
	}

	private void pause() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Performs the action for the Play Button
	 * Starts the Requisition object for capturing the data from Simulation
	 */
	private void play() {
		requisition.setStop(false);
		t.start();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}