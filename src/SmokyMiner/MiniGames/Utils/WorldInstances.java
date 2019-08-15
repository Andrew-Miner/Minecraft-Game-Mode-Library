package SmokyMiner.MiniGames.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.World;


public class WorldInstances
{
	// Key: OG World Name
	// Val: Instanced World Name
	private HashMap<String, String> worldNames;
	private HashMap<String, String> worldPaths;
	private HashMap<String, Integer> instanceNumbers;
	
	private int numberRange = 0;
	private TreeSet<Integer> availableNumbers;
	
	String getInstanceName(String name)
	{
		return worldNames.get(name);
	}
	
	World getWorld(String name)
	{
		String realName = worldNames.get(name);
		
		if(realName == null)
			return null;
		
		return Bukkit.getWorld(realName);
	}
	
	boolean addWorld(String name, String path)
	{
		
		return true;
	}
	
	boolean removeWorld(String name, boolean save)
	{
		String iName = worldNames.get(name);
		
		if(iName == null)
			return false;

		
		int id = instanceNumbers.get(name);
		availableNumbers.add(id);

		Bukkit.getServer().unloadWorld(iName, save);

		String path = worldPaths.get(name);
		if(!save)
		{
			File world = new File(path);
			WorldUtils.deleteDir(world);
		}
		
		worldNames.remove(name);
		worldPaths.remove(name);
		instanceNumbers.remove(name);
		
		return true;
	}
	
	private int nextInstanceNumber()
	{
		if(availableNumbers.isEmpty())
			return numberRange++;
		else
			return availableNumbers.pollFirst();
	}
}
