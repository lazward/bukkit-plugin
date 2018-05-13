package me.lazward.julianplugin;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import org.bukkit.Material;

public class CustomWeapon {
	
	private ItemStack itemstack ;
	private String name = "" ;
	private List<String> lore ;
	
	public CustomWeapon(String n, List<String> l, Material m, boolean u) {
		
		name = n ;
		lore = l ;
		itemstack = new ItemStack(m, 1) ;
		ItemMeta im = itemstack.getItemMeta() ;
		im.setLore(lore);
		im.setDisplayName(name);
		im.setUnbreakable(u);
		itemstack.setItemMeta(im) ;
		
	}
	
	public ItemStack getItemStack() {
		
		return itemstack ;
		
	}
	
	public String getName() {
		
		return name ;
		
	}
	
	public List<String> getLore() {
		
		return lore ;
		
	}
	
	public void setUnbreakable() {
		
		itemstack.getItemMeta().setUnbreakable(true);
		
	}

}
