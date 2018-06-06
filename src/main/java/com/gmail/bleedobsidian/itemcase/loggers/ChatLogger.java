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
package com.gmail.bleedobsidian.itemcase.loggers;

import com.gmail.bleedobsidian.itemcase.LanguageTranslator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * Utility class used for communicating to players in game with a translator.
 * 
 * @author Jesse Prescott (BleedObsidian).
 */
public final class ChatLogger {

    /**
     * The prefix placed before all messages.
     */
    private final static String PREFIX = 
            ChatColor.BLUE + "[ItemCase]: " + ChatColor.RESET;
    
    /**
     * Language translator.
     */
    private final LanguageTranslator translator;
    
    /**
     * Constructor.
     * 
     * @param translator language translator.
     */
    public ChatLogger(LanguageTranslator translator) {
        
        // Set translator.
        this.translator = translator;
    }
    
    /**
     * Send given Player a message.
     * 
     * @param player Player to send message.
     * @param string Message key or message.
     */
    public void message(Player player, String string) {
        
        // If string is a message key.
        if(this.translator.isKey(string)) {
            
            // Translate.
            string = this.translator.getTranslation(string);
            
        }
        
        // Send message.
        player.sendMessage(ChatLogger.PREFIX + string);
    }
}
