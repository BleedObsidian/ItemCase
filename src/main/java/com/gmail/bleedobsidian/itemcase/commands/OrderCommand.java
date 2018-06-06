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
import com.gmail.bleedobsidian.itemcase.managers.OrderManager.ItemcaseOrder;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A command handler for the 'order' command.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class OrderCommand implements Command {

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
        
        // If not enough arguments.
        if(args.length < 2) {
            
            // Show help.
            this.showHelp(player, label);
            
            // Exit.
            return;
        }
        
        // Check if player has active order.
        if(!ItemCaseCore.instance.getOrderManager().hasOrder(player)) {
            
            // Send message
            chatLogger.message(player, "command.order.no-order");
            
            // Exit.
            return;
        }
        
        // Get sub command.
        String subCommand = args[1];
        
        // Switch sub command.
        switch(subCommand) {
            case "buy":
                this.executeBuy(player);
                break;
            case "sell":
                this.executeSell(player);
                break;
            case "amount":
                this.executeAmount(player, label, args);
                break;
            default:
                this.showHelp(player, label);
                return;
        }
        
        // Exit.
        return;
    }
    
    /**
     * Show help to given player.
     * 
     * @param player Player.
     * @param label Label used.
     */
    public void showHelp(Player player, String label) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Set placeholder.
        translator.setPlaceholder("%COMMAND%", "");
        
        // Show command help.
        chatLogger.message(player, "command.itemcase-help");
        
        // Set placeholder.
        translator.setPlaceholder("%LABEL%", label);
        
        // Show specific help.
        chatLogger.message(player, "command.order.help-amount");
        
        // Show specific help.
        chatLogger.message(player, "command.order.help-buy");
        
        // Show specific help.
        chatLogger.message(player, "command.order.help-sell");
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
        
        // Show help.
        this.showHelp(player, label);
        
        // Return.
        return true;
    }
    
    /**
     * Execute buy command.
     * 
     * @param player Player.
     */
    private void executeBuy(Player player) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.buy")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // Get order.
        ItemcaseOrder order = 
                ItemCaseCore.instance.getOrderManager().getOrder(player);
        
        // Get itemcase.
        Itemcase itemcase = order.getItemcase();
        
        // If itemcase can not be bought from.
        if(itemcase.getType() == Type.SHOP_SELL) {
            
            // Send message.
            chatLogger.message(player, "command.order.no-buy");
            
            // Exit.
            return;
        }
        
        // Check if server has vault if items are not free.
        if(!ItemCaseCore.instance.hasVault() &&
                order.getItemcase().getBuyPrice() > 0) {
            
            // Show message.
            chatLogger.message(player, "command.order.no-vault");
            
            // Exit.
            return;
        }
        
        // Get itemstack being bought.
        ItemStack itemstack = itemcase.getItemStack().clone();
            
        // If itemcase does not have enough items.
        if(!itemcase.hasEnough(order.getAmount())) {

            // If stock is 0.
            if(itemcase.getStockLevel() == 0) {
                
                // Send message.
                chatLogger.message(player, "command.order.no-stock");
                
                // Exit.
                return;
            }
            
            // Set amount placeholder.
            translator.setPlaceholder("%AMOUNT%", 
                    "" + ChatColor.GOLD + itemcase.getStockLevel() +
                            ChatColor.RESET);

            // Send message.
            chatLogger.message(player, "command.order.not-enough-stock");
            
            // Exit.
            return;
        }
        
        // Calculate total price.
        double total = order.getItemcase().getBuyPrice() * order.getAmount();
        
        // Get economy provider.
        Economy economyProvider = ItemCaseCore.instance.getEconomyProvider();
        
        // If total is greator than 0, vault must be used.
        if(total == 0) {
            
            // Give player items.
            itemstack.setAmount(order.getAmount());
            player.getInventory().addItem(itemstack);

            // Remove items from storage.
            itemcase.takeStock(order.getAmount());
            
            // Exit.
            return;
        }
        
        // Format money.
        String money = ChatColor.GOLD + economyProvider.format(total);

        // If name exists.
        if(!economyProvider.currencyNameSingular().equals("")) {

            // If greator than 1.
            if(total > 1) {

                // Plural name.
                money += " " + economyProvider.currencyNamePlural();
            } else {

                // Singular name.
                money += " " + economyProvider.currencyNameSingular();
            }
        }

        // Reset color.
        money += ChatColor.RESET;
        
        // Set placeholder.
        translator.setPlaceholder("%MONEY%", money);

        // If player does not have enough money.
        if(economyProvider.getBalance(player,
                player.getWorld().getName()) < total) {

            // Send message.
            chatLogger.message(player, "command.order.not-enough-money");

            // Exit.
            return;
        }
        
        // Create blank response.
        EconomyResponse response1 = 
                new EconomyResponse(0, 0, ResponseType.SUCCESS, null);

        // If itemcase is not infinite.
        if(itemcase.getStorageType() == StorageType.FINITE) {
            
            // Deposit money to owner.
            response1 = economyProvider.depositPlayer(
                    order.getItemcase().getOwner(),
                    player.getWorld().getName(), total);
        }

        // Withdraw money from player.
        EconomyResponse response2 = economyProvider.withdrawPlayer(
                player, player.getWorld().getName(), total);

        // If successful.
        if(response1.transactionSuccess() && response2.transactionSuccess()) {

            // Give player items.
            itemstack.setAmount(order.getAmount());
            player.getInventory().addItem(itemstack);

            // Remove items from storage.
            itemcase.takeStock(order.getAmount());
            
            // Mark order as completed.
            ItemCaseCore.instance.getOrderManager().completeOrder(player);
            
            // Send message.
            chatLogger.message(player, "command.order.withdrew");
            
            // If owner is online and itemcase is not infinite.
            if(itemcase.getStorageType() == StorageType.FINITE && 
                    order.getItemcase().getOwner().isOnline()) {
                
                // Get owner.
                Player owner = order.getItemcase().getOwner().getPlayer();
                
                // Set placeholder.
                translator.setPlaceholder("%PLAYER%", player.getDisplayName());
                
                // If item has custom display name.
                if(itemcase.getItemStack().getItemMeta().hasDisplayName()) {

                    // Set placeholder.
                    translator.setPlaceholder("%ITEM_NAME%", 
                            ChatColor.GOLD + 
                                itemcase.getItemStack().getItemMeta()
                                .getDisplayName() + ChatColor.RESET);
                } else {

                    // Set placeholder.
                    translator.setPlaceholder("%ITEM_NAME%", 
                            ChatColor.GOLD + 
                                    itemcase.getItemStack().getType().name()  
                                    + ChatColor.RESET);
                }
                
                // Set placeholder.
                translator.setPlaceholder("%AMOUNT%", "" + ChatColor.GOLD + 
                        order.getAmount() + ChatColor.RESET);
                
                // Send message.
                chatLogger.message(owner, "command.order.bought-from");
            }
        } else {

            // Show message.
            chatLogger.message(player, "command.order.failed");

        }
    }
    
    /**
     * Execute sell command.
     * 
     * @param player Player.
     * @param label Label.
     * @param args Arguments.
     */
    private void executeSell(Player player) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Get translator.
        LanguageTranslator translator = ItemCaseCore.instance.getTranslator();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.sell")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // Get order.
        ItemcaseOrder order = 
                ItemCaseCore.instance.getOrderManager().getOrder(player);
        
        // Get itemcase.
        Itemcase itemcase = order.getItemcase();
        
        // If itemcase can not sell.
        if(itemcase.getType() == Type.SHOP_BUY) {
            
            // Send message.
            chatLogger.message(player, "command.order.no-sell");
            
            // Exit.
            return;
        }
        
        // Check if server has no vault and players get no money for items.
        if(!ItemCaseCore.instance.hasVault() &&
                order.getItemcase().getSellPrice() > 0) {
            
            // Show message.
            chatLogger.message(player, "command.order.no-vault");
            
            // Exit.
            return;
        }
        
        // Get itemstack being bought.
        ItemStack itemstack = itemcase.getItemStack().clone();
            
        // If player does not have enough items.
        if(!player.getInventory().containsAtLeast(itemstack,
                order.getAmount())) {

            // Send message.
            chatLogger.message(player, "command.order.not-enough-items");
            
            // Exit.
            return;
        }
        
        // Calculate total price.
        double total = order.getItemcase().getSellPrice() * order.getAmount();
        
        // Get economy provider.
        Economy economyProvider = ItemCaseCore.instance.getEconomyProvider();
        
        // If total is greator than 0, vault must be used.
        if(total == 0) {
            
            // Remove items from player.
            itemstack.setAmount(order.getAmount());
            player.getInventory().removeItem(itemstack);

            // Add stock to storage.
            itemcase.addStock(order.getAmount());
            
            // Exit.
            return;
        }
        
        // Format money.
        String money = ChatColor.GOLD + economyProvider.format(total);

        // If name exists.
        if(!economyProvider.currencyNameSingular().equals("")) {

            // If greator than 1.
            if(total > 1) {

                // Plural name.
                money += " " + economyProvider.currencyNamePlural();
            } else {

                // Singular name.
                money += " " + economyProvider.currencyNameSingular();
            }
        }

        // Reset color.
        money += ChatColor.RESET;
        
        // Set placeholder.
        translator.setPlaceholder("%MONEY%", money);

        // If owner does not have enough money and storage is finite.
        if(order.getItemcase().getStorageType() == StorageType.FINITE &&
                (economyProvider.getBalance(order.getItemcase().getOwner(),
                player.getWorld().getName()) < total)) {

            // Send message.
            chatLogger.message(player, "command.order.owner-not-enough-money");

            // Exit.
            return;
        }
        
        // Create blank response.
        EconomyResponse response1 = 
                new EconomyResponse(0, 0, ResponseType.SUCCESS, null);

        // If itemcase is not infinite.
        if(itemcase.getStorageType() == StorageType.FINITE) {
            
            // Withdraw money from owner.
            response1 = economyProvider.withdrawPlayer(
                    order.getItemcase().getOwner(),
                    player.getWorld().getName(), total);
        }
        
        // Deposit money to player.
        EconomyResponse response2 = economyProvider.depositPlayer(
                player,
                player.getWorld().getName(), total);

        // If successful.
        if(response1.transactionSuccess() && response2.transactionSuccess()) {

            // Remove items from player.
            itemstack.setAmount(order.getAmount());
            player.getInventory().removeItem(itemstack);

            // Add stock to storage.
            itemcase.addStock(order.getAmount());
            
            // Mark order as completed.
            ItemCaseCore.instance.getOrderManager().completeOrder(player);
            
            // Send message.
            chatLogger.message(player, "command.order.deposit");
            
            // If owner is online and itemcase is not infinite.
            if(itemcase.getStorageType() == StorageType.FINITE && 
                    order.getItemcase().getOwner().isOnline()) {
                
                // Get owner.
                Player owner = order.getItemcase().getOwner().getPlayer();
                
                // Set placeholder.
                translator.setPlaceholder("%PLAYER%", player.getDisplayName());
                
                // If item has custom display name.
                if(itemcase.getItemStack().getItemMeta().hasDisplayName()) {

                    // Set placeholder.
                    translator.setPlaceholder("%ITEM_NAME%", 
                            ChatColor.GOLD + 
                                itemcase.getItemStack().getItemMeta()
                                .getDisplayName() + ChatColor.RESET);
                } else {

                    // Set placeholder.
                    translator.setPlaceholder("%ITEM_NAME%", 
                            ChatColor.GOLD + 
                                    itemcase.getItemStack().getType().name()  
                                    + ChatColor.RESET);
                }
                
                // Set placeholder.
                translator.setPlaceholder("%AMOUNT%", "" + ChatColor.GOLD + 
                        order.getAmount() + ChatColor.RESET);
                
                // Send message.
                chatLogger.message(owner, "command.order.sold-to");
            }
            
        } else {

            // Show message.
            chatLogger.message(player, "command.order.failed");

        }
    }
    
    /**
     * Execute amount command.
     * 
     * @param player Player.
     * @param label Label.
     * @param args Arguments.
     */
    private void executeAmount(Player player, String label, String[] args) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();
        
        // Check if player has permission.
        if(!player.hasPermission("itemcase.buy") &&
                !player.hasPermission("itemcase.sell")) {
            
            // Send message.
            chatLogger.message(player, "command.permission");
            
            // Exit.
            return;
        }
        
        // Check argument length.
        if(args.length != 3) {
            
            // Show help.
            this.showHelp(player, label);
            
            // Exit.
            return;
        }
        
        // Define amount.
        int amount = 0;
        
        // Attempt to parse amount.
        try {
            
            // Parse amount.
            amount = Integer.parseInt(args[2]);
            
        } catch(NumberFormatException e) {
            
            // Show message.
            chatLogger.message(player, "command.order.invalid-amount");
            
            // Exit.
            return;
        }
        
        // Set amount.
        ItemCaseCore.instance.getOrderManager().getOrder(player)
                .setAmount(amount);

        // Get translator.
        LanguageTranslator translator = 
                ItemCaseCore.instance.getTranslator();
            
        // Set placeholder.
        translator.setPlaceholder("%AMOUNT%", "" + ChatColor.GOLD + amount +
                ChatColor.RESET);
        
        // Show message.
        chatLogger.message(player, "command.order.amount-update");
        
        // Exit.
        return;
    }
}
