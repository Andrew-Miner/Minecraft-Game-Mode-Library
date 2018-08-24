package com.gmail.andrew.miner3.plugins.InventoryMenu.PagedMenu;

import org.bukkit.entity.Player;

import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemClickEvent;
import com.gmail.andrew.miner3.plugins.InventoryMenu.ItemListener;

public class NextPageListener implements ItemListener
{	
	@Override
	public void onItemClick(ItemClickEvent e) 
	{
		if(e.getMenu() instanceof PagedMenu && e.getWhoClicked() instanceof Player)
		{
			PagedMenu menu = (PagedMenu) e.getMenu();
			
			int nextPage = menu.getCurrentPage(e.getWhoClicked().getUniqueId()) + 1;
			
			if(nextPage < menu.pageCount())
				menu.openMenu((Player) e.getWhoClicked(), nextPage);
		}
		else
			throw new IllegalStateException("NextPageListener not in PagedMenu");
	}
}
