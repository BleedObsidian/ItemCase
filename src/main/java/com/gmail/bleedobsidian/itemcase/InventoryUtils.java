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

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Static utility helper methods for inventories.
 * 
 * @author Jesse Prescott (BleedObsidian).
 */
public final class InventoryUtils {
    
    /**
     * Count the number of given ItemStacks exist in the given Inventory.
     * 
     * @param inventory Inventory.
     * @param itemstack ItemStack to count.
     * @return Count.
     */
    public static int count(Inventory inventory, ItemStack itemstack) {
        
        // Define stock.
        int stock = 0;

        // For every itemstack.
        for(ItemStack content : inventory.getContents()) {
            
            // If content is null, skip.
            if(content == null)
                continue;

            // If itemstacks match.
            if(content.isSimilar(itemstack)) {

                // Add amount to stock.
                stock += content.getAmount();
            }
        }
        
        // Return stock.
        return stock;
    }
    
}
