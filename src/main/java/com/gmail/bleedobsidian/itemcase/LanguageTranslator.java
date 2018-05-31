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

import com.gmail.bleedobsidian.itemcase.configurations.LanguageFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility class used for communicating in different dialects.
 * 
 * @author Jesse Prescott (BleedObsidian).
 */
public final class LanguageTranslator {
    
    /**
     * Supported languages.
     */
    public static enum Language {
        EN
    };
    
    /**
     * The language file for the chosen language.
     */
    private LanguageFile file;
    
    /**
     * A hash map of placeholders.
     */
    private HashMap<String, String> placeholders = new HashMap<>();
    
    /**
     * Load corresponding language file.
     * 
     * @param plugin JavaPlugin.
     * @param language Language to communicate in.
     * @return If loading was successful.
     */
    public boolean load(JavaPlugin plugin, Language language) {
        
        // Create language file.
        this.file = new LanguageFile(language);
        
        // Attempt to load language file.
        try {
            
            // Load language file.
            this.file.load(plugin);
            
        } catch (IOException e) {
            
            // Display error.
            ItemCaseCore.instance.getConsoleLogger().severe(
                    "Failed to load configuration file.", e);
            
            // Failed to load.
            return false;
        }
        
        // Successfully loaded.
        return true;
    }
    
    /**
     * Get translation.
     * 
     * @param key Message key.
     * @return String.
     */
    public String getTranslation(String key) {
        
        // Get raw translation.
        String translation = this.file.getRawTranslation(key);
        
        // For every placeholder.
        for(Entry<String, String> entry : this.placeholders.entrySet()) {
            
            // Replace the placeholder with given value if placeholder is
            // present in string.
            translation = translation.replace(entry.getKey(), entry.getValue());
        }
       
        // Return translation.
        return translation;
    }
    
    /**
     * Set the value of a given placeholder.
     * 
     * @param placeholder Placeholder.
     * @param value Value.
     */
    public void setPlaceholder(String placeholder, String value) {
        
        // Update HashMap.
        this.placeholders.put(placeholder, value);
    }
    
    /**
     * If given string is a valid message key.
     * 
     * @param string String.
     * @return Boolean.
     */
    public boolean isKey(String string) {
        
        // If the file is not loaded yet.
        if(!this.file.isLoaded()) {
            
            // Return false as we cant do translations yet.
            return false;
        }
        
        // Check if key exists and return result.
        return this.file.hasKey(string);
    }
}
