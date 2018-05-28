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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An object with a corresponding configuration file that can be loaded/saved.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public class ConfigurationFile {
    
    /**
     * FileConfiguration handle.
     */
    protected FileConfiguration file;
    
    /**
     * The name of this configuration file.
     */
    private String name;
    
    /**
     * @param name The name of this configuration file.
     */
    public ConfigurationFile(String name) {
        
        // Set name.
        this.name = name;
    }
    
    /**
     * Loads this configuration file from the given plugin's data folder, if it
     * does not exist, it is copied from the plugins jar.
     * 
     * @param plugin JavaPlugin.
     * @throws IOException.
     */
    public void load(JavaPlugin plugin) throws IOException {
        
        // Create file reference.
        File fileReference = new File(plugin.getDataFolder(), this.name);
        
        // If the file doesn't exist...
        if(!fileReference.exists()) {
            
            // Copy default config from jar to data folder.
            this.copyDefault(plugin);
        }
        
        // Attempt to load it.
        this.file = YamlConfiguration.loadConfiguration(fileReference);
    }
    
    /**
     * Attempt to copy the default configuration file from the given plugin's
     * jar to the data folder.
     * 
     * @param plugin JavaPlugin.
     * @throws IOException.
     */
    private void copyDefault(JavaPlugin plugin) throws IOException {
        
        // Create reference to output file.
        File outputFile = new File(plugin.getDataFolder(), this.name);
        
        // Attempt to create an input stream of the default file in the jar.
        InputStream inputStream = plugin.getResource(this.name);
        
        // Check if we succeeded.
        if(inputStream == null) {
            
            // Throw exception.
            throw new IOException("Failed to create input stream to default"
                    + " file.");
        }
        
        // Create initial file.
        outputFile.getParentFile().mkdirs();
        
        // Copy file.
        Files.copy(inputStream, outputFile.toPath(), 
                StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Save this configuration file in the given plugin's data folder.
     * 
     * @param plugin Plugin.
     * @throws IOException.
     */
    public void save(JavaPlugin plugin) throws IOException {
        
        // Create reference to output file.
        File outputFile = new File(plugin.getDataFolder(), this.name);
        
        // Attempt to save file.
        this.file.save(outputFile);
    }
}
