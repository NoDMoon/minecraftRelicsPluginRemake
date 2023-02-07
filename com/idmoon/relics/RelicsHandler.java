package com.idmoon.relics;

import com.idmoon.relics.relics.*;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;
import org.bukkit.metadata.FixedMetadataValue;

import org.bukkit.Material;
import org.bukkit.ChatColor;

import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.EntityType;

public final class RelicsHandler {

  Relics plugin;

  public RelicsHandler(Relics plugin){
    this.plugin = plugin;
   }

  Material relicMaterial = Material.CARROT_STICK;
  public Material getRelicMaterial(){return relicMaterial;}

  public String toMCString(String s){
    return ChatColor.translateAlternateColorCodes​('&', s);
  }

  public ItemStack createRelic(String id){

    Relic relic = plugin.getRelicList().get(id);

    ItemStack relicItem = new ItemStack(relicMaterial);
    
    // TODO в новых версиях переделать под не NMS
    // добавление тега реликвий к айтемстаку
    // тег: RelicID:id
    net.minecraft.server.v1_12_R1.ItemStack nmsRelicItem = CraftItemStack.asNMSCopy(relicItem);
    NBTTagCompound relicCompound = new NBTTagCompound();
    relicCompound.set("relicID", new NBTTagString(id));
    nmsRelicItem.setTag(relicCompound);

    relicItem = CraftItemStack.asBukkitCopy(nmsRelicItem);

    ItemMeta relicMeta = relicItem.getItemMeta();

    relicMeta.setDisplayName(toMCString(relic.getName()));

    List<String> colorLore = new ArrayList<String>();
    for(String loreLine: relic.getDescription())
      colorLore.add(toMCString​(loreLine));
    relicMeta.setLore(colorLore);

    relicItem.setDurability(relic.getModel());

    relicMeta.setUnbreakable(true);
    relicMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

    relicItem.setItemMeta(relicMeta);

    return relicItem;
  }

  public boolean isRelic(ItemStack item, String id){
    // TODO в новых версиях переделать под не NMS
    // проверка: есть ли у данного айтемстака тег реликвий и отсутсвует ли тег gm (поддержка CControl)
    net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
    NBTTagCompound itemCompound = nmsItem.getTag();
    return (itemCompound != null)
        && itemCompound.hasKey("relicID")
        && itemCompound.getString("relicID").equals(id)
        && !(itemCompound.hasKey("gm"));
  }
  
  public boolean isRelicEnabled(String id){
    return ((plugin.getRelicList().get(id) != null)
        &&   plugin.getConfig().getBoolean("enabledRelics." + id));
  }

  public boolean canRelicBeUsedBy(ItemStack item, String id, Player player){
    boolean result = false;
    if (isRelic(item, id)) {
      if (isRelicEnabled(id)) {
        if (!player.hasCooldown(relicMaterial)) {
          result = true;
        }
      } else {
        // TODO заменить на надпись в конфиге с поддержкой цветов
        player.sendMessage("Данная реликвия не разрешена на сервере.");
      }
    }
    return result;
  }

  public boolean canBeDamagedByRelic(LivingEntity entity){
    boolean result = false;
    if (!entity.hasMetadata("NPC") // ignore Citizens NPCs
    || ((entity instanceof HumanEntity)
    && !((HumanEntity) entity).isBlocking()) ) result = true;
    return result;
  }

  public void launchRelicAmmo(ProjectileSource launcher, Class<? extends Projectile> ammo, String id, String value){
    launcher.launchProjectile(ammo).setMetadata(id, new FixedMetadataValue(plugin, value));
  }

  public int getRelicConfig(String id, String value) {
    return plugin.getConfig().getInt("general." + id + "." + value);
  }
  
} 
