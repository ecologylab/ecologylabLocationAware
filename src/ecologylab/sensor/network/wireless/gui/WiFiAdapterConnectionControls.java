/**
 * 
 */
package ecologylab.sensor.network.wireless.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import stec.jenie.NativeException;
import ecologylab.sensor.network.wireless.WiFiAdapter;
import ecologylab.sensor.network.wireless.gui.strings.WiFiStatusStrings;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 */
public class WiFiAdapterConnectionControls extends JPanel implements
		ActionListener, WiFiStatusStrings
{
	private static final long		serialVersionUID	= 2352632177790522459L;

	private static final String	CONNECT_WIFI		= "connect";

	private static final String	DISCONNECT_WIFI	= "disconnect";

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code> components of
	 * <code>parent</code> in a grid. Each component in a column is as wide as
	 * the maximum preferred width of the components in that column; height is
	 * similarly determined for each row. The parent is made just big enough to
	 * fit them all.
	 * 
	 * @author Sun Microsystems -
	 *         http://java.sun.com/docs/books/tutorial/uiswing/examples/layout/SpringFormProject/src/layout/SpringUtilities.java
	 * 
	 * @param rows
	 *           number of rows
	 * @param cols
	 *           number of columns
	 * @param initialX
	 *           x location to start the grid at
	 * @param initialY
	 *           y location to start the grid at
	 * @param xPad
	 *           x padding between cells
	 * @param yPad
	 *           y padding between cells
	 */
	public static void makeCompactGrid(Container parent, int rows, int cols,
			int initialX, int initialY, int xPad, int yPad)
	{
		SpringLayout layout;
		try
		{
			layout = (SpringLayout) parent.getLayout();
		}
		catch (ClassCastException exc)
		{
			System.err
					.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++)
		{
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++)
			{
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols)
						.getWidth());
			}
			for (int r = 0; r < rows; r++)
			{
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c,
						parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++)
		{
			Spring height = Spring.constant(0);
			for (int c = 0; c < cols; c++)
			{
				height = Spring.max(height, getConstraintsForCell(r, c, parent,
						cols).getHeight());
			}
			for (int c = 0; c < cols; c++)
			{
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c,
						parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH, y);
		pCons.setConstraint(SpringLayout.EAST, x);
	}

	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(int row,
			int col, Container parent, int cols)
	{
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	private WiFiConnectionController	connController;

	private final JLabel					connectionStatusLabel;

	private final JButton				connectButton;

	private final Integer[]				updateIntervals	=
																		{ 100, 500, 1000, 2000,
			5000, 10000, 30000, 60000							};

	private final JComboBox				updateIntervalPulldown;

	/**
	 * 
	 */
	public WiFiAdapterConnectionControls(WiFiConnectionController connController)
	{
		connectionStatusLabel = new JLabel();

		connectButton = new JButton(CONNECT_WIFI);

		updateIntervalPulldown = new JComboBox(updateIntervals);
		updateIntervalPulldown.setSelectedIndex(2); // set to 1 second
		updateIntervalPulldown.addActionListener(this);
		
		initializeGUI();

		this.connController = connController;

		WiFiAdapter currentWiFi = this.connController.getWiFiAdapter();

		if (currentWiFi == null || !currentWiFi.connected())
		{
			this.setStatus(DISCONNECTED);
		}
		else
		{
			this.setStatus(CONNECTED);
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (this.updateIntervalPulldown == e.getSource())
		{
			 Integer interval = (Integer)updateIntervalPulldown.getSelectedItem();
			 
			 if (this.connController != null && this.connController.getWiFiAdapter() != null)
				 this.connController.getWiFiAdapter().setUpdateInterval(interval);
		}
		else
		{
			String command = e.getActionCommand();

			if (command == CONNECT_WIFI)
			{
				try
				{
					if (this.connController.connectWiFi())
					{
						this.setStatus(CONNECTED);
					}
					else
					{
						this.setStatus(DISCONNECTED);
					}
				}
				catch (NativeException e1)
				{
					this.setStatus(SOFTWARE_FAILURE);
				}
			}
			else if (command == DISCONNECT_WIFI)
			{
				connController.disconnectWiFi();

				this.setStatus(DISCONNECTED);
			}
		}
	}

	private JPanel initForm()
	{
		// Create and populate the panel.
		JPanel p = new JPanel(new SpringLayout());

		JLabel l = new JLabel("update rate (ms): ", JLabel.TRAILING);
		p.add(l);
		l.setLabelFor(this.updateIntervalPulldown);
		p.add(this.updateIntervalPulldown);

		// Lay out the panel.
		makeCompactGrid(p, 1, 2, // rows, cols
				6, 6, // initX, initY
				6, 6); // xPad, yPad

		return p;
	}

	/**
	 * Configure graphical components, and setup layout for this panel.
	 * 
	 * Called automatically by the constructor.
	 */
	private void initializeGUI()
	{
		connectButton.setActionCommand(CONNECT_WIFI);

		this.add(initForm());

		this.add(this.connectButton);

		this.add(this.connectionStatusLabel);

		connectButton.addActionListener(this);
	}

	/**
	 * @param connected
	 * @param portName
	 * @param baudRateSelector
	 */
	private void setStatus(String status)
	{
		if (status == CONNECTED)
		{
			this.connectionStatusLabel.setText(status);

			this.connectButton.setText(DISCONNECT_WIFI);
			this.connectButton.setActionCommand(DISCONNECT_WIFI);
		}
		else if (status == DISCONNECTED)
		{
			this.connectionStatusLabel.setText(status);

			this.connectButton.setText(CONNECT_WIFI);
			this.connectButton.setActionCommand(CONNECT_WIFI);
		}
		else
		{
			this.connectionStatusLabel.setText(status);
		}
	}
}