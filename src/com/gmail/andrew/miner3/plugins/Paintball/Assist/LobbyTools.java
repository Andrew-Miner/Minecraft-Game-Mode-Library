package com.gmail.andrew.miner3.plugins.Paintball.Assist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;

import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyBrowser.LobbyBrowser;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.Bound;

public class LobbyTools 
{
	public static ArrayList<Lobby> getOpenLobbies(ArrayList<Lobby> lobbies)
	{
		ArrayList<Lobby> open = new ArrayList<Lobby>();
		Iterator<Lobby> it = lobbies.iterator();
		
		while(it.hasNext())
		{
			Lobby lobby = it.next();
			
			if(!lobby.isFull())
				open.add(lobby);
		}
		
		if(!open.isEmpty())
			return open;
		
		return null;
	}
	
	public static Lobby getLargestPlayerCount(ArrayList<Lobby> lobbies)
	{
		Iterator<Lobby> it = lobbies.iterator();
		Lobby largest = null;
		
		while(it.hasNext())
		{
			Lobby l = it.next();
			
			if(largest == null || l.playerCount() > largest.playerCount())
				largest = l;
		}
		
		return largest;
	}
	
	public static Lobby getSmallestPlayerCount(ArrayList<Lobby> lobbies)
	{
		Iterator<Lobby> it = lobbies.iterator();
		Lobby smallest = null;
		
		while(it.hasNext())
		{
			Lobby l = it.next();
			
			if(smallest == null || l.playerCount() < smallest.playerCount())
				smallest = l;
		}
		
		return smallest;
	}
	
	public static void insertSorted(ArrayList<Lobby> list, Lobby lobby) 
	{
	    int pos = Collections.binarySearch(list, lobby);
	    
	    if (pos < 0)
	        list.add(-pos-1, lobby);
	}
	
	public static void fixBoundSelection(Bound bound)
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
	
	public static List<String> buildLobbyLore(Lobby lobby)
	{
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add("");
		lore.add(LobbyBrowser.PLAYERS_PREFIX + ChatColor.WHITE + lobby.playerCount() + '/' + lobby.getMaxPlayerCount());
		lore.add("");
		lore.add(ChatColor.WHITE + "" + ChatColor.UNDERLINE + "Click To Join!");
		
		return lore;
	}
}
