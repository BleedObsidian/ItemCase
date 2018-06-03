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
import com.gmail.bleedobsidian.itemcase.managers.ItemcaseManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command handler for the 'create' command.
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
        
         // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Cast sender to player.
        Player player = (Player) sender;
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.destroy")) {
            
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
        target = player.getTargetBlock(null, 3).getLocation();
        
        // Check this target is an itemcase.
        if(!manager.isItemcase(target)) {
            
            // Show message.
            chatLogger.message(player, "command.destroy.invalid-location");
            
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
                chatLogger.message(player, "command.destroy.not-owner");
                
                // Exit.
                return;
            }
        }
        
        // Destroy itemcase.
        manager.destroyItemcase(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.destroy.success");
    }
}
