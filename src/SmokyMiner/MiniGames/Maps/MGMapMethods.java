package SmokyMiner.MiniGames.Maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.Minigame.Main.MGManager;

public class MGMapMethods 
{
	public static final String MAP_NAME = "Map Name";
	public static final String DESCRIPTION = "Description";
	public static final String MAX_PLAYERS = "Max Players";
	public static final String MAX_TEAMS = "Max Teams";
	public static final String WORLD = "World";
	public static final String MAP_CENTER = "Map Center";
	public static final String SPEC_SPAWN = "Spectator Spawn";
	public static final String DEFAULT_BOUNDS = "Default Bounds";
	public static final String INGAME_BOUNDS = "In Game Bounds";
	public static final String SPEC_BOUNDS = "Spectator Bounds";
	public static final String NUMB_OF_BOUNDS = "Number Of Bounds";
	public static final String BOUND = "B";
	public static final String SPAWN_AREAS = "Spawn Areas";
	public static final String AREA_COUNT = "Area Count";
	public static final String AREA = "A";
	public static final String SPAWN_POINTS = "Spawn Points";
	public static final String POINT_COUNT = "Point Count";
	public static final String POINT = "P";
	public static final String TEAM = "Team";
	public static final String COLOR = "Color";
	public static final String NAME = "Name";
	public static final char DOT = '.';
	
	public static MGMapMetadata createMapMetadata(String mapName)
	{
		return null;
	}
	
	public static MGMapMetadata loadMapMetadata(MGManager manager, FileConfiguration mapMetadata)
	{	
		try
		{
			validateMetadata(mapMetadata);
		}
		catch (IllegalArgumentException e)
		{
			manager.plugin().getLogger().severe(e.getMessage());
			return null;
		}
		
		int teamCount = mapMetadata.getInt(MAX_TEAMS);
		World world = Bukkit.getWorld(mapMetadata.getString(WORLD));
		
		ArrayList<Double> center = (ArrayList<Double>) mapMetadata.getDoubleList(MAP_CENTER);
		ArrayList<Float> centerF = (ArrayList<Float>) mapMetadata.getFloatList(MAP_CENTER);
		ArrayList<Double> specSpawn = (ArrayList<Double>) mapMetadata.getDoubleList(SPEC_SPAWN);
		ArrayList<Float> specSpawnF = (ArrayList<Float>) mapMetadata.getFloatList(SPEC_SPAWN);
		
		HashMap<Integer, String> colors = new HashMap<Integer, String>();
		HashMap<Integer, String> names = new HashMap<Integer, String>();
		
		for(int i = 0; i < teamCount; i++)
		{
			String color = mapMetadata.getString(TEAM + i + DOT + COLOR);
			color = ChatColor.translateAlternateColorCodes(';', color);
			colors.put(i, color);
			names.put(i, mapMetadata.getString(TEAM + i + DOT + NAME));
		}
		
		Location centerLoc = new Location(world, center.get(0), center.get(1), center.get(2));
		centerLoc.setPitch(centerF.get(3));
		centerLoc.setYaw(centerF.get(4));
		
		Location specSpawnLoc = new Location(world, specSpawn.get(0), specSpawn.get(1), specSpawn.get(2));
		specSpawnLoc.setPitch(specSpawnF.get(3));
		specSpawnLoc.setYaw(specSpawnF.get(4));
		
		String descr = null;
		
		if(mapMetadata.contains(DESCRIPTION))
			descr = mapMetadata.getString(DESCRIPTION);
		
		return new MGMapMetadata(manager, mapMetadata.getString(MAP_NAME), 
				descr, 
				mapMetadata.getInt(MAX_PLAYERS), 
				teamCount, 
				mapMetadata.getString(WORLD), 
				loadBounds(mapMetadata, DEFAULT_BOUNDS), 
				centerLoc, 
				loadBounds(mapMetadata, INGAME_BOUNDS), 
				loadBounds(mapMetadata, SPEC_BOUNDS), 
				specSpawnLoc, 
				loadSpawnAreas(mapMetadata, teamCount, world), 
				loadSpawnPoints(mapMetadata, teamCount, world),
				colors, names);
	}
	
	public static void validateMetadata(FileConfiguration mapMetadata)
	{
		if(mapMetadata == null)
			throw new IllegalArgumentException("Map Configuration Cannot Be Null!");
		
		try
		{
			checkForPath(MAP_NAME, mapMetadata);
			
			checkForPath(MAX_PLAYERS, mapMetadata);
			checkForPath(MAX_TEAMS, mapMetadata);
			
			checkForPath(WORLD, mapMetadata);
			checkForPath(MAP_CENTER, mapMetadata);
			
			checkForPath(SPEC_SPAWN, mapMetadata);
			
			checkForPath(DEFAULT_BOUNDS, mapMetadata);
			checkForPath(DEFAULT_BOUNDS + DOT + NUMB_OF_BOUNDS, mapMetadata);
			
			checkForPath(INGAME_BOUNDS, mapMetadata);
			checkForPath(INGAME_BOUNDS + DOT + NUMB_OF_BOUNDS, mapMetadata);
			
			checkForPath(SPEC_BOUNDS, mapMetadata);
			checkForPath(SPEC_BOUNDS + DOT + NUMB_OF_BOUNDS, mapMetadata);
			
			checkForPath(SPAWN_AREAS, mapMetadata);
			
			checkForPath(SPAWN_POINTS, mapMetadata);
			

			int teamCount = mapMetadata.getInt(MAX_TEAMS);
			
			for(int i = 0; i < teamCount; i++)
			{
				checkForPath(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA_COUNT, mapMetadata);
				checkForPath(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT_COUNT, mapMetadata);
				checkForPath(TEAM + i + DOT + COLOR, mapMetadata);
				checkForPath(TEAM + i + DOT + NAME, mapMetadata);
			}
		}
		catch(IllegalArgumentException e)
		{
			throw e;
		}
	}
	


	public static void validateSpawns(MGManager manager, MGMapMetadata map) 
	{
		FileConfiguration config = map.getMapConfig();
		
		if(config == null)
			config = manager.getMapConfig(map.getMapName().toLowerCase() + ".yml");
		
		if(config == null)
			throw new IllegalStateException("Failed to find " + map.getMapName() + " Config!");
		
		int teamCount = config.getInt(MAX_TEAMS);
		int maxPlayers = config.getInt(MAX_PLAYERS);
		int minPlayersPerTeam = maxPlayers / 2;
		
		if(config.getDoubleList(SPEC_SPAWN).size() != 5)
			throw new IllegalArgumentException("Map Configuration For " + map.getMapName() + " Doesn't Meet Spawn Requirements!");
		
		for(int i = 0; i < teamCount; i++)
		{
			int areaCount = config.getInt(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA_COUNT);
			int pointCount = config.getInt(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT_COUNT);
			
			if(areaCount == 0 && pointCount < minPlayersPerTeam)
				throw new IllegalArgumentException("Map Configuration For " + map.getMapName() + " Doesn't Meet Spawn Requirements!");
		}
	}
	
	public static boolean checkForPath(String path, FileConfiguration mapMetadata)
	{
		if(!mapMetadata.contains(path))
			throw new IllegalArgumentException("Map Configuration missing \"" + path + "\"!");
		return true;
	}

	public static HashMap<Integer, ArrayList<Location>> loadSpawnPoints(FileConfiguration mapMetadata, int maxTeams, World world) 
	{
		HashMap<Integer, ArrayList<Location>> points = new HashMap<Integer, ArrayList<Location>>();
		
		for(int i = 0; i < maxTeams; i++)
		{
			ArrayList<Location> locs = new ArrayList<Location>(); 
			int pointCount = mapMetadata.getInt(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT_COUNT);
			
			for(int j = 0; j < pointCount; j++)
			{
				ArrayList<Double> p0 = (ArrayList<Double>) mapMetadata.getDoubleList(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT + j);
				ArrayList<Float> p0f = (ArrayList<Float>) mapMetadata.getFloatList(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT + j);
				
				Location loc = new Location(world, p0.get(0), p0.get(1), p0.get(2));
				loc.setPitch(p0f.get(3));
				loc.setYaw(p0f.get(4));
				
				locs.add(loc);
			}
			
			points.put(i, locs);
		}
		
		return points;
	}

	public static HashMap<Integer, ArrayList<MGBound>> loadSpawnAreas(FileConfiguration mapMetadata, int maxTeams, World world) 
	{
		HashMap<Integer, ArrayList<MGBound>> areas = new HashMap<Integer, ArrayList<MGBound>>();
		
		for(int i = 0; i < maxTeams; i++)
		{
			ArrayList<MGBound> bounds = new ArrayList<MGBound>(); 
			int areaCount = mapMetadata.getInt(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA_COUNT);
			
			for(int j = 0; j < areaCount; j++)
			{
				ArrayList<Double> p0 = (ArrayList<Double>) mapMetadata.getDoubleList(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA + j + DOT + POINT + "0");
				ArrayList<Double> p1 = (ArrayList<Double>) mapMetadata.getDoubleList(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA + j + DOT + POINT + "1");
				
				Location loc1 = new Location(world, p0.get(0), p0.get(1), p0.get(2));
				Location loc2 = new Location(null, p1.get(0), p1.get(1), p1.get(2));
				
				bounds.add(new MGBound(loc1, loc2));
			}
			
			areas.put(i, bounds);
		}
		
		return areas;
	}

	public static ArrayList<MGBound> loadBounds(FileConfiguration config, String mapDir) 
	{
		ArrayList<MGBound> bounds = new ArrayList<MGBound>();
		
		int numbOfBounds = config.getInt(mapDir + ".Number Of Bounds");
		
		for(int i = 0; i < numbOfBounds; i++)
		{
			ArrayList<Double> p0 = (ArrayList<Double>) config.getDoubleList(mapDir + DOT + BOUND + i + DOT + POINT + "0");
			ArrayList<Double> p1 = (ArrayList<Double>) config.getDoubleList(mapDir + DOT + BOUND + i + DOT + POINT + "1");
			bounds.add(new MGBound(new Location(null, p0.get(0), p0.get(1), p0.get(2)), new Location(null, p1.get(0), p1.get(1), p1.get(2))));
		}
		
		return bounds;
	}
	
	public static ArrayList<Double> convertLocation(Location loc, boolean includeView)
	{
		ArrayList<Double> locList = new ArrayList<Double>();
		
		locList.add(loc.getX());
		locList.add(loc.getY());
		locList.add(loc.getZ());
		
		if(includeView)
		{
			locList.add((double) loc.getPitch());
			locList.add((double) loc.getYaw());
		}
		
		return locList;
	}
	
	public static int addPoint(String path, Location loc, FileConfiguration mapConfig)
	{
		ArrayList<Double> locList = convertLocation(loc, true);
		int pointCount = mapConfig.getInt(path + DOT + POINT_COUNT);
		
		mapConfig.set(path + DOT + POINT + pointCount++, locList);
		mapConfig.set(path + DOT + POINT_COUNT, pointCount);
		
		return pointCount;
	}

	public static void clearPoints(String path, FileConfiguration mapConfig) 
	{
		mapConfig.set(path, null);
		mapConfig.set(path + DOT + POINT_COUNT, 0);
	}

	public static int addBound(String path, MGBound bound, FileConfiguration mapConfig) 
	{
		ArrayList<Double> loc1 = convertLocation(bound.loc1, false);
		ArrayList<Double> loc2 = convertLocation(bound.loc2, false);
		
		int boundCount = mapConfig.getInt(path + DOT + NUMB_OF_BOUNDS);
		
		mapConfig.set(path + DOT + BOUND + boundCount + DOT + POINT + "0", loc1);
		mapConfig.set(path + DOT +BOUND + boundCount + DOT + POINT + "1", loc2);
		
		mapConfig.set(path + DOT + NUMB_OF_BOUNDS, ++boundCount);
		
		return boundCount;
	}
	
	public static void setBounds(String path, ArrayList<MGBound> bounds, FileConfiguration mapConfig)
	{
		clearBounds(path, mapConfig);
		
		for(MGBound b : bounds)
		{
			addBound(path, b, mapConfig);
		}
	}

	public static void clearBounds(String path, FileConfiguration mapConfig) 
	{
		mapConfig.set(path, null);
		mapConfig.set(path + DOT + NUMB_OF_BOUNDS, 0);
	}
	
	public static boolean adjustTeamSize(MGManager manager, MGMapMetadata arena, int max) 
	{
		int lastMax = arena.getMaxTeams();
		
		if(lastMax == max)
			return false;
		
		arena.setMaxTeams(max);
		FileConfiguration config = arena.getMapConfig();
		
		if(config == null)
			config = manager.getMapConfig(arena.getMapName().toLowerCase() + ".yml");
		
		if(config == null)
			throw new IllegalStateException("Failed to find " + arena.getMapName() + " Config!");
		
		if(lastMax > max)	// Adjust Teams By Removing Extra Old Teams
		{
			manager.plugin().getLogger().info("Last Max: " + lastMax + " Max: " + max + " lastMax > max");
			for(int i = max; i < lastMax; i++)
			{
				manager.plugin().getLogger().info("Removing Team" + i);
				if(config.contains(SPAWN_AREAS + DOT + TEAM + i))
					config.set(SPAWN_AREAS + DOT + TEAM + i, null);
				if(config.contains(SPAWN_POINTS + DOT + TEAM + i))
					config.set(SPAWN_POINTS + DOT + TEAM + i, null);
				if(config.contains(TEAM + i))
					config.set(TEAM + i, null);
			}
		}
		else				// Adjust Teams By Adding New Teams
		{
			manager.plugin().getLogger().info("Last Max: " + lastMax + " Max: " + max + " lastMax < max");
			for(int i = lastMax; i < max; i++)
			{
				manager.plugin().getLogger().info("Adding Team" + i);
				config.set(SPAWN_AREAS + DOT + TEAM + i + DOT + AREA_COUNT, 0);
				config.set(SPAWN_POINTS + DOT + TEAM + i + DOT + POINT_COUNT, 0);
				config.set(TEAM + i + DOT + COLOR, ";F");
				config.set(TEAM + i + DOT + NAME, "Default Name");
			}
		}
		
		arena.saveMap();
		
		return true;
	}

	
	// PRINTING FUNCTIONS
	
	public static void printMapMetadata(JavaPlugin plugin, MGMapMetadata meta)
	{
		plugin.getLogger().info(" ");
		plugin.getLogger().info("----------------------------");
		plugin.getLogger().info("Map Name: " + meta.getMapName());
		plugin.getLogger().info("Description: " + meta.getDescription());
		plugin.getLogger().info("Max Players: " + meta.getMaxPlayers());
		plugin.getLogger().info("Max Teams: " + meta.getMaxTeams());
		plugin.getLogger().info("World: " + meta.getWorldName());

		plugin.getLogger().info("Def Bounds: ");
		printBounds(plugin, meta.getDefaultBounds());

		plugin.getLogger().info("In Game Bounds: ");
		printBounds(plugin, meta.getInGameBounds());

		plugin.getLogger().info("Spectator Bounds");
		printBounds(plugin, meta.getSpectatorBounds());

		plugin.getLogger().info("Spawn Areas: ");

		HashMap<Integer, ArrayList<MGBound>> areas = meta.getSpawnAreas();
		Iterator<Map.Entry<Integer, ArrayList<MGBound>>> it = areas.entrySet().iterator();

		while(it.hasNext())
		{
			Map.Entry<Integer, ArrayList<MGBound>> pair = it.next();

			plugin.getLogger().info("	Team " + pair.getKey() + ": ");

			int c = 0;
			for(MGBound bound : pair.getValue())
			{
				plugin.getLogger().info("    B" + c + ":");
				plugin.getLogger().info("      P0: " + bound.loc1.getX() + " " + bound.loc1.getY() + " " + bound.loc1.getZ());
				plugin.getLogger().info("      P1: " + bound.loc2.getX() + " " + bound.loc2.getY() + " " + bound.loc2.getZ());
				c++;
			}
		}

		plugin.getLogger().info("Spawn Points: ");

		HashMap<Integer, ArrayList<Location>> points = meta.getSpawnPoints();
		Iterator<Map.Entry<Integer, ArrayList<Location>>> it1 = points.entrySet().iterator();

		while(it1.hasNext())
		{
			Map.Entry<Integer, ArrayList<Location>> pair = it1.next();

			plugin.getLogger().info("	Team " + pair.getKey() + ": ");

			int c = 0;
			for(Location loc : pair.getValue())
			{
				plugin.getLogger().info("    P" + c + ": " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
				c++;
			}
		}
	}
	
	public static void printBounds(CommandSender sender, ArrayList<MGBound> bounds) 
	{
		int c = 0;
		for(MGBound bound : bounds)
		{
			sender.sendMessage(ChatColor.GRAY + "   B" + c + ":");
			sender.sendMessage(ChatColor.GRAY + "     P0: " + bound.loc1.getX() + " " + bound.loc1.getY() + " " + bound.loc1.getZ());
			sender.sendMessage(ChatColor.GRAY + "     P1: " + bound.loc2.getX() + " " + bound.loc2.getY() + " " + bound.loc2.getZ());
			c++;
		}
	}
	
	public static void printBounds(JavaPlugin plugin, ArrayList<MGBound> bounds)
	{
		int c = 0;
		for(MGBound bound : bounds)
		{
			plugin.getLogger().info("	B" + c + ":");
			plugin.getLogger().info("    P0: " + bound.loc1.getX() + " " + bound.loc1.getY() + " " + bound.loc1.getZ());
			plugin.getLogger().info("    P1: " + bound.loc2.getX() + " " + bound.loc2.getY() + " " + bound.loc2.getZ());
			c++;
		}
	}
}