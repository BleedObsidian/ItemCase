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
import com.gmail.bleedobsidian.itemcase.Itemcase.StorageType;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.multiverse.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
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
        
        // Set this Itemcase's type.
        this.file.set(key + "type", itemcase.getType().name());
        
        // Set this Itemcase's Owner.
        String uuid = itemcase.getOwner().getUniqueId().toString();
        this.file.set(key + "owner", uuid);
        
        // Set this Itemcase's ItemStack.
        Map<String, Object> itemstack = itemcase.getItemStack().serialize();
        this.file.set(key + "itemstack", itemstack);
        
        // If itemcase is a shop.
        if(itemcase.getType() != Type.SHOWCASE) {
            
            // Set storage type.
            this.file.set(key + "shop.storage-type",
                    itemcase.getStorageType().name());
            
            // If shop has finite storage.
            if(itemcase.getStorageType() == StorageType.FINITE) {
                
                // Serialize inventory.
                Map<String, Object> inventory = 
                        this.serializeInventory(itemcase.getStorage());
                
                // Set inventory.
                this.file.set(key + "shop.storage", inventory);
            }
            
            // If this itemcase buys.
            if(itemcase.getType() == Type.SHOP_BUY ||
                    itemcase.getType() == Type.SHOP_MULTI) {
                
                // Set buy price.
                this.file.set(key + "shop.buy-price", itemcase.getBuyPrice());
            }
            
            // If this itemcase sells.
            if(itemcase.getType() == Type.SHOP_SELL ||
                    itemcase.getType() == Type.SHOP_MULTI) {
                
                // Set sell price.
                this.file.set(key + "shop.sell-price", itemcase.getSellPrice());
            }
        } else {
            
            // Set shop section to null.
            this.file.set(key + "shop", null);
        }
        
        // Attempt to save to file.
        this.save(ItemCaseCore.instance);
    }
    
    /**
     * Deletes the given Itemcase from config.
     * 
     * @param itemcase Itemcase.
     * @throws IOException
     */
    public void deleteItemcase(Itemcase itemcase) throws IOException {
        
        // Get block coordinates of Itemcase.
        int blockX = itemcase.getLocation().getBlockX();
        int blockY = itemcase.getLocation().getBlockY();
        int blockZ = itemcase.getLocation().getBlockZ();
        
        // Create a unique key for this itemcase based on location.
        String key = "itemcases." + blockX + "/" + blockY + "/" + blockZ;
        
        // Delete itemcase.
        this.file.set(key, null);
        
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
            
            // Get type.
            Type type = Itemcase.Type.valueOf(
                    this.file.getString(key + ".type"));
            
            // Get owner.
            UUID uuid = UUID.fromString(this.file.getString(key + ".owner"));
            OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
            
            // Get ItemStack.
            Map<String, Object> itemstackMap = 
                    this.file.getConfigurationSection(key + "itemstack")
                            .getValues(true);
            ItemStack itemstack = ItemStack.deserialize(itemstackMap);
            
            // Create itemcase object.
            Itemcase itemcase = new Itemcase(type, itemstack, location, owner);
            
            // If itemcase is a shop.
            if(type != Type.SHOWCASE) {
                
                // Get storage type.
                StorageType storageType = StorageType.valueOf(
                        this.file.getString(key + "shop.storage-type"));
                
                // Set storage type.
                itemcase.setStorageType(storageType);
                
                // If itemcase has finite storage.
                if(storageType == StorageType.FINITE) {
                    
                    // Deserialse inventory.
                    Inventory inventory = this.deserializeInventory(
                            this.file.getConfigurationSection(
                                    key + "shop.storage").getValues(true));
                    
                    // Set inventory.
                    itemcase.setStorage(inventory);
                }
                
                // If this itemcase buys.
                if(type == Type.SHOP_BUY || type == Type.SHOP_MULTI) {

                    // Get buy price.
                    double buyPrice = 
                            this.file.getDouble(key + "shop.buy-price");
                    
                    // Set buy price.
                    itemcase.setBuyPrice(buyPrice);
                }

                // If this itemcase sells.
                if(type == Type.SHOP_SELL || type == Type.SHOP_MULTI) {

                    // Get sell price.
                    double sellPrice = 
                            this.file.getDouble(key + "shop.sell-price");
                    
                    // Set sell price.
                    itemcase.setSellPrice(sellPrice);
                }
            }
            
            // Add to list.
            itemcases.add(itemcase);
        }
        
        // Return list of loaded itemcases.
        return itemcases;
    }
    
    /**
     * Delete directory and config.
     * 
     * @throws IOException
     */
    public void deleteDirectory() throws IOException {
        
        // Create file reference.
        File fileReference = new File(ItemCaseCore.instance.getDataFolder(),
                this.world.getName());
        
        // Nullify reference.
        this.file = null;
            
        // Delete directory.
        FileUtils.deleteDirectory(fileReference);
    }
    
    /**
     * Serialize the given inventory.
     * 
     * @param inventory Inventory.
     * @return Map.
     */
    private Map<String, Object> serializeInventory(Inventory inventory) {
        
        // Create map.
        Map<String, Object> map = new HashMap();
        
        // Set size.
        map.put("size", inventory.getSize());
        
        // Set name.
        map.put("name", inventory.getName());
        
        // Loop through all content slots.
        for(int i = 0; i < inventory.getSize(); i++) {
            
            // Check if content slot has any items.
            if (inventory.getItem(i) != null) {
                
                // Serialize itemstack in content slot.
                map.put("" + i, inventory.getItem(i).serialize());
            }
        }
        
        // Return map.
        return map;
    }
    
    /**
     * Deserialize inventory from given map.
     * 
     * @param map Map.
     * @return Inventory.
     */
    private Inventory deserializeInventory(Map<String, Object> map) {
        
        // Get size.
        int size = Integer.parseInt((String) map.get("size"));
        
        // Get name.
        String name = (String) map.get("name");
        
        // Create inventory.
        Inventory inventory = Bukkit.createInventory(null, size, name);
        
        // For every map entry.
        for(Entry<String, Object> entry : map.entrySet()) {
            
            // Check entry is parameter.
            if(entry.getKey().equals("size") ||
                    entry.getKey().equals("name")) {
                
                // Skip.
                continue;
            }
            
            // Get slot.
            int slot = Integer.parseInt(entry.getKey());
            
            // Deserialize item.
            ItemStack item = ItemStack.deserialize(
                    (Map<String, Object>) entry.getValue());
            
            // Set item in slot.
            inventory.setItem(slot, item);
        }
        
        // Return inventory.
        return inventory;
    }
}
