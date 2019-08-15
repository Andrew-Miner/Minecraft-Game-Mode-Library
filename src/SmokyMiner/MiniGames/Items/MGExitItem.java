package SmokyMiner.MiniGames.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.InventoryMenu.ActionBlock.MGActionBlock;
import SmokyMiner.MiniGames.Lobby.MGLobby;

public class MGExitItem extends MGActionBlock implements MGItemListener
{
	public static final String EXIT_NAME = ChatColor.RED + "Leave Lobby";
	JavaPlugin plugin;
	MGActionBlock item;
	MGLobby lobby;
	
	public MGExitItem(JavaPlugin plugin, MGLobby lobby)
	{
		super(plugin, Material.MINECART, -1, EXIT_NAME, null);
		this.lobby = lobby;
		this.registerListener(this);
	}
	
	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		if(lobby.findPlayer(e.getPlayer().getUniqueId()) != null)
		{
			lobby.removePlayer(e.getPlayer().getUniqueId());
			e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, .5f, 1f);
		}
	}

	@Override
	public void onItemClick(MGItemClickEvent e)
	{
		// TODO Auto-generated method stub
		
	}
}
