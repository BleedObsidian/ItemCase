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
package com.gmail.bleedobsidian.itemcase.managers;

import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.configurations.WorldFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A manager of all Itemcases in a server. This manager is also responsible 
 * for loading and saving Itemcases to a config.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ItemcaseManager {
    
    /**
     * A HashMap of each world and its corresponding WorldFile.
     */
    private final HashMap<World, WorldFile> worldFiles = new HashMap<>();
    
    /**
     * A list of all active Itemcase instances.
     */
    private final ArrayList<Itemcase> itemcases = new ArrayList<>();
    
    /**
     * Constructor.
     */
    public ItemcaseManager() {
        
        // For every world.
        for(World world : Bukkit.getWorlds()) {
            
            // Create WorldFile object.
            WorldFile file = new WorldFile(world);
            
            // Add to hashmap.
            this.worldFiles.put(world, file);
            
            // Attempt to load itemcases and add them to list.
            try {
                
                // Load itemcaes.
                ArrayList<Itemcase> loadedItemcases = file.loadItemcases();
                
                // Add to list.
                this.itemcases.addAll(loadedItemcases);
                
            } catch (IOException e) {
                
                // Log error.
                ItemCaseCore.instance.getConsoleLogger().severe(
                        "Failed to load itemcases for world: " +
                                world.getName(), e);
                
                // Exit.
                return;
            }
        }
    }
    
    /**
     * Create a new Itemcase.
     * 
     * @param itemStack The ItemStack to be displayed.
     * @param location The location of the itemcase.
     * @param owner The owner of this itemcase.
     */
    public void createItemcase(ItemStack itemStack, Location location,
            Player owner) {
        
        // Create new itemcase instance.
        Itemcase itemcase = new Itemcase(itemStack, location, owner);
        
        // Add itemcase to the list.
        this.itemcases.add(itemcase);
        
        // Get config file for itemcase's world.
        WorldFile file = this.worldFiles.get(location.getWorld());
        
        // Attempt to save itemcase.
        try {
            
            // Save itemcase.
            file.saveItemcase(itemcase);
            
        } catch (IOException e) {
            
             // Log error.
            ItemCaseCore.instance.getConsoleLogger().severe(
                        "Failed to save new itemcase to config.", e);
        }
    }
    
    /**
     * @return A list of all active Itemcase instances.
     */
    public ArrayList<Itemcase> getItemcases() {
        return this.itemcases;
    }
}
