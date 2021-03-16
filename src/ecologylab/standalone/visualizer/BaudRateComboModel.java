/**
 * 
 */
package ecologylab.standalone.visualizer;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import ecologylab.generic.Debug;

/**
 * @author Z O. Toups (zach@ecologylab.net)
 *
 */
public class BaudRateComboModel extends Debug implements ComboBoxModel
{
	class BaudRateComboDyad
	{
		private int baudRate;
		private String identifier;
		
		BaudRateComboDyad(int baudRate, String identifier)
		{
			this.baudRate = baudRate;
			this.identifier = identifier;
		}
		
		
	}

	/**
	 * 
	 */
	public BaudRateComboModel()
	{
	}

	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object anItem)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l)
	{
		// TODO Auto-generated method stub

	}

}
