package com.idmoon.relics.relics;

import com.idmoon.relics.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

public final class Ticket extends Relic{

  int ticketCooldown;

  public Ticket(Relics plugin,String id){
    super(plugin, id);
    this.ticketCooldown = plugin.handler.getRelicConfig(this.id, "ticketCooldown");
  }

  @EventHandler
  public void act(PlayerInteractEntityEvent event) {
    Player player = event.getPlayer();
    if (plugin.handler.canRelicBeUsedBy(player.getInventory().getItemInMainHand(),
                                        this.id,
                                        player)) {
      event.getRightClicked().addPassenger(player);
      player.setCooldown(plugin.handler.getRelicMaterial(),
                         ticketCooldown);
    }
  }

}


