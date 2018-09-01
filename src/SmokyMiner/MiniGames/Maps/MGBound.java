package SmokyMiner.MiniGames.Maps;

import org.bukkit.Location;

public class MGBound 
{
	public Location loc1;
	public Location loc2;
	
	public MGBound()
	{
		loc1 = null;
		loc2 = null;
	}
	
	public MGBound(Location loc1, Location loc2)
	{
		this.loc1 = loc1;
		this.loc2 = loc2;
	}
	
	public boolean contains(Location loc)
	{
		if(loc1 == null || loc2 == null)
			return false;
		
		return contains(loc1.getX(), loc2.getX(), loc.getX()) && 
				contains(loc1.getY(), loc2.getY(), loc.getY()) && 
				contains(loc1.getZ(), loc2.getZ(), loc.getZ());
	}
	
	private boolean contains(double bound1, double bound2, double pos)
	{		
		if(bound1 - bound2 < 0)
			return pos <= bound2 && pos >= bound1;
		return pos <= bound1 && pos >= bound2;
	}
}