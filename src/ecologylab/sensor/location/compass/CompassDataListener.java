package ecologylab.sensor.location.compass;

public interface CompassDataListener
{
	public void compassDataUpdated(float heading, float pitch, float roll, float temp);
}
