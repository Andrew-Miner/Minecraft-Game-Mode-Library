package com.gmail.andrew.miner3.plugins.Paintball.MapClasses;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;

public class MapMetadata 
{
	private ArrayList<Bound> inGameBounds;
	private ArrayList<Bound> spectatorBounds;
	private ArrayList<Bound> defBound;

	private HashMap<Integer, ArrayList<Bound>> spawnAreas;

	private HashMap<Integer, ArrayList<Location>> spawnPoints;

	private HashMap<Integer, String> teamColors;
	private HashMap<Integer, String> teamPrefixes;

	private String mapName;
	private String description;

	private int maxPlayers;
	private int maxTeams;

	private String worldName;

	private Location mapCenter;
	private Location spectatorSpawn;

	private boolean inUse;

	private FileConfiguration mapConfig;

	private PluginManager plugin;

	public MapMetadata(PluginManager plugin)
	{
		this.plugin = plugin;

		mapName = null;
		description = null;
		maxPlayers = 0;
		maxTeams = 0;
		worldName = null;

		inGameBounds = null;
		spectatorBounds = null;
		defBound = null;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		mapCenter = null;

		defBound = null;
		inGameBounds = null;
		spectatorBounds = null;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<Bound> defBound, Location mapCenter, Location spectatorSpawn)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defBound = defBound;
		inGameBounds = null;
		spectatorBounds = null;

		spawnAreas = null;
		spawnPoints = null;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<Bound> defBound, Location mapCenter, ArrayList<Bound> inGameBounds, ArrayList<Bound> spectatorBounds)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defBound = defBound;
		this.inGameBounds = inGameBounds;
		this.spectatorBounds = spectatorBounds;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, Location spectatorSpawn, HashMap<Integer, ArrayList<Bound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = null;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = null;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<Bound> defBound, Location mapCenter, ArrayList<Bound> inGameBounds, ArrayList<Bound> spectatorBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<Bound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defBound = defBound;
		this.inGameBounds = inGameBounds;
		this.spectatorBounds = spectatorBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<Bound> defBound, Location mapCenter, ArrayList<Bound> inGameBounds, ArrayList<Bound> spectatorBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<Bound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints, HashMap<Integer, String> teamColors, HashMap<Integer, String> teamPrefixes)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defBound = defBound;
		this.inGameBounds = inGameBounds;
		this.spectatorBounds = spectatorBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = teamColors;
		this.teamPrefixes = teamPrefixes;

		inUse = false;

		mapConfig = null;
	}

	public MapMetadata(PluginManager plugin, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<Bound> defBound, Location mapCenter, ArrayList<Bound> inGameBounds, ArrayList<Bound> spectatorBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<Bound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints, HashMap<Integer, String> teamColors, HashMap<Integer, String> teamPrefixes, FileConfiguration mapConfig)
	{
		this.plugin = plugin;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defBound = defBound;
		this.inGameBounds = inGameBounds;
		this.spectatorBounds = spectatorBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = teamColors;
		this.teamPrefixes = teamPrefixes;

		inUse = false;

		this.mapConfig = mapConfig;
	}

	public MapMetadata(PluginManager plugin, FileConfiguration mapConfig)
	{
		this.plugin = plugin;
		
		buildMapConfig(mapConfig);
	}

	public boolean rebuildMap()
	{
		try
		{
			buildMapConfig(mapConfig);
		}
		catch(IllegalStateException e)
		{
			return false;
		}
		
		return true;
	}
	
	private void buildMapConfig(FileConfiguration mapConfig) 
	{
		try
		{
			MapMetadataMethods.validateMetadata(mapConfig);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException(e.getMessage());
		}

		int teamCount = mapConfig.getInt(MapMetadataMethods.MAX_TEAMS);
		World world = Bukkit.getWorld(mapConfig.getString(MapMetadataMethods.WORLD));

		ArrayList<Double> center = (ArrayList<Double>) mapConfig.getDoubleList(MapMetadataMethods.MAP_CENTER);
		ArrayList<Float> centerF = (ArrayList<Float>) mapConfig.getFloatList(MapMetadataMethods.MAP_CENTER);
		ArrayList<Double> specSpawn = (ArrayList<Double>) mapConfig.getDoubleList(MapMetadataMethods.SPEC_SPAWN);
		ArrayList<Float> specSpawnF = (ArrayList<Float>) mapConfig.getFloatList(MapMetadataMethods.SPEC_SPAWN);

		HashMap<Integer, String> colors = new HashMap<Integer, String>();
		HashMap<Integer, String> names = new HashMap<Integer, String>();

		for(int i = 0; i < teamCount; i++)
		{
			String color = mapConfig.getString(MapMetadataMethods.TEAM + i + MapMetadataMethods.DOT + MapMetadataMethods.COLOR);
			color = ChatColor.translateAlternateColorCodes(';', color);
			colors.put(i, color);
			names.put(i, mapConfig.getString(MapMetadataMethods.TEAM + i + MapMetadataMethods.DOT + MapMetadataMethods.NAME));
		}

		String descr = null;

		if(mapConfig.contains(MapMetadataMethods.DESCRIPTION))
			descr = mapConfig.getString(MapMetadataMethods.DESCRIPTION);

		Location centerLoc = new Location(world, center.get(0), center.get(1), center.get(2));
		centerLoc.setPitch(centerF.get(3));
		centerLoc.setYaw(centerF.get(4));

		Location specSpawnLoc = new Location(world, specSpawn.get(0), specSpawn.get(1), specSpawn.get(2));
		specSpawnLoc.setPitch(specSpawnF.get(3));
		specSpawnLoc.setYaw(specSpawnF.get(4));

		this.mapName = mapConfig.getString(MapMetadataMethods.MAP_NAME);
		this.description = descr;
		this.maxPlayers = mapConfig.getInt(MapMetadataMethods.MAX_PLAYERS);
		this.maxTeams = teamCount;
		this.worldName = mapConfig.getString(MapMetadataMethods.WORLD);
		this.defBound = MapMetadataMethods.loadBounds(mapConfig, MapMetadataMethods.DEFAULT_BOUNDS);
		this.mapCenter = centerLoc;
		this.spectatorSpawn = specSpawnLoc;
		this.inGameBounds = MapMetadataMethods.loadBounds(mapConfig, MapMetadataMethods.INGAME_BOUNDS);
		this.spectatorBounds = MapMetadataMethods.loadBounds(mapConfig, MapMetadataMethods.SPEC_BOUNDS);
		this.spawnAreas = MapMetadataMethods.loadSpawnAreas(mapConfig, teamCount, world);
		this.spawnPoints = MapMetadataMethods.loadSpawnPoints(mapConfig, teamCount, world);
		this.teamColors = colors;
		this.teamPrefixes = names;
		this.mapConfig = mapConfig;
	}

	public String getMapName() { return mapName; }

	public String getDescription() { return description; }
	public void setDescription(String description) 
	{ 
		this.description = description;
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		mapConfig.set(MapMetadataMethods.DESCRIPTION, description);
		saveMap();
	}

	public int getMaxPlayers() { return maxPlayers; }
	public void setMaxPlayers(int maxPlayers) 
	{ 
		this.maxPlayers = maxPlayers; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
			
		mapConfig.set(MapMetadataMethods.MAX_PLAYERS, maxPlayers);
		saveMap();
	}

	public int getMaxTeams() { return maxTeams; }
	public void setMaxTeams(int maxTeams) 
	{ 
		this.maxTeams = maxTeams;
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		mapConfig.set(MapMetadataMethods.MAX_TEAMS, maxTeams);
		saveMap();
	}

	public HashMap<Integer, ArrayList<Bound>> getSpawnAreas() { return spawnAreas; }
	public void setSpawnAreas(HashMap<Integer, ArrayList<Bound>> spawnAreas) { this.spawnAreas = spawnAreas; }

	public HashMap<Integer, ArrayList<Location>> getSpawnPoints() { return spawnPoints; }
	public void setSpawnPoints(HashMap<Integer, ArrayList<Location>> spawnPoints) { this.spawnPoints = spawnPoints; }
	
	public void addSpawnPoint(int teamIndex, Location point)
	{
		if(teamIndex >= getMaxTeams())
			throw new IllegalArgumentException("Team Index Out Of Range!");
		
		spawnPoints.get(teamIndex).add(point);

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MapMetadataMethods.addPoint(MapMetadataMethods.SPAWN_POINTS + "." + MapMetadataMethods.TEAM + teamIndex, point, mapConfig);
		saveMap();

	}
	
	public void clearSpawnPoints(int teamIndex)
	{
		if(teamIndex >= getMaxTeams())
			throw new IllegalArgumentException("Team Index Out Of Range!");
		
		spawnPoints.get(teamIndex).clear();

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MapMetadataMethods.clearPoints(MapMetadataMethods.SPAWN_POINTS + "." + MapMetadataMethods.TEAM + teamIndex, mapConfig);
		saveMap();
	}

	public ArrayList<Bound> getDefaultBounds() { return defBound; }
	public void setDefaultBounds(ArrayList<Bound> defBound) 
	{ 
		this.defBound = defBound; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.setBounds(MapMetadataMethods.DEFAULT_BOUNDS, defBound, mapConfig);
		saveMap();
	}

	public ArrayList<Bound> getInGameBounds() { return inGameBounds; }
	public void setInGameBounds(ArrayList<Bound> inGameBounds) 
	{ 
		this.inGameBounds = inGameBounds;
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.setBounds(MapMetadataMethods.INGAME_BOUNDS, inGameBounds, mapConfig);
		saveMap();
	}

	public ArrayList<Bound> getSpectatorBounds() { return spectatorBounds; }
	public void setSpectatorBounds(ArrayList<Bound> spectatorBounds) 
	{ 
		this.spectatorBounds = spectatorBounds; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.setBounds(MapMetadataMethods.SPEC_BOUNDS, spectatorBounds, mapConfig);
		saveMap();
	}

	public String getWorldName() { return worldName; }
	public void setWorldName(String worldName) 
	{ 
		this.worldName = worldName;

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		mapConfig.set(MapMetadataMethods.WORLD, worldName);
		saveMap();
		rebuildMap();
	}

	public Location getMapCenter() { return mapCenter; }
	
	public void setMapCenter(Location center) 
	{ 
		mapCenter = center;

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MapMetadataMethods.addPoint(MapMetadataMethods.MAP_CENTER, center, mapConfig);
		saveMap();
	}
	
	public void resetMapCenter() 
	{
		Location center = new Location(null, 0, 0, 0, 0, 0);
		setMapCenter(center);
	}

	public Location getSpectatorSpawn() { return spectatorSpawn; }
	
	public void setSpectatorSpawn(Location spawn) 
	{ 
		spectatorSpawn = spawn; 

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MapMetadataMethods.addPoint(MapMetadataMethods.SPEC_SPAWN, spawn, mapConfig);
		saveMap();
	}
	
	public void resetSpectatorSpawn()
	{
		Location spawn = new Location(null, 0, 0, 0, 0, 0);
		setSpectatorSpawn(spawn);
	}

	public HashMap<Integer, String> getTeamColors() { return teamColors; }
	public void setTeamColors(HashMap<Integer, String> colors) { teamColors = colors; }

	public HashMap<Integer, String> getTeamPrefixes() { return teamPrefixes; }
	public void setTeamPrefixes(HashMap<Integer, String> prefixes) { teamPrefixes = prefixes; }

	public boolean isInUse() { return inUse; }
	public void setInUse(boolean used) { inUse = used; }

	public void setMapConfig(FileConfiguration config) { mapConfig = config; }
	public FileConfiguration getMapConfig() { return mapConfig; }

	public void saveMap()
	{
		plugin.saveMapConfig(getMapName().toLowerCase() + ".yml");
	}

	public String getColor(int team)
	{
		return teamColors.get(team);
	}

	public String getPrefix(int team)
	{
		return teamPrefixes.get(team);
	}

	public void setBounds(ArrayList<Bound> defBound, ArrayList<Bound> inGameBounds, ArrayList<Bound> spectatorBounds)
	{
		setDefaultBounds(defBound);
		setInGameBounds(inGameBounds);
		setSpectatorBounds(spectatorBounds);
	}

	public boolean contains(Location loc, boolean spectating)
	{
		if(spectating && spectatorBounds != null && !spectatorBounds.isEmpty())
		{
			for(Bound bound : spectatorBounds)
			{
				if(bound.contains(loc))
					return true;
			}
		}
		else if(!spectating && inGameBounds != null && !inGameBounds.isEmpty())
		{
			for(Bound bound : inGameBounds)
			{
				if(bound.contains(loc))
					return true;
			}
		}
		else if(defBound != null && !defBound.isEmpty())
		{
			for(Bound bound : defBound)
			{
				if(bound.contains(loc))
					return true;
			}
		}
		else
		{
			plugin.getLogger().info("ELSE COMMAND CHECK");
			return true;
		}

		return false;
	}

	public Location getSpawnAreaLoc(int team) 
	{
		ArrayList<Bound> areas = spawnAreas.get(team);

		if(areas == null || areas.size() == 0)
			return null;

		Bound area = areas.get((int) (Math.random() * areas.size()));

		return new Location(null, generateRandomRange(area.loc1.getX(), area.loc2.getX()),
				generateRandomRange(area.loc1.getY(), area.loc2.getY()),
				generateRandomRange(area.loc1.getZ(), area.loc2.getZ()));
	}

	private double generateRandomRange(double x, double y) 
	{
		double large, small;

		if(x < y)
		{
			small = x;
			large = y;
		}
		else
		{
			large = x;
			small = y;
		}

		return (Math.random() * (large - small + 1)) + small; 
	}

	public void addDefaultBound(Bound bound) 
	{
		defBound.add(bound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.addBound(MapMetadataMethods.DEFAULT_BOUNDS, bound, mapConfig);
		saveMap();
	}

	public void addInGameBound(Bound bound) 
	{
		inGameBounds.add(bound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.addBound(MapMetadataMethods.INGAME_BOUNDS, bound, mapConfig);
		saveMap();
	}

	public void addSpectatorBound(Bound bound) 
	{
		spectatorBounds.add(bound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.addBound(MapMetadataMethods.SPEC_BOUNDS, bound, mapConfig);
		saveMap();
	}

	public void clearDefaultBounds() 
	{
		defBound.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.clearBounds(MapMetadataMethods.DEFAULT_BOUNDS, mapConfig);
		saveMap();
	}

	public void clearInGameBounds() 
	{
		inGameBounds.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.clearBounds(MapMetadataMethods.INGAME_BOUNDS, mapConfig);
		saveMap();
	}

	public void clearSpectatorBounds() 
	{
		spectatorBounds.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MapMetadataMethods.clearBounds(MapMetadataMethods.SPEC_BOUNDS, mapConfig);
		saveMap();
	}
}
