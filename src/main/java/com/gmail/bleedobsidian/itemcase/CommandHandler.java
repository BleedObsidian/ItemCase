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

import com.gmail.bleedobsidian.itemcase.commands.CreateCommand;
import com.gmail.bleedobsidian.itemcase.commands.ModifyCommand;
import com.gmail.bleedobsidian.itemcase.commands.DestroyCommand;
import com.gmail.bleedobsidian.itemcase.commands.OrderCommand;
import com.gmail.bleedobsidian.itemcase.commands.StorageCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The central command handler for all ItemCase commands.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        
        // Check we have atleast 1 argument (sub-command label).
        if(args.length <= 0) {
            
            // Show help message.
            this.showHelp(sender, label);
            
            // We have handled this error.
            return true;
        }
        
        // Direct sub-command to corresponding command handler.
        switch(args[0]) {
            
            case "create":
                new CreateCommand().execute(sender, label, args);
                break;
            case "order":
                new OrderCommand().execute(sender, label, args);
                break;
            case "destroy":
                new DestroyCommand().execute(sender, label, args);
                break;
            case "modify":
                new ModifyCommand().execute(sender, label, args);
                break;
            case "storage":
                new StorageCommand().execute(sender, label, args);
                break;
            default:
                this.showHelp(sender, label);
                break;
        }
        
        // We have handled this command.
        return true;
    }
    
    /**
     * Show help for main command to given sender.
     * 
     * @param sender CommandSender.
     */
    private void showHelp(CommandSender sender, String label) {
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Set placeholder to blank.
        translator.setPlaceholder("%COMMAND%", "");
        
        // Set label placeholder.
        translator.setPlaceholder("%LABEL%", label);
        
        // Set placeholder.
        String command = "/" + label + " [create/order/destroy/modify/storage]";
        
        // Check if sender is a player or console.
        if(sender instanceof Player) {
            
            // Cast sender to player.
            Player player = (Player) sender;
            
            // Get chat logger.
            ChatLogger logger = ItemCaseCore.instance.getChatLogger();
            
            // Send message to player.
            logger.message(player, "command.itemcase-help");
            
            // Send message to player.
            logger.message(player, command);
            
            // Send message to player.
            logger.message(player, "command.command-help");
            
        } else {
            
            // Get console logger.
            GenericLogger logger = ItemCaseCore.instance.getGenericLogger();
            
            // Send console message.
            logger.message(sender, "command.itemcase-help");
            
            // Send console message.
            logger.message(sender, command);
            
            // Send console message.
            logger.message(sender, "command.command-help");
        }
    }
}
