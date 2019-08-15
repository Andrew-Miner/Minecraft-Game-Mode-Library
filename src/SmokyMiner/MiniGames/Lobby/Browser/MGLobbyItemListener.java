package SmokyMiner.MiniGames.Lobby.Browser;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.Lobby.MGMatchMaker;

public class MGLobbyItemListener implements MGItemListener
{
	private UUID lobbyId;
	private MGMatchMaker manager;
	private JavaPlugin plugin;
	
	public MGLobbyItemListener(JavaPlugin plugin, MGMatchMaker manager, UUID lobbyId)
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
		else if(manager.joinLobby(e.getWhoClicked().getUniqueId(), lobbyId))
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

	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		// TODO Auto-generated method stub
		
	}

}
