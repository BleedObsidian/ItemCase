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

import com.gmail.bleedobsidian.itemcase.ChatLogger;
import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.LanguageTranslator;
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command handler for the 'destroy' command.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class DestroyCommand implements Command {

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
        
        // Check if player has permission (Uses create permission).
        if(!player.hasPermission("itemcase.create")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // The target location.
        Location target = null;
        
        // Get ItemcaseManager.
        ItemcaseManager manager = ItemCaseCore.instance.getItemcaseManager();

        // Get the players target block.
        target = player.getTargetBlock(null, 5).getLocation();
        
        // Check this target is an itemcase.
        if(!manager.isItemcase(target)) {
            
            // Show message.
            chatLogger.message(player, "command.invalid-location");
            
            // Exit.
            return;
        }
        
        // Get itemcase.
        Itemcase itemcase = manager.getItemcase(target);
        
        // Get owner.
        OfflinePlayer owner = itemcase.getOwner();
        
        // Check if this player owns this itemcase.
        if(!owner.equals(player)) {
            
            // Check if player is allowed to destroy other peoples itemcases.
            if(!player.hasPermission("itemcase.destroy.other")) {
                
                // Show message.
                chatLogger.message(player, "command.not-owner");
                
                // Exit.
                return;
            }
        }
        
        // Destroy itemcase.
        manager.destroyItemcase(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.destroy.success");
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
        translator.setPlaceholder("%COMMAND%", "/" + label + " destroy");
        
        // Show command help.
        chatLogger.message(player, "command.itemcase-help");
        
        // Show specific help.
        chatLogger.message(player, "command.destroy.help");
        
        return true;
    }
}
