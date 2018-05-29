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
import java.util.ArrayList;
import org.bukkit.Material;

/**
 * The main configuration file for ItemCase. 
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public class ConfigFile extends ConfigurationFile {
    
    /**
     * Constructor.
     */
    public ConfigFile() {
        
        // The name of this configuration file.
        super("config.yml");
    }
    
    /**
     * @return The chosen locale to use.
     */
    public String getLocale() {
        return this.file.getString("Locale");
    }
    
    /**
     * @return If players can create Itemcases by sneaking and right-clicking.
     */
    public boolean canSneakCreate() {
        return !this.file.getBoolean("Options.Disable-Sneak-Create");
    }
    
    /**
     * @return If orders can timeout.
     */
    public boolean canTimeout() {
        return !this.file.getBoolean("Order.Disable-Timeout");
    }
    
    /**
     * @return The number of seconds required to pass for an order to timeout.
     */
    public int getTimeout() {
        return this.file.getInt("Order.Timeout");
    }
    
    /**
     * @return An array list of materials that can be used as Itemcases.
     */
    public ArrayList<Material> getMaterials() {
        
        // Create array list.
        ArrayList<Material> materials = new ArrayList<>();
        
        // Loop through all material IDs in config.
        this.file.getStringList("Materials").forEach((id) -> {
            
            // Add material to list.
            materials.add(Material.getMaterial(id));
        });
        
        // Return list.
        return materials;
    }
}