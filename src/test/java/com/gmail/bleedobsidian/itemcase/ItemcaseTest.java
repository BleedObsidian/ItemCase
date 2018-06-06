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

import com.gmail.bleedobsidian.itemcase.Itemcase.ItemcaseTask;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.powermock.api.mockito.PowerMockito;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test for {@link com.gmail.bleedobsidian.itemcase.Itemcase}
 * 
 * @author Jesse Prescott (BleedObsidian).
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Itemcase.class)
public class ItemcaseTest {
    
    /**
     * The material of the itemcase
     */
    private Material itemcaseMaterial;
    
    /**
     * Creates a valid itemcase.
     * 
     * @return Itemcase.
     */
    private Itemcase createValidItemcase() throws Exception {
        
        // Itemcase type.
        Type type = Type.SHOP_MULTI;
        
        // Mock ItemMeta.
        ItemMeta meta = PowerMockito.mock(ItemMeta.class);
        
        // Mock itemstack.
        ItemStack itemstack = PowerMockito.mock(ItemStack.class);
        
        // When clone method is called, return mock itemstack.
        when(itemstack.clone()).thenReturn(itemstack);
        
        // Return mock item meta.
        when(itemstack.getItemMeta()).thenReturn(meta);
        
        // Mock world.
        World world = PowerMockito.mock(World.class);
        
        // Mock block.
        Block block = PowerMockito.mock(Block.class);
        
        // Return material.
        when(block.getType()).thenReturn(this.itemcaseMaterial);
        
        // Mock location.
        Location location = PowerMockito.mock(Location.class);
        
        // Return mock world when asked for.
        when(location.getWorld()).thenReturn(world);
        
        // Return mock block.
        when(location.getBlock()).thenReturn(block);
        
        // Mock chunk.
        Chunk chunk = PowerMockito.mock(Chunk.class);
        
        // When asking for the chunk from location, return mock.
        when(location.getChunk()).thenReturn(chunk);
        
        // Mock player.
        OfflinePlayer player = PowerMockito.mock(OfflinePlayer.class);
        
        // Create new itemcase to test.
        Itemcase itemcase = new Itemcase(type, itemstack, location, player);
        
        // Test type is correct.
        assertEquals(itemcase.getType(), type);
        
        // Test itemstack is correct.
        assertEquals(itemcase.getItemStack(), itemstack);
        
        // Verify itemstack amount was set to 1.
        verify(itemstack).setAmount(1);
        
        // Test owner is correct.
        assertEquals(itemcase.getOwner(), player);
        
        // Return itemcase.
        return itemcase;
    }
    
    @Test
    public void constructor_valid_pass() throws Exception {
        
        // Create itemcase.
        Itemcase itemcase = this.createValidItemcase();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullType_exception() {
        
        // Mock itemstack.
        ItemStack itemstack = PowerMockito.mock(ItemStack.class);
        
        // When clone method is called, return mock itemstack.
        when(itemstack.clone()).thenReturn(itemstack);
        
        // Mock location.
        Location location = PowerMockito.mock(Location.class);
        
        // Mock chunk.
        Chunk chunk = PowerMockito.mock(Chunk.class);
        
        // When asking for the chunk from location, return mock.
        when(location.getChunk()).thenReturn(chunk);
        
        // Mock player.
        OfflinePlayer player = PowerMockito.mock(OfflinePlayer.class);
        
        // Create object.
        new Itemcase(null, itemstack, location, player);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullItemStack_exception() {
        
        // Itemcase type.
        Type type = Type.SHOP_MULTI;
        
        // Mock location.
        Location location = PowerMockito.mock(Location.class);
        
        // Mock chunk.
        Chunk chunk = PowerMockito.mock(Chunk.class);
        
        // When asking for the chunk from location, return mock.
        when(location.getChunk()).thenReturn(chunk);
        
        // Mock player.
        OfflinePlayer player = PowerMockito.mock(OfflinePlayer.class);
        
        // Create itemcase.
        new Itemcase(type, null, location, player);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullLocation_exception() {
        
        // Itemcase type.
        Type type = Type.SHOP_MULTI;
        
        // Mock itemstack.
        ItemStack itemstack = PowerMockito.mock(ItemStack.class);
        
        // When clone method is called, return mock itemstack.
        when(itemstack.clone()).thenReturn(itemstack);
        
        // Mock player.
        OfflinePlayer player = PowerMockito.mock(OfflinePlayer.class);
        
        // Create itemcase.
        new Itemcase(type, itemstack, null, player);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void constructor_nullOwner_exception() {
        
        // Itemcase type.
        Type type = Type.SHOP_MULTI;
        
        // Mock itemstack.
        ItemStack itemstack = PowerMockito.mock(ItemStack.class);
        
        // When clone method is called, return mock itemstack.
        when(itemstack.clone()).thenReturn(itemstack);
        
        // Mock location.
        Location location = PowerMockito.mock(Location.class);
        
        // Mock chunk.
        Chunk chunk = PowerMockito.mock(Chunk.class);
        
        // When asking for the chunk from location, return mock.
        when(location.getChunk()).thenReturn(chunk);
        
        // Create itemcase.
        new Itemcase(type, itemstack, location, null);
    }
    
    public void spawnItem_taskStarted_pass() throws Exception {
        
        // Create valid itemcase.
        Itemcase itemcase = this.createValidItemcase();
        
        // Create mock bukkit runnable.
        ItemcaseTask runnable = PowerMockito.mock(ItemcaseTask.class);
        
        // When creating a new bukkit runnable (as done so inside of spawnItem
        // return a mock.
        whenNew(ItemcaseTask.class).withAnyArguments().thenReturn(runnable);
        
        // Pretend chunk is loaded.
        when(itemcase.getLocation().getWorld().isChunkLoaded(
                any(Chunk.class))).thenReturn(true);
        
        // Spawn item.
        itemcase.spawnItem();
        
        // verify task was started.
        verify(runnable)
                .runTaskTimer(any(JavaPlugin.class), anyInt(), anyInt());
    }
}
