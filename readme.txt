Plugin Name: SupplySign by redsgreens

URL        : http://forums.bukkit.org/threads/7938/

Description: Use this plugin to distribute items and blocks to your players by creating [Supply] signs
             that load a chest interface when right-clicked.

Permissions: Ops and players with the "supplysign.create" permission will be able to place supply signs.
             Ops and players with the "supplysign.access" permission will be able to access them.
             Ops and players with the "supplysign.destroy" permission can destroy existing supply signs.
             Ops and players with the "supplysign.reload" permission can trigger a reload of items/kits data.
             Players with "supplysign.access.kitname" can access the kit called "kitname".

The Basics : 1. Place a sign 
		     2. Set the first line to [Supply]
		     3. On each of the remaining lines, put the name(or item id) of the contents of the supply sign
		     4. Right-click the sign to open the chest. 

Chest Sign : To place a sign on a chest, make sure it is not within one block of another chest. There are 
             cases where this might work, but it can be glitchy, so be careful. Place the sign on the ground
             in front of the chest, then after you are done editing it, the sign will attach itself to the
             chest. Right-clicking the sign or the chest will display the contents of the sign. If you use 
             a large chest, you can place a sign on both halves of it. 

Dispensers : Attach a sign to a dispenser in the same way you would attach one to a sign. The inventory of
             the dispenser will always be full of whatever is on the sign. If you use a kit, or if you put 
             multiple items on the sign, only the first will be used. Remember that dispensers require
             redstone to activate, so it might be a good idea to wire up the dispenser and test it before
             attaching the sign. 

Using Kits : To use a kit, set the first line to [Supply] as usual, then put "kit:" followed by the kit name
             on the second line, ie. "kit:wool". 
             
Kits List  : wool, dye, woodtools, woodplus, irontools, ironplus, diamondtool, diamondplus, goldtools, 
             goldplus, desert, makecake, chainmail

Data Files : After the plugin has been loaded for the first time, a "supplysign" data folder will be created
             in the bukkit plugins folder with two files in it: items.csv and kits.yml. You can customize
             these files to add new kits or modify item names. Use "/supplysign reload" to load your
             changes without restarting the server. 

Config File: Two configurable parameters are available: ShowErrorsInClient, and FixSignOnSignGlitch. Comments
             in the file describe what they do and how they are used.

Thanks     : This plugin is based heavily on the [Free] sign code from Essentials by Zenexer. Without his
             hard work there would be no SupplySign plugin. Thanks! 
             Also thanks to vitaminmoo for testing this plugin and developing the kits.yml format.
