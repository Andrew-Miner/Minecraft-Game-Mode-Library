package SmokyMiner.MiniGames.Maps;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import SmokyMiner.Minigame.Main.MGManager;

public class MGMapMetadata 
{
	private ArrayList<MGBound> inGameMCGBounds;
	private ArrayList<MGBound> spectatorMCGBounds;
	private ArrayList<MGBound> defMCGBound;

	private HashMap<Integer, ArrayList<MGBound>> spawnAreas;

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

	private MGManager manager;

	public MGMapMetadata(MGManager manager)
	{
		this.manager = manager;

		mapName = null;
		description = null;
		maxPlayers = 0;
		maxTeams = 0;
		worldName = null;

		inGameMCGBounds = null;
		spectatorMCGBounds = null;
		defMCGBound = null;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		mapCenter = null;

		defMCGBound = null;
		inGameMCGBounds = null;
		spectatorMCGBounds = null;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<MGBound> defMCGBound, Location mapCenter, Location spectatorSpawn)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defMCGBound = defMCGBound;
		inGameMCGBounds = null;
		spectatorMCGBounds = null;

		spawnAreas = null;
		spawnPoints = null;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<MGBound> defMCGBound, Location mapCenter, ArrayList<MGBound> inGameMCGBounds, ArrayList<MGBound> spectatorMCGBounds)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defMCGBound = defMCGBound;
		this.inGameMCGBounds = inGameMCGBounds;
		this.spectatorMCGBounds = spectatorMCGBounds;

		spawnAreas = null;
		spawnPoints = null;
		spectatorSpawn = null;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, Location spectatorSpawn, HashMap<Integer, ArrayList<MGBound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints)
	{
		this.manager = manager;

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

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<MGBound> defMCGBound, Location mapCenter, ArrayList<MGBound> inGameMCGBounds, ArrayList<MGBound> spectatorMCGBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<MGBound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defMCGBound = defMCGBound;
		this.inGameMCGBounds = inGameMCGBounds;
		this.spectatorMCGBounds = spectatorMCGBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = null;
		this.teamPrefixes = null;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<MGBound> defMCGBound, Location mapCenter, ArrayList<MGBound> inGameMCGBounds, ArrayList<MGBound> spectatorMCGBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<MGBound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints, HashMap<Integer, String> teamColors, HashMap<Integer, String> teamPrefixes)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defMCGBound = defMCGBound;
		this.inGameMCGBounds = inGameMCGBounds;
		this.spectatorMCGBounds = spectatorMCGBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = teamColors;
		this.teamPrefixes = teamPrefixes;

		inUse = false;

		mapConfig = null;
	}

	public MGMapMetadata(MGManager manager, String mapName, String description, int maxPlayers, int maxTeams, String worldName, ArrayList<MGBound> defMCGBound, Location mapCenter, ArrayList<MGBound> inGameMCGBounds, ArrayList<MGBound> spectatorMCGBounds, Location spectatorSpawn, HashMap<Integer, ArrayList<MGBound>> spawnAreas, HashMap<Integer, ArrayList<Location>> spawnPoints, HashMap<Integer, String> teamColors, HashMap<Integer, String> teamPrefixes, FileConfiguration mapConfig)
	{
		this.manager = manager;

		this.mapName = mapName;
		this.description = description;
		this.maxPlayers = maxPlayers;
		this.maxTeams = maxTeams;
		this.worldName = worldName;
		this.mapCenter = mapCenter;

		this.defMCGBound = defMCGBound;
		this.inGameMCGBounds = inGameMCGBounds;
		this.spectatorMCGBounds = spectatorMCGBounds;

		this.spawnAreas = spawnAreas;
		this.spawnPoints = spawnPoints;
		this.spectatorSpawn = spectatorSpawn;

		this.teamColors = teamColors;
		this.teamPrefixes = teamPrefixes;

		inUse = false;

		this.mapConfig = mapConfig;
	}

	public MGMapMetadata(MGManager manager, FileConfiguration mapConfig)
	{
		this.manager = manager;
		
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
			MGMapMethods.validateMetadata(mapConfig);
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalStateException(e.getMessage());
		}

		int teamCount = mapConfig.getInt(MGMapMethods.MAX_TEAMS);
		World world = Bukkit.getWorld(mapConfig.getString(MGMapMethods.WORLD));

		ArrayList<Double> center = (ArrayList<Double>) mapConfig.getDoubleList(MGMapMethods.MAP_CENTER);
		ArrayList<Float> centerF = (ArrayList<Float>) mapConfig.getFloatList(MGMapMethods.MAP_CENTER);
		ArrayList<Double> specSpawn = (ArrayList<Double>) mapConfig.getDoubleList(MGMapMethods.SPEC_SPAWN);
		ArrayList<Float> specSpawnF = (ArrayList<Float>) mapConfig.getFloatList(MGMapMethods.SPEC_SPAWN);

		HashMap<Integer, String> colors = new HashMap<Integer, String>();
		HashMap<Integer, String> names = new HashMap<Integer, String>();

		for(int i = 0; i < teamCount; i++)
		{
			String color = mapConfig.getString(MGMapMethods.TEAM + i + MGMapMethods.DOT + MGMapMethods.COLOR);
			color = ChatColor.translateAlternateColorCodes(';', color);
			colors.put(i, color);
			names.put(i, mapConfig.getString(MGMapMethods.TEAM + i + MGMapMethods.DOT + MGMapMethods.NAME));
		}

		String descr = null;

		if(mapConfig.contains(MGMapMethods.DESCRIPTION))
			descr = mapConfig.getString(MGMapMethods.DESCRIPTION);

		Location centerLoc = new Location(world, center.get(0), center.get(1), center.get(2));
		centerLoc.setPitch(centerF.get(3));
		centerLoc.setYaw(centerF.get(4));

		Location specSpawnLoc = new Location(world, specSpawn.get(0), specSpawn.get(1), specSpawn.get(2));
		specSpawnLoc.setPitch(specSpawnF.get(3));
		specSpawnLoc.setYaw(specSpawnF.get(4));

		this.mapName = mapConfig.getString(MGMapMethods.MAP_NAME);
		this.description = descr;
		this.maxPlayers = mapConfig.getInt(MGMapMethods.MAX_PLAYERS);
		this.maxTeams = teamCount;
		this.worldName = mapConfig.getString(MGMapMethods.WORLD);
		this.defMCGBound = MGMapMethods.loadBounds(mapConfig, MGMapMethods.DEFAULT_BOUNDS);
		this.mapCenter = centerLoc;
		this.spectatorSpawn = specSpawnLoc;
		this.inGameMCGBounds = MGMapMethods.loadBounds(mapConfig, MGMapMethods.INGAME_BOUNDS);
		this.spectatorMCGBounds = MGMapMethods.loadBounds(mapConfig, MGMapMethods.SPEC_BOUNDS);
		this.spawnAreas = MGMapMethods.loadSpawnAreas(mapConfig, teamCount, world);
		this.spawnPoints = MGMapMethods.loadSpawnPoints(mapConfig, teamCount, world);
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
		
		mapConfig.set(MGMapMethods.DESCRIPTION, description);
		saveMap();
	}

	public int getMaxPlayers() { return maxPlayers; }
	public void setMaxPlayers(int maxPlayers) 
	{ 
		this.maxPlayers = maxPlayers; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
			
		mapConfig.set(MGMapMethods.MAX_PLAYERS, maxPlayers);
		saveMap();
	}

	public int getMaxTeams() { return maxTeams; }
	public void setMaxTeams(int maxTeams) 
	{ 
		this.maxTeams = maxTeams;
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		mapConfig.set(MGMapMethods.MAX_TEAMS, maxTeams);
		saveMap();
	}

	public HashMap<Integer, ArrayList<MGBound>> getSpawnAreas() { return spawnAreas; }
	public void setSpawnAreas(HashMap<Integer, ArrayList<MGBound>> spawnAreas) { this.spawnAreas = spawnAreas; }

	public HashMap<Integer, ArrayList<Location>> getSpawnPoints() { return spawnPoints; }
	public void setSpawnPoints(HashMap<Integer, ArrayList<Location>> spawnPoints) { this.spawnPoints = spawnPoints; }
	
	public void addSpawnPoint(int teamIndex, Location point)
	{
		if(teamIndex >= getMaxTeams())
			throw new IllegalArgumentException("Team Index Out Of Range!");
		
		spawnPoints.get(teamIndex).add(point);

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MGMapMethods.addPoint(MGMapMethods.SPAWN_POINTS + "." + MGMapMethods.TEAM + teamIndex, point, mapConfig);
		saveMap();

	}
	
	public void clearSpawnPoints(int teamIndex)
	{
		if(teamIndex >= getMaxTeams())
			throw new IllegalArgumentException("Team Index Out Of Range!");
		
		spawnPoints.get(teamIndex).clear();

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MGMapMethods.clearPoints(MGMapMethods.SPAWN_POINTS + "." + MGMapMethods.TEAM + teamIndex, mapConfig);
		saveMap();
	}

	public ArrayList<MGBound> getDefaultBounds() { return defMCGBound; }
	public void setDefaultBounds(ArrayList<MGBound> defBound) 
	{ 
		this.defMCGBound = defBound; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.setBounds(MGMapMethods.DEFAULT_BOUNDS, defBound, mapConfig);
		saveMap();
	}

	public ArrayList<MGBound> getInGameBounds() { return inGameMCGBounds; }
	public void setInGameBounds(ArrayList<MGBound> inGameBounds) 
	{ 
		this.inGameMCGBounds = inGameBounds;
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.setBounds(MGMapMethods.INGAME_BOUNDS, inGameBounds, mapConfig);
		saveMap();
	}

	public ArrayList<MGBound> getSpectatorBounds() { return spectatorMCGBounds; }
	public void setSpectatorBounds(ArrayList<MGBound> spectatorBounds) 
	{ 
		this.spectatorMCGBounds = spectatorBounds; 
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.setBounds(MGMapMethods.SPEC_BOUNDS, spectatorBounds, mapConfig);
		saveMap();
	}

	public String getWorldName() { return worldName; }
	public void setWorldName(String worldName) 
	{ 
		this.worldName = worldName;

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		mapConfig.set(MGMapMethods.WORLD, worldName);
		saveMap();
		rebuildMap();
	}

	public Location getMapCenter() { return mapCenter; }
	
	public void setMapCenter(Location center) 
	{ 
		mapCenter = center;

		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");

		MGMapMethods.addPoint(MGMapMethods.MAP_CENTER, center, mapConfig);
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

		MGMapMethods.addPoint(MGMapMethods.SPEC_SPAWN, spawn, mapConfig);
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
		manager.saveMapConfig(getMapName().toLowerCase() + ".yml");
	}

	public String getColor(int team)
	{
		return teamColors.get(team);
	}

	public String getPrefix(int team)
	{
		return teamPrefixes.get(team);
	}

	public void setBounds(ArrayList<MGBound> defMCGBound, ArrayList<MGBound> inGameMCGBounds, ArrayList<MGBound> spectatorMCGBounds)
	{
		setDefaultBounds(defMCGBound);
		setInGameBounds(inGameMCGBounds);
		setSpectatorBounds(spectatorMCGBounds);
	}

	public boolean contains(Location loc, boolean spectating)
	{
		if(spectating && spectatorMCGBounds != null && !spectatorMCGBounds.isEmpty())
		{
			for(MGBound MCGBound : spectatorMCGBounds)
			{
				if(MCGBound.contains(loc))
					return true;
			}
		}
		else if(!spectating && inGameMCGBounds != null && !inGameMCGBounds.isEmpty())
		{
			for(MGBound MCGBound : inGameMCGBounds)
			{
				if(MCGBound.contains(loc))
					return true;
			}
		}
		else if(defMCGBound != null && !defMCGBound.isEmpty())
		{
			for(MGBound MCGBound : defMCGBound)
			{
				if(MCGBound.contains(loc))
					return true;
			}
		}
		else
		{
			manager.plugin().getLogger().info("No Bounds Found");
			return true;
		}

		return false;
	}

	public Location getSpawnAreaLoc(int team) 
	{
		ArrayList<MGBound> areas = spawnAreas.get(team);

		if(areas == null || areas.size() == 0)
			return null;

		MGBound area = areas.get((int) (Math.random() * areas.size()));

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

	public void addDefaultBound(MGBound MCGBound) 
	{
		defMCGBound.add(MCGBound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.addBound(MGMapMethods.DEFAULT_BOUNDS, MCGBound, mapConfig);
		saveMap();
	}

	public void addInGameBound(MGBound bound) 
	{
		inGameMCGBounds.add(bound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.addBound(MGMapMethods.INGAME_BOUNDS, bound, mapConfig);
		saveMap();
	}

	public void addSpectatorBound(MGBound bound) 
	{
		spectatorMCGBounds.add(bound);
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.addBound(MGMapMethods.SPEC_BOUNDS, bound, mapConfig);
		saveMap();
	}

	public void clearDefaultBounds() 
	{
		defMCGBound.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.clearBounds(MGMapMethods.DEFAULT_BOUNDS, mapConfig);
		saveMap();
	}

	public void clearInGameBounds() 
	{
		inGameMCGBounds.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.clearBounds(MGMapMethods.INGAME_BOUNDS, mapConfig);
		saveMap();
	}

	public void clearSpectatorBounds() 
	{
		spectatorMCGBounds.clear();
		
		if(mapConfig == null)
			throw new IllegalStateException("Map " + getMapName() + " Missing File Configuration!");
		
		MGMapMethods.clearBounds(MGMapMethods.SPEC_BOUNDS, mapConfig);
		saveMap();
	}
}