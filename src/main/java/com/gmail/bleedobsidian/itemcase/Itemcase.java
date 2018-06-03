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

package com.gmail.bleedobsidian.itemcase;

import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Handler of an Itemcase.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class Itemcase {
    
    /**
     * Types of Itemcase.
     */
    public static enum Type {
        SHOWCASE,
        SHOP_BUY,
        SHOP_SELL,
        SHOP_MULTI
    };
    
    /**
     * Storage types.
     */
    public static enum StorageType {
        FINITE,
        INFINITE
    }
    
    /**
     * The ItemStack that this itemcase is showing.
     */
    private final ItemStack itemStack;
    
    /**
     * The Location of this itemcase.
     */
    private final Location location;
    
    /**
     * The chunk that this itemcase is in.
     */
    private final Chunk chunk;
    
    /**
     * The owner of this itemcase.
     */
    private final OfflinePlayer owner;
    
    /**
     * This itemcase's task.
     */
    private ItemcaseTask task;
    
    /**
     * The active item that is currently on display.
     */
    private Item displayItem;
    
    /**
     * This itemcase's Type.
     */
    private Type type = Type.SHOWCASE;
    
    /**
     * The storage type of this itemcase.
     */
    private StorageType storageType = StorageType.FINITE;
    
    /**
     * The storage of this itemcase.
     */
    private Inventory storage;
    
    /**
     * The buy price of this itemcase.
     */
    private double buyPrice = 0;
    
    /**
     * The sell price of this itemcase.
     */
    private double sellPrice = 0;
    
    /**
     * Constructor.
     * 
     * @param type The type of Itemcase.
     * @param itemStack The ItemStack to be displayed.
     * @param location The location of the itemcase.
     * @param owner The owner of this itemcase.
     */
    public Itemcase(Type type, ItemStack itemStack, Location location,
            OfflinePlayer owner) {
        
        // Set type.
        this.type = type;
        
        // Set item stack and ensure stack size is 1.
        this.itemStack = itemStack.clone();
        this.itemStack.setAmount(1);
        
        // Set display name to random UUID to prevent graphical item stacking.
        ItemMeta metadata = this.itemStack.getItemMeta();
        metadata.setDisplayName("com.gmail.bleedobsidian.itemcase:" +
                UUID.randomUUID().toString());
        this.itemStack.setItemMeta(metadata);
        
        // Set location.
        this.location = location;
        
        // Set the chunk. Accessing the chunk with location.getChunk() when we
        // need it causes the chunk to load, meaning the chunk appears as always
        // loaded. Storing it now means we can actually see when it becomes
        // unloaded.
        this.chunk = location.getChunk();
        
        // Set owner.
        this.owner = owner;
        
        // Spawn display item for the first time.
        this.spawnItem();
    }
    
    /**
     * Spawn the display item.
     */
    public void spawnItem()  {
        
        // Get the world that this itemcase is in.
        World world = this.location.getWorld();
        
        // If task task was previously cancelled or never made.
        if(this.task == null || this.task.isCancelled()) {
            
            // Schedule itemcase task to execute every 200 server 
            // ticks (10 secs).
            this.task = new ItemcaseTask(this);
            this.task.runTaskTimer(ItemCaseCore.instance, 0, 200);
        }
        
        // Check if the chunk is currently loaded.
        if(!world.isChunkLoaded(this.chunk)) {
            
            // Not loaded, so don't try to spawn display item.
            return;
        }
        
        // If there was a display item previously.
        if(this.displayItem != null) {
            
            // Remove this item.
            this.displayItem.remove();
        }
        
        // Get the location to spawn the display item at.
        Location displayItemSpawnLocation = this.getDisplayItemSpawnLocation();
        
        // Spawn the item.
        this.displayItem = this.location.getWorld().dropItem(
                displayItemSpawnLocation, this.itemStack);
        
        // Add custom metadata so we know that this item is a display item and
        // shouldn't be picked up by players and which itemcase it belongs to.
        this.displayItem.setMetadata("ItemCase", new FixedMetadataValue(
                ItemCaseCore.instance, this.location.toVector()));
        
        // Prevent the item from having a random veloctiy when spawning so that
        // it falls directly down in to the middle of the block.
        this.displayItem.setVelocity(new Vector(0, 0, 0));
    }
    
    /**
     * Despawn the display item for this itemcase.
     */
    public void despawnItem() {
        
        // Cancel running task.
        this.task.cancel();
        
        // Remove current display item from world.
        this.displayItem.remove();
    }
    
    /**
     * Checks if a given item entity is an itemcase display item of any kind.
     * 
     * @param item Item.
     * @return Boolean.
     */
    private static boolean isItemcaseDisplayItem(Item item) {

        // If item does not have a display name, it can't be a display item.
        if(!item.getItemStack().getItemMeta().hasDisplayName()) {
            return false;
        }

        // Attempt to split display name by ':'
        String[] displayNameParts = 
                item.getItemStack().getItemMeta().getDisplayName().split(":");

        // If display name does not have exactly two parts, it can't be a
        // display item.
        if(displayNameParts.length != 2) {
            return false;
        }

        // Check if item belongs to us, if it does return true.
        return displayNameParts[0].equals("com.gmail.bleedobsidian.itemcase");
    }
    
    /**
     * @return The ItemStack that this itemcase is showing.
     */
    public ItemStack getItemStack() {
        
        // The ItemStack of this Itemcase.
        return this.itemStack;
    }
    
    /**
     * @return The Location of this itemcase.
     */
    public Location getLocation() {
        
        // Location of this itemcase.
        return this.location;
    }
    
    /**
     * @return The owner of this itemcase.
     */
    public OfflinePlayer getOwner() {
        
        // Owner of this itemcase.
        return this.owner;
    }
    
    /**
     * @return Display item.
     */
    public Item getDisplayItem() {
        
        // Display item.
        return this.displayItem;
    }
    
    /**
     * @param type Type.
     */
    public void setType(Type type) {
        
        // Set type.
        this.type = type;
    }
    
    /**
     * @return Type.
     */
    public Type getType() {
        
        // Return type.
        return this.type;
    }
    
    /**
     * @param storageType StorageType.
     */
    public void setStorageType(StorageType storageType) {
        
        // Set storage type.
        this.storageType= storageType;
    }
    
    /**
     * @return Storage Type.
     */
    public StorageType getStorageType() {
        
        // Return storage type.
        return this.storageType;
    }
    
    /**
     * @param inventory Inventory.
     */
    public void setStorage(Inventory inventory) {
        
        // Set inventory.
        this.storage = inventory;
    }
    
    /**
     * @return Inventory.
     */
    public Inventory getStorage() {
        
        // Return inventory.
        return this.storage;
    }
    
    /**
     * @param buyPrice Buy price.
     */
    public void setBuyPrice(double buyPrice) {
        
        // Set price.
        this.buyPrice = buyPrice;
    }
    
    /**
     * @return Buy price.
     */
    public double getBuyPrice() {
        
        // Return price.
        return this.buyPrice;
    }
    
    /**
     * @param buyPrice Sell price.
     */
    public void setSellPrice(double sellPrice) {
        
        // Set price.
        this.sellPrice = sellPrice;
    }
    
    /**
     * @return Sell price.
     */
    public double getSellPrice() {
        
        // Return price.
        return this.sellPrice;
    }
    
    /**
     * @return The default location to spawn the display item.
     */
    public Location getDisplayItemSpawnLocation() {
        
        // Create a location that is in the centre of the block and slightly 
        // above.
        Location displayItemLocation = new Location(
                this.location.getWorld(),
                this.location.getBlockX() + 0.5,
                this.location.getBlockY() + 1.5,
                this.location.getBlockZ() + 0.5);
        
        // Return the default location to spawn the display item.
        return displayItemLocation;
    }
    
    /**
     * A listener used for the functionality of an itemcase. This listener is 
     * used to prevent display items from being picked up or despawned.
     */
    public static final class ItemcaseListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEntityPickupItem(EntityPickupItemEvent event) {

            // If this item entity is a display item.
            if(Itemcase.isItemcaseDisplayItem(event.getItem())) {
                
                // Prevent this item from being picked up.
                event.setCancelled(true);
            }
        }
       
        @EventHandler(priority = EventPriority.HIGHEST)
        public void onItemDespawn(ItemDespawnEvent event) {

            // If this item entity is a display item.
            if(Itemcase.isItemcaseDisplayItem(event.getEntity())) {

                // Prevent this item from despawning.
                event.setCancelled(true);
            }
        }
        
        @EventHandler(priority = EventPriority.NORMAL)
        public void onBlockBreakEvent(BlockBreakEvent event) {
            
            // Get ItemcaseManager.
            ItemcaseManager itemcaseManager = 
                    ItemCaseCore.instance.getItemcaseManager();

            // For every Itemcase.
            for(Itemcase itemcase : itemcaseManager.getItemcases()) {
                
                // Check if this block was an itemcase.
                if(itemcase.location.equals(event.getBlock().getLocation())) {
                    
                    // Cancel the event.
                    event.setCancelled(true);
                }
            }
        }
        
        @EventHandler(priority = EventPriority.NORMAL)
        public void onBlockPlaceEvent(BlockPlaceEvent event) {
            
            // Get ItemcaseManager.
            ItemcaseManager itemcaseManager = 
                    ItemCaseCore.instance.getItemcaseManager();

            // For every Itemcase.
            for(Itemcase itemcase : itemcaseManager.getItemcases()) {
                
                // Check if the block placed was on an itemcase.
                if(itemcase.location.equals(event.getBlock().getLocation())) {
                    
                    // Cancel the event.
                    event.setCancelled(true);
                }
            }
        }
    }
    
    /**
     * A runnable task that is executed every 10 seconds to check if an 
     * itemcase's display item is for some reason dead or has been moved. This
     * is particularly useful when servers use anti-lag plugins that forcibly
     * kill entities or a player has somehow caused an item to move.
     */
    private final class ItemcaseTask extends BukkitRunnable {
        
        /**
         * The itemcase that this task is for.
         */
        private final Itemcase itemcase;
        
        /**
         * Constructor.
         * 
         * @param itemcase The itemcase this task is for.
         */
        public ItemcaseTask(Itemcase itemcase) {
            
            // Set itemcase.
            this.itemcase = itemcase;
        }

        @Override
        public void run() {
            
            // If chunk is not currently loaded.
            if(!this.itemcase.chunk.isLoaded()) {
                
                // Dont bother running this task.
                return;
            }
            
            // Get the default display item location.
            Location location = this.itemcase.getLocation();
            
            // List of valid materials.
            ArrayList<Material> materials = 
                    ItemCaseCore.instance.getConfigFile().getMaterials();
            
            // Check if this block still exists.
            if(!materials.contains(location.getBlock().getType())) {
                
                // Set to default.
                location.getBlock().setType(materials.get(0));
            }
            
            // Get all entites near itemcase. (Uses quite a large area just in
            // case item is not where it should be).
            Collection<Entity> entities = location.getWorld().getNearbyEntities(
                    location, 5f, 5f, 5f);
            
            // A counter to count how many itemcase items are nearby.
            int numberOfItemcaseItems = 0;
            
            // Loop through every entity.
            for(Entity entity : entities) {
                
                // If the entity is not an item, skip.
                if(!(entity instanceof Item)) {
                    continue;
                }
                
                // Check the entity is an ItemCase display item.
                if(entity.hasMetadata("ItemCase")) {
                    
                    // Get the vector location that belongs to this itemcase 
                    // item.
                    Vector entityVector = (Vector) ((FixedMetadataValue) 
                            entity.getMetadata("ItemCase").get(0)).value();
                    
                    // Check that the entity is an item for this itemcase only.
                    if(entityVector.getBlockX() != 
                            this.itemcase.location.getBlockX() ||
                            entityVector.getBlockY() != 
                            this.itemcase.location.getBlockY() ||
                            entityVector.getBlockZ() != 
                            this.itemcase.location.getBlockZ()) {
                        
                        // Skip if not.
                        continue;
                    }
                    
                    // Increment counter.
                    numberOfItemcaseItems++;
                    
                    // If the entity is not the current item we know about, we
                    // must have a duplicate caused by nms.
                    if(this.itemcase.getDisplayItem().getUniqueId().compareTo(
                            entity.getUniqueId()) != 0) {
                        
                        // Remove this item.
                        entity.remove();
                    } else {
                        
                        // If the item on the floor has the same UUID, update 
                        // the reference just in case it has changed.
                        this.itemcase.displayItem = (Item) entity;
                    }
                    
                // If the item has no metadata but is some form of display item.
                } else if(Itemcase.isItemcaseDisplayItem((Item) entity)) {
                    
                    // Remove this item as it is probably left over from a
                    // restart hence no metadata.
                    entity.remove();
                }
            }
            
            // If there were no itemcase items nearby.
            if(numberOfItemcaseItems == 0) {
                
                // Spawn a new item as for some reason the item has despawned
                // (usually because of anti-lag plugins.)
                this.itemcase.spawnItem();
            }

            // Get the current location of the display item.
            double x = this.itemcase.getDisplayItem().getLocation().getX();
            double y = this.itemcase.getDisplayItem().getLocation().getBlockY();
            double z = this.itemcase.getDisplayItem().getLocation().getZ();
            
            // Get the correct spawn location of the display item.
            Location displayItemSpawnLocation = 
                    this.itemcase.getDisplayItemSpawnLocation();

            // Check if the display item has for some reason moved.
            if(x != displayItemSpawnLocation.getX() ||
                    y != displayItemSpawnLocation.getBlockY() - 1 ||
                    z != displayItemSpawnLocation.getZ()) {

                // Move the display item back to where it should be.
                this.itemcase.getDisplayItem().teleport(
                        displayItemSpawnLocation);
            }
        }
    }
}