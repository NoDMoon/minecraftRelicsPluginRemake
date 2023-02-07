package com.idmoon.relics.relics;

import com.idmoon.relics.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.HumanEntity;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.metadata.Metadatable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.AttributeInstance;

import java.lang.Math;

public final class Conductor extends Relic{

  int conductorCooldown;
  int conductorDamage;

  public Conductor(Relics plugin,String id){
    super(plugin, id);
    conductorCooldown = plugin.handler.getRelicConfig(this.id, "conductorCooldown");
    conductorDamage = plugin.handler.getRelicConfig(this.id,   "conductorDamage");
  }

  @EventHandler
  public void act(EntityDamageByEntityEvent event) {
    if ((event.getDamager() instanceof Player)
    && (event.getEntity() instanceof LivingEntity)){
      Player damager = (Player) event.getDamager();
      if (plugin.handler.canRelicBeUsedBy(damager.getInventory().getItemInMainHand(), this.id, damager)){
        LivingEntity victim  = (LivingEntity) event.getEntity();
        if (plugin.handler.canBeDamagedByRelic(victim)){
          event.setDamage(conductorDamage);
          damager.setHealth(Math.min(damager.getHealth() + event.getFinalDamage(),
                                     damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()));
          damager.setCooldown(plugin.handler.getRelicMaterial(), conductorCooldown);
        }
      }
    }
  }
}


