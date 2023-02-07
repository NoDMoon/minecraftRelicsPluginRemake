package com.idmoon.relics;

import com.idmoon.relics.relics.*;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

import org.bukkit.entity.Player;

public final class Relics extends JavaPlugin {

  public RelicsHandler handler = new RelicsHandler(this);

  HashMap<String, Relic> relicList = new HashMap<String, Relic>();
  public HashMap<String, Relic> getRelicList(){return relicList;}

  public boolean onCommand(CommandSender sender,
                           Command command,
                           String label,
                           String[] args){

    if (command.getName().equals("relic")) {		
      if (sender instanceof Player){

        Player player = (Player) sender;

        if ((args.length == 2) && args[0].equals("create")){
          if(relicList.keySet().contains(args[1])){
            if (handler.isRelicEnabled(args[1])){
              player.getInventory().setItemInMainHand(handler.createRelic(args[1]));
            } else {
              // TODO заменить на надпись в конфиге
              player.sendMessage("Эта реликвия не разрешена на сервере.");
            }
          } else {
            // TODO заменить на надпись в конфиге
            player.sendMessage("Неверный ID реликвии");
          }
        }else {
          // TODO заменить на надпись в конфиге
          player.sendMessage("Использование: /relic create [id]");
        }
      }
    }
    return false;
  }

  public void onEnable() {

    this.saveDefaultConfig();

    new Ticket(this, "ticket");
    new Conductor(this, "conductor");
    new Puppeteer(this, "puppeteer");

    // TODO заменить на надпись в конфиге
    getLogger().info("Relics have been planted");
   
  }

  public void onDisable() {
      // TODO заменить на надпись в конфиге
      getLogger().info("Relics have been defused");
  }
} 
