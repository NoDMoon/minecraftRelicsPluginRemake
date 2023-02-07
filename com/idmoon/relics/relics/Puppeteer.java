package com.idmoon.relics.relics;

import com.idmoon.relics.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Egg;
import org.bukkit.entity.AreaEffectCloud;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.event.block.Action;

import org.bukkit.metadata.Metadatable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Puppeteer extends Relic{

  int puppeteerMeleeCooldown;
  int puppeteerMeleeDamage;

  int puppeteerRangeCooldown;
  int puppeteerRangeDamage;

  int puppeteerSpecialCooldown;
  int puppeteerSpecialRadius;
  int puppeteerSpecialDamage;

  int puppeteerCloudDuration;
  int puppeteerEffectDuration;
  int puppeteerEffectAmplifier;

  int puppeteerPuppetHealth;
  int puppeteerPuppetSpeed;
  int puppeteerPuppetDamage;

  

  public Puppeteer(Relics plugin,String id){
    super(plugin, id);

    puppeteerMeleeCooldown = plugin.handler.getRelicConfig(this.id,   "puppeteerMeleeCooldown");
    puppeteerMeleeDamage = plugin.handler.getRelicConfig(this.id,     "puppeteerMeleeDamage");

    puppeteerRangeCooldown = plugin.handler.getRelicConfig(this.id,   "puppeteerRangeCooldown");
    puppeteerRangeDamage = plugin.handler.getRelicConfig(this.id,     "puppeteerRangeDamage");

    puppeteerSpecialCooldown = plugin.handler.getRelicConfig(this.id, "puppeteerSpecialCooldown");
    puppeteerSpecialRadius = plugin.handler.getRelicConfig(this.id,   "puppeteerSpecialRadius");
    puppeteerSpecialDamage = plugin.handler.getRelicConfig(this.id,   "puppeteerSpecialDamage");
   
    puppeteerCloudDuration = plugin.handler.getRelicConfig(this.id,   "puppeteerCloudDuration");
    puppeteerEffectDuration = plugin.handler.getRelicConfig(this.id,  "puppeteerEffectDuration");
    puppeteerEffectAmplifier = plugin.handler.getRelicConfig(this.id, "puppeteerEffectAmplifier");

    puppeteerPuppetHealth = plugin.handler.getRelicConfig(this.id,    "puppeteerPuppetHealth");
    puppeteerPuppetDamage = plugin.handler.getRelicConfig(this.id,    "puppeteerPuppetSpeed");
    puppeteerPuppetDamage = plugin.handler.getRelicConfig(this.id,    "puppeteerPuppetDamage");

  }

  @EventHandler
  public void act(EntityDamageByEntityEvent event) {
    if ((event.getDamager() instanceof Player)
    && (event.getEntity() instanceof LivingEntity)){
      Player damager = (Player) event.getDamager();
      if (plugin.handler.canRelicBeUsedBy(damager.getInventory().getItemInMainHand(),
                                          this.id,
                                          damager)){
        LivingEntity victim  = (LivingEntity) event.getEntity();
        if (plugin.handler.canBeDamagedByRelic(victim)){
          event.setDamage(puppeteerMeleeDamage);

          victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                                                  puppeteerEffectDuration,
                                                  puppeteerEffectAmplifier));

          damager.setCooldown(plugin.handler.getRelicMaterial(), puppeteerMeleeCooldown);
        }
      }
    }
  }

  @EventHandler
  public void act(PlayerInteractEvent event) {
  if ((event.getAction() == Action.RIGHT_CLICK_AIR)
     && (plugin.handler.canRelicBeUsedBy(event.getPlayer().getInventory().getItemInMainHand(),
                                         this.id,
                                         event.getPlayer()))){

      if (event.getPlayer().isSneaking()){
        plugin.handler.launchRelicAmmo(event.getPlayer(),
                                       Egg.class,
                                       this.id,
                                       "ammo");
        event.getPlayer().setCooldown(plugin.handler.getRelicMaterial(), puppeteerRangeCooldown);
      }

      else {
          for (Entity e: event.getPlayer().getNearbyEntities(puppeteerSpecialRadius,
                                                             puppeteerSpecialRadius,
                                                             puppeteerSpecialRadius)){ 
            if(e instanceof LivingEntity){
              LivingEntity fang = (LivingEntity) e.getWorld().spawnEntity(e.getLocation(), EntityType.EVOKER_FANGS);
              fang.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                  .addModifier(new AttributeModifier("GENERIC_ATTACK_DAMAGE",
                                                     puppeteerSpecialDamage,
                                                     AttributeModifier.Operation.ADD_NUMBER));
            }
          }
       event.getPlayer().setCooldown(plugin.handler.getRelicMaterial(), puppeteerSpecialCooldown);
      }
    }
  }

  @EventHandler
  public void act(ProjectileHitEvent event) {
    if ((event.getEntity().getShooter() instanceof Player)
    && (event.getEntity().hasMetadata(this.id))){
      if ((event.getHitEntity() != null)
      && (event.getHitEntity() instanceof LivingEntity)){
        LivingEntity victim = (LivingEntity) event.getHitEntity();
        victim.damage(puppeteerMeleeDamage);

        victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER,
                                                puppeteerEffectDuration,
                                                puppeteerEffectAmplifier));
      }
      else if (event.getHitBlock() != null){

        Location location = event.getHitBlock().getLocation();
        EntityType summonType = null;   
        
        ItemStack headgear = ((Player) event.getEntity().getShooter()).getInventory().getHelmet();
        if((headgear != null) && (headgear.getType() == Material.SKULL_ITEM))
        switch (SkullType.values()[headgear.getDurability()]){
          case SKELETON:
            summonType = EntityType.STRAY;
            break;
          case ZOMBIE:
            summonType = EntityType.HUSK;
            break;
          default:
            break;
        }

        if(summonType != null) {
          LivingEntity puppet = ((LivingEntity)event.getEntity().getWorld().spawnEntity(location, summonType));

          puppet.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .addModifier(new AttributeModifier("GENERIC_MAX_HEALTH",
                                                   puppeteerPuppetHealth,
                                                   AttributeModifier.Operation.ADD_NUMBER));

          puppet.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .addModifier(new AttributeModifier("GENERIC_MOVEMENT_SPEED",
                                                   puppeteerPuppetSpeed,
                                                   AttributeModifier.Operation.ADD_NUMBER));

          puppet.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .addModifier(new AttributeModifier("GENERIC_ATTACK_DAMAGE",
                                                   puppeteerPuppetDamage,
                                                   AttributeModifier.Operation.ADD_NUMBER));
        }
        else {
          ((AreaEffectCloud)event.getEntity().getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD))
          .addCustomEffect(new PotionEffect(PotionEffectType.POISON,
                                            puppeteerCloudDuration,
                                            puppeteerEffectAmplifier),true);
        }
      }
    }
  }
}

