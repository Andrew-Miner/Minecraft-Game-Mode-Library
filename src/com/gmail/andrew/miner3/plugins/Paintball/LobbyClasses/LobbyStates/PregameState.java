package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyStates;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.gmail.andrew.miner3.plugins.Paintball.PBPlayer;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.SpawnKit;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Scoreboards.PregameScoreboard;
import com.gmail.andrew.miner3.plugins.Paintball.MapClasses.MapMetadata;

public class PregameState extends LobbyState
{
	PregameScoreboard board;
	ArrayList<BukkitTask> tasks;
	SpawnKit kit;
	
	public PregameState(PluginManager plugin, Lobby lobby, MapMetadata map) 
	{
		super(plugin, lobby, map);
		
		board = new PregameScoreboard(plugin, lobby, 120);
		tasks = new ArrayList<BukkitTask>();
		
		kit = new SpawnKit(ChatColor.WHITE);
		kit.setItem(8, lobby.getExitItem().getItemStack());
	}
	
	public void startState()
	{
		board.resetTimer();
		
		if(!board.updatePlayerCount())
			board.startTimer();
		
		board.refreshPlayerList();
		board.open();
		
		for(PBPlayer p : lobby.getPlayers())
		{
			p.setPregame();
			p.spawn(map.getMapCenter(), kit);
		}

		scheduleTasks();
	}
	
	public void endState()
	{
		cancelTasks();
		board.resetTimer();
	}
	
	public void addPlayer(PBPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		
		board.addPlayer(p);
		
		player.setPregame();

		if(!map.contains(p.getLocation(), false))
			player.spawn(map.getMapCenter(), kit);
		else
		{
			p.getInventory().clear();
			kit.giveKit(p);
		}

		lobby.broadcastMessage(ChatColor.YELLOW + p.getName() + " joined the lobby!");
	}
	
	public void removePlayer(PBPlayer player)
	{
		Player p = Bukkit.getPlayer(player.getID());
		board.removePlayer(p);
	}

	private void scheduleTasks() 
	{
		tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() 
		{
			@Override
			public void run() 
			{
				lobby.broadcastLines(3);
				lobby.broadcastMessage(ChatColor.WHITE + "           Welcome To Paintball!         ");
				lobby.broadcastMessage(ChatColor.GOLD + " ==============================");
				lobby.broadcastMessage(ChatColor.WHITE +  "      Please Note:  " + ChatColor.YELLOW + "This plugin is   ");
				lobby.broadcastMessage(ChatColor.YELLOW + "    currently under developement.  ");
				lobby.broadcastMessage(ChatColor.YELLOW + "   We are in the early alpha stage.");
				lobby.broadcastMessage(ChatColor.WHITE +  "           There Will Be Bugs!       ");
				lobby.broadcastLines(2);
			}
		}, 0L, 20L * 60L));
		
		tasks.add(Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() 
		{
			int taskId = tasks.size();

			@Override
			public void run() 
			{
				if(board.updatePlayerCount())
				{
					lobby.broadcastMessage("");
					lobby.broadcastMessage(ChatColor.GOLD + "Waiting for more players: " + ChatColor.RED + lobby.getPlayers().size() + "/" + lobby.getMinPlayers());
					lobby.broadcastMessage("");
				}
				else
				{
					BukkitTask temp = tasks.get(taskId);
					tasks.remove(taskId);
					temp.cancel();
				}
			}
		}, 20L * 5L, 20L * 20L));
	}
	
	private void cancelTasks()
	{
		for(BukkitTask task : tasks)
		{
			task.cancel();
		}

		tasks.clear();
	}

}
