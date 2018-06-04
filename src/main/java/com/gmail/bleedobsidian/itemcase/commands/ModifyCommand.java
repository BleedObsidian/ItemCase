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
import com.gmail.bleedobsidian.itemcase.GenericLogger;
import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.Itemcase.StorageType;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import com.gmail.bleedobsidian.itemcase.LanguageTranslator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * A command handler for the 'modify' command.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class ModifyCommand implements Command {

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
        
        // Check command params have been given.
        if(args.length != 3) {
            
            // Shop help message.
            this.showHelp(player, label);
            
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

        // Get the players target block.
        target = player.getTargetBlock(null, 3).getLocation();
        
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
        
        // Get flag.
        String flag = args[1];
        
        // Get value.
        String value = args[2];
        
        // Switch flag.
        switch(flag.toLowerCase()) {
            case "type":
                this.typeFlag(label, player, itemcase, value);
                break;
            case "storage_type":
                this.storageTypeFlag(label, player, itemcase, value);
                break;
            case "buy_price":
                this.buyPriceFlag(label, player, itemcase, value);
                break;
            case "sell_price":
                this.sellPriceFlag(label, player, itemcase, value);
                break;
            default:
                this.showHelp(player, label);
                break;
        }
        
        // Exit.
        return;
    }
    
    /**
     * Type flag.
     * 
     * @param label label.
     * @param player Player.
     * @param itemcase Itemcase.
     * @param value Flag value.
     */
    private void typeFlag(String label, Player player, Itemcase itemcase,
            String value) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // If the itemcase was a shop.
        boolean wasShop = false;
        
        // If the itemcase was a shop.
        if(itemcase.getType() != Type.SHOWCASE) {
            
            // Set boolean.
            wasShop = true;
        }
        
        // If the itemcase is now a shop.
        boolean isNowShop = false;
        
        // Switch value.
        switch(value.toLowerCase()) {
            
            // Showcase.
            case "showcase":
                
                // Set type.
                itemcase.setType(Type.SHOWCASE);
                
                // Break.
                break;
                
            // Shop buy.
            case "shop_buy":
                
                // Check if player has permission.
                if(!player.hasPermission("itemcase.create.shop.buy")) {

                    // Send message.
                    chatLogger.message(player, "command.permission");

                    // Exit.
                    return;
                }
                
                // Set boolean
                isNowShop = true;
                
                // Set type.
                itemcase.setType(Type.SHOP_BUY);
                
                // Break.
                break;
                
            // Shop sell.
            case "shop_sell":
                
                // Check if player has permission.
                if(!player.hasPermission("itemcase.create.shop.sell")) {

                    // Send message.
                    chatLogger.message(player, "command.permission");

                    // Exit.
                    return;
                }
                
                // Set boolean
                isNowShop = true;
                
                // Set type.
                itemcase.setType(Type.SHOP_SELL);
                
                // Break.
                break;
                
            // Shop multi.
            case "shop_multi":
                
                // Check if player has permission.
                if(!player.hasPermission("itemcase.create.shop.buy") ||
                        !player.hasPermission("itemcase.create.shop.sell")) {

                    // Send message.
                    chatLogger.message(player, "command.permission");

                    // Exit.
                    return;
                }
                
                // Set boolean
                isNowShop = true;
                
                // Set type.
                itemcase.setType(Type.SHOP_MULTI);
                
                // Break.
                break;
                
            // Default.
            default:
                
                // Show help.
                this.showHelp(player, label);
                
                // Exit.
                return;
        }
        
        // If itemcase is now a shop but wasn't before.
        if(isNowShop && !wasShop) {
            
            // Set storage.
            itemcase.setStorage(Bukkit.createInventory(
                    null, 54, Itemcase.INVENTORY_NAME));
        }
        
        // Save itemcase.
        ItemCaseCore.instance.getItemcaseManager().saveItemcases(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.modify.success");
    }
    
    /**
     * Storage Type flag.
     * 
     * @param label label.
     * @param player Player.
     * @param itemcase Itemcase.
     * @param value Flag value.
     */
    private void storageTypeFlag(String label, Player player, Itemcase itemcase,
            String value) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // If itemcase is not a shop.
        if(itemcase.getType() == Type.SHOWCASE) {
            
            // Show message.
            chatLogger.message(player, "command.not-shop");
            
            // Exit.
            return;
        }
        
        // Switch value.
        switch(value.toLowerCase()) {
            
            // Finite.
            case "finite":
                
                // Set storage type.
                itemcase.setStorageType(StorageType.FINITE);
                
                // Break.
                break;
                
            // Infinite.
            case "infinite":
                
                // Check if player has permission.
                if(!player.hasPermission("itemcase.create.shop.infinite")) {

                    // Send message.
                    chatLogger.message(player, "command.permission");

                    // Exit.
                    return;
                }
                
                // Set storage type.
                itemcase.setStorageType(StorageType.INFINITE);
                
                // Break.
                break;
                
            // Default.
            default:
                
                // Show help.
                this.showHelp(player, label);
                
                // Exit.
                return;
        }
        
        // Save itemcase.
        ItemCaseCore.instance.getItemcaseManager().saveItemcases(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.modify.success");
    }
    
    /**
     * Buy Price flag.
     * 
     * @param label label.
     * @param player Player.
     * @param itemcase Itemcase.
     * @param value Flag value.
     */
    private void buyPriceFlag(String label, Player player, Itemcase itemcase,
            String value) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.create.shop.buy")) {

            // Send message.
            chatLogger.message(player, "command.permission");

            // Exit.
            return;
        }
        
        // If itemcase is not a shop.
        if(itemcase.getType() == Type.SHOWCASE) {
            
            // Show message.
            chatLogger.message(player, "command.not-shop");
            
            // Exit.
            return;
        }
        
        // If itemcase only sells items.
        if(itemcase.getType() == Type.SHOP_SELL) {
            
            // Show message.
            chatLogger.message(player, "command.modify.sell-only");
            
            // Exit.
            return;
        }
        
        // Attempt to cast value to double.
        try {
            
            // Cast to double.
            double buyPrice = Double.parseDouble(value);
            
            // Set price.
            itemcase.setBuyPrice(buyPrice);
            
        // Failed to parse value.
        } catch (NumberFormatException e) {
            
            // Show message.
            chatLogger.message(player, "command.modify.invalid-price");
            
            // Exit.
            return;
        }
        
        // Save itemcase.
        ItemCaseCore.instance.getItemcaseManager().saveItemcases(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.modify.success");
    }
    
    /**
     * Sell Price flag.
     * 
     * @param label label.
     * @param player Player.
     * @param itemcase Itemcase.
     * @param value Flag value.
     */
    private void sellPriceFlag(String label, Player player, Itemcase itemcase,
            String value) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.create.shop.sell")) {

            // Send message.
            chatLogger.message(player, "command.permission");

            // Exit.
            return;
        }
        
        // If itemcase is not a shop.
        if(itemcase.getType() == Type.SHOWCASE) {
            
            // Show message.
            chatLogger.message(player, "command.not-shop");
            
            // Exit.
            return;
        }
        
        // If itemcase only buys items.
        if(itemcase.getType() == Type.SHOP_BUY) {
            
            // Show message.
            chatLogger.message(player, "command.modify.buy-only");
            
            // Exit.
            return;
        }
        
        // Attempt to cast value to double.
        try {
            
            // Cast to double.
            double sellPrice = Double.parseDouble(value);
            
            // Set price.
            itemcase.setSellPrice(sellPrice);
            
        // Failed to parse value.
        } catch (NumberFormatException e) {
            
            // Show message.
            chatLogger.message(player, "command.modify.invalid-price");
            
            // Exit.
            return;
        }
        
        // Save itemcase.
        ItemCaseCore.instance.getItemcaseManager().saveItemcases(itemcase);
        
        // Show message.
        chatLogger.message(player, "command.modify.success");
    }
    
    /**
     * Show help for main command to given sender.
     * 
     * @param sender CommandSender.
     */
    private void showHelp(Player player, String label) {
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Set placeholder.
        translator.setPlaceholder("%COMMAND%", 
                "/" + label + " modify [flag] [value]");
            
        // Get chat logger.
        ChatLogger logger = ItemCaseCore.instance.getChatLogger();

        // Send message to player.
        logger.message(player, "command.itemcase-help");

        // Send message to player.
        logger.message(player, "command.modify.help");

        // Send flag help.
        logger.message(player, "[flag] [value]:");

        // Send flag help.
        logger.message(player, 
                "   type [showcase/shop_buy/shop_sell/shop_multi]");

        // Send flag help.
        logger.message(player, 
                "   storage_type [finite/infinite]");

        // Send flag help.
        logger.message(player, 
                "   buy_price [price]");

        // Send flag help.
        logger.message(player, 
                "   sell_price [price]");
    }
}
