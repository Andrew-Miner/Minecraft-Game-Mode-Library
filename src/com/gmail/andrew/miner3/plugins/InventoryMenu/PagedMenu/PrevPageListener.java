package com.gmail.andrew.miner3.plugins.InventoryMenu.PagedMenu;

import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemClickEvent;
import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemListener;

public class PrevPageListener implements ItemListener
{
	@Override
	public void onItemClick(ItemClickEvent e) 
	{
		if(e.getMenu() instanceof PagedMenu && e.getWhoClicked() instanceof Player)
		{
			PagedMenu menu = (PagedMenu) e.getMenu();
			
			int prevPage = menu.getCurrentPage(e.getWhoClicked().getUniqueId()) - 1;
			
			if(!(prevPage < 0))
				menu.openMenu((Player) e.getWhoClicked(), prevPage);
		}
		else
			throw new IllegalStateException("PrevPageListener not in PagedMenu");
	}
}
