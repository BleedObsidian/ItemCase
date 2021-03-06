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
import com.gmail.bleedobsidian.itemcase.Itemcase.StorageType;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import com.gmail.bleedobsidian.itemcase.configurations.WorldFile;
import com.onarandombox.MultiverseCore.event.MVWorldDeleteEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.world.WorldLoadEvent;
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
     * Initialize this class.
     */
    public void initialize() {
        
        // For every currently loaded world.
        for(World world : Bukkit.getWorlds()) {
            
            // Request itemcases to be loaded.
            this.loadItemcases(world);
        }
    }
    
    /**
     * Loads itemcases for the given world.
     * 
     * @param world World.
     */
    private void loadItemcases(World world) {
        
        // Create WorldFile object.
        WorldFile file = new WorldFile(world);

        // Add to hashmap.
        ItemcaseManager.this.worldFiles.put(world, file);

        // Attempt to load itemcases and add them to list.
        try {

            // Load itemcaes.
            ArrayList<Itemcase> loadedItemcases = file.loadItemcases();

            // Add to list.
            ItemcaseManager.this.itemcases.addAll(loadedItemcases);

        } catch (IOException e) {

            // Log error.
            ItemCaseCore.instance.getConsoleLogger().severe(
                    "Failed to load itemcases for world: " +
                            world.getName(), e);

            // Exit.
            return;
        }

        // Set world name placeholder and log.
        ItemCaseCore.instance.getTranslator().setPlaceholder(
                "%WORLD_NAME%", world.getName());
        ItemCaseCore.instance.getConsoleLogger().info(
                "console.info.loaded");
    }
    
    /**
     * Save given Itemcase.
     * 
     * @param itemcase Itemcase.
     */
    public void saveItemcases(Itemcase itemcase) {
        
        // Get world file.
        WorldFile file = this.worldFiles.get(itemcase.getLocation().getWorld());
        
        // Attempt to save itemcase.
        try {
            
            // Save itemcase.
            file.saveItemcase(itemcase);
            
        } catch (IOException e) {
            
             // Log error.
            ItemCaseCore.instance.getConsoleLogger().severe(
                        "Failed to save itemcase to config.", e);
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
            OfflinePlayer owner) {
        
        // Create new itemcase instance.
        Itemcase itemcase = new Itemcase(Itemcase.Type.SHOWCASE, itemStack,
                location, owner);
        
        // Spawn item.
        itemcase.spawnItem();
        
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
     * Destroy given Itemcase.
     * 
     * @param itemcase Itemcase.
     */
    public void destroyItemcase(Itemcase itemcase) {
        
        // Despawn Itemcase's item.
        itemcase.despawnItem();
        
        // Remove itemcase from list.
        this.itemcases.remove(itemcase);
        
        // Get config file for itemcase's world.
        WorldFile file = this.worldFiles.get(itemcase.getLocation().getWorld());
        
         // Attempt to delete itemcase.
        try {
            
            // Delete itemcase.
            file.deleteItemcase(itemcase);
            
        } catch (IOException e) {
            
             // Log error.
            ItemCaseCore.instance.getConsoleLogger().severe(
                        "Failed to delete itemcase from config.", e);
        }
    }
    
    /**
     * Unload all currently loaded Itemcases.
     */
    public void unloadItemcases() {
        
        // For every loaded itemcase.
        for(Itemcase itemcase : this.itemcases) {
            
            // Despawn the item.
            itemcase.despawnItem();
        }
        
        // Clear list.
        this.itemcases.clear();
    }
    
    /**
     * Register the event listener for this class.
     */
    public void registerListener() {
        
        // Register listener with bukkit.
        Bukkit.getPluginManager().registerEvents(
                new ItemcaseManagerListener(), ItemCaseCore.instance);
    }
    
    /**
     * If the given location is an itemcase or not.
     * 
     * @param location Location.
     * @return Boolean.
     */
    public boolean isItemcase(Location location) {
        
        // For every itemcase.
        for(Itemcase itemcase : this.itemcases) {
            
            // Check if location matches.
            if(itemcase.getLocation().equals(location)) {
                
                // Return true.
                return true;
            }
        }
        
        // Otherwise return false.
        return false;
    }
    
    /**
     * Attempt to get the itemcase at the given location.
     * 
     * @param location Location.
     * @return ItemCase.
     */
    public Itemcase getItemcase(Location location) {
        
        // For every itemcase.
        for(Itemcase itemcase : this.itemcases) {
            
            // Check if location matches.
            if(itemcase.getLocation().equals(location)) {
                
                // Return itemcase.
                return itemcase;
            }
        }
        
        // No itemcase found.
        return null;
    }
    
    /**
     * @return A list of all active Itemcase instances.
     */
    public ArrayList<Itemcase> getItemcases() {
        
        // List of Itemcases.
        return this.itemcases;
    }
    
    /**
     * A bukkit listener for the ItemcaseManager. Used to load Itemcases upon
     * world loading.
     */
    private final class ItemcaseManagerListener implements Listener {
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onWorldLoadEvent(WorldLoadEvent event) {
            
            // Get world.
            World world = event.getWorld();
            
            // Request itemcases to be loaded.
            ItemcaseManager.this.loadItemcases(world);
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onWorldDeleteEvent(MVWorldDeleteEvent event) {
            
            // Get world name.
            String worldName = event.getWorld().getName();
            
            // WorldFile.
            WorldFile worldFile = null;
            
            // For every entry.
            for(Entry<World, WorldFile> entry :
                ItemcaseManager.this.worldFiles.entrySet()) {
                
                // Check if world name matches.
                if(entry.getKey().getName() == worldName) {
                    
                    // Attempt to delete config.
                    try {
                        
                        // Set world file.
                        worldFile = entry.getValue();
                        
                        // Delete config.
                        entry.getValue().deleteDirectory();
                        
                    } catch (IOException e) {
                        
                         // Log error.
                        ItemCaseCore.instance.getConsoleLogger().severe(
                            "Failed to delete itemcase config for world:"
                                    + worldName, e);
                    }
                }
            }
            
            // Remove world file.
            ItemcaseManager.this.worldFiles.values().remove(worldFile);
            
            // List to store itemcases of this world.
            ArrayList<Itemcase> itemcases = new ArrayList();
            
            // For every itemcase.
            for(Itemcase itemcase : ItemcaseManager.this.itemcases) {
                
                // Check if itemcase was in deleted world.
                if(itemcase.getLocation().getWorld().getName() == worldName) {
                    
                    // Despawn item.
                    itemcase.despawnItem();
                    
                    // Add to list.
                    itemcases.add(itemcase);
                }
            }
            
            // Remove all itemcases that were in this world from list.
            ItemcaseManager.this.itemcases.removeAll(itemcases);
        }
        
        @EventHandler(priority = EventPriority.MONITOR)
        public void onInventoryCloseEvent(InventoryCloseEvent event) {
            
            // Get inventory name.
            String name = event.getInventory().getName();
            
            // If inventory is Itemcase inventory.
            if(!name.equals(Itemcase.INVENTORY_NAME)) {
                
                // Exit.
                return;
            }
            
            // For every Itemcase.
            for(Itemcase itemcase : ItemcaseManager.this.itemcases) {
                
                // If itemcase is not a shop it wont have storage.
                if(itemcase.getType() == Type.SHOWCASE) {
                    
                    // Skip.
                    continue;
                }
                
                // If itemcase is inifinite, it wont have storage.
                if(itemcase.getStorageType() == StorageType.INFINITE) {
                    
                    // Skip.
                    continue;
                }
                
                // If inventory belongs to this itemcase.
                if(itemcase.getStorage().equals(event.getInventory())) {
                    
                    // Get world file.
                    WorldFile file = ItemcaseManager.this.worldFiles.get(
                            itemcase.getLocation().getWorld());
                    
                    // Attempt to save itemcase.
                    try {
                        
                        // Save itemcase.
                        file.saveItemcase(itemcase);
                        
                    } catch (IOException e) {
                        
                         // Log error.
                        ItemCaseCore.instance.getConsoleLogger().severe(
                            "Failed to save itemcase after storage change.", e);
                    }
                }
            }
        }
    }
    
}
