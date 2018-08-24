package com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyBrowser;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemClickEvent;
import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemListener;
import com.gmail.andrew.miner3.plugins.Paintball.PluginManager;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.Lobby;
import com.gmail.andrew.miner3.plugins.Paintball.LobbyClasses.LobbyManager;

public class LobbyItemListener implements ItemListener
{
	private UUID lobbyId;
	private LobbyManager manager;
	private JavaPlugin plugin;
	
	public LobbyItemListener(JavaPlugin plugin, LobbyManager manager, UUID lobbyId)
	{
		this.plugin = plugin;
		this.manager = manager;
		this.lobbyId = lobbyId;
	}
	
	@Override
	public void onItemClick(ItemClickEvent e) 
	{
		if(manager.getLobbyByPlayer(e.getWhoClicked().getUniqueId()) != null)
		{
			e.getWhoClicked().sendMessage(ChatColor.YELLOW + "[" + plugin.getName() + "] " + ChatColor.RED + "Error: You must leave your lobby first!");
			e.setClickSound(Sound.ENTITY_ITEM_BREAK);
		}
		else if(manager.JoinMatch(e.getWhoClicked().getUniqueId(), lobbyId))
		{
			e.setClickSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
			e.getWhoClicked().closeInventory();
		}
		else
			e.setClickSound(Sound.ENTITY_ITEM_BREAK);
	}

	public UUID getLobbyID() 
	{
		return lobbyId;
	}

}
