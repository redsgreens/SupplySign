package redsgreens.SupplySign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Sign;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.block.CraftSign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event.Type;
import org.yaml.snakeyaml.Yaml;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * SupplySign for Bukkit
 *
 * @author redsgreens
 */
public class SupplySign extends JavaPlugin {
//	private static SupplySign plugin;
    private final SupplySignBlockListener blockListener = new SupplySignBlockListener(this);
    private final SupplySignPlayerListener playerListener = new SupplySignPlayerListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
 	private final static Logger logger = Logger.getLogger("Minecraft");
	private static Map<String, ItemStack> ItemsMap = new HashMap<String, ItemStack>();
	private static HashMap<String,ArrayList<Object>> KitsMap = new HashMap<String,ArrayList<Object>>();
	private static HashMap<String,Object> ConfigMap = new HashMap<String, Object>(); 
	private static File folder;

	public static PermissionHandler Permissions = null;

    public void onEnable() {
//    	plugin = this;
    	
    	// link to permissions
      	setupPermissions();
   	 
     	try {
        	// create the data folder if it doesn't exist
        	folder = this.getDataFolder();
        	if(!folder.exists()){
        		folder.mkdirs();
        	}
        	
        	loadConfig();
     		loadItems();
			loadKits();

			// test for a blank or broken kits file
			if(KitsMap == null){
				KitsMap = new HashMap<String,ArrayList<Object>>();
				ArrayList<Object> al = new ArrayList<Object>();
				al.add("null");
				KitsMap.put("null", al);
			}
			
			// print size of loaded structures
			System.out.println("SupplySign loaded " + ConfigMap.size() + " config parameters from config.yml.");
			System.out.println("SupplySign loaded " + ItemsMap.size() + " items from items.csv.");
			System.out.println("SupplySign loaded " + KitsMap.size() + " kits from kits.yml.");

     	} catch (Exception e) {
			System.out.println("SupplySign error: " + e.getMessage());
		}

        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.SIGN_CHANGE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.BLOCK_DISPENSE, blockListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);

		// print loaded message
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    // catches the /supplysign command
    {
    	String commandName = cmd.getName().toLowerCase();
    	
    	if(sender instanceof Player)
    	{
    		System.out.println("Command cmd = " + sender);
    		System.out.println("Args = " + args);
    		Player player = (Player) sender;

    		// supplysign command
    		if(commandName.equals("supplysign") && args.length == 1)
    		{
    			if(args[0].equalsIgnoreCase("reload"))
    			{
    				if(isAuthorized(player, "reload"))
    				{
            			// supplysign reload
        				try{
             	        	loadConfig();
            	        	loadItems();
            	        	loadKits();

            	    		System.out.println("SupplySign loaded " + ItemsMap.size() + " items from items.csv.");
            	    		System.out.println("SupplySign loaded " + KitsMap.size() + " kits from kits.yml.");
        				} catch (Exception e) {
        					System.out.println("SupplySign error: " + e.getMessage());
           				}
        				
            			player.sendMessage("SupplySign data reloaded.");
        				return true;
    				}
    				else
    					player.sendMessage("§cErr: You don't have SupplySign reload permission.");
    			}
    			else if(args[0].equalsIgnoreCase("listkits"))
    			{
    				player.sendMessage("Available SupplySign kits:");
    				
    				// supplysign/ss list
    				Iterator<String> i = KitsMap.keySet().iterator();
    				while(i.hasNext())
    					player.sendMessage(i.next());
    				
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    // return true if Player p has the permission perm
    public static boolean isAuthorized(Player p, String perm){
    	boolean retval = p.isOp();
    	
    	try{
    		if(Permissions != null)
    			  if (Permissions.has(p, "supplysign." + perm))
    			      retval = true;
    	}
    	catch (Exception ex){}
    	
//    	if(retval == false && getConfigShowErrorsInClient())
//    		p.sendMessage("Error: You don't have SupplySign " + perm + " permission.");
    	
    	return retval;	
    }
    
    private void setupPermissions() {
    	try{
            Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

            if (Permissions == null) {
                if (test != null) {
                    Permissions = ((Permissions)test).getHandler();
                }
            }
    	}
    	catch (Exception ex){}
    }

    // read items.csv file
	public static void loadItems() throws IOException
	{
		// create the file from the one in the jar if it doesn't exist on disk
		File itemsFile = new File(folder, "items.csv");
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
					logger.warning("Error parsing items.csv on line " + i + ". " + ex.getMessage());
				}
			}
		}
		finally
		{
			rx.close();
		}
	}

	// load kits from kits.yml
	@SuppressWarnings("unchecked")
	public static void loadKits() throws IOException {
		// create the file from the one in the jar if it doesn't exist on disk
    	File kitsFile = new File(folder, "kits.yml");
		if (!kitsFile.exists()){
			kitsFile.createNewFile();
			InputStream res = SupplySign.class.getResourceAsStream("/kits.yml");
			FileWriter tx = new FileWriter(kitsFile);
			for (int i = 0; (i = res.read()) > 0;) tx.write(i);
			tx.flush();
			tx.close();
			res.close();
		}
		
		BufferedReader rx = new BufferedReader(new FileReader(kitsFile));
		Yaml yaml = new Yaml();
		
		KitsMap.clear();
		
		try{
			KitsMap = (HashMap<String,ArrayList<Object>>)yaml.load(rx);
		}
		finally
		{
			rx.close();
		}
	}

	// load config settings from config.yml
	@SuppressWarnings("unchecked")
	public static void loadConfig() throws Exception {
		// create the file from the one in the jar if it doesn't exist on disk
    	File configFile = new File(folder, "config.yml");
		if (!configFile.exists()){
			configFile.createNewFile();
			InputStream res = SupplySign.class.getResourceAsStream("/config.yml");
			FileWriter tx = new FileWriter(configFile);
			for (int i = 0; (i = res.read()) > 0;) tx.write(i);
			tx.flush();
			tx.close();
			res.close();
		}
		
		BufferedReader rx = new BufferedReader(new FileReader(configFile));
		Yaml yaml = new Yaml();
		
		ConfigMap.clear();
		
		try{
			ConfigMap = (HashMap<String,Object>)yaml.load(rx);
		}
		finally
		{
			rx.close();
		}
	}

	// return value of FixSignOnSignGlitch setting in config file
	public static String getConfigFixSignOnSignGlitch(){
		
		if(ConfigMap.containsKey("FixSignOnSignGlitch")){
			String configStr = (String)ConfigMap.get("FixSignOnSignGlitch");
			if(configStr.equalsIgnoreCase("SupplySignOnly"))
				return "SupplySignOnly";
			else if(configStr.equalsIgnoreCase("Global"))
				return "Global";
			else return "Disabled";
		}
		else return "Disabled";
	}
	
	// return value of ShowErrorsInClient setting in config file
	public static boolean getConfigShowErrorsInClient(){
		if(ConfigMap.containsKey("ShowErrorsInClient")){
			boolean configBool = (Boolean)ConfigMap.get("ShowErrorsInClient");
			if(configBool)
				return true;
			else return false;		
		}
		else return false;
	}
	
	// return an ItemStack from by name
	public static ItemStack getItem(String id) throws Exception
	{
		if (ItemsMap.containsKey(id)){
			ItemStack is = ItemsMap.get(id);
			return is;
		}
		throw new Exception("Unknown item name: " + id);
	}

	// return a kit by name
	public static ArrayList<Object> getKit(String kit) throws Exception{
		if (KitsMap.containsKey(kit)){
			ArrayList<Object> al = KitsMap.get(kit);
			return al;
		}
		throw new Exception("Unknown kit name: " + kit);
	}
	
	// arranges the items to be displayed and shows the inventory dialog
	public static void showInventory(Player p, ArrayList<Object> itemList){
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

	// check to see if this is a chest without a supply sign already on it
	public static boolean isValidChest(Block b){
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++){
			if(adjBlocks[i].getType() == Material.WALL_SIGN){
				Sign sign = new CraftSign(adjBlocks[i]);
				if(sign.getLine(0).equals("§1[Supply]"))
					return false;
			}
		}
		
		return true;
	}

	// check to see if this is a dispenser without a supply sign already on it
	public static boolean isValidDispenser(Block b){
		if(b.getType() != Material.DISPENSER)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++){
			if(adjBlocks[i].getType() == Material.WALL_SIGN){
				Sign sign = new CraftSign(adjBlocks[i]);
				if(sign.getLine(0).equals("§1[Supply]"))
					return false;
			}
		}
		
		return true;
	}

	// check to see if this is a single wide chest
	public static boolean isSingleChest(Block b){
		
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++)
			if(adjBlocks[i].getType() == Material.CHEST)
				return false;
	
		return true;
	}

	// check to see if this is a single wide chest
	public static boolean isDoubleChest(Block b){
		
		if(b.getType() != Material.CHEST)
			return false;

		Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

		for(int i=0; i<adjBlocks.length; i++)
			if(adjBlocks[i].getType() == Material.CHEST)
				return true;
	
		return false;
	}

	// find a sign attached to a chest
	public static Sign getAttachedSign(Block b){
		if((b.getType() != Material.CHEST) && (b.getType() != Material.DISPENSER))
			return null;
		
		if(isSingleChest(b) || (b.getType() == Material.DISPENSER)){
			// it's a single chest or dispenser, so check the four adjacent blocks
			if(b.getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.NORTH));
			else if(b.getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.EAST));
			else if(b.getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.SOUTH));
			else if(b.getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.WEST));
			
			// didn't find a sign so return null
			return null;
		}
		else if (isDoubleChest(b)){
			// it's a double, so check the adjacent faces of this block first
			if(b.getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.NORTH));
			else if(b.getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.EAST));
			else if(b.getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.SOUTH));
			else if(b.getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
				return new CraftSign(b.getFace(BlockFace.WEST));

			// didn't find one, so find the other half of the chest and check it's faces
			Block[] adjBlocks = new Block[]{b.getFace(BlockFace.NORTH), b.getFace(BlockFace.EAST), b.getFace(BlockFace.SOUTH), b.getFace(BlockFace.WEST)};

			for(int i=0; i<adjBlocks.length; i++)
				if(adjBlocks[i].getType() == Material.CHEST){
					if(adjBlocks[i].getFace(BlockFace.NORTH).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.NORTH));
					else if(adjBlocks[i].getFace(BlockFace.EAST).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.EAST));
					else if(adjBlocks[i].getFace(BlockFace.SOUTH).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.SOUTH));
					else if(adjBlocks[i].getFace(BlockFace.WEST).getType() == Material.WALL_SIGN)
						return new CraftSign(adjBlocks[i].getFace(BlockFace.WEST));
				}

			// still no attached sign, so return null
			return null;
		}
		else
			return null;
	}

	public static void fillDispenser(Dispenser dispenser, Sign sign){
		try{
			ArrayList<Object> itemList = new ArrayList<Object>();
			
			// test to see if it's a kit
			if(sign.getLine(1).trim().contains("kit:")){
				String[] split = sign.getLine(1).trim().split(":");
				itemList = getKit(split[1]);
			}
			else
			{
				// it's not a kit, so load the items from the lines on the sign
				if(!sign.getLine(1).trim().equalsIgnoreCase(""))
					itemList.add(sign.getLine(1).trim());
				if(!sign.getLine(2).trim().equalsIgnoreCase(""))
					itemList.add(sign.getLine(2).trim());
				if(!sign.getLine(3).trim().equalsIgnoreCase(""))
					itemList.add(sign.getLine(3).trim());
			}

			// if any valid items were found, fill the inventory with first item in itemList
			if(itemList.size() > 0)
			{
				Inventory inv = dispenser.getInventory();

				for(int x=0; x < inv.getSize(); x++){
					inv.clear(x);
					inv.setItem(x, getItem(itemList.get(0).toString()));
				}
			}
		}
		catch(Exception ex){}
		
	}
	
	// get the block that has a wall sign on it
	public static Block getBlockBehindWallSign(Sign sign)
	{
		Block blockAgainst = null;
		Block signBlock = sign.getBlock();
		
		if(sign.getType() == Material.WALL_SIGN)
		{
			switch(signBlock.getData()){ // determine sign direction and get block behind it
			case 2: // facing east
				blockAgainst = signBlock.getFace(BlockFace.WEST);
				break;
			case 3: // facing west
				blockAgainst = signBlock.getFace(BlockFace.EAST);
				break;
			case 4: // facing north
				blockAgainst = signBlock.getFace(BlockFace.SOUTH);
				break;
			case 5: // facing south
				blockAgainst = signBlock.getFace(BlockFace.NORTH);
				break;
			}
		}
		
		return blockAgainst;
	}
	
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }
    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}

