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
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command handler for the 'create' command.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class CreateCommand implements Command {

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
        if(!player.hasPermission("itemcase.create")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // List of materials that can be used as itemcases.
        ArrayList<Material> materials =
                ItemCaseCore.instance.getConfigFile().getMaterials();
        
        // The target location.
        Location target = null;
        
        // ItemStack to use.
        ItemStack itemStack = null;

        // Get the players target block.
        target = player.getTargetBlock(null, 3).getLocation();

        // If target block is not on the list of accepted materials.
        if(!materials.contains(target.getBlock().getType())) {

            // Show message.
            chatLogger.message(player, "command.create.invalid-type");

            // Exit.
            return;
        }

        // Get item in players main hand to use as the Itemcase item.
        itemStack = player.getInventory().getItemInMainHand();

        // If the player is not holding anything...
        if(itemStack == null || itemStack.getType() == Material.AIR) {

            // Show message.
            chatLogger.message(player, "command.create.main-hand");

            // Exit.
            return;
        }
        
        // Create itemcase.
        ItemCaseCore.instance.getItemcaseManager().createItemcase(
                itemStack, target, player);
        
        // Show message.
        chatLogger.message(player, "command.create.success");
    }
}
