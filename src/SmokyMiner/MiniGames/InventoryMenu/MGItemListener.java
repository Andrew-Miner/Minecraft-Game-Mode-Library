package SmokyMiner.MiniGames.InventoryMenu;

import org.bukkit.event.player.PlayerInteractEvent;

public interface MGItemListener 
{
	public void onItemClick(MGItemClickEvent e);
	public void onItemInteraction(PlayerInteractEvent e);
}