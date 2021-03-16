package ecologylab.standalone;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import ecologylab.oodss.logging.playback.ExtensionFilter;
import ecologylab.sensor.location.gps.data.GeoCoordinate;

public class GoogleMapBuilder
{
	static int							serverNumber		= 0;

	static Random						r						= new Random();

	private static final double	minimumWaitTime	= 15.0;

	private static final double	randomWaitRange	= 7.5;

	/**
	 * Get the vertical tile number from a latitude using Mercator
	 * projection formula
	 */
	private static double getMercatorLatitude(double lati, int zoom)
	{
		double maxlat = Math.PI;

		double lat = lati;

		if (lat > 90)
			lat = lat - 180;
		if (lat < -90)
			lat = lat + 180;

		// conversion degre=>radians
		double phi = Math.PI * lat / 180;

		double res;

		res = 0.5 * Math.log((1 + Math.sin(phi)) / (1 - Math.sin(phi)));
		double maxTileY = Math.pow(2, zoom);
		double result = (((1 - res / maxlat) / 2) * (maxTileY));

		return (result);
	}

	private static String getStreetUrl(int x, int y, int zoom)
	{
		return "http://mt" + r.nextInt(4)
				+ ".google.com/vt/lyrs=m@113&hl=sk&src=api&x=" + (x) + "&y=" + (y)
				+ "&z=" + zoom;
	}

	private static String getSatelliteUrl(int x, int y, int zoom)
	{
		return "http://khm" + r.nextInt(4) + ".google.com/kh/v=39&hl=en&x=" + (x)
				+ "&s=G&y=" + (y) + "&z=" + zoom;
	}

	/**
	 * Get's an image from google maps for the specified area.
	 * 
	 * @param neCorner
	 *           North-East Corner of the area
	 * @param swCorner
	 *           South-East Corner of the area
	 * @param zoomLevel
	 *           Zoom level form map tiles: 0 is the closest guaranteed level, 17
	 *           is whole world. Negative values possible. Note: also determines
	 *           resolution of returned image.
	 * @param satellite
	 *           If true returned image is a satellite image, false it is a map
	 * @return
	 * @throws IOException
	 */
	static BufferedImage getMapImage(GeoCoordinate neCorner,
			GeoCoordinate swCorner, int zoomLevel, boolean satellite)
			throws IOException
	{
		double longDegrees = neCorner.getLon() - swCorner.getLon();
		double latDegrees = (neCorner.getLat() - swCorner.getLat()) / 2;

		double latDegreesPerTile = 180 / Math.pow(2, 17 - zoomLevel);
		double longDegreesPerTile = 360 / Math.pow(2, 17 - zoomLevel);

		int tilesX = (int) Math.ceil(longDegrees / longDegreesPerTile) + 1;
		double tilesYFloating = Math.ceil(getMercatorLatitude(swCorner.getLat(),
				17 - zoomLevel)
				- getMercatorLatitude(neCorner.getLat(), 17 - zoomLevel));
		int tilesY = (int) Math.ceil(tilesYFloating) + 1;

		double tilePosX = ((swCorner.getLon() + 180) / longDegreesPerTile);
		double tilePosY = getMercatorLatitude(neCorner.getLat(), 17 - zoomLevel);

		int tileStartX = (int) tilePosX;
		int tileStartY = (int) tilePosY;

		BufferedImage map = new BufferedImage(tilesX * 256, tilesY * 256,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g2 = (Graphics2D) map.getGraphics();

		ArrayList<Point> tileCoordinates = new ArrayList<Point>();

		for (int x = 0; x < tilesX; x++)
		{
			for (int y = 0; y < tilesY; y++)
			{
				tileCoordinates.add(new Point(x, y));
			}
		}

		while (tileCoordinates.size() > 0)
		{
			Point randomTile = tileCoordinates.remove(r.nextInt(tileCoordinates
					.size()));
			int x = randomTile.x;
			int y = randomTile.y;

			String tileUrl = (satellite) ? getSatelliteUrl(x + tileStartX, y
					+ tileStartY, 17 - zoomLevel) : getStreetUrl(x + tileStartX, y
					+ tileStartY, 17 - zoomLevel);

			BufferedImage tile = null;
			try
			{
				tile = ImageIO.read(new URL(tileUrl));
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			g2.drawImage(tile, x * 256, y * 256, null);
			System.out.println("Got tile: (" + x + ", " + y + ") "
					+ tileCoordinates.size() + " tiles left");

			try
			{
				Thread
						.sleep((long) ((r.nextDouble() * randomWaitRange + minimumWaitTime) * 1000.0));
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		return map.getSubimage((int) ((tilePosX - tileStartX) * 256),
				(int) ((tilePosY - tileStartY) * 256), (int) (longDegrees
						/ longDegreesPerTile * 256), (int) (tilesYFloating * 256));

		// return map;
	}

	public static void main(String[] args) throws IOException
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		
		FileFilter filter = new ExtensionFilter("png");
		chooser.setFileFilter(filter);
		
		int returnVal = chooser.showOpenDialog(null);
		
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			GeoCoordinate ne = new GeoCoordinate(30.620785, -96.335091, 0);
			GeoCoordinate sw = new GeoCoordinate(30.613863, -96.345288, 0);
			BufferedImage map = getMapImage(ne, sw, -2, true);
	
			ImageIO.write(map, "PNG", chooser.getSelectedFile());
			
			JFrame frame = new JFrame();
				
			ImageIcon icon = new ImageIcon(map);
	
			JLabel label = new JLabel();
			label.setHorizontalAlignment(SwingConstants.CENTER);
	
			label.setIcon(icon);
	
			frame.add(label);
	
			frame.setSize(map.getWidth(frame) + 50, map.getHeight(frame) + 50);
	
			frame.setVisible(true);
		}
	}

}
