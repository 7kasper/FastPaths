![# FastPaths](https://github.com/7kasper/FastPaths/raw/main/FastPaths.png)  
Bukkit plugin that provides Fast Paths :-)

# Usage
Configure the plugin using the config.yml:
```yaml
#======FastPahs=======#
# Main config file of FastPaths.
#
# Path materials can be altered here.
#=====================#

# Blocks that give you speed when an entity walks on them.
path-blocks:
 - GRAVEL
 - DIRT_PATH

# Check interval for entity / player move.
# This is in milliseconds. 
# Lower number is more accurate but higher taxing on the server.
interval: 200

# Speed duration when standing on path block.
duration: 7

# Speed amplification when on path block.
amplification: 1

# Blocks that will not have snow form on the
no-snow-blocks:
 - GRAVEL
 - DIRT_PATH

# DO NOT TOUCH
version: 0.1
```
When walking on blocks specified by path-blocks any living entity will receive a speed boost.
Players the permission `fastpaths.use` (granted on default) will also receive a speed boost.
Any blocks specified in the no-snow-blocks will not have snow forming on them. 
You can use this to have nice looking paths in snow biomes and then not have to place way too many torches :smile:

#### Requires: [BKCommonLib](https://www.spigotmc.org/resources/bkcommonlib.39590/)
