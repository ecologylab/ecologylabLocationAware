package ecologylab.swing;

import java.awt.Graphics;

import javax.swing.JPanel;

import org.geotools.map.MapContext;

import com.vividsolutions.jts.geom.Coordinate;

import ecologylab.swing.GTTiledMap.moveListener;
import ecologylab.swing.GTTiledMap.rotateListener;

public class GTTiledDistanceMap extends GTTiledMap
{
	
	private boolean initialSizeSet = false;
	
	private float visibleDistance;
	
	public GTTiledDistanceMap(Coordinate center, MapContext map, float visibleDistance, int tileDimension)
	{
		super(center, map, tileDimension);
		

		this.addKeyListener(new moveListener());
		this.addMouseWheelListener(new zoomListener());
		
		this.visibleDistance = visibleDistance;
		
		//setup initial scales
		this.worldToScreenScale = worldToScreenScale ;
		this.worldToTileScale = worldToScreenScale;
		this.tileToScreenScale = 1.0f;
	}
	
	public GTTiledDistanceMap(Coordinate center, MapContext map, float visibleDistance)
	{
		this(center, map, visibleDistance, 256);
	}
	
	@Override
	protected void resizeAuxillary(int width, int height)
	{
		initialSizeSet = true;
		
		repaint();
		
		float verticalScreenDistance = height / 2.0f;
		float _worldToScreenScale = verticalScreenDistance / visibleDistance;
		
		double scaleRatio = _worldToScreenScale / worldToScreenScale;
		worldToScreenScale = _worldToScreenScale;
		tileToScreenScale *= scaleRatio;
		
		
		
		float newRadius = (float) Math.sqrt(width * width + height * height) / 2.0f;
		
		if(tileToScreenScale > 1.5f || tileToScreenScale < 1.0f / 1.5f)
		{
			//scale is too much or to small we need total reset
			
			tileToScreenScale = 1.0f;
			worldToTileScale = worldToScreenScale;
			
			tileRadius = newRadius / tileToScreenScale;
			
			completeReset();
		}
		
		else
		{
			//not too extreme just treat it like a resize of the map
			worldToTileScale = worldToScreenScale / tileToScreenScale;
			
			newRadius /= tileToScreenScale;
			
			if(newRadius > tileRadius + tileDimension / 2 )
			{
				tileRadius = newRadius;
				resizeReset();
			}
			else if(newRadius < tileRadius - tileDimension / 2)
			{
				tileRadius = newRadius ;
				resizeReset();
			}
		}
	}
	
	public void paint(Graphics g)
	{
		if(initialSizeSet)
		{
			super.paint(g);
		}
	}
}
