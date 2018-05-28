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

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Handles bStats plugin metrics and any custom graphs.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class PluginMetrics {
    
    /**
     * Constructor, starts bStats.
     * 
     * @param plugin The Bukkit JavaPlugin.
     */
    public PluginMetrics(JavaPlugin plugin) {
        
        // Start bStats.
        Metrics metrics = new Metrics(plugin);
    }
}
