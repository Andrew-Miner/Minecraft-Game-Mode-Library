package SmokyMiner.MiniGames.Commands;

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

import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandParser;
import SmokyMiner.MiniGames.Commands.CommandHandler.MGCommandParser.command_info;
import SmokyMiner.MiniGames.Commands.CommandHandler.MGParsedCommand;
import SmokyMiner.MiniGames.Lobby.MGLobbyManager;
import SmokyMiner.MiniGames.Lobby.MGLobbyTools;
import SmokyMiner.MiniGames.Maps.MGBound;
import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Maps.MGMapMethods;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.Minigame.Main.MGConfigMethods;
import SmokyMiner.Minigame.Main.MGManager;

//TODO: Adjust Size Of Max Lobbies Based On Enabled Map Count, Update When Enabled/Disabled
//TODO: Make Location Lock Be +- 1 coord instead of locked at block loc
//TODO: Don't allow editing maps in active rotation
//TODO: Spawn Areas add/remove
//TODO: Add/Remove Team
//TODO: Set Team Color/Name

public class MGCommandMap implements MGCommand
{
	MGManager mgManager;
	MGLobbyManager manager;
	
	public MGCommandMap(MGManager mgManager, MGLobbyManager manager)
	{
		this.mgManager = mgManager;
		this.manager = manager;
	}

	@Override
	public command_info getCommand() 
	{
		return command_info.ARENA;
	}

	@Override
	public boolean onCommand(MGParsedCommand command) 
	{
		if(command.getCommand() != command_info.ARENA)
			return false;

		ArrayList<String> nameList = command.getArgs(MGCommandParser.ARENA_ID);
		ArrayList<String> types = command.getArgs(MGCommandParser.TYPE_ID);

		if(nameList.isEmpty())
		{
			if(types.isEmpty())
				displayHelp(command.getSender());
			else if(types.get(0).equalsIgnoreCase(MGCommandParser.command_info.LIST.string()))
				listMaps(command.getSender());
		}
		else
		{
			mgManager.plugin().getLogger().info("Name List Size: " + nameList.size());
			mgManager.plugin().getLogger().info("Command Size: " + command.size());
			String arenaName = null;

			for(String name : nameList)
			{
				if(arenaName == null)
					arenaName = name;
				else
					arenaName += " " + name;
			}

			MGMapMetadata  arena = mgManager.getMaps().findMap(arenaName);

			if(types.isEmpty())
			{
				if(command.size() != nameList.size() + 1)
					return false;

				if(arena == null)
				{
					createArena(arenaName, command);
					arena = mgManager.getMaps().findMap(arenaName);

					if(arena == null)
					{
						command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Failed To Create " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Map!");

						for(MGMapMetadata map : mgManager.getMaps().getMaps())
						{
							command.getSender().sendMessage(map.getMapName());
						}
					}
					else
						command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Successfully Created!");
				}
				else
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Already Exists!");
			}
			else
			{
				String type = types.get(0);
				if(arena != null)
				{
					
					if(type.equalsIgnoreCase(MGCommandParser.command_info.INFO.string()))
						displayMapInfo(command.getSender(), arena);
					else if(type.equalsIgnoreCase(MGCommandParser.command_info.ENABLE.string()))
						enableMap(command, arenaName);
					else if(type.equalsIgnoreCase(MGCommandParser.command_info.DISABLE.string()))
						disableMap(command, arenaName);
					else if(type.equalsIgnoreCase(MGCommandParser.command_info.DESCRIPTION.string()))
						return changeDescription(command, arena);
					else
					{
						if(!mgManager.getMaps().isActive(arena))
						{
							if(type.equalsIgnoreCase(MGCommandParser.command_info.WORLD.string()))
								return changeWorldName(command, arena);
							else if(type.equalsIgnoreCase(MGCommandParser.command_info.Players.string()))
								return changeMaxPlayers(command, arena);
							else if(type.equalsIgnoreCase(MGCommandParser.command_info.Teams.string()))
								return changeMaxTeams(command, arena);
							else if(type.equalsIgnoreCase(MGCommandParser.command_info.ADD.string()) || type.equalsIgnoreCase(MGCommandParser.command_info.REMOVE.string()))
							{
								ArrayList<String> typeList = command.getArgs(MGCommandParser.WHAT_TYPE_ID);
								
								if(typeList.isEmpty())
									return false;
								
								String arType = typeList.get(0);
								
								if(type.equalsIgnoreCase(MGCommandParser.command_info.REMOVE.string()))
								{
									if(arType.equalsIgnoreCase(MGCommandParser.command_info.SPAWN.string()) || arType.equalsIgnoreCase(MGCommandParser.command_info.CENTER.string()))
										return clearPoints(command, arena);
									else if(arType.equalsIgnoreCase(MGCommandParser.command_info.BOUND.string()))
										return clearBounds(command, arena);
								}
								else
								{
									if(arType.equalsIgnoreCase(MGCommandParser.command_info.SPAWN.string()) || arType.equalsIgnoreCase(MGCommandParser.command_info.CENTER.string()))
										return addPoint(command, arena);
									else if(arType.equalsIgnoreCase(MGCommandParser.command_info.BOUND.string()))
										return addBound(command, arena);
								}
								
							}
						}
						else
							command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Cannot Edit Map While It Is In Active Rotation!");
					}
				}
				else
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW +  arenaName + ChatColor.RED + " Doesn't Exists!");
			}
		}


		return true;
	}

	private boolean clearBounds(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> whatList = command.getArgs(MGCommandParser.WHAT_ID);
		
		if(whatList.isEmpty())
			return false;
		
		String whatType = whatList.get(0);
		
		if(whatType.equalsIgnoreCase(MGCommandParser.command_info.DEFAULT.string()))
		{
			try { arena.clearDefaultBounds(); }
			catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Cleared Default Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.INGAME.string()))
		{
			try { arena.clearInGameBounds(); }
			catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Cleared In Game Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.SPECTATOR.string()))
		{
			try { arena.clearSpectatorBounds(); }
			catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Cleared Spectator Bounds On Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
		}
		else
			return false;
		
		return true;
	}

	private boolean clearPoints(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> whatList = command.getArgs(MGCommandParser.WHAT_ID);
		
		if(whatList.isEmpty())
			return false;
		
		String whatType = whatList.get(0);
		
		if(whatType.equalsIgnoreCase(MGCommandParser.command_info.CENTER.string()))
		{
			try { arena.resetMapCenter(); }
			catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Center Reset To Origin!");
		}
		else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.SPECTATOR.string()))
		{
			try { arena.resetSpectatorSpawn(); }
			catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Spectator Spawn Reset To Origin!");
		}
		else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.TEAM.string()))
		{
			ArrayList<String> teamIdList = command.getArgs(MGCommandParser.TEAM_ID);

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
				mgManager.plugin().getLogger().warning(e.getMessage()); 
			}
			catch(IllegalArgumentException e) {
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.RED + ": " + e.getMessage());
				return true;
			}
			
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Spawn Points For Team " + ChatColor.YELLOW + teamId + ChatColor.GREEN + " Cleared!");
		}
		
		
		return true;
	}

	private boolean changeDescription(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> descList = command.getArgs(MGCommandParser.DESC_ID);
		
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
		catch(IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
		
		command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + " Description Changed!");
		return true;
	}

	private boolean addBound(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> addWhatList = command.getArgs(MGCommandParser.WHAT_ID);
		
		if(addWhatList.isEmpty())
			return false;
		
		String whatType = addWhatList.get(0);
		
		if(command.getSender() instanceof Player)
		{
			Player p = (Player) command.getSender();
			MGPlayer pbp = manager.findPlayer(p.getUniqueId());
			
			if(pbp == null)
				return true;
			
			MGBound bound  = pbp.getSelection();
			MGLobbyTools.fixBoundSelection(bound);
			
			if(bound.loc1 != null && bound.loc2 != null)
			{
				if(whatType.equalsIgnoreCase(MGCommandParser.command_info.DEFAULT.string()))
				{
					try { arena.addDefaultBound(bound); }
					catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Added Default Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.INGAME.string()))
				{
					try { arena.addInGameBound(bound); }
					catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Added In Game Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else if(whatType.equalsIgnoreCase(MGCommandParser.command_info.SPECTATOR.string()))
				{
					try { arena.addSpectatorBound(bound); }
					catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Successfully Added Spectator Bound To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
				}
				else
					return false;
			}
			else
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Invalid Selection!");
		}
		else
			mgManager.plugin().getLogger().info("This command cannot be executed by the console!");
		
		return true;
	}
	
	private boolean addPoint(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> addWhatList = command.getArgs(MGCommandParser.WHAT_ID);

		if(addWhatList.isEmpty())
			return false;

		String addType = addWhatList.get(0).toLowerCase();

		if(command.getSender() instanceof Player)
		{
			DecimalFormat df = new DecimalFormat("#.#");

			Player p = (Player) command.getSender();
			Location loc = p.getLocation();

			if(addType.equalsIgnoreCase(MGCommandParser.command_info.CENTER.string()))
			{
				try { arena.setMapCenter(loc); }
				catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Center Set To " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + "!");
			}
			else if(addType.equalsIgnoreCase(MGCommandParser.command_info.SPECTATOR.string()))
			{
				try { arena.setSpectatorSpawn(loc); }
				catch (IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.GREEN + "Spectator Spawn Set " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + "!");
			}
			else if(addType.equalsIgnoreCase(MGCommandParser.command_info.TEAM.string()))
			{
				ArrayList<String> teamIdList = command.getArgs(MGCommandParser.TEAM_ID);

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
					mgManager.plugin().getLogger().warning(e.getMessage()); 
				}
				catch(IllegalArgumentException e) {
					command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.RED + ": " + e.getMessage());
					return true;
				}
				
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Spawn Point " + ChatColor.YELLOW + "{" + df.format(loc.getX()) + " " + df.format(loc.getY()) + " " + df.format(loc.getZ()) + "}" + ChatColor.GREEN + " Added To Map " + ChatColor.YELLOW + arena.getMapName() + ChatColor.GREEN + "!");
			}
			else
				return false;
		}
		else
			mgManager.plugin().getLogger().info("This command cannot be executed by the console!");

		return true;
	}

	private void listMaps(CommandSender sender) 
	{
		ArrayList<MGMapMetadata> activeMaps = mgManager.getMaps().getActiveMaps();
		ArrayList<MGMapMetadata> allMaps = mgManager.getMaps().getMaps();

		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "Paintball Maps");
		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + " Active Rotation: " + ChatColor.YELLOW + activeMaps.size());

		for(MGMapMetadata map : activeMaps)
		{
			if(map.isInUse())
				sender.sendMessage(ChatColor.GREEN + " - " + map.getMapName());
			else
				sender.sendMessage(ChatColor.GRAY + " - " + map.getMapName());
		}

		sender.sendMessage(" ");
		sender.sendMessage(ChatColor.GOLD + " All Maps: " + ChatColor.YELLOW + allMaps.size());

		for(MGMapMetadata map : allMaps)
		{
			if(map.isInUse())
				sender.sendMessage(ChatColor.GREEN + " - " + map.getMapName());
			else
				sender.sendMessage(ChatColor.GRAY + " - " + map.getMapName());
		}

		sender.sendMessage(" ");
	}

	private void displayMapInfo(CommandSender sender, MGMapMetadata map) 
	{
		sender.sendMessage(" ");
		sender.sendMessage(" " + ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + map.getMapName());
		sender.sendMessage(" ");
		sender.sendMessage(" " + ChatColor.GOLD + "Description: " + ChatColor.GRAY + map.getDescription());
		sender.sendMessage(" " + ChatColor.GOLD + "Max Players: " + ChatColor.GRAY + map.getMaxPlayers());
		sender.sendMessage(" " + ChatColor.GOLD + "Max Teams: " + ChatColor.GRAY + map.getMaxTeams());
		sender.sendMessage(" " +ChatColor.GOLD + "World: " + ChatColor.GRAY + map.getWorldName());

		sender.sendMessage(" " + ChatColor.GOLD + "Default Bounds: " + ChatColor.GRAY + map.getDefaultBounds().size());
		MGMapMethods.printBounds(sender, map.getDefaultBounds());

		sender.sendMessage(" " + ChatColor.GOLD + "In Game Bounds: " + ChatColor.GRAY + map.getInGameBounds().size());
		MGMapMethods.printBounds(sender, map.getInGameBounds());

		sender.sendMessage(" " + ChatColor.GOLD + "Spectator Bounds: " + ChatColor.GRAY + map.getSpectatorBounds().size());
		MGMapMethods.printBounds(sender, map.getSpectatorBounds());



		HashMap<Integer, ArrayList<MGBound>> areas = map.getSpawnAreas();
		int areaCount = 0;

		for(Map.Entry<Integer, ArrayList<MGBound>> entry : areas.entrySet())
		{
			if(entry.getValue() != null)
				areaCount += entry.getValue().size();
		}

		sender.sendMessage(" " + ChatColor.GOLD + "Spawn Areas: " + ChatColor.GRAY + areaCount);

		Iterator<Map.Entry<Integer, ArrayList<MGBound>>> it = areas.entrySet().iterator();

		while(it.hasNext())
		{
			Map.Entry<Integer, ArrayList<MGBound>> pair = it.next();

			sender.sendMessage(ChatColor.GRAY + "   Team " + pair.getKey() + ": ");

			int c = 0;
			for(MGBound bound : pair.getValue())
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

	private void enableMap(MGParsedCommand command, String arenaName)
	{
		try
		{
			if(mgManager.getMaps().addToRotation(arenaName))
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Added To Map Rotation!");
			else
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Already In Map Rotation!");
		}
		catch (IllegalStateException e)
		{
			if(command.getSender() instanceof Player)
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + mgManager.getMaps().findMap(arenaName).getMapName()  + ChatColor.RED + " Doesn't Meet Spawn Requirements!");
			mgManager.plugin().getLogger().warning(e.getMessage());
		}
	}

	private void disableMap(MGParsedCommand command, String arenaName)
	{
		if(mgManager.getMaps().removeFromRotation(arenaName))
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arenaName + ChatColor.GREEN + " Removed From Map Rotation!");
		else
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arenaName + ChatColor.RED + " Not In Map Rotation!");
	}

	private boolean changeWorldName(MGParsedCommand command, MGMapMetadata map)
	{
		ArrayList<String> worldList = command.getArgs(MGCommandParser.WORLD_ID);

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
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + map.getMapName() + "'s " + ChatColor.RED + " World Already Set To " + ChatColor.YELLOW + worldName + ChatColor.RED + "!");
		else
		{
			try { map.setWorldName(worldName); }
			catch(IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
			
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + map.getMapName() + "'s" + ChatColor.GREEN + " World Changed To " + ChatColor.YELLOW + worldName + ChatColor.GREEN + "!");
		}

		return true;
	}

	private boolean changeMaxTeams(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> maxTeamList = command.getArgs(MGCommandParser.MAX_TEAMS_ID);

		if(maxTeamList.size() != 1)
			return false;

		String maxTeams = maxTeamList.get(0);

		try
		{
			int max = Integer.parseInt(maxTeams);

			if(MGMapMethods.adjustTeamSize(mgManager, arena, max))
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s" + ChatColor.GREEN + " Max Teams Changed To " + ChatColor.YELLOW + max + ChatColor.GREEN + "!");			
			else
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.RED + " Max Teams Already Set To " + ChatColor.YELLOW + max + ChatColor.RED + "!");
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		catch(IllegalStateException e)
		{
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + ChatColor.ITALIC + " " + e.getMessage());
			command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + ChatColor.ITALIC + " Are the Map's Name and its File Name different disregarding case?");
			mgManager.plugin().getLogger().warning(e.getMessage());
			mgManager.plugin().getLogger().warning("Are the Map's Name and its File Name different disregarding case?");
		}

		return true;
	}

	private boolean changeMaxPlayers(MGParsedCommand command, MGMapMetadata arena) 
	{
		ArrayList<String> maxPlayerList = command.getArgs(MGCommandParser.MAX_PLAYERS_ID);

		if(maxPlayerList.size() != 1)
			return false;

		String maxPlayerss = maxPlayerList.get(0);

		try
		{
			int max = Integer.parseInt(maxPlayerss);

			if(arena.getMaxPlayers() == max)
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.RED + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s " + ChatColor.RED + " Max Players Already Set To " + ChatColor.YELLOW + max + ChatColor.RED + "!");
			else
			{
				try { arena.setMaxPlayers(max); }
				catch(IllegalStateException e) { mgManager.plugin().getLogger().warning(e.getMessage()); }
				
				command.getSender().sendMessage(ChatColor.GOLD + MGManager.logPrefix + ChatColor.GREEN + " Map " + ChatColor.YELLOW + arena.getMapName() + "'s" + ChatColor.GREEN + " Max Players Changed To " + ChatColor.YELLOW + max + ChatColor.GREEN + "!");
			}
		}
		catch(NumberFormatException e)
		{
			return false;
		}

		return true;
	}

	private void createArena(String arenaName, MGParsedCommand command) 
	{
		FileConfiguration registryConfig = mgManager.getMapRegistry();
		ArrayList<String> mapRegistry = (ArrayList<String>) registryConfig.getStringList("Map Registry");
		mapRegistry.add(arenaName.toLowerCase() + ".yml");
		registryConfig.set("Map Registry", mapRegistry);
		mgManager.saveMapRegistry();

		File mapFile = MGConfigMethods.createFile(mgManager.plugin(), arenaName.toLowerCase() + ".yml");
		FileConfiguration mapConfig = MGConfigMethods.loadFileConfiguration(mgManager.plugin(), mapFile, "defmap.yml");
		mapConfig.set(MGMapMethods.MAP_NAME, arenaName);

		if(command.getSender() instanceof Player)
			mapConfig.set(MGMapMethods.WORLD, ((Player) command.getSender()).getWorld().getName());

		mgManager.addMapConfig(mapFile, mapConfig);
		mgManager.saveMapConfig(mapFile.getName());
	}
}
