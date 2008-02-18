package ecologylab.standalone;

import stec.jenie.INT32;
import stec.jenie.UCHARArray;
import stec.jenie.AnsiString;
import stec.jenie.LONG;
import stec.jenie.Pointer;
import stec.jenie.Dll;
import stec.jenie.NativeParameter;
import stec.jenie.NativeException;
import java.awt.*;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class DisplaySignalStrength extends JApplet{

	final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static BasicStroke stroke = new BasicStroke(2.0f);
    
	public static void main(String[] args) throws Exception
	{
		JFrame f = new JFrame("WiFi Signal Meter");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	System.exit(0);
            	}
        });
        
        JApplet applet = new DisplaySignalStrength();
        f.getContentPane().add("Center", applet);
        applet.init();
        f.pack();
        f.setSize(new Dimension(300,100));
        f.setResizable(false);
        f.setVisible(true);
        
        /*
		for(int i=0; i<15; i++)
		{
			System.out.println(getSignalStrength() + " dBm");
			Thread.sleep(1000);
		}*/
	}
	
    public void init() {
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
    }
    
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaintMode();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setPaint(bg);
        g2.fillRect(0,0,300,100);
        
        int mySS = -100;
        int areaInfo = -99;
        
        try{
        	mySS = getSignalStrength();
        	//areaInfo = getAPData();
        	//System.out.print("Number visible access points: " + areaInfo);
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
        
        try{
        	//mySS = getSignalStrength();
        	//areaInfo = getAPData();
        	//System.out.println("Length of return string: " + areaInfo);
        	System.out.println(getAPData());
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
        
        
        double barlength = mySS;
        barlength += 20;
        barlength /= 80;
        int colorscale = (int)(-1*255*barlength);
        if(colorscale > 255)
        	colorscale = 255;
        else if(colorscale < 0)
        	colorscale = 0;
        
        Color signalColor = new Color(colorscale, 255-colorscale, 0);
        g2.setPaint(signalColor);
        g2.setStroke(stroke);
        barlength *= 300;
        if(barlength > 0)
        	barlength = 0;
        else if(barlength < -300)
        	barlength = -300;
        
        g2.fillRect(0, 0, 300 + (int)barlength, 100);
        
        g2.setPaint(bg);
        g2.fillRect(0, 55, 70, 100);
        
        g2.setColor(Color.BLACK);
        
        String displaydBm = Integer.toString(mySS) + " dBm";
        
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString(displaydBm, 5, 70);

        try{
        	Thread.sleep(1000);
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
        finally
        {
        	this.paint(g);
        }
        
    }

	public static int getSignalStrength() throws NativeException
	{
		Dll myDll = new Dll("Jwifi");
		INT32 returnValue = new INT32();
		
		try
		{
			myDll.getFunction("getSS").call((NativeParameter[])null, returnValue);
		}
		finally
		{
			myDll.release();
		}
		
		return returnValue.getValue();
	}
	
	public static String getAPData() throws NativeException
	{
		Dll myDll = new Dll("Jwifi");
		
		UCHARArray returnArray = new UCHARArray(1000);
		//AnsiString retValue = new AnsiString();
		Pointer toReturnArray = new Pointer(returnArray);
		
		INT32 ptvalue = new INT32();
		
		
		try
		{
			myDll.getFunction("getAPList").call
			(
					toReturnArray, ptvalue
			);
		}
		finally
		{
			myDll.release();
		}
		
		//CHARArray retValue = new CHARArray(toRetValue, strlen.getValue());
		//toRetValue.setValue(ptvalue.getValue());
		
		char[] resizer = new char[ptvalue.getValue()];
		
		for(int x = 0; x<ptvalue.getValue(); x++)
			resizer[x] = (char)returnArray.getValueAt(x);
		
		String toReturn = new String(resizer);
		
		return toReturn;
	}
}

/*
*
Snakes on a Plane
00:18:39:d0:4d:43
-38dBm = 98 percent
Ch: 8
Access Point
Secured
*
masterblaster
a6:d5:32:6a:35:6f
-48dBm = 79 percent
Ch: 11
Adhoc
Secured
*/
