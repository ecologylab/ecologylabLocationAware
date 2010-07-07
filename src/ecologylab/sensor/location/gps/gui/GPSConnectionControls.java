/**
 * 
 */
package ecologylab.sensor.location.gps.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Spring;
import javax.swing.SpringLayout;

import ecologylab.sensor.location.NMEAReader;
import ecologylab.sensor.location.gps.gui.strings.GPSStatusStrings;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class GPSConnectionControls extends JPanel implements ActionListener, GPSStatusStrings
{
	class CommPortIdentifierRenderer extends JLabel implements ListCellRenderer
	{
		private static final long	serialVersionUID	= 1131516153469419203L;

		public CommPortIdentifierRenderer()
		{
			setOpaque(true);
			setHorizontalAlignment(LEFT);
			setVerticalAlignment(CENTER);
		}

		/**
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
		 *      boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			// Get the selected index. (The index param isn't
			// always valid, so just use the value.)
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText(((CommPortIdentifier) value).getName());

			return this;
		}

	}

	private static final long		serialVersionUID	= 2352632177790522459L;

	private static final String	CONNECT_GPS			= "connect";

	private static final String	DISCONNECT_GPS		= "disconnect";

	/**
	 * Aligns the first <code>rows</code> * <code>cols</code> components of <code>parent</code> in a grid. Each
	 * component in a column is as wide as the maximum preferred width of the components in that column; height is
	 * similarly determined for each row. The parent is made just big enough to fit them all.
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
	public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad,
			int yPad)
	{
		SpringLayout layout;
		try
		{
			layout = (SpringLayout) parent.getLayout();
		}
		catch (ClassCastException exc)
		{
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++)
		{
			Spring width = Spring.constant(0);
			for (int r = 0; r < rows; r++)
			{
				width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
			}
			for (int r = 0; r < rows; r++)
			{
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
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
				height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
			}
			for (int c = 0; c < cols; c++)
			{
				SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
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
   private static SpringLayout.Constraints getConstraintsForCell(
                                               int row, int col,
                                               Container parent,
                                               int cols) {
       SpringLayout layout = (SpringLayout) parent.getLayout();
       Component c = parent.getComponent(row * cols + col);
       return layout.getConstraints(c);
   }

	private final Integer[]			baudRates			=
																	{ 115200, 57600, 38400, 19200, 9600, 4800, 2400, 1200 };

	private GPSController	projVis;

	private final JLabel				statusLabel;

	private final JButton			connectButton;

	private final JComboBox			baudRateSelector;

	private final JComboBox			portNameSelector;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked") public GPSConnectionControls(GPSController projVis)
	{
		statusLabel = new JLabel();
		connectButton = new JButton(CONNECT_GPS);
		baudRateSelector = new JComboBox(baudRates);

		Enumeration<CommPortIdentifier> commPortsEnum = CommPortIdentifier.getPortIdentifiers();

		ArrayList<CommPortIdentifier> commPorts = new ArrayList<CommPortIdentifier>();

		while (commPortsEnum.hasMoreElements())
		{
			commPorts.add(commPortsEnum.nextElement());
		}

		portNameSelector = new JComboBox(commPorts.toArray());

		initializeGUI();

		this.projVis = projVis;

		NMEAReader currentGPS = this.projVis.getGps();

		if (currentGPS == null || !currentGPS.connected())
		{
			this.setStatus(DISCONNECTED);
		}
		else
		{
			this.setStatus(CONNECTED, currentGPS.getPortName(), currentGPS.getBaudRate());
		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if (command == CONNECT_GPS)
		{
			CommPortIdentifier selectedPort = (CommPortIdentifier) this.portNameSelector.getSelectedItem();
			Integer selectedBaudRate = (Integer) this.baudRateSelector.getSelectedItem();

			if (selectedPort != null)
			{
				try
				{
					if (this.projVis.connectGPS(selectedPort, selectedBaudRate))
					{
						this.setStatus(CONNECTED, selectedPort.getName(), selectedBaudRate);
					}
					else
					{
						this.setStatus(IO_ERROR, selectedPort.getName(), selectedBaudRate);
					}
				}
				catch (PortInUseException e1)
				{
					e1.printStackTrace();

					this.setStatus(PORT_IN_USE, selectedPort.getName(), selectedBaudRate);
				}
				catch (UnsupportedCommOperationException e1)
				{
					e1.printStackTrace();

					this.setStatus(UNSUPPORTED_OP, selectedPort.getName(), selectedBaudRate);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();

					this.setStatus(IO_ERROR, selectedPort.getName(), selectedBaudRate);
				}
				catch (TooManyListenersException e1)
				{
					e1.printStackTrace();

					this.setStatus(PORT_IN_USE, selectedPort.getName(), selectedBaudRate);
				}
				catch (NoSuchPortException e1)
				{
					e1.printStackTrace();

					this.setStatus(NO_SUCH_PORT, selectedPort.getName(), selectedBaudRate);
				}
			}
		}
		else if (command == DISCONNECT_GPS)
		{
			projVis.disconnectGPS();
			
			this.setStatus(DISCONNECTED);
		}
	}

	private JPanel initForm()
	{
		// Create and populate the panel.
		JPanel p = new JPanel(new SpringLayout());

		JLabel l = new JLabel("port: ", JLabel.TRAILING);
		p.add(l);
		l.setLabelFor(this.portNameSelector);
		p.add(this.portNameSelector);

		l = new JLabel("baud rate: ", JLabel.TRAILING);
		p.add(l);
		l.setLabelFor(this.baudRateSelector);
		p.add(this.baudRateSelector);

		// Lay out the panel.
		makeCompactGrid(p, 2, 2, // rows, cols
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
		connectButton.setActionCommand(CONNECT_GPS);

		portNameSelector.setRenderer(new CommPortIdentifierRenderer());

		this.add(initForm());
		
		this.add(this.connectButton);
		
		this.add(this.statusLabel);
		
		connectButton.addActionListener(this);
	}

	/**
	 * @param disconnected
	 */
	private void setStatus(String status)
	{
		this.setStatus(status, null, -1);
	}
	
	/**
	 * @param connected
	 * @param portName
	 * @param baudRateSelector
	 */
	private void setStatus(String status, String portName, int baudRate)
	{
		if (status == CONNECTED)
		{
			this.statusLabel.setText(status + " - " + portName + "@" + baudRate);

			this.connectButton.setText(DISCONNECT_GPS);
			this.connectButton.setActionCommand(DISCONNECT_GPS);
		}
		else if (status == DISCONNECTED)
		{
			this.statusLabel.setText(status);

			this.connectButton.setText(CONNECT_GPS);
			this.connectButton.setActionCommand(CONNECT_GPS);
		}
		else
		{
			this.statusLabel.setText(CONNECTION_FAILED
					+ (portName != null && baudRate != -1 ? " " + portName + "@" + baudRate : "") + ": " + status);
		}
	}

}
