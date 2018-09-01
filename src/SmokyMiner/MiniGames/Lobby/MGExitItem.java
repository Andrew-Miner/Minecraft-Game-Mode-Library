package SmokyMiner.MiniGames.Lobby;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.ActionBlock.MGActionBlock;
import SmokyMiner.MiniGames.InventoryMenu.ActionBlock.MGActionBlockListener;

public class MGExitItem extends MGActionBlockListener
{
	public static final String EXIT_NAME = ChatColor.RED + "Leave Lobby";
	JavaPlugin plugin;
	MGActionBlock item;
	MGLobby lobby;
	
	public MGExitItem(JavaPlugin plugin, MGLobby lobby)
	{
		this.plugin = plugin;
		this.lobby = lobby;
		
		item = new MGActionBlock(plugin, Material.MINECART, -1, EXIT_NAME, null, this);
	}
	
	public ItemStack getItemStack()
	{
		return item.getItemStack();
	}
	
	public void giveBlock(Player p)
	{
		item.giveBlock(p);
	}
	
	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		if(lobby.findPlayer(e.getPlayer().getUniqueId()) != null)
			lobby.removePlayer(e.getPlayer().getUniqueId());
	}
}
