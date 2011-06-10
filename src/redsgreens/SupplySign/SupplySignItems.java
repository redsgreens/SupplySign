package redsgreens.SupplySign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SupplySignItems {

	private final SupplySign Plugin;	

	private static Map<String, ItemStack> ItemsMap = new HashMap<String, ItemStack>();

	public SupplySignItems(final SupplySign plugin)
	{
		Plugin = plugin;
	}

    // read items.csv file
	public void loadItems() throws IOException
	{
		// create the file from the one in the jar if it doesn't exist on disk
		File itemsFile = new File(Plugin.getDataFolder(), "items.csv");
		if (!itemsFile.exists()){
			itemsFile.createNewFile();
			InputStream res = SupplySign.class.getResourceAsStream("/items.csv");
			FileWriter tx = new FileWriter(itemsFile);
			for (int i = 0; (i = res.read()) > 0;) tx.write(i);
			tx.flush();
			tx.close();
			res.close();
		}
		
		ItemsMap = new HashMap<String, ItemStack>();
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
					
					String itemName = parts[0];
					int itemID = Integer.parseInt(parts[1]);
					Byte itemDamage = Byte.parseByte(parts[2]);
					int itemStackSize = Integer.parseInt(parts[3]);
					
					ItemStack stack = new ItemStack(itemID, itemStackSize, itemDamage);
					
					ItemsMap.put(itemName, stack);
					
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
		
		System.out.println("SupplySign loaded " + ItemsMap.size() + " items from items.csv.");

	}

	// return an ItemStack from by name
	public ItemStack getItem(String id) throws Exception
	{
		if (ItemsMap.containsKey(id)){
			ItemStack is = ItemsMap.get(id);
			return is;
		}
		throw new Exception("Unknown item name: " + id);
	}

	// arranges the items to be displayed and shows the inventory dialog
	public void showInventory(Player p, ArrayList<Object> itemList){
		CraftPlayer cp = (CraftPlayer)p;
		CraftInventoryPlayer inv = new CraftInventoryPlayer(new InventoryPlayer(cp.getHandle()));
		
		// clear the inventory
		inv.clear();
		
		try {
			switch(itemList.size()){
			case 1:
				for(int i=0; i<36; i++)
					inv.addItem(getItem(itemList.get(0).toString()));
				break;

			case 2:
				for(int i=0; i<18; i++)
					inv.addItem(getItem(itemList.get(0).toString()));
				for(int i=0; i<18; i++)
					inv.addItem(getItem(itemList.get(1).toString()));
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
					for(int i=0; i<7; i++){
						int jmax;
						if(i % 2 == 0) jmax = 5;
						else jmax = 4;
						
						for(int j=0; j<jmax; j++)
							  inv.addItem(getItem(itemList.get(i).toString()));
					}
				break;

			case 8:
					for(int i=0; i<4; i++){
						  inv.addItem(getItem(itemList.get((2*i)).toString()));
						  inv.addItem(getItem(itemList.get((2*i)).toString()));
						  inv.addItem(getItem(itemList.get((2*i)).toString()));
						  inv.addItem(getItem(itemList.get((2*i)).toString()));
						  inv.addItem(getItem(itemList.get((2*i)+1).toString()));
						  inv.addItem(getItem(itemList.get((2*i)+1).toString()));
						  inv.addItem(getItem(itemList.get((2*i)+1).toString()));
						  inv.addItem(getItem(itemList.get((2*i)+1).toString()));
						  inv.addItem(getItem(itemList.get((2*i)+1).toString()));
					}
				break;

			case 9:
				for(int j=0; j<4; j++)
					for(int i=0; i<9; i++)
						  inv.addItem(getItem(itemList.get(i).toString()));
				break;
				
			case 10:
			case 11:
			case 12:
					for(int i=0; i<itemList.size(); i++)
						for(int j=0; j<3; j++)
							  inv.addItem(getItem(itemList.get(i).toString()));
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
			System.out.println("SupplySign exception: " + e.getMessage());
		}
		
		// show the inventory dialog 
		IInventory ii = (IInventory)inv.getInventory();
		cp.getHandle().a(ii);
	}

}
