package SmokyMiner.MiniGames.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

import SmokyMiner.MiniGames.InventoryMenu.MGItemClickEvent;
import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.InventoryMenu.ActionBlock.MGActionBlock;
import SmokyMiner.Minigame.Main.MGManager;

public class MGItem extends MGActionBlock implements MGItemListener
{
	protected MGManager manager;
	protected HashMap<UUID, Long> lastInteraction;
	
	protected int cost;
	
	public MGItem(MGManager manager, String name, Material material)
	{
		super(manager.plugin(), material, -1, name, null);
		this.manager = manager;
		this.registerListener(this);
		cost = 0;
		
		ArrayList<String> lore = buildLore();

		if(lore != null)
		{
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		
		lastInteraction = new HashMap<UUID, Long>();
	}
	
	public MGItem(MGManager manager, String name, Material material, int cost)
	{
		super(manager.plugin(), material, -1, name, null);
		this.manager = manager;
		this.registerListener(this);
		this.cost = cost;
		
		ArrayList<String> lore = buildLore();
		
		if(lore != null)
		{
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		
		lastInteraction = new HashMap<UUID, Long>();
	}
	
	protected ArrayList<String> buildLore()
	{
		return null;
	}

	@Override
	public void onItemClick(MGItemClickEvent e)
	{
		lastInteraction.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
	}

	@Override
	public void onItemInteraction(PlayerInteractEvent e)
	{
		lastInteraction.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
	}
	
	public long timeSinceLastInteraction(UUID player)
	{
		if(!lastInteraction.containsKey(player))
			return System.currentTimeMillis();
		return System.currentTimeMillis() - lastInteraction.get(player);
	}
}
