package com.gmail.andrew.miner3.plugins.InventoryMenu;

import java.util.EventObject;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.ClickType;

@SuppressWarnings("serial")
public class ItemClickEvent extends EventObject
{
	private MenuItem item;
	private ClickType clickType;
	private HumanEntity player;
	private Sound sound;
	
	boolean cancelled;

	public ItemClickEvent(Object source, MenuItem item, ClickType clickType, HumanEntity player, Sound clickSound) 
	{
		super(source);
		
		this.item = item;
		this.clickType = clickType;
		this.player = player;
		this.sound = clickSound;
		
		cancelled = false;
	}
	
	public InventoryMenu getMenu() { return (InventoryMenu) super.getSource(); }
	public MenuItem getItem() { return item; }
	public ClickType getClickType() { return clickType; }
	public HumanEntity getWhoClicked() { return player; }
	
	public Sound getClickSound()
	{
		return sound;
	}
	
	public void setClickSound(Sound sound)
	{
		this.sound = sound;
	}
}
