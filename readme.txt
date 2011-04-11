Plugin Name: SupplySign 1.2 by redsgreens

Description: Use this plugin to distribute items and blocks to your players by creating [Supply] signs
             that load a chest interface when right-clicked.

Permissions: Ops and players with the "supplysign.create" permission will be able to place supply signs.
             Ops and players with the "supplysign.access" permission will be able to access them.
             Ops and players with the "supplysign.destroy" permission can destroy existing supply signs.
             Ops and players with the "supplysign.reload" permission can trigger a reload of items/kits data.

The Basics : 1. Place a sign 
		     2. Set the first line to [Supply]
		     3. On each of the remaining lines, put the name(or item id) of the contents of the supply sign
		     4. Right-click the sign to open the chest. 

Chest Sign : To place a sign on a chest, make sure it is a single-wide chest, and make sure it is not within
             one block of another single chest. There are cases where this might work, but it can be
             glitchy, so be careful. Right-clicking the sign or the chest will display the contents of the
             sign.

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
