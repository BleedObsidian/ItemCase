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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

/**
 * A custom logger used to add color to the console when using warnings and
 * errors.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ConsoleLogger extends Logger {
    
     /**
     * White color string for console.
     */
    private static final String WHITE_COLOR = 
            Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString();
    
    /**
     * Yellow color string for console.
     */
    private static final String YELLOW_COLOR = 
            Ansi.ansi().fg(Ansi.Color.YELLOW).bold().toString();
    
    /**
     * Red color string for console.
     */
    private static final String RED_COLOR =
            Ansi.ansi().fg(Ansi.Color.RED).bold().toString();
    
    /**
     * The plugin prefix.
     */
    private final String prefix;
    
    /**
     * Language translator.
     */
    private final LanguageTranslator translator;
    
    /**
     * Constructor.
     * 
     * @param plugin The B
     */
    public ConsoleLogger(JavaPlugin plugin, LanguageTranslator translator) {
        
        // Call parent constructor.
        super(plugin.getName(), null);
        
        // Set logger settings.
        super.setParent(plugin.getServer().getLogger());
        super.setLevel(Level.ALL);
        
        // Set plugin prefix.
        this.prefix = "[" + plugin.getName() + "] ";
        
        // Set translator.
        this.translator = translator;
    }
    
    @Override
    public void log(LogRecord logRecord) {
        
        // Message.
        String message = logRecord.getMessage();
        
        // If message is a message key.
        if(translator.isKey(message)) {
            
            // Translate.
            message = translator.getTranslation(message);
            
        }
        
        // Default white text.
        logRecord.setMessage(this.prefix + message);
        
        // Parent.
        super.log(logRecord);
    }
    
    @Override
    public void warning(String message) {
        
        // If message is a message key.
        if(translator.isKey(message)) {
            
            // Translate.
            message = translator.getTranslation(message);
            
        }
        
        // Add color to message.
        message = ConsoleLogger.YELLOW_COLOR + message +
                ConsoleLogger.WHITE_COLOR;
        
        // Log message.
        this.log(Level.WARNING, message);
    }
    
    @Override
    public void severe(String message) {
        
        // If message is a message key.
        if(translator.isKey(message)) {
            
            // Translate.
            message = translator.getTranslation(message);
            
        }
        
        // Add color to message.
        message = ConsoleLogger.RED_COLOR + message +
                ConsoleLogger.WHITE_COLOR;
        
        // Log message.
        this.log(Level.SEVERE, message);
    }
    
    /**
     * Severe message with throwable exception.
     * 
     * @param message The message to display.
     * @param throwable The exception to display.
     */
    public void severe(String message, Throwable throwable) {
        
        // If message is a message key.
        if(translator.isKey(message)) {
            
            // Translate.
            message = translator.getTranslation(message);
            
        }
        
        // Add color to message.
        message = ConsoleLogger.RED_COLOR + message +
                ConsoleLogger.WHITE_COLOR;
        
        // Log message.
        this.log(Level.SEVERE, message, throwable);
    }
}
