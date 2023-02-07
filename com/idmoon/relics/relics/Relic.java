package com.idmoon.relics.relics;

import com.idmoon.relics.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.List;

public abstract class Relic implements Listener{

  final Relics plugin;

  String id ;

  short model;
  String name ;
  List<String> description;

  public String   getID()              {return id;}

  public short getModel()              {return model;}
  public String   getName()            {return name;}
  public List<String> getDescription() {return description;}

  public Relic(Relics plugin, String id){
    
    this.plugin = plugin;
    this.id = id;

    this.model = (short) this.plugin.getConfig().getInt("general." + this.id + ".model");
    this.name = this.plugin.getConfig().getString("general." + this.id + ".name");
    this.description = this.plugin.getConfig().getStringList("general." + this.id + ".description");

    if (plugin.getRelicList().get(this.id) == null) plugin.getRelicList().put(this.id, this);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void act(PlayerInteractEvent event){};

  @EventHandler
  public void act(PlayerInteractEntityEvent event){};

  @EventHandler
  public void act(EntityDamageByEntityEvent event){};

  @EventHandler
  public void act(ProjectileHitEvent event){};
}

