package view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import processing.Requisition;
import view.Principal;

public class ButtonsListenerPrincipal implements MouseListener {
	
	private Requisition requisition; // Object for points requisition
	private Thread t; // Thread for manipulate the Requisition
	private Configuration configuration; // Window of configuration
	private boolean isSuspended; // Boolean for suspension state
	private boolean isStoped; // Boolean for stop state
	ResourceBundle messagesButListenerPrincipal;

	public ButtonsListenerPrincipal(ResourceBundle messages) {
		requisition = new Requisition();
		t = new Thread(requisition);
		configuration = Configuration.getInstance(messages);
		messagesButListenerPrincipal = messages;
		isSuspended = false;
		isStoped = false;
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

	/**
	 * Opens the configuration window
	 */
	private void configuration() {
		configuration.setVisible(true);
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
		requisition.setStop(true);
		isSuspended = false;
		isStoped = true;
		t.interrupt();
		Principal.getInstance(messagesButListenerPrincipal).reboot();
	}

	@SuppressWarnings("deprecation")
	private void pause() {
		// TODO Auto-generated method stub
		requisition.pauseSimulation();
		t.suspend();
		isSuspended = true;
		isStoped = false;
	}

	/**
	 * Performs the action for the Play Button
	 * Starts the Requisition object for capturing the data from Simulation
	 */
	@SuppressWarnings("deprecation")
	private void play() {
		requisition.setStop(false);
		if ( this.isSuspended == false ) {
			requisition.setVariables(InitialCondition.getInstance().getVariables());
			requisition.passVariablesToSimulation();
			if ( isStoped == false ) {
				t.start();				
			}
			else {
				t.notify();
				requisition.notifySimulation();
			}
			isStoped = false;
		}
		else {
			requisition.resumeSimulation();
			t.resume();
			isSuspended = false;
		}
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