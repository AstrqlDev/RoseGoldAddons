**REGARDING SKYTILS USERS: https://imgur.com/a/N8ckKlD <--- (VERY IMPORTANT!! PLEASE READ) <---**

This project is discontinued.\
Most of the code is very bad, if you copy something make sure you fix it up.\
Thanks to the ones who have supported me during the making of this

Download - https://github.com/AstrqlDev/RoseGoldAddons/releases/tag/Release

# RoseGoldAddons Feature List:
### Keybinds:
All the features below are clickable to open a drop-down with more information inside.
<details><summary>Arrow Align Aura</summary>
	
- Click keybind to instantly solve Floor 7's Phase 3 Arrow Align terminal
- This leaves one item frame unsolved, that has to be solved by hand for the server to properly register
		
</details>
<details><summary>Blood Triggerbot</summary>
	
- Toggle to shoot blood room enemies that are looked at
		
</details>
<details><summary>Brewing Macro</summary>
	
- Toggle to start automatically brewing potions
- Supports Speed and  Weakness potions
- Change modes and other options in the RoseGoldAddons config menu under "Alchemy"
	
</details>
<details><summary>Crop Nuker</summary>
	
- Toggle to start breaking crops in range of the player
- Change configuration in the RoseGoldAddons config menu under "Farming"
	
</details>
<details><summary>Custom Item Macro</summary>
	
- Toggle to start all Custom Item Macros
- See [Explanation of Custom Item Macros](#explanation-of-custom-item-macros)
- Saves between sessions
</details>
<details><summary>Enderman Macro</summary>
	
- Toggle to start Enderman Macro
- Uses Precursor Eye to shoot Endermen around the player
- Change configuration in the RoseGoldAddons config menu under "Macros"
</details>
<details><summary>Foraging Island Macro</summary>
	
- Toggle to start Foraging Island Macro
- Change configuration in the RoseGoldAddons config menu under "Foraging"
- See [Explanation of Foraging Island Macro](#explanation-of-foraging-island-macro)
</details>
<details><summary>Foraging Nuker</summary>
	
- Toggle to start foraging trees in range of the player
</details>
<details><summary>Gemstone Nuker</summary>
	
- Toggle to start mining gemstones in range of the player
- Uses Mining Speed Boost
</details>
<details><summary>Ghost Macro</summary>
	
- Toggle to start looking at closest ghost
- Recommended to use with [other features](#more-info-about-ghost-macro) such as custom item macros
</details>
<details><summary>Hardstone Nuker</summary>
	
- Toggle to start Hardstone Nuker
- Includes powder chest solver
- Change configuration in the RoseGoldAddons config menu under "Mining"
</details>
<details><summary>Mithril Macro</summary>
	
- Toggle to start Mithril Macro
- Automatically mines blocks around the player, using actual head rotations and holding down left click
- Slower than Mithril Nuker but more "Legit"
</details>
<details><summary>Mithril Nuker</summary>
	
- Toggle to start a Mithril Nuker
- Automatically mines mithril around the player
- Change configuration in the RoseGoldAddons config menu under "Mining"
- No failsafes
</details>
<details><summary>Necron Aimbot</summary>
	
- Toggle to lock the player's camera onto Necron's position
- I dont know why this is a feature, blame APhatL
</details>
<details><summary>Powder Chest Macro</summary>
	
- Toggle to automatically solve Crystal Hollows powder chests as they spawn in
- Bundled in with Hardstone Nuker, no need to activate both
</details>

### Config Menu:
- TBA

#### Explanation of Custom Item Macros:
	
Usage: `/usecooldown [milliseconds] [left]`

- Will create a new custom macro for the currently held item

- Use `/usecooldown 7000` to set a custom macro for Wand of Atonement

- Use `/usecooldown 100 left` to set a custom macro for Terminator

#### Explanation of Foraging Island Macro:

- Macro was made for [this method of foraging](https://youtu.be/ZPdeElnhB08) 

- Make sure the only 4 dirt blocks in range of the player are the ones to plant the tree on

- Make sure the tree can grow naturally:
	-  There is enough air above the dirt to let the tree grow
	- There are no solid blocks surrounding the tree

- Need to have in hotbar:
	- Treecapitator
	- Jungle / Dark Oak / Spruce Saplings
	- Enchanted Bonemeal
	- Fishing Rod
- Autopet rules: 
	- `When: You throw a fishing hook` -> `Equip Monkey`
	- `When: You gain Foraging XP` -> `Equip Ocelot`

- You can set up a redstone clock hooked up to a dropper system to allow for full automation

#### More info about Ghost Macro:
- A custom macro can be set to automatically use Soul Whip to shoot  at the closest ghost
- A custom macro can be set to automatically use healing items to stay alive
- It is also possible to use a different mod, such as [Oringo Client](https://github.com/AstrqlDev/Oringo-Client/releases/tag/Release)'s Kill Aura to automatically kill ghosts in combination with custom macros
