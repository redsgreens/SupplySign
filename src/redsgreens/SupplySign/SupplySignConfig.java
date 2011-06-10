package redsgreens.SupplySign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class SupplySignConfig {

	private final SupplySign Plugin;	
	private static HashMap<String,Object> ConfigMap = new HashMap<String, Object>(); 

	public Boolean ShowErrorsInClient = false;
	public Boolean AllowNonOpAccess = false;
	public SupplySignOnSign FixSignOnSignGlitch = SupplySignOnSign.Disabled;
	
	public SupplySignConfig(final SupplySign plugin)
	{
		Plugin = plugin;
	}

	// load config settings from config.yml
	@SuppressWarnings("unchecked")
	public void loadConfig() throws Exception {
		// create the file from the one in the jar if it doesn't exist on disk
    	File configFile = new File(Plugin.getDataFolder(), "config.yml");
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
		
		if(ConfigMap.containsKey("FixSignOnSignGlitch")){
			String configStr = (String)ConfigMap.get("FixSignOnSignGlitch");
			if(configStr.equalsIgnoreCase("SupplySignOnly"))
				FixSignOnSignGlitch = SupplySignOnSign.SupplySignOnly;
			else if(configStr.equalsIgnoreCase("Global"))
				FixSignOnSignGlitch = SupplySignOnSign.Global;
		}

		if(ConfigMap.containsKey("ShowErrorsInClient")){
			boolean configBool = (Boolean)ConfigMap.get("ShowErrorsInClient");
			if(configBool)
				ShowErrorsInClient = true;
			else ShowErrorsInClient = false;		
		}

		if(ConfigMap.containsKey("AllowNonOpAccess")){
			boolean configBool = (Boolean)ConfigMap.get("AllowNonOpAccess");
			if(configBool)
				AllowNonOpAccess = true;
			else AllowNonOpAccess = false;		
		}

		System.out.println("SupplySign: ShowErrorsInClient=" + ShowErrorsInClient);
		System.out.println("SupplySign: FixSignOnSignGlitch=" + FixSignOnSignGlitch);
		
		if(Plugin.Permissions == null)
			System.out.println("SupplySign: AllowNonOpAccess=" + AllowNonOpAccess);

	}

}
