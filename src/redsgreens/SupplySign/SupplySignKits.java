package redsgreens.SupplySign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class SupplySignKits {

	private final SupplySign Plugin;	

	private HashMap<String,ArrayList<Object>> KitsMap = new HashMap<String,ArrayList<Object>>();
	
	public SupplySignKits(final SupplySign plugin)
	{
		Plugin = plugin;
	}

	// load kits from kits.yml
	@SuppressWarnings("unchecked")
	public void loadKits() throws IOException {
		// create the file from the one in the jar if it doesn't exist on disk
    	File kitsFile = new File(Plugin.getDataFolder(), "kits.yml");
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
		
		HashMap<String,ArrayList<Object>> tmpMap = new HashMap<String,ArrayList<Object>>();
		
		try{
			tmpMap = (HashMap<String,ArrayList<Object>>)yaml.load(rx);
		}
		finally
		{
			rx.close();
		}
		
		// test for a blank or broken kits file
		if(tmpMap == null){
			KitsMap = new HashMap<String,ArrayList<Object>>();
			ArrayList<Object> al = new ArrayList<Object>();
			al.add("null");
			KitsMap.put("null", al);
		}
		else
		{
			KitsMap.clear();
			Iterator<String> itr = tmpMap.keySet().iterator();
			while(itr.hasNext())
			{
				String k = itr.next();
				KitsMap.put(k.toLowerCase(), tmpMap.get(k));
			}
		}

		System.out.println("SupplySign loaded " + KitsMap.size() + " kits from kits.yml.");
	}

	// return a kit by name
	public ArrayList<Object> getKit(String kit) throws Exception{
		String k = SupplySignUtil.stripColorCodes(kit.toLowerCase());
		if (KitsMap.containsKey(k)){
			ArrayList<Object> al = KitsMap.get(k);
			return al;
		}
		throw new Exception("Unknown kit name: " + k);
	}

	public Set<String> getKitNames()
	{
		return KitsMap.keySet();
	}
}
