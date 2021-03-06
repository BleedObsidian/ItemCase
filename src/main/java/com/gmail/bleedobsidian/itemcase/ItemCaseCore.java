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

import com.gmail.bleedobsidian.itemcase.loggers.ConsoleLogger;
import com.gmail.bleedobsidian.itemcase.loggers.GenericLogger;
import com.gmail.bleedobsidian.itemcase.loggers.ChatLogger;
import com.gmail.bleedobsidian.itemcase.Itemcase.ItemcaseListener;
import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import com.gmail.bleedobsidian.itemcase.managers.OrderManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import java.io.IOException;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
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
     * The language translator.
     */
    private final LanguageTranslator translator = new LanguageTranslator();
    
    /**
     * Custom plugin console logger.
     */
    private final ConsoleLogger consoleLogger = new ConsoleLogger(this,
            translator);
    
    /**
     * Chat logger.
     */
    private final ChatLogger chatLogger = new ChatLogger(translator);
    
    /**
     * Generic Logger.
     */
    private final GenericLogger genericLogger = new GenericLogger(translator);
    
    /**
     * ItemcaseManager.
     */
    private final ItemcaseManager itemcaseManager =  new ItemcaseManager();
    
    /**
     * OrderManager.
     */
    private final OrderManager orderManager = new OrderManager();
    
    /**
     * If the server has Vault.
     */
    private boolean hasVault;
    
    /**
     * If the server has WorldGuard.
     */
    private boolean hasWorldGuard;
    
    /**
     * The economy provider if there is one.
     */
    private Economy economyProvider;
    
    /**
     * WorldGuard.
     */
    private WorldGuardPlugin worldGuard;

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
        
        // Attempt to load translator with given locale.
        if(!this.translator.load(this, this.configFile.getLocale())) {
            
            // Failed to load, stop loading.
            return;
        }
        
        // Log.
        this.consoleLogger.info("console.info.config-loaded");
        
        // Set language placeholder and log.
        this.translator.setPlaceholder("%LANGUAGE%",
                this.configFile.getLocale().name());
        this.consoleLogger.info("console.info.locale");
        
        // Initialize ItemcaseManager.
        this.itemcaseManager.registerListener();
        
        // Register ItemcaseListener.
        this.getServer().getPluginManager().registerEvents(
                new ItemcaseListener(), this);
        
        // Load itemcases for already loaded worlds.
        this.itemcaseManager.initialize();
        
        // Set command executor.
        this.getCommand("itemcase").setExecutor(new CommandHandler());
        
        // Attempt to load Vault.
        this.loadVault();
        
        // Attempt to load WorldGuard.
        this.loadWorldGuard();
        
        // Set version placeholder and log.
        this.translator.setPlaceholder("%VERSION%",
                this.getDescription().getVersion());
        this.consoleLogger.info("console.info.enabled");
    }
    
    @Override
    public void onDisable() {
        
        // Unload all itemcases.
        this.itemcaseManager.unloadItemcases();
        
        // Log.
        this.consoleLogger.info("console.info.unloaded");
    }
    
    /**
     * Attempt to load Vault.
     */
    private void loadVault() {
        
        // Check if this server has Vault installed.
        if(getServer().getPluginManager().getPlugin("Vault") == null) {
            
            // Set false.
            this.hasVault = false;
            
            // Exit.
            return;
        }
        
        // Get server provider of economy class.
        RegisteredServiceProvider<Economy> rsp =
                getServer().getServicesManager().getRegistration(Economy.class);
        
        // If could not find economy service provider.
        if(rsp == null) {
            
            // Set false.
            this.hasVault = false;
            
            // Exit.
            return;
        }
        
        // Log.
        this.consoleLogger.info("console.info.vault-hooked");
        
        // Set economy provider.
        this.economyProvider = rsp.getProvider();
        
        // Set true.
        this.hasVault = true;
    }
    
    /**
     * Attempt to load WorldGuard.
     */
    private void loadWorldGuard() {
        
        // Get plugin.
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        
        // Check if the server has WorldGuard installed.
        if(plugin == null) {
            
            // Set false.
            this.hasWorldGuard = false;
            
            // Exit.
            return;
        }
        
        // Log.
        this.consoleLogger.info("console.info.worldguard-hooked");
        
        // Set true.
        this.hasWorldGuard = true;
        
        // Set worldguard.
        this.worldGuard = (WorldGuardPlugin) plugin;
    }
    
    /**

     * @return Main ItemCase configuration file.
     */
    public ConfigFile getConfigFile() {
        return this.configFile;
    }
    
    /**
     * @return Language translator.
     */
    public LanguageTranslator getTranslator() {
        return this.translator;
    }
    
    /**
     * @return Custom plugin console logger.
     */
    public ConsoleLogger getConsoleLogger() {
        return this.consoleLogger;
    }
    
    /**
     * @return Chat logger.
     */
    public ChatLogger getChatLogger() {
        return this.chatLogger;
    }
    
    /**
     * @return Generic Logger.
     */
    public GenericLogger getGenericLogger() {
        return this.genericLogger;
    }
    
    /**
     * @return Itemcase manager.
     */
    public ItemcaseManager getItemcaseManager() {
        return this.itemcaseManager;
    }
    
    /**
     * @return OrderManager.
     */
    public OrderManager getOrderManager() {
        return this.orderManager;
    }
    
    /**
     * @return If vault is setup on this server.
     */
    public boolean hasVault() {
        return this.hasVault;
    }
    
    /**
     * @return If WorldGuard is setup on this server.
     */
    public boolean hasWorldGuard() {
        return this.hasWorldGuard;
    }
    
    /**
     * @return EconomyProvider.
     */
    public Economy getEconomyProvider() {
        return this.economyProvider;
    }
    
    /**
     * @return WorldGuard.
     */
    public WorldGuardPlugin getWorldGuard() {
        return this.worldGuard;
    }
}
