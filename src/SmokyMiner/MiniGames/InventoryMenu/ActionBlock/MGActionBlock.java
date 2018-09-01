package SmokyMiner.MiniGames.InventoryMenu.ActionBlock;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;

public class MGActionBlock extends MGMenuItem implements Listener
{
	MGActionBlockListener listener;
	
	public MGActionBlock(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, MGActionBlockListener listener) 
	{
		super(plugin, icon, iconData, name, lore, listener);
		this.listener = listener;
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void giveBlock(Player p)
	{
		p.getInventory().addItem(super.getItemStack());
	}
	
	@EventHandler
	public void onClickEvent(PlayerInteractEvent e)
	{
		if(super.getItemStack().equals(e.getPlayer().getInventory().getItemInMainHand()))
		{
			e.setCancelled(true);
			listener.onItemInteraction(e);
		}
	}
}
