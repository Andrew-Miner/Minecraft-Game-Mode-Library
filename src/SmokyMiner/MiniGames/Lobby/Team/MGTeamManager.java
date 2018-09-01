package SmokyMiner.MiniGames.Lobby.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import SmokyMiner.MiniGames.Maps.MGMapMetadata;
import SmokyMiner.MiniGames.Player.MGInvFunctions;
import SmokyMiner.MiniGames.Player.MGPlayer;
import SmokyMiner.MiniGames.Player.MGSpawnKit;

public class MGTeamManager 
{
	private ArrayList<MGTeam> teams;
	private ArrayList<MGSpawnKit> teamKit;
	
	public MGTeamManager()
	{
		teams = new ArrayList<MGTeam>();
	}
	
	public MGTeamManager(int teamCount)
	{
		teams = new ArrayList<MGTeam>();
		
		for(int i = 0; i < teamCount; i++)
		{
			teams.add(new MGTeam(i, "Team #" + i));
		}
	}
	
	public MGTeamManager(ArrayList<Integer> teamIds)
	{
		teams = new ArrayList<MGTeam>();
		
		for(int id : teamIds)
		{
			teams.add(new MGTeam(id, "Team #" + id));
		}
	}
	
	public MGTeamManager(MGMapMetadata map)
	{
		buildTeams(map);
	}
	
	public void buildTeams(MGMapMetadata map)
	{
		final int snowBallClips = 2;
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		
		MGInvFunctions.PlayerInvIndex index = MGInvFunctions.PlayerInvIndex.HotBar0;
		
		items.put(index.getValue(), new ItemStack(Material.BOW, 1));
		index = index.next();
		
		items.put(index.getValue(), new ItemStack(Material.WOODEN_SWORD, 1));
		index = index.next();
		
		for(int i = 0; i < snowBallClips; i++)
		{
			items.put(index.getValue(), new ItemStack(Material.SNOWBALL, 16));
			index = index.next();
		}

		teams = new ArrayList<MGTeam>();
		teamKit = new ArrayList<MGSpawnKit>();
		int teamCount = map.getMaxTeams();
		
		for(int i = 0; i < teamCount; i++)
		{
			String color = map.getColor(i);
			teams.add(new MGTeam(i, color + map.getPrefix(i), color));
			teamKit.add(new MGSpawnKit(MGInvFunctions.convertChatColor(color), items));
		}
	}
	
	public void clear()
	{
		Iterator<MGTeam> it = teams.iterator();

		while(it.hasNext())
		{
			it.next().clear();
		}
	}
	
	public MGTeam joinTeam(MGPlayer player)
	{
		MGTeam lowest = null;
		Iterator<MGTeam> it = teams.iterator();

		while(it.hasNext())
		{
			MGTeam team = it.next();

			if(lowest == null)
				lowest = team;
			else
			{
				if(team.size() < lowest.size())
					lowest = team;
			}
		}
		
		it = teams.iterator();
		ArrayList<MGTeam> equalSize = new ArrayList<MGTeam>();

		while(it.hasNext())
		{
			MGTeam team = it.next();
			
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
	
	public boolean leaveTeam(MGPlayer player)
	{
		MGTeam team = getTeam(player.getID());
		
		if(team == null)
			return false;

		team.removePlayer(player.getID());
		
		return true;
	}
	
	public MGTeam getTeam(int teamId)
	{
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
			
			if(team.getId() == teamId)
				return team;
		}
		
		return null;
	}
	
	public MGTeam getTeam(MGPlayer player)
	{
		return getTeam(player.getID());
	}
	
	public MGTeam getTeam(UUID pid)
	{
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
			
			if(team.getPlayer(pid) != null)
				return team;
		}
		
		return null;
	}
	
	public int getTeamCount()
	{
		return teams.size();
	}
	
	public void spawnTeams(MGMapMetadata map)
	{
		HashMap<Integer, ArrayList<Location>> spawnPoints = map.getSpawnPoints();
		int teamCount = map.getMaxTeams();
		
		for(int i = 0; i < teamCount; i++)
		{
			MGTeam team = getTeam(i);
			
			if(team == null)
				continue;

			ArrayList<MGPlayer> players = team.getPlayers();
			ArrayList<Location> points = spawnPoints.get(i);
			int teamSize = team.size();
			MGSpawnKit kit = teamKit.get(team.getId());
			
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
	
	public ArrayList<MGTeam> getAliveTeams()
	{
		ArrayList<MGTeam> alive = new ArrayList<MGTeam>();
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
			
			if(!team.isDead())
				alive.add(team);
		}
		
		return alive;
	}
	
	public boolean playable()
	{
		int playableTeams = 0;
		
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
			
			if(team.size() > 0)
				playableTeams++;
				
		}
		
		return playableTeams > 1;
	}
	
	public void addWinsAndLosses(MGTeam winningTeam)
	{
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
			
			if(team.getId() == winningTeam.getId())
				team.addWin();
			else
				team.addLoss();
		}
	}
	
	public void displayTeamTitles(int fadeIn, int stay, int fadeOut)
	{
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			it.next().displayTeamTitle(fadeIn, stay, fadeOut);
		}
	}
	
	public void displayTitles(int fadeIn, int stay, int fadeOut, String title, String subtitle)
	{
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			it.next().displayTitle(fadeIn, stay, fadeOut, title, subtitle);
		}
	}
	
	public ArrayList<MGTeam> getTeams()
	{
		return (ArrayList<MGTeam>) teams.clone();
	}
	
	public int getPlayerCount()
	{
		int count = 0;
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			count += it.next().size();
		}
		
		return count;
	}
	
	public MGTeam getLargestTeam()
	{
		MGTeam largest = null;
		int size = 0;
		
		Iterator<MGTeam> it = teams.iterator();
		
		while(it.hasNext())
		{
			MGTeam team = it.next();
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