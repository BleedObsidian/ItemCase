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
 * A custom logger used to add color to the console when using warnings and errors.
 *
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ConsoleLogger extends Logger {

    /**
     * The Bukkit JavaPlugin.
     */
    private JavaPlugin plugin;
    
    /**
     * The plugin prefix.
     */
    private final String prefix;
    
    /**
     * White color string for console.
     */
    private final String whiteColor;
    
    /**
     * Yellow color string for console.
     */
    private final String yellowColor;
    
    /**
     * Red color string for console.
     */
    private final String redColor;
    
    /**
     * Constructor.
     * 
     * @param plugin The B
     */
    public ConsoleLogger(JavaPlugin plugin) {
        
        // Call parent constructor.
        super(plugin.getName(), null);
        
        // Set logger settings.
        super.setParent(plugin.getServer().getLogger());
        super.setLevel(Level.ALL);
        
        // Set console color strings.
        this.whiteColor = Ansi.ansi().fg(Ansi.Color.WHITE).boldOff().toString();
        this.yellowColor = Ansi.ansi().fg(Ansi.Color.YELLOW).bold().toString();
        this.redColor = Ansi.ansi().fg(Ansi.Color.RED).bold().toString();
        
        // Set plugin prefix.
        this.prefix = "[" + plugin.getName() + "] ";
    }
    
    @Override
    public void log(LogRecord logRecord) {
        
        // Default white text.
        logRecord.setMessage(this.prefix + logRecord.getMessage());
        
        // Parent.
        super.log(logRecord);
    }
    
    @Override
    public void warning(String message) {
        
        // Add color to message.
        message = this.yellowColor + message + this.whiteColor;
        
        // Log message.
        this.log(Level.WARNING, message);
    }
    
    @Override
    public void severe(String message) {
        
        // Add color to message.
        message = this.redColor + message + this.whiteColor;
        
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
        
        // Add color to message.
        message = this.redColor + message + this.whiteColor;
        
        // Log message.
        this.log(Level.SEVERE, message, throwable);
    }
}
