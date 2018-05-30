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

import com.gmail.bleedobsidian.itemcase.Itemcase.ItemcaseListener;
import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import java.io.IOException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ItemCase is a Bukkit plugin allowing you to showcase items on slabs, that
 * can also be used as shops.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ItemCaseCore extends JavaPlugin {
    
    /**
     * Current running instance of ItemCaseCore.
     */
    public static ItemCaseCore instance;
    
    /**
     * Main ItemCase configuration file.
     */
    private final ConfigFile configFile = new ConfigFile();
    
    /**
     * Custom plugin console logger.
     */
    private final ConsoleLogger consoleLogger = new ConsoleLogger(this);
    
    /**
     * ItemcaseManager.
     */
    private final ItemcaseManager itemcaseManager =  new ItemcaseManager();

    @Override
    public void onEnable() {
        
        // Set current instance.
        ItemCaseCore.instance = this;
        
        // Start metrics.
        PluginMetrics metrics = new PluginMetrics(this);
        
        // Attempt to load configuration file.
        try {
            
            // Load configuration file.
            this.configFile.load(this);
            
        } catch (IOException e) {
            
            // Display error.
            this.consoleLogger.severe("Failed to load configuration file.", e);
            
            // Stop loading.
            return;
        }
        
        // Log.
        this.consoleLogger.info("Successfully loaded main configuration file.");
        
        // Initialize ItemcaseManager.
        this.itemcaseManager.registerListener();
        
        // Register ItemcaseListener.
        this.getServer().getPluginManager().registerEvents(
                new ItemcaseListener(), this);
        
        // Log.
        this.consoleLogger.info("Main event listener registered.");
        
        // Log.
        this.consoleLogger.info("ItemCase " +
                this.getDescription().getVersion() + " enabled.");
    }
    
    @Override
    public void onDisable() {
        
        // Unload all itemcases.
        this.itemcaseManager.unloadItemcases();
        
        // Log.
        this.consoleLogger.info("Itemcases unloaded successfully.");
    }
    
    /**

     * @return Main ItemCase configuration file.
     */
    public ConfigFile getConfigFile() {
        return this.configFile;
    }
    
    /**
     * @return Custom plugin console logger.
     */
    public ConsoleLogger getConsoleLogger() {
        return this.consoleLogger;
    }
    
    /**
     * @return Itemcase manager.
     */
    public ItemcaseManager getItemcaseManager() {
        return this.itemcaseManager;
    }
}
