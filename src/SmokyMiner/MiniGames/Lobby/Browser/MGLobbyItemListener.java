package SmokyMiner.MiniGames.Lobby.Browser;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.Lobby.MGLobbyManager;

public class MGLobbyItemListener implements MGItemListener
{
	private UUID lobbyId;
	private MGLobbyManager manager;
	private JavaPlugin plugin;
	
	public MGLobbyItemListener(JavaPlugin plugin, MGLobbyManager manager, UUID lobbyId)
	{
		this.plugin = plugin;
		this.manager = manager;
		this.lobbyId = lobbyId;
	}
	
	@Override
	public void onItemClick(MGItemClickEvent e) 
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
