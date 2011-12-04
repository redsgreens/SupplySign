package redsgreens.SupplySign;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.IInventory;
import net.minecraft.server.PlayerInventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SupplySignItems {

	private final SupplySign Plugin;	

	private static Map<String, SupplySignItemStack> ItemsMap = new HashMap<String, SupplySignItemStack>();

	public SupplySignItems(final SupplySign plugin)
	{
		Plugin = plugin;
	}

    // read items.csv file
	public void loadItems()
	{
		try {
			loadBaseItems();
			loadCustomItems();
			loadLegacyItems();
		} catch (IOException e) {}
		
		System.out.println("SupplySign loaded " + ItemsMap.size() + " items.");
	}

	private void loadLegacyItems() throws IOException
	{
		File legacyFile = new File(Plugin.getDataFolder(), "items.csv");
		if (!legacyFile.exists())
			return;

		Map<String, SupplySignItemStack> legacyMap = new HashMap<String, SupplySignItemStack>();
		
		BufferedReader rx = new BufferedReader(new FileReader(legacyFile));
		try
		{

			for (int i = 0; rx.ready(); i++)
			{
				try
				{
					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#")) continue;

					String[] parts = line.split(",");
					
					String itemName = parts[0].toLowerCase();
					int itemID = Integer.parseInt(parts[1]);
					Short itemDamage = Short.parseShort(parts[2]);
					int itemStackSize = Integer.parseInt(parts[3]);
					
					SupplySignItemStack stack = new SupplySignItemStack(Material.getMaterial(itemID), itemDamage, itemStackSize);

					if(ItemsMap.containsKey(itemName))
					{
						SupplySignItemStack stack2 = ItemsMap.get(itemName);
						
						if(stack.getMaterial() != stack2.getMaterial() || stack.getDurability() != stack2.getDurability() || stack.getAmount() != stack2.getAmount())
						{
							ItemsMap.remove(itemName);
							ItemsMap.put(itemName, stack);
							legacyMap.put(itemName, stack);
						}
						
					}
					else
					{
						ItemsMap.put(itemName, stack);
						legacyMap.put(itemName, stack);
					}
					
				}
				catch (Exception ex)
				{
					Plugin.logger.warning("Error parsing items.csv on line " + i + ". " + ex.getMessage());
				}
			}
		}
		finally
		{
			rx.close();
		}

		if(legacyMap.size() != 0)
		{
			File customFile = new File(Plugin.getDataFolder(), "items-custom.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(customFile, true));
			Iterator<String> i = legacyMap.keySet().iterator();
			
			
			try {
				while (i.hasNext()) {
					String itemName = i.next().toLowerCase();
					if(!itemName.equalsIgnoreCase("rfish") && !itemName.equalsIgnoreCase("cmcart") && !itemName.equalsIgnoreCase("slab") && !itemName.equalsIgnoreCase("redrose") && !itemName.equalsIgnoreCase("step") && !itemName.equalsIgnoreCase("17.1") && !itemName.equalsIgnoreCase("17.2") && !itemName.equalsIgnoreCase("manyarrow") && !itemName.equalsIgnoreCase("air"))
					{
						SupplySignItemStack stack = legacyMap.get(itemName);
						bw.write(itemName + "," + stack.getMaterial().getId() + ","
								+ stack.getDurability() + "," + stack.getAmount());
						bw.newLine();
					}
				}
				bw.flush();
			} 
			finally {
				bw.close();
			}
			


		}

		legacyFile.renameTo(new File(Plugin.getDataFolder(), "items.csv.old"));

	}

	private void loadCustomItems() throws IOException
	{
		// create the file from the one in the jar if it doesn't exist on disk
		File itemsFile = new File(Plugin.getDataFolder(), "items-custom.csv");
		if (!itemsFile.exists())
		{
			itemsFile.createNewFile();
			InputStream res = SupplySign.class.getResourceAsStream("/items-custom.csv");
			
			FileWriter tx = new FileWriter(itemsFile);
			for (int i = 0; (i = res.read()) > 0;) tx.write(i);
			tx.flush();
			tx.close();
			res.close();

			return;
		}

		BufferedReader rx = new BufferedReader(new FileReader(itemsFile));
		try
		{

			for (int i = 0; rx.ready(); i++)
			{
				try
				{
					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#")) continue;

					String[] parts = line.split(",");
					
					String itemName = parts[0].toLowerCase();
					int itemID = Integer.parseInt(parts[1]);
					Short itemDamage = Short.parseShort(parts[2]);
					int itemStackSize = Integer.parseInt(parts[3]);

					if(itemID > 0)
					{
						SupplySignItemStack stack = new SupplySignItemStack(Material.getMaterial(itemID), itemDamage, itemStackSize);

						if(ItemsMap.containsKey(itemName))
							ItemsMap.remove(itemName);
						
						ItemsMap.put(itemName, stack);
					}
				}
				catch (Exception ex)
				{
					Plugin.logger.warning("Error parsing items-custom.csv on line " + i + ". " + ex.getMessage());
				}
			}
		}
		finally
		{
			rx.close();
		}

		
	}

	private void loadBaseItems() throws IOException
	{
		// recreate the file from the one in the jar every time
		File itemsFile = new File(Plugin.getDataFolder(), "items-base.csv");

		if (itemsFile.exists())
			itemsFile.delete();

		itemsFile = new File(Plugin.getDataFolder(), "items-base.csv");
		itemsFile.createNewFile();
		InputStream res = SupplySign.class.getResourceAsStream("/items.csv");

		FileWriter tx = new FileWriter(itemsFile);
		for (int i = 0; (i = res.read()) > 0;) 
			tx.write(i);
		
		tx.flush();
		tx.close();
		res.close();

		ItemsMap = new HashMap<String, SupplySignItemStack>();
		ItemsMap.clear();
		BufferedReader rx = new BufferedReader(new FileReader(itemsFile));
		try
		{

			for (int i = 0; rx.ready(); i++)
			{
				try
				{
					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#")) continue;

					String[] parts = line.split(",");
					
					String itemName = parts[0].toLowerCase();
					int itemID = Integer.parseInt(parts[1]);
					Short itemDamage = Short.parseShort(parts[2]);
					Material material = Material.getMaterial(itemID);
					SupplySignItemStack stack = new SupplySignItemStack(material, itemDamage, material.getMaxStackSize());
					
					ItemsMap.put(itemName, stack);
					
				}
				catch (Exception ex)
				{
					Plugin.logger.warning("Error parsing items-base.csv on line " + i + ". " + ex.getMessage());
				}
			}
		}
		finally
		{
			rx.close();
		}

	}
	
	// return an ItemStack from by name
	public ItemStack getItem(String id) throws Exception
	{
		// see if it's in the config files
		String id2 = SupplySignUtil.stripColorCodes(id.toLowerCase());
		if (ItemsMap.containsKey(id2)){
			ItemStack is = ItemsMap.get(id2).getItemStack();
			return is;
		}
		
		try
		{
			// not in config files, see if bukkit can parse it
			Material m = Material.getMaterial(id2);
			if(m != null)
				return new ItemStack(m, m.getMaxStackSize());

			try
			{
				Integer i = Integer.parseInt(id2);
				m = Material.getMaterial(i);
				if(m != null)
					return new ItemStack(m, m.getMaxStackSize());
			}
			catch(NumberFormatException nfex) {}
			
			// replace . with : (to allow either . or : to separate item and durability)
			if(id2.contains("."))
				id2.replace(".", ":");
			
			// there's a : in the string, see if it looks like itemid:durability
			if(id2.contains(":"))
			{
				String split[] = id2.split(":");
				if(split.length == 2)
				{
					Integer i = Integer.parseInt(split[0]);
					Short d = Short.parseShort(split[1]);
					m = Material.getMaterial(i);
					return new ItemStack(m, m.getMaxStackSize(), d);
				}
			}

		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		
		throw new Exception("Unknown item name: " + id2);
	}

	// arranges the items to be displayed and shows the inventory dialog
	public void showInventory(Player p, ArrayList<Object> itemList){
		CraftPlayer cp = (CraftPlayer)p;
		CraftInventoryPlayer inv = new CraftInventoryPlayer(new PlayerInventory(cp.getHandle()));
		
		// clear the inventory
		inv.clear();

		try {
			switch(itemList.size()){
			case 1:
				for(int i=0; i<36; i++)
					inv.setItem(i, getItem(itemList.get(0).toString()));
				break;

			case 2:
				for(int i=0; i<18; i++)
					inv.setItem(i, getItem(itemList.get(0).toString()));
				for(int i=0; i<18; i++)
					inv.setItem(i+18, getItem(itemList.get(1).toString()));
				break;
			
			case 3:
					for(int i=0; i<4; i++){
						inv.setItem((i*9), getItem(itemList.get(0).toString())); inv.setItem((i*9)+1, getItem(itemList.get(0).toString())); inv.setItem((i*9)+2, getItem(itemList.get(0).toString()));
						inv.setItem((i*9)+3, getItem(itemList.get(1).toString())); inv.setItem((i*9)+4, getItem(itemList.get(1).toString())); inv.setItem((i*9)+5, getItem(itemList.get(1).toString()));
						inv.setItem((i*9)+6, getItem(itemList.get(2).toString())); inv.setItem((i*9)+7, getItem(itemList.get(2).toString())); inv.setItem((i*9)+8, getItem(itemList.get(2).toString()));
					}
				break;
				
			case 4:
					for(int i=0; i<4; i++)
						for(int j=0; j<9; j++)
							inv.setItem((i*9)+j, getItem(itemList.get(i).toString()));
				break;

			case 5:
					for(int i=0; i<4; i++)
						for(int j=0; j<7; j++)
							  inv.setItem((i*9)+j, getItem(itemList.get(i).toString()));
					for(int k=0; k<4; k++){
						inv.setItem((k*9)+7, getItem(itemList.get(4).toString()));
						inv.setItem((k*9)+8, getItem(itemList.get(4).toString()));
					}
				break;
				
			case 6:
				for(int i=0; i<2; i++){
					inv.setItem((i*9), getItem(itemList.get(0).toString())); inv.setItem((i*9)+1, getItem(itemList.get(0).toString())); inv.setItem((i*9)+2, getItem(itemList.get(0).toString()));
					inv.setItem((i*9)+3, getItem(itemList.get(1).toString())); inv.setItem((i*9)+4, getItem(itemList.get(1).toString())); inv.setItem((i*9)+5, getItem(itemList.get(1).toString()));
					inv.setItem((i*9)+6, getItem(itemList.get(2).toString())); inv.setItem((i*9)+7, getItem(itemList.get(2).toString())); inv.setItem((i*9)+8, getItem(itemList.get(2).toString()));
				}
				for(int i=2; i<4; i++){
					inv.setItem((i*9), getItem(itemList.get(3).toString())); inv.setItem((i*9)+1, getItem(itemList.get(3).toString())); inv.setItem((i*9)+2, getItem(itemList.get(3).toString()));
					inv.setItem((i*9)+3, getItem(itemList.get(4).toString())); inv.setItem((i*9)+4, getItem(itemList.get(4).toString())); inv.setItem((i*9)+5, getItem(itemList.get(4).toString()));
					inv.setItem((i*9)+6, getItem(itemList.get(5).toString())); inv.setItem((i*9)+7, getItem(itemList.get(5).toString())); inv.setItem((i*9)+8, getItem(itemList.get(5).toString()));
				}

				break;

			case 7:
					int pos1 = 0;
					for(int i=0; i<7; i++){
						int jmax;
						if(i % 2 == 0) jmax = 5;
						else jmax = 4;
						
						for(int j=0; j<jmax; j++)
							  inv.setItem(pos1++, getItem(itemList.get(i).toString()));
						
					}
				break;

			case 8:
					int pos2 = 0;
					for(int i=0; i<4; i++){
						  inv.setItem(pos2++, getItem(itemList.get((2*i)).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)+1).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)+1).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)+1).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)+1).toString()));
						  inv.setItem(pos2++, getItem(itemList.get((2*i)+1).toString()));
					}
				break;

			case 9:
					int pos3 = 0;
					for(int j=0; j<4; j++)
						for(int i=0; i<9; i++)
							  inv.setItem(pos3++, getItem(itemList.get(i).toString()));
				break;
				
			case 10:
			case 11:
			case 12:
				int pos4 = 0;
					for(int i=0; i<itemList.size(); i++)
						for(int j=0; j<3; j++)
							  inv.setItem(pos4++, getItem(itemList.get(i).toString()));
				break;

			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
				int pos;
				for(int i=0; i<itemList.size(); i++){
					if(i<9)
						pos=i;
					else pos=i+9;
					inv.setItem(pos, getItem(itemList.get(i).toString()));
					inv.setItem(pos+9, getItem(itemList.get(i).toString()));
				}
				break;
				
			default: 
				for(int i=0; i<itemList.size(); i++)
					inv.setItem(i, getItem(itemList.get(i).toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// show the inventory dialog 
		IInventory ii = (IInventory)inv.getInventory();
		cp.getHandle().a(ii);
	}

}
