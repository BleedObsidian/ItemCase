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
package com.gmail.bleedobsidian.itemcase.managers;

import com.gmail.bleedobsidian.itemcase.loggers.ChatLogger;
import com.gmail.bleedobsidian.itemcase.ItemCaseCore;
import com.gmail.bleedobsidian.itemcase.Itemcase;
import com.gmail.bleedobsidian.itemcase.Itemcase.Type;
import com.gmail.bleedobsidian.itemcase.LanguageTranslator;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Manages all active Itemcase orders.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public final class OrderManager {
    
    /**
     * HashMap of players and their active orders.
     */
    private final HashMap<Player, ItemcaseOrder> orders = new HashMap();
    
    /**
     * Create new order.
     * 
     * @param itemcase Itemcase.
     * @param player Player.
     */
    public void createOrder(Itemcase itemcase, Player player) {
        
        // Get chat logger.
        ChatLogger chatLogger = ItemCaseCore.instance.getChatLogger();

        // Get translator.
        LanguageTranslator translator = 
                ItemCaseCore.instance.getTranslator();
        
        // Create new order for amount 1.
        ItemcaseOrder order = new ItemcaseOrder(itemcase, 1);
        
        // Add to list.
        this.orders.put(player, order);

        // Send message.
        chatLogger.message(player, "---------- Order ----------");

        // If item has custom display name.
        if(itemcase.getItemStack().getItemMeta().hasDisplayName()) {

            // Set placeholder.
            translator.setPlaceholder("%ITEM_NAME%", 
                    ChatColor.GOLD + itemcase.getItemStack().getItemMeta()
                            .getDisplayName());
        } else {

            // Set placeholder.
            translator.setPlaceholder("%ITEM_NAME%", 
                    ChatColor.GOLD + itemcase.getItemStack().getType().name());
        }
        
        // Send message.
        chatLogger.message(player, "order.item");
        
        // Set amount placeholder.
        translator.setPlaceholder("%AMOUNT%", ChatColor.GOLD + 
                String.valueOf(1));
        
        // Send message.
        chatLogger.message(player, "order.amount");
        
        // If shop buys products.
        if(itemcase.getType() == Type.SHOP_BUY ||
                itemcase.getType() == Type.SHOP_MULTI) {
            
            // Set placeholder.
            translator.setPlaceholder("%BUY_PRICE%", "" + ChatColor.GOLD +
                    itemcase.getBuyPrice());
            
            // Send message.
            chatLogger.message(player, "order.buy-price");
        }
        
        // If shop sells products.
        if(itemcase.getType() == Type.SHOP_SELL ||
                itemcase.getType() == Type.SHOP_MULTI) {
            
            // Set placeholder.
            translator.setPlaceholder("%SELL_PRICE%", "" + ChatColor.GOLD +
                    itemcase.getSellPrice());
            
            // Send message.
            chatLogger.message(player, "order.sell-price");
        }
        
        // Send message.
        chatLogger.message(player, "---------------------------");
        
        // Send message.
        chatLogger.message(player, "order.help");
        
        
        // Send message.
        chatLogger.message(player, "---------------------------");
    }
    
    /**
     * Mark order as completed.
     * 
     * @param player Player.
     */
    public void completeOrder(Player player) {
        
        // Remove oder.
        this.orders.remove(player);
    }
    
    /**
     * @param player Player.
     * @return If order exists for given player.
     */
    public boolean hasOrder(Player player) {
        
        // Return if order exists for given player.
        return this.orders.containsKey(player);
    }
    
    /**
     * @param player Player.
     * @return The order linked with this player.
     */
    public ItemcaseOrder getOrder(Player player) {
        
        // Return order linked to player.
        return this.orders.get(player);
    }
    
    /**
     * An order for a specific itemcase of some amount.
     */
    public final class ItemcaseOrder {
        
        /**
         * The itemcase this order is for.
         */
        private final Itemcase itemcase;
        
        /**
         * The amount.
         */
        private int amount;
        
        /**
         * Constructor.
         * 
         * @param itemcase Itemcase.
         * @param amount Amount.
         */
        public ItemcaseOrder(Itemcase itemcase, int amount) {
            
            // Set attributes.
            this.itemcase = itemcase;
            this.amount = amount;
        }
        
        /**
         * @return Itemcase.
         */
        public Itemcase getItemcase() {
            
            // Return itemcase.
            return itemcase;
        }

        /**
         * @return Amount.
         */
        public int getAmount() {
            
            // Amount.
            return amount;
        }
        
        /**
         * @param amount Amount.
         */
        public void setAmount(int amount) {
            
            // Set amount.
            this.amount = amount;
        }
    }
}
