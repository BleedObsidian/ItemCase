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

import org.bukkit.plugin.java.JavaPlugin;

/**
 * ItemCase is a Bukkit plugin allowing you to showcase items on slabs, that can also be used as
 * shops.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ItemCase extends JavaPlugin {
    
    /**
     * Custom plugin console logger.
     */
    private ConsoleLogger consoleLogger;

    @Override
    public void onEnable() {
        
        // Start metrics.
        PluginMetrics metrics = new PluginMetrics(this);
       
        // Set custom plugin console logger.
        this.consoleLogger = new ConsoleLogger(this);
    }
    
    /**
     * @return Custom plugin console logger.
     */
    public ConsoleLogger getConsoleLogger() {
        
        // Custom plugin console logger.
        return this.consoleLogger;
    }
}
