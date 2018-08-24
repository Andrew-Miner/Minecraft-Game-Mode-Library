package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.SpawnKit;
import com.gmail.andrew.miner3.plugins.Paintball.Assist.InventoryMethods;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;

public class TeamManager 
{
	private ArrayList<PaintballTeam> teams;
	private ArrayList<SpawnKit> teamKit;
	
	public TeamManager()
	{
		teams = new ArrayList<PaintballTeam>();
	}
	
	public TeamManager(int teamCount)
	{
		teams = new ArrayList<PaintballTeam>();
		
		for(int i = 0; i < teamCount; i++)
		{
			teams.add(new PaintballTeam(i, "Team #" + i));
		}
	}
	
	public TeamManager(ArrayList<Integer> teamIds)
	{
		teams = new ArrayList<PaintballTeam>();
		
		for(int id : teamIds)
		{
			teams.add(new PaintballTeam(id, "Team #" + id));
		}
	}
	
	public TeamManager(MapMetadata map)
	{
		buildTeams(map);
	}
	
	public void buildTeams(MapMetadata map)
	{
		final int snowBallClips = 2;
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		
		InventoryMethods.PlayerInvIndex index = InventoryMethods.PlayerInvIndex.HotBar0;
		
		items.put(index.getValue(), new ItemStack(Material.BOW, 1));
		index = index.next();
		
		items.put(index.getValue(), new ItemStack(Material.WOOD_SWORD, 1));
		index = index.next();
		
		for(int i = 0; i < snowBallClips; i++)
		{
			items.put(index.getValue(), new ItemStack(Material.SNOW_BALL, 16));
			index = index.next();
		}

		teams = new ArrayList<PaintballTeam>();
		teamKit = new ArrayList<SpawnKit>();
		int teamCount = map.getMaxTeams();
		
		for(int i = 0; i < teamCount; i++)
		{
			String color = map.getColor(i);
			teams.add(new PaintballTeam(i, color + map.getPrefix(i), color));
			teamKit.add(new SpawnKit(InventoryMethods.convertChatColor(color), items));
		}
	}
	
	public void clear()
	{
		Iterator<PaintballTeam> it = teams.iterator();

		while(it.hasNext())
		{
			it.next().clear();
		}
	}
	
	public PaintballTeam joinTeam(PBPlayer player)
	{
		PaintballTeam lowest = null;
		Iterator<PaintballTeam> it = teams.iterator();

		while(it.hasNext())
		{
			PaintballTeam team = it.next();

			if(lowest == null)
				lowest = team;
			else
			{
				if(team.size() < lowest.size())
					lowest = team;
			}
		}
		
		it = teams.iterator();
		ArrayList<PaintballTeam> equalSize = new ArrayList<PaintballTeam>();

		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(team.size() == lowest.size())
				equalSize.add(team);
		}
		
		lowest = equalSize.get((int) (Math.random()*equalSize.size()));
		int before = lowest.size();
		lowest.addPlayer(player);
		int after = lowest.size();
		
		Bukkit.getLogger().info(Bukkit.getPlayer(player.getID()) + " " + before + "|" + after + " ||" + teams.size());
		return lowest;
	}
	
	public boolean leaveTeam(PBPlayer player)
	{
		PaintballTeam team = getTeam(player.getID());
		
		if(team == null)
			return false;

		team.removePlayer(player.getID());
		
		return true;
	}
	
	public PaintballTeam getTeam(int teamId)
	{
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(team.getId() == teamId)
				return team;
		}
		
		return null;
	}
	
	public PaintballTeam getTeam(PBPlayer player)
	{
		return getTeam(player.getID());
	}
	
	public PaintballTeam getTeam(UUID pid)
	{
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(team.getPlayer(pid) != null)
				return team;
		}
		
		return null;
	}
	
	public int getTeamCount()
	{
		return teams.size();
	}
	
	public void spawnTeams(MapMetadata map)
	{
		HashMap<Integer, ArrayList<Location>> spawnPoints = map.getSpawnPoints();
		int teamCount = map.getMaxTeams();
		
		for(int i = 0; i < teamCount; i++)
		{
			PaintballTeam team = getTeam(i);
			
			if(team == null)
				continue;

			ArrayList<PBPlayer> players = team.getPlayers();
			ArrayList<Location> points = spawnPoints.get(i);
			int teamSize = team.size();
			SpawnKit kit = teamKit.get(team.getId());
			
			Bukkit.getLogger().info("Team Size: " + teamSize);
			

			if(team.size() <= points.size())
			{ 
				for(int j = 0; j < teamSize; j++)
				{
					players.get(j).spawn(points.get(j), kit);
					players.get(j).storeLocation();
				}
			}
			else
			{
				int pid = 0;
				
				for(Location loc : points)
				{
					players.get(pid).spawn(loc, kit);
					players.get(pid).storeLocation();
					pid++;
				}

				for(int p = pid; p < teamSize; p++)
				{
					players.get(p).spawn(map.getSpawnAreaLoc(i), kit);
					players.get(p).storeLocation();
				}

			}
		} 
	}
	
	public ArrayList<PaintballTeam> getAliveTeams()
	{
		ArrayList<PaintballTeam> alive = new ArrayList<PaintballTeam>();
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(!team.isDead())
				alive.add(team);
		}
		
		return alive;
	}
	
	public boolean playable()
	{
		int playableTeams = 0;
		
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(team.size() > 0)
				playableTeams++;
				
		}
		
		return playableTeams > 1;
	}
	
	public void addWinsAndLosses(PaintballTeam winningTeam)
	{
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			
			if(team.getId() == winningTeam.getId())
				team.addWin();
			else
				team.addLoss();
		}
	}
	
	public void displayTeamTitles(int fadeIn, int stay, int fadeOut)
	{
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			it.next().displayTeamTitle(fadeIn, stay, fadeOut);
		}
	}
	
	public void displayTitles(int fadeIn, int stay, int fadeOut, String title, String subtitle)
	{
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			it.next().displayTitle(fadeIn, stay, fadeOut, title, subtitle);
		}
	}
	
	public ArrayList<PaintballTeam> getTeams()
	{
		return (ArrayList<PaintballTeam>) teams.clone();
	}
	
	public int getPlayerCount()
	{
		int count = 0;
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			count += it.next().size();
		}
		
		return count;
	}
	
	public PaintballTeam getLargestTeam()
	{
		PaintballTeam largest = null;
		int size = 0;
		
		Iterator<PaintballTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			PaintballTeam team = it.next();
			int s = team.size();
			
			if(largest == null || s > size)
			{
				size = team.size();
				largest = team;
			}
		}
		
		return largest;
	}
}
