package SmokyMiner.MiniGames.Lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;

import SmokyMiner.MiniGames.Lobby.Browser.MGLobbyBrowser;
import SmokyMiner.MiniGames.Maps.MGBound;

public class MGLobbyTools 
{
	public static ArrayList<MGLobby> getOpenLobbies(ArrayList<MGLobby> lobbies)
	{
		ArrayList<MGLobby> open = new ArrayList<MGLobby>();
		Iterator<MGLobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			MGLobby lobby = it.next();
			
			if(!lobby.isFull())
				open.add(lobby);
		}
		
		if(!open.isEmpty())
			return open;
		
		return null;
	}
	
	public static MGLobby getLargestPlayerCount(ArrayList<MGLobby> lobbies)
	{
		Iterator<MGLobby> it = lobbies.iterator();
		MGLobby largest = null;
		
		while(it.hasNext())
		{
			MGLobby l = it.next();
			
			if(largest == null || l.playerCount() > largest.playerCount())
				largest = l;
		}
		
		return largest;
	}
	
	public static MGLobby getSmallestPlayerCount(ArrayList<MGLobby> lobbies)
	{
		Iterator<MGLobby> it = lobbies.iterator();
		MGLobby smallest = null;
		
		while(it.hasNext())
		{
			MGLobby l = it.next();
			
			if(smallest == null || l.playerCount() < smallest.playerCount())
				smallest = l;
		}
		
		return smallest;
	}
	
	public static void insertSorted(ArrayList<MGLobby> list, MGLobby lobby) 
	{
	    int pos = Collections.binarySearch(list, lobby);
	    
	    if (pos < 0)
	        list.add(-pos-1, lobby);
	}
	
	public static void fixBoundSelection(MGBound bound)
	{
		if(bound.loc1 != null && bound.loc2 != null)
		{
			if(bound.loc1.getX() - bound.loc2.getX() < 0)
			{
				bound.loc1.setX(bound.loc1.getX() + 1.29);
				bound.loc2.setX(bound.loc2.getX() - 0.29);
			}
			else
			{
				bound.loc2.setX(bound.loc2.getX() + 1.29);
				bound.loc1.setX(bound.loc1.getX() - 0.29);
			}
			

			if(bound.loc1.getY() - bound.loc2.getY() < 0)
			{
				bound.loc1.setY(bound.loc1.getY() + 1);
				bound.loc2.setY(bound.loc2.getY() - 1.79);
			}
			else
			{
				bound.loc2.setY(bound.loc2.getY() + 1);
				bound.loc1.setY(bound.loc1.getY() - 1.79);
			}
			

			if(bound.loc1.getZ() - bound.loc2.getZ() < 0)
			{
				bound.loc1.setZ(bound.loc1.getZ() + 1.29);
				bound.loc2.setZ(bound.loc2.getZ() - 0.29);
			}
			else
			{
				bound.loc2.setZ(bound.loc2.getZ() + 1.29);
				bound.loc1.setZ(bound.loc1.getZ() - 0.29);
			}
		}
	}
	
	public static List<String> buildLobbyLore(MGLobby lobby)
	{
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add("");
		lore.add(MGLobbyBrowser.PLAYERS_PREFIX + ChatColor.WHITE + lobby.playerCount() + '/' + lobby.getMaxPlayerCount());
		lore.add("");
		lore.add(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Click To Join!");
		
		return lore;
	}
}