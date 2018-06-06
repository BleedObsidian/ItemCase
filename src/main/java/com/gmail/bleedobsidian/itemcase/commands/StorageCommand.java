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
package com.gmail.bleedobsidian.itemcase.commands;

import com.gmail.bleedobsidian.itemcase.Command;
import com.gmail.bleedobsidian.itemcase.loggers.ChatLogger;
import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.Itemcase.StorageType;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import com.gmail.bleedobsidian.itemcase.LanguageTranslator;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command handler for the 'create' command.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class StorageCommand implements Command {

    @Override
    public void execute(CommandSender sender, String label,
            String[] args) {
        
        // If sender is not a player.
        if(!(sender instanceof Player)) {
            
            // Send message.
            ItemCaseCore.instance.getGenericLogger().message(
                    sender, "command.not-player");
            
            // Exit.
            return;
        }
        
        // Cast sender to player.
        Player player = (Player) sender;
        
        // Check if player is asking for help.
        if(this.isAskingForHelp(player, label, args)) {
            
            // Exit.
            return;
        }
        
         // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.create")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // The target location.
        Location target = null;

        // Get the players target block.
        target = player.getTargetBlock(null, 5).getLocation();
        
        // Check if itemcase exists here.
        if(!ItemCaseCore.instance.getItemcaseManager().isItemcase(target)) {
            
            // Show message.
            chatLogger.message(player, "command.invalid-location");
            
            // Exit.
            return;
        }
        
         // Get itemcase.
        Itemcase itemcase = 
                ItemCaseCore.instance.getItemcaseManager().getItemcase(target);
        
        // Get owner.
        OfflinePlayer owner = itemcase.getOwner();
        
        // Check if this player owns this itemcase.
        if(!owner.equals(player)) {
            
            // Check if player is allowed to modify other peoples itemcases.
            if(!player.hasPermission("itemcase.modify.other")) {
                
                // Show message.
                chatLogger.message(player, "command.not-owner");
                
                // Exit.
                return;
            }
        }
        
        // Check if itemcase is a shop.
        if(itemcase.getType() == Type.SHOWCASE) {
            
            // Show message.
            chatLogger.message(player, "command.not-shop");
            
            // Exit.
            return;
        }
        
        // If itemcase has infinite storage.
        if(itemcase.getStorageType() == StorageType.INFINITE) {
            
            // Show message.
            chatLogger.message(player, "command.storage.infinite");
            
            // Exit.
            return;
        }
        
        // Open itemcase storage to player.
        player.openInventory(itemcase.getStorage());
    }
    
    /**
     * @return If the command sender is asking for help about this command.
     */
    public boolean isAskingForHelp(Player player, String label, String[] args) {
        
        // If args length equals 2.
        if(args.length != 2) {
            
            // False.
            return false;
        }
        
        // Get argument.
        String argument = args[1];
        
        // If not equal to help.
        if(!argument.equalsIgnoreCase("help")) {
            
            return false;
        }
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Set placeholder.
        translator.setPlaceholder("%COMMAND%", "/" + label + " storage");
        
        // Show command help.
        chatLogger.message(player, "command.itemcase-help");
        
        // Show specific help.
        chatLogger.message(player, "command.storage.help");
        
        // Return.
        return true;
    }
}
