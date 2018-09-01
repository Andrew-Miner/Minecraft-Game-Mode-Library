package SmokyMiner.MiniGames.InventoryMenu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MGMenuBlock extends MGMenuItem implements Listener
{
	private final JavaPlugin plugin;
	private MGInventoryMenu menu;
	
	public MGMenuBlock(JavaPlugin plugin, MGInventoryMenu menu, Material icon, int iconData, String name, List<String> lore)
	{
		super(plugin, icon, iconData, name);
		
		this.plugin = plugin;
		this.menu = menu;
		
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
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
			menu.openMenu(e.getPlayer());
			e.setCancelled(true);
		}
	}
}
