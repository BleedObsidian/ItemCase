/*
 * ItemCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package com.gmail.bleedobsidian.itemcase.configurations;

import com.gmail.bleedobsidian.itemcase.ConfigurationFile;
import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 * A configuration file that holds all Itemcase saves for a specific world.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public class WorldFile extends ConfigurationFile {
    
    /**
     * The world this configuration file is for.
     */
    private final World world;
    
    /**
     * Constructor.
     * 
     * @param world World.
     */
    public WorldFile(World world) {
        
        // Append world name to config file name.
        super(world.getName() + "/itemcases.yml", "itemcases.yml");
        
        // Set world.
        this.world = world;
    }
    
    /**
     * Saves the given Itemcase to config.
     * 
     * @param itemcase Itemcase.
     * @throws java.io.IOException
     */
    public void saveItemcase(Itemcase itemcase) throws IOException {
        
        // Get block coordinates of Itemcase.
        int blockX = itemcase.getLocation().getBlockX();
        int blockY = itemcase.getLocation().getBlockY();
        int blockZ = itemcase.getLocation().getBlockZ();
        
        // Create a unique key for this itemcase based on location.
        String key = "itemcases." + blockX + "/" + blockY + "/" + blockZ + ".";
        
        // Set this Itemcase's Owner.
        String uuid = itemcase.getOwner().getUniqueId().toString();
        this.file.set(key + "owner", uuid);
        
        // Set this Itemcase's ItemStack.
        Map<String, Object> itemstack = itemcase.getItemStack().serialize();
        this.file.set(key + "itemstack", itemstack);
        
        // Attempt to save to file.
        this.save(ItemCaseCore.instance);
    }
    
    /**
     * Attempts to load all itemcases from the config.
     * 
     * @return Array list of loaded itemcases.
     * @throws java.io.IOException
     */
    public ArrayList<Itemcase> loadItemcases() throws IOException {
        
        // Attempt to load raw config file.
        this.load(ItemCaseCore.instance);
        
        // Create new array list to store loaded itemcases.
        ArrayList<Itemcase> itemcases = new ArrayList<>();
        
        // Get the list of keys (itemcase locations).
        Set<String> keys = 
                this.file.getConfigurationSection("itemcases").getKeys(false);
        
        // For every key (itemcase).
        for(String key : keys) {
            
            // Split key by '/' to obtain individual coordinates.
            String[] coordinates = key.split("/");
            
            // Convert to block integer coordinates.
            int blockX = Integer.parseInt(coordinates[0]);
            int blockY = Integer.parseInt(coordinates[1]);
            int blockZ = Integer.parseInt(coordinates[2]);
            
            // Create fuller key for ease of use later.
            key = "itemcases." + key + ".";
            
            // Convert to location.
            Location location = new Location(this.world, blockX, blockY,
                    blockZ);
            
            // Get owner.
            UUID uuid = UUID.fromString(this.file.getString(key + ".owner"));
            OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
            
            // Get ItemStack.
            Map<String, Object> itemstackMap = 
                    this.file.getConfigurationSection(key + "itemstack")
                            .getValues(true);
            ItemStack itemstack = ItemStack.deserialize(itemstackMap);
            
            // Create itemcase object.
            Itemcase itemcase = new Itemcase(itemstack, location, owner);
            
            // Add to list.
            itemcases.add(itemcase);
        }
        
        // Return list of loaded itemcases.
        return itemcases;
    }
}
