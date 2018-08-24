package com.gmail.andrew.miner3.plugins.Paintball.Commands;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.ConfigMethods;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.LobbyTools;
import com.gmail.andrew.miner3.plugins.Paintball.Commands.CommandParser.Command;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadataMethods;


// TODO: Adjust Size Of Max Lobbies Based On Enabled Map Count, Update When Enabled/Disabled
// TODO: Make Location Lock Be +- 1 coord instead of locked at block loc
// TODO: Don't allow editing maps in active rotation
// TODO: Spawn Areas add/remove
// TODO: Add/Remove Team
// TODO: Set Team Color/Name

public class CommandMap implements PaintballCommand
{
	PluginManager plugin;
	LobbyManager manager;
	
	public CommandMap(PluginManager plugin, LobbyManager manager)
	{
		this.plugin = plugin;
		this.manager = manager;
	}

	@Override
	public Command getCommand() 
	{
		return Command.ARENA;
	}

	@Override
	public boolean onCommand(ParsedCommand command) 
	{
		if(command.getCommand() != Command.ARENA)
			return false;

		ArrayList<String> nameList = command.getArgs(CommandParser.ARENA_ID);
		ArrayList<String> types = command.getArgs(CommandParser.TYPE_ID);

		if(nameList.isEmpty())
		{
			if(types.isEmpty())
				displayHelp(command.getSender());
			else if(types.get(0).equalsIgnoreCase(CommandParser.Command.LIST.string()))
				listMaps(command.getSender());
		}
		else
		{
			plugin.getLogger().info("Name List Size: " + nameList.size());
			plugin.getLogger().info("Command Size: " + command.size());
			String arenaName = null;

			for(String name : nameList)
			{
				if(arenaName == null)
					arenaName = name;
				else
					arenaName += " " + name;
			}

			MapMetadata  arena = plugin.getMaps().findMap(arenaName);

			if(types.isEmpty())
			{
				if(command.size() != nameList.size() + 1)
					return false;

				if(arena == null)
				{
					createArena(arenaName, command);
					arena = plugin.getMaps().findMap(arenaName);

					if(arena == null)
					{
						command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Failed To Create " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Map!");

						for(MapMetadata map : plugin.getMaps().getMaps())
						{
							command.getSender().sendMessage(map.getMapName());
						}
					}
					else
						command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Successfully Created!");
				}
				else
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Already Exists!");
			}
			else
			{
				String type = types.get(0);
				if(arena != null)
				{
					
					if(type.equalsIgnoreCase(CommandParser.Command.INFO.string()))
						displayMapInfo(command.getSender(), arena);
					else if(type.equalsIgnoreCase(CommandParser.Command.ENABLE.string()))
						enableMap(command, arenaName);
					else if(type.equalsIgnoreCase(CommandParser.Command.DISABLE.string()))
						disableMap(command, arenaName);
					else if(type.equalsIgnoreCase(CommandParser.Command.DESCRIPTION.string()))
						return changeDescription(command, arena);
					else
					{
						if(!plugin.getMaps().isActive(arena))
						{
							if(type.equalsIgnoreCase(CommandParser.Command.WORLD.string()))
								return changeWorldName(command, arena);
							else if(type.equalsIgnoreCase(CommandParser.Command.Players.string()))
								return changeMaxPlayers(command, arena);
							else if(type.equalsIgnoreCase(CommandParser.Command.Teams.string()))
								return changeMaxTeams(command, arena);
							else if(type.equalsIgnoreCase(CommandParser.Command.ADD.string()) || type.equalsIgnoreCase(CommandParser.Command.REMOVE.string()))
							{
								ArrayList<String> typeList = command.getArgs(CommandParser.WHAT_TYPE_ID);
								
								if(typeList.isEmpty())
									return false;
								
								String arType = typeList.get(0);
								
								if(type.equalsIgnoreCase(CommandParser.Command.REMOVE.string()))
								{
									if(arType.equalsIgnoreCase(CommandParser.Command.SPAWN.string()) || arType.equalsIgnoreCase(CommandParser.Command.CENTER.string()))
										return clearPoints(command, arena);
									else if(arType.equalsIgnoreCase(CommandParser.Command.BOUND.string()))
										return clearBounds(command, arena);
								}
								else
								{
									if(arType.equalsIgnoreCase(CommandParser.Command.SPAWN.string()) || arType.equalsIgnoreCase(CommandParser.Command.CENTER.string()))
										return addPoint(command, arena);
									else if(arType.equalsIgnoreCase(CommandParser.Command.BOUND.string()))
										return addBound(command, arena);
								}
								
							}
						}
						else
							command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Cannot Edit Map While It Is In Active Rotation!");
					}
				}
				else
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW +  arenaName + ChatColor.RED + " Doesn't Exists!");
			}
		}


		return true;
	}

	private boolean clearBounds(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> whatList = command.getArgs(CommandParser.WHAT_ID);
		
		if(whatList.isEmpty())
			return false;
		
		String whatType = whatList.get(0);
		
		if(whatType.equalsIgnoreCase(CommandParser.Command.DEFAULT.string()))
		{
			try { arena.clearDefaultBounds(); }
			catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Cleared Default Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else if(whatType.equalsIgnoreCase(CommandParser.Command.INGAME.string()))
		{
			try { arena.clearInGameBounds(); }
			catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Cleared In Game Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else if(whatType.equalsIgnoreCase(CommandParser.Command.SPECTATOR.string()))
		{
			try { arena.clearSpectatorBounds(); }
			catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Cleared Spectator Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else
			return false;
		
		return true;
	}

	private boolean clearPoints(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> whatList = command.getArgs(CommandParser.WHAT_ID);
		
		if(whatList.isEmpty())
			return false;
		
		String whatType = whatList.get(0);
		
		if(whatType.equalsIgnoreCase(CommandParser.Command.CENTER.string()))
		{
			try { arena.resetMapCenter(); }
			catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Center Reset To Origin!");
		}
		else if(whatType.equalsIgnoreCase(CommandParser.Command.SPECTATOR.string()))
		{
			try { arena.resetSpectatorSpawn(); }
			catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Spectator Spawn Reset To Origin!");
		}
		else if(whatType.equalsIgnoreCase(CommandParser.Command.TEAM.string()))
		{
			ArrayList<String> teamIdList = command.getArgs(CommandParser.TEAM_ID);

			if(teamIdList.isEmpty())
				return false;

			int teamId;
			try { 
				teamId = Integer.parseInt(teamIdList.get(0)); 
			}
			catch(NumberFormatException e) { 
				return false; 
			}

			try { 
				arena.clearSpawnPoints(teamId); 
			}
			catch(IllegalStateException e) { 
				plugin.getLogger().warning(e.getMessage()); 
			}
			catch(IllegalArgumentException e) {
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.RED + ": " + e.getMessage());
				return true;
			}
			
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Spawn Points For Team " + ChatColor.YELLOW + teamId + ChatColor.GREEN + " Cleared!");
		}
		
		
		return true;
	}

	private boolean changeDescription(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> descList = command.getArgs(CommandParser.DESC_ID);
		
		if(descList.isEmpty())
			return false;
		
		String descr = null;
		
		for(String str : descList)
		{
			if(descr == null)
				descr = str;
			else
				descr += " " + str;
		}
		
		try { arena.setDescription(descr); }
		catch(IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
		
		command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + " Description Changed!");
		return true;
	}

	private boolean addBound(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> addWhatList = command.getArgs(CommandParser.WHAT_ID);
		
		if(addWhatList.isEmpty())
			return false;
		
		String whatType = addWhatList.get(0);
		
		if(command.getSender() instanceof Player)
		{
			Player p = (Player) command.getSender();
			PBPlayer pbp = manager.findPlayer(p.getUniqueId());
			
			if(pbp == null)
				return true;
			
			Bound bound  = pbp.getSelection();
			LobbyTools.fixBoundSelection(bound);
			
			if(bound.loc1 != null && bound.loc2 != null)
			{
				if(whatType.equalsIgnoreCase(CommandParser.Command.DEFAULT.string()))
				{
					try { arena.addDefaultBound(bound); }
					catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Added Default Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else if(whatType.equalsIgnoreCase(CommandParser.Command.INGAME.string()))
				{
					try { arena.addInGameBound(bound); }
					catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Added In Game Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else if(whatType.equalsIgnoreCase(CommandParser.Command.SPECTATOR.string()))
				{
					try { arena.addSpectatorBound(bound); }
					catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Successfully Added Spectator Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else
					return false;
			}
			else
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Invalid Selection!");
		}
		else
			plugin.getLogger().info("This command cannot be executed by the console!");
		
		return true;
	}
	
	private boolean addPoint(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> addWhatList = command.getArgs(CommandParser.WHAT_ID);

		if(addWhatList.isEmpty())
			return false;

		String addType = addWhatList.get(0).toLowerCase();

		if(command.getSender() instanceof Player)
		{
			DecimalFormat df = new DecimalFormat("#.#");

			Player p = (Player) command.getSender();
			Location loc = p.getLocation();

			if(addType.equalsIgnoreCase(CommandParser.Command.CENTER.string()))
			{
				try { arena.setMapCenter(loc); }
				catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Center Set To " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + "!");
			}
			else if(addType.equalsIgnoreCase(CommandParser.Command.SPECTATOR.string()))
			{
				try { arena.setSpectatorSpawn(loc); }
				catch (IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Spectator Spawn Set " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + "!");
			}
			else if(addType.equalsIgnoreCase(CommandParser.Command.TEAM.string()))
			{
				ArrayList<String> teamIdList = command.getArgs(CommandParser.TEAM_ID);

				if(teamIdList.isEmpty())
					return false;

				int teamId;
				try { 
					teamId = Integer.parseInt(teamIdList.get(0)); 
				}
				catch(NumberFormatException e) { 
					return false; 
				}

				try { 
					arena.addSpawnPoint(teamId, loc); 
				}
				catch(IllegalStateException e) { 
					plugin.getLogger().warning(e.getMessage()); 
				}
				catch(IllegalArgumentException e) {
					command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.RED + ": " + e.getMessage());
					return true;
				}
				
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Spawn Point " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + " Added To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
			}
			else
				return false;
		}
		else
			plugin.getLogger().info("This command cannot be executed by the console!");

		return true;
	}

	private void listMaps(CommandSender sender) 
	{
		ArrayList<MapMetadata> activeMaps = plugin.getMaps().getActiveMaps();
		ArrayList<MapMetadata> allMaps = plugin.getMaps().getMaps();

		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "Paintball Maps");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + " Active Rotation: " + ChatColor.YELLOW + activeMaps.size());

		for(MapMetadata map : activeMaps)
		{
			if(map.isInUse())
				sender.sendMessage(ChatColor.GREEN + " - " + map.getMapName());
			else
				sender.sendMessage(ChatColor.GRAY + " - " + map.getMapName());
		}

		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + " All Maps: " + ChatColor.YELLOW + allMaps.size());

		for(MapMetadata map : allMaps)
		{
			if(map.isInUse())
				sender.sendMessage(ChatColor.GREEN + " - " + map.getMapName());
			else
				sender.sendMessage(ChatColor.GRAY + " - " + map.getMapName());
		}

		sender.sendMessage(" ");
	}

	private void displayMapInfo(CommandSender sender, MapMetadata map) 
	{
		sender.sendMessage(" ");
		sender.sendMessage(" " + ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + map.getMapName());
		sender.sendMessage(" ");
		sender.sendMessage(" " + ChatColor.GOLD + "Description: " + ChatColor.GRAY + map.getDescription());
		sender.sendMessage(" " + ChatColor.GOLD + "Max Players: " + ChatColor.GRAY + map.getMaxPlayers());
		sender.sendMessage(" " + ChatColor.GOLD + "Max Teams: " + ChatColor.GRAY + map.getMaxTeams());
		sender.sendMessage(" " +ChatColor.GOLD + "World: " + ChatColor.GRAY + map.getWorldName());

		sender.sendMessage(" " + ChatColor.GOLD + "Default Bounds: " + ChatColor.GRAY + map.getDefaultBounds().size());
		MapMetadataMethods.printBounds(sender, map.getDefaultBounds());

		sender.sendMessage(" " + ChatColor.GOLD + "In Game Bounds: " + ChatColor.GRAY + map.getInGameBounds().size());
		MapMetadataMethods.printBounds(sender, map.getInGameBounds());

		sender.sendMessage(" " + ChatColor.GOLD + "Spectator Bounds: " + ChatColor.GRAY + map.getSpectatorBounds().size());
		MapMetadataMethods.printBounds(sender, map.getSpectatorBounds());



		HashMap<Integer, ArrayList<Bound>> areas = map.getSpawnAreas();
		int areaCount = 0;

		for(Map.Entry<Integer, ArrayList<Bound>> entry : areas.entrySet())
		{
			if(entry.getValue() != null)
				areaCount += entry.getValue().size();
		}

		sender.sendMessage(" " + ChatColor.GOLD + "Spawn Areas: " + ChatColor.GRAY + areaCount);

		Iterator<Map.Entry<Integer, ArrayList<Bound>>> it = areas.entrySet().iterator();

		while(it.hasNext())
		{
			Map.Entry<Integer, ArrayList<Bound>> pair = it.next();

			sender.sendMessage(ChatColor.GRAY + "   Team " + pair.getKey() + ": ");

			int c = 0;
			for(Bound bound : pair.getValue())
			{
				sender.sendMessage(ChatColor.GRAY + "     B" + c + ":");
				sender.sendMessage(ChatColor.GRAY + "       P0: " + bound.loc1.getX() + " " + bound.loc1.getY() + " " + bound.loc1.getZ());
				sender.sendMessage(ChatColor.GRAY + "       P1: " + bound.loc2.getX() + " " + bound.loc2.getY() + " " + bound.loc2.getZ());
				c++;
			}
		}



		HashMap<Integer, ArrayList<Location>> points = map.getSpawnPoints();
		int pointCount = 0;

		for(Map.Entry<Integer, ArrayList<Location>> entry : points.entrySet())
		{
			if(entry.getValue() != null)
				pointCount += entry.getValue().size();
		}

		sender.sendMessage(" " + ChatColor.GOLD + "Spawn Points: " + ChatColor.GRAY + pointCount);

		Iterator<Map.Entry<Integer, ArrayList<Location>>> it1 = points.entrySet().iterator();

		while(it1.hasNext())
		{
			Map.Entry<Integer, ArrayList<Location>> pair = it1.next();

			sender.sendMessage(ChatColor.GRAY + "   Team " + pair.getKey() + ": ");

			int c = 0;
			for(Location loc : pair.getValue())
			{
				sender.sendMessage(ChatColor.GRAY + "     P" + c + ": " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
				c++;
			}
		}

		int maxTeams = map.getMaxTeams();

		for(int i = 0; i < maxTeams; i++)
		{
			sender.sendMessage(ChatColor.GOLD + " Team " + i + ": ");
			sender.sendMessage(ChatColor.GRAY + "     Color: " + map.getTeamColors().get(i).charAt(1));
			sender.sendMessage(ChatColor.GRAY + "     Name: " + map.getTeamPrefixes().get(i));
		}

		sender.sendMessage(" ");
	}

	private void displayHelp(CommandSender sender) {
		// TODO Auto-generated method stub
		sender.sendMessage("displayHelp() called!");
	}

	private void enableMap(ParsedCommand command, String arenaName)
	{
		try
		{
			if(plugin.getMaps().addToRotation(arenaName))
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Added To Map Rotation!");
			else
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Already In Map Rotation!");
		}
		catch (IllegalStateException e)
		{
			if(command.getSender() instanceof Player)
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + plugin.getMaps().findMap(arenaName).getMapName()  + ChatColor.RED + " Doesn't Meet Spawn Requirements!");
			plugin.getLogger().warning(e.getMessage());
		}
	}

	private void disableMap(ParsedCommand command, String arenaName)
	{
		if(plugin.getMaps().removeFromRotation(arenaName))
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Removed From Map Rotation!");
		else
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Not In Map Rotation!");
	}

	private boolean changeWorldName(ParsedCommand command, MapMetadata map)
	{
		ArrayList<String> worldList = command.getArgs(CommandParser.WORLD_ID);

		if(worldList.size() == 0)
			return false;

		String worldName = null;

		for(String str : worldList)
		{
			if(worldName == null)
				worldName = str;
			else
				worldName += " " + str;
		}

		if(map.getWorldName().equals(worldName))
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + map.getMapName() + "'s " + ChatColor.RED + " World Already Set To " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
		else
		{
			try { map.setWorldName(worldName); }
			catch(IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
			
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + map.getMapName() + "'s" + ChatColor.GREEN + " World Changed To " + ChatColor.YELLOW + worldName + ChatColor.GREEN + "!");
		}

		return true;
	}

	private boolean changeMaxTeams(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> maxTeamList = command.getArgs(CommandParser.MAX_TEAMS_ID);

		if(maxTeamList.size() != 1)
			return false;

		String maxTeams = maxTeamList.get(0);

		try
		{
			int max = Integer.parseInt(maxTeams);

			if(MapMetadataMethods.adjustTeamSize(plugin, arena, max))
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s" + ChatColor.GREEN + " Max Teams Changed To " + ChatColor.YELLOW + max + ChatColor.GREEN + "!");			
			else
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.RED + " Max Teams Already Set To " + ChatColor.YELLOW + max + ChatColor.RED + "!");
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		catch(IllegalStateException e)
		{
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + ChatColor.ITALIC + " " + e.getMessage());
			command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + ChatColor.ITALIC + " Are the Map's Name and its File Name different disregarding case?");
			plugin.getLogger().warning(e.getMessage());
			plugin.getLogger().warning("Are the Map's Name and its File Name different disregarding case?");
		}

		return true;
	}

	private boolean changeMaxPlayers(ParsedCommand command, MapMetadata arena) 
	{
		ArrayList<String> maxPlayerList = command.getArgs(CommandParser.MAX_PLAYERS_ID);

		if(maxPlayerList.size() != 1)
			return false;

		String maxPlayerss = maxPlayerList.get(0);

		try
		{
			int max = Integer.parseInt(maxPlayerss);

			if(arena.getMaxPlayers() == max)
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.RED + " Max Players Already Set To " + ChatColor.YELLOW + max + ChatColor.RED + "!");
			else
			{
				try { arena.setMaxPlayers(max); }
				catch(IllegalStateException e) { plugin.getLogger().warning(e.getMessage()); }
				
				command.getSender().sendMessage(ChatColor.GOLD + PluginManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s" + ChatColor.GREEN + " Max Players Changed To " + ChatColor.YELLOW + max + ChatColor.GREEN + "!");
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}

		return true;
	}

	private void createArena(String arenaName, ParsedCommand command) 
	{
		FileConfiguration registryConfig = plugin.getMapRegistry();
		ArrayList<String> mapRegistry = (ArrayList<String>) registryConfig.getStringList("Map Registry");
		mapRegistry.add(arenaName.toLowerCase() + ".yml");
		registryConfig.set("Map Registry", mapRegistry);
		plugin.saveMapRegistry();

		File mapFile = ConfigMethods.createFile(plugin, arenaName.toLowerCase() + ".yml");
		FileConfiguration mapConfig = ConfigMethods.loadFileConfiguration(plugin, mapFile, "defmap.yml");
		mapConfig.set(MapMetadataMethods.MAP_NAME, arenaName);

		if(command.getSender() instanceof Player)
			mapConfig.set(MapMetadataMethods.WORLD, ((Player) command.getSender()).getWorld().getName());

		plugin.addMapConfig(mapFile, mapConfig);
		plugin.saveMapConfig(mapFile.getName());
	}
}
