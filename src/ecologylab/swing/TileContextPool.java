package ecologylab.swing;

import ecologylab.generic.ResourcePoolWithSize;

public class TileContextPool extends ResourcePoolWithSize<TileContext> {

	public TileContextPool(int initialPoolSize, int minimumPoolSize,
			int resourceObjectCapacity) 
	{
		super(initialPoolSize, minimumPoolSize, resourceObjectCapacity);
	}

	@Override
	protected TileContext generateNewResource() 
	{
		return new TileContext(resourceObjectCapacity);
	}

	@Override
	protected void clean(TileContext objectToClean) 
	{
		objectToClean.reset();
	}

}
