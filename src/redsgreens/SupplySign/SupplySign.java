package redsgreens.SupplySign;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.event.Event.Type;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * SupplySign for Bukkit
 *
 * @author redsgreens
 */
public class SupplySign extends JavaPlugin {
    private final SupplySignBlockListener blockListener = new SupplySignBlockListener(this);
    private final SupplySignPlayerListener playerListener = new SupplySignPlayerListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
 	public final Logger logger = Logger.getLogger("Minecraft");
 	
 	public final SupplySignConfig Config = new SupplySignConfig(this);
 	public final SupplySignKits Kits = new SupplySignKits(this);
 	public final SupplySignItems Items = new SupplySignItems(this);

	public PermissionHandler Permissions = null;

    public void onEnable() {
    	
    	// link to permissions
      	setupPermissions();
   	 
     	try {
        	// create the data folder if it doesn't exist
     		File folder = this.getDataFolder();
        	if(!folder.exists()){
        		folder.mkdirs();
        	}
        	
        	Config.loadConfig();
     		Items.loadItems();
			Kits.loadKits();

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
             	        	Config.loadConfig();
            	        	Items.loadItems();
            	        	Kits.loadKits();
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
    				Iterator<String> i = Kits.getKitNames().iterator();
    				while(i.hasNext())
    					player.sendMessage(i.next());
    				
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    // return true if Player p has the permission perm
    public boolean isAuthorized(Player p, String perm){
    	boolean retval = p.isOp();

    	if(Permissions == null && retval == false && Config.AllowNonOpAccess == true)
    		if(perm.equalsIgnoreCase("access"))
    			return true;
    	
    	try{
    		if(Permissions != null)
    			  if (Permissions.has(p, "supplysign." + perm))
    			      retval = true;
    	}
    	catch (Exception ex){}
    	
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

