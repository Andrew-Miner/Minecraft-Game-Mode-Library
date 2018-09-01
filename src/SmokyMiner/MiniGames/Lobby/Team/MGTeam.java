package SmokyMiner.MiniGames.Lobby.Team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Player.MGPlayer;

public class MGTeam 
{
	private ArrayList<MGPlayer> players;
	private int teamId;
	private int deadCount;
	private String teamName;
	private String teamColor;
	
	
	public MGTeam()
	{
		players = null;
		teamId = -1;
		deadCount = -1;
	}
	
	public MGTeam(int teamId, String teamName)
	{
		this.teamId = teamId;
		players = new ArrayList<MGPlayer>();
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor("" + ChatColor.WHITE);
	}
	
	public MGTeam(int teamId, String teamName, String color)
	{
		this.teamId = teamId;
		players = new ArrayList<MGPlayer>();
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor(color);
	}

	public MGTeam(int teamId, ArrayList<MGPlayer> players, String teamName, String teamColor)
	{
		this.teamId = teamId;
		this.players = players;
		deadCount = 0;
		this.setTeamName(teamName);
		this.setTeamColor(teamColor);
	}
	
	public void addPlayer(MGPlayer player)
	{
		players.add(player);
	}
	
	public void removePlayer(MGPlayer player)
	{
		removePlayer(player.getID());
	}
	
	public MGPlayer removePlayer(UUID player)
	{
		Iterator<MGPlayer> it = players.iterator();
		while(it.hasNext())
		{
			MGPlayer p = it.next();
			
			if(p.getID() == player)
			{
				if(p.isDead())
					deadCount--;
				
				it.remove();
				return p;
			}
		}
		return null;
	}
	
	public MGPlayer getPlayer(UUID player)
	{
		Iterator<MGPlayer> it = players.iterator();
		while(it.hasNext())
		{
			MGPlayer p = it.next();
			
			if(p.getID() == player)
				return p;
		}
		return null;
	}
	
	public int getDeadCount()
	{
		return deadCount;
	}
	
	public boolean isDead()
	{
		return deadCount == players.size();
	}
	
	public void clear()
	{
		players.clear();
		deadCount = 0;
	}
	
	public void clearDead()
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().setDead(false);
		}
		
		deadCount = 0;
	}
	
	public boolean setDead(UUID player)
	{
		MGPlayer p = getPlayer(player);
		
		if(p == null)
			return false;
		
		p.setDead(true);
		deadCount++;
		
		return true;
	}
	
	public boolean setAlive(UUID player)
	{
		MGPlayer p = getPlayer(player);
		
		if(p == null)
			return false;
		
		p.setDead(false);
		deadCount--;
		
		return true;
	}
	
	public int getId()
	{
		return teamId;
	}
	
	public int size()
	{
		return players.size();
	}
	
	public ArrayList<MGPlayer> getPlayers()
	{
		return players;
	}

	public String getTeamName() 
	{
		return teamName;
	}

	public void setTeamName(String teamName) 
	{
		this.teamName = teamName;
	}
	
	public void addWin()
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().addWin();
		}
	}
	
	public void addLoss()
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			it.next().addLoss();
		}
	}
	
	public void displayTitle(int fadeIn, int stay, int fadeOut, String title, String subtitle)
	{
		Iterator<MGPlayer> it = players.iterator();
		
		while(it.hasNext())
		{
			Bukkit.getPlayer(it.next().getID()).sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		}
	}
	
	public void displayTeamTitle(int fadeIn, int stay, int fadeOut)
	{
		String title = ChatColor.GOLD + "Welcome To The " + teamName + " " + ChatColor.GOLD + " Team!";
		String subtitle = "";
		
		
		Iterator<MGPlayer> it = players.iterator();
		
		if(it.hasNext())
		{
			subtitle = Bukkit.getPlayer(it.next().getID()).getName();
		
			while(it.hasNext())
			{
				subtitle += ", " + Bukkit.getPlayer(it.next().getID()).getName();
			}
			
			displayTitle(fadeIn, stay, fadeOut, title, subtitle);
		}
	}

	public String getTeamColor() 
	{
		return teamColor;
	}

	public void setTeamColor(String color) 
	{
		this.teamColor = color;
	}
}
