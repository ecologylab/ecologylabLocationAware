package ecologylab.sensor.network.wireless2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class WifiStatusViewer extends JFrame
{
	private class WifiUpdater implements WifiListener
	{

		public void onConnect()
		{
			setTitle("Connected");
			ssidLabel.setText("ssid: " + WifiUtils.getSSID());
			bssidLabel.setText("bssid: " + WifiUtils.getBSSID());
			ipaddrLabel.setText("ip-addr: " + WifiUtils.getAddress());
			signalQualityBar.setValue(WifiUtils.getQuality());
			signalQualityBar.setString(WifiUtils.getQuality() + "%");
			signalStrengthBar.setValue(WifiUtils.getRSSIPercentage());
			signalStrengthBar.setString(WifiUtils.getRSSI() +" dBm");
		}

		public void onDisconnect()
		{
			setTitle("Disconnected");
			ssidLabel.setText("ssid: " + WifiUtils.getSSID());
			bssidLabel.setText("bssid: " + WifiUtils.getBSSID());
			ipaddrLabel.setText("ip-addr: " + WifiUtils.getAddress());
			signalQualityBar.setValue(WifiUtils.getQuality());
			signalQualityBar.setString(WifiUtils.getQuality() + "%");
			signalStrengthBar.setValue(WifiUtils.getRSSIPercentage());
			signalStrengthBar.setString(WifiUtils.getRSSI() +" dBm");
			
		}

		public void onUpdate()
		{
			setTitle(WifiUtils.isConnected()?"Connected":"Disconnected");
			ssidLabel.setText("ssid: " + WifiUtils.getSSID());
			bssidLabel.setText("bssid: " + WifiUtils.getBSSID());
			ipaddrLabel.setText("ip-addr: " + WifiUtils.getAddress());
			signalQualityBar.setValue(WifiUtils.getQuality());
			signalQualityBar.setString(WifiUtils.getQuality() + "%");
			signalStrengthBar.setValue(WifiUtils.getRSSIPercentage());
			signalStrengthBar.setString(WifiUtils.getRSSI() +" dBm");
			
			//System.out.println(getStatusString());
		}
		
	}
	
	private class ScanListener implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
			System.err.println("Scan result: " + WifiUtils.scan());
		}
	}
	
	private JProgressBar signalQualityBar, signalStrengthBar;
	private JLabel ssidLabel;
	private JLabel bssidLabel;
	private JLabel ipaddrLabel;
	public WifiUpdater wifiUpdater = new WifiUpdater();
	private JButton scanButton;
	
	public WifiStatusViewer()
	{
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		setLayout(gridbag);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		
		ssidLabel = new JLabel("ssid: ");
		ssidLabel.setHorizontalTextPosition(JLabel.CENTER);
		gridbag.setConstraints(ssidLabel, c);
		add(ssidLabel);

		bssidLabel = new JLabel("bssid: ");
		ssidLabel.setHorizontalTextPosition(JLabel.CENTER);
		gridbag.setConstraints(bssidLabel, c);
		add(bssidLabel);
		
		ipaddrLabel = new JLabel("ip-addr: ");
		ipaddrLabel.setHorizontalTextPosition(JLabel.CENTER);
		gridbag.setConstraints(ipaddrLabel, c);
		add(ipaddrLabel);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		
		JLabel qualityLabel = new JLabel("Signal-Quality: ");
		qualityLabel.setHorizontalTextPosition(JLabel.RIGHT);
		gridbag.setConstraints(qualityLabel, c);
		add(qualityLabel);
		
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		signalQualityBar = new JProgressBar(0,100);
		signalQualityBar.setStringPainted(true);
		gridbag.setConstraints(signalQualityBar, c);
		add(signalQualityBar);
		
		c.gridwidth = GridBagConstraints.RELATIVE;
		
		JLabel strengthLabel = new JLabel("Signal-Strength: ");
		strengthLabel.setHorizontalTextPosition(JLabel.RIGHT);
		gridbag.setConstraints(strengthLabel, c);
		add(strengthLabel);
		
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		signalStrengthBar = new JProgressBar(0,100);
		signalStrengthBar.setStringPainted(true);
		gridbag.setConstraints(signalStrengthBar, c);
		add(signalStrengthBar);
		
		scanButton = new JButton("Scan!");
		scanButton.addActionListener(new ScanListener());
		gridbag.setConstraints(scanButton, c);
		add(scanButton);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 400);
		this.pack();
	
		this.setEnabled(true);
		this.setResizable(true);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		WifiStatusViewer view = new WifiStatusViewer();
		WifiUtils.addListener(view.wifiUpdater);
		
	}
}
