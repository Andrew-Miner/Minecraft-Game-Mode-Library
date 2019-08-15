package SmokyMiner.MiniGames.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.Items.MGItem;
import SmokyMiner.Minigame.Main.MGManager;

public class MGInventoryItem extends MGItem
{
	protected final static String INV_NAME = ChatColor.AQUA + "Inventory";

	public MGInventoryItem(MGManager manager, Material material)
	{
		super(manager, INV_NAME, material);
		this.setCanDrop(false);
	}

	@Override
	public void onItemClick(MGItemClickEvent e)
	{
		e.getWhoClicked().openInventory(manager.getPlayerManager().getMGPlayer(e.getWhoClicked().getUniqueId()).getInventory());
		e.setClickSound(Sound.BLOCK_ENDER_CHEST_OPEN);
	}

	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		e.getPlayer().openInventory(manager.getPlayerManager().getMGPlayer(e.getPlayer().getUniqueId()).getInventory());
		e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1.0f, 1.0f);
		e.setCancelled(true);
	}

}
