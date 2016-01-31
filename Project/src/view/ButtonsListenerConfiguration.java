/**
 * 
 */
package view;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author Danielson Flávio Xavier da Silva
 * Actions for the configuration window's buttons
 */
public class ButtonsListenerConfiguration implements MouseListener {

	Configuration config;
	public ButtonsListenerConfiguration (ResourceBundle messages, Configuration c) {
		this.config = c;
	}
		
	@Override
	public void mouseClicked(MouseEvent e) {
		JButton o = (JButton) e.getSource();
		int id = (int) o.getClientProperty("id");
		switch (id) {
			//Button OK
			case 1:
				Confirm();
				break;
			//Button Cancel
			case 2:
				Cancel();
				break;
			//Error
			default:
				JOptionPane.showMessageDialog(null, "ButtonError");
				break;				
		}
	}

	/**
	 * Closes the configuration window
	 */
	private void Cancel() {
		config.seteVisible(false);
	}

	/**
	 * Take the numbers to the InitialCondition
	 */
	private void Confirm() {
		double[] v = new double[42];
		int k = 0;
		for( JTextField f : config.getTexts() ) { 
			v[k] = Double.parseDouble(f.getText());
			k = k + 1;
		}
		InitialCondition initial = InitialCondition.getInstance();
		initial.Insert(v);
		config.seteVisible(false);
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
