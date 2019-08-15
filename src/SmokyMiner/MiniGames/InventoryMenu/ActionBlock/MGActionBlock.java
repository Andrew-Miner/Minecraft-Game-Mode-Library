package SmokyMiner.MiniGames.InventoryMenu.ActionBlock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import SmokyMiner.MiniGames.InventoryMenu.MGItemListener;
import SmokyMiner.MiniGames.InventoryMenu.MGMenuItem;

public class MGActionBlock extends MGMenuItem
{
	private boolean canDrop;
	
	public MGActionBlock(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore, MGItemListener listener) 
	{
		super(plugin, icon, iconData, name, lore, listener);
		this.registerListener(listener);
		canDrop = true;
	}
	
	public MGActionBlock(JavaPlugin plugin, Material icon, int iconData, String name, List<String> lore) 
	{
		super(plugin, icon, iconData, name, lore);
		canDrop = true;
	}
	
	public void giveBlock(Player p)
	{
		p.getInventory().addItem(super.getItemStack());
	}
	
	public boolean removeBlock(Player p, int amount)
	{
		ItemStack temp = this.getItemStack().clone();
		temp.setAmount(amount);
		
		if(p.getInventory().containsAtLeast(temp, amount))
		{
			HashMap<Integer, ? extends ItemStack> similar = p.getInventory().all(this.getMaterial());

		    Iterator<?> it = similar.entrySet().iterator();
		    while(it.hasNext() && amount > 0)
		    {
		    	@SuppressWarnings("unchecked")
				Map.Entry<Integer, ? extends ItemStack> pair = (Map.Entry<Integer, ? extends ItemStack>)it.next();
		    	if(pair.getValue().isSimilar(this.getItemStack()))
		    	{
		    		int size = pair.getValue().getAmount() - amount;
		    		
		    		if(size <= 0)
		    		{
		    			p.getInventory().clear(pair.getKey());
		    			amount = Math.abs(size);
		    		}
		    		else
		    		{
		    			pair.getValue().setAmount(size);
		    			amount = 0;
		    		}
		    	}
		    }
		    
		    return true;
		}
		
		return false;
	}
	
	public void setCanDrop(boolean canDrop)
	{
		this.canDrop = canDrop;
	}
	
	public boolean canDrop()
	{
		return canDrop;
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(!canDrop && event.getItemDrop().getItemStack().equals(super.getItemStack()))
			event.setCancelled(true);
	}
}
