package io.github.bedwarsrel.BedwarsRel.Shop.Specials;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import io.github.bedwarsrel.BedwarsRel.Main;
import io.github.bedwarsrel.BedwarsRel.Utils;
import io.github.bedwarsrel.BedwarsRel.Game.Game;
import io.github.bedwarsrel.BedwarsRel.Game.GameState;
import io.github.bedwarsrel.BedwarsRel.Game.Team;

public class TNTSheepListener implements Listener {

  public TNTSheepListener() {
    try {
      // register entities
      Class<?> tntRegisterClass = Main.getInstance().getVersionRelatedClass("TNTSheepRegister");
      ITNTSheepRegister register = (ITNTSheepRegister) tntRegisterClass.newInstance();
      register.registerEntities(Main.getInstance().getIntConfig("specials.tntsheep.entity-id", 91));
    } catch (Exception e) {
      Main.getInstance().getBugsnag().notify(e);
      e.printStackTrace();
    }
  }

  @SuppressWarnings("deprecation")
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent event) {
    if (event.getAction().equals(Action.LEFT_CLICK_AIR)
        || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    
    if (event.getPlayer() == null) {
      return;
    }

    Player player = event.getPlayer();

    Game game = Main.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING && !game.isStopping()) {
      return;
    }

    ItemStack inHand = null;
    TNTSheep creature = new TNTSheep();

    if (Main.getInstance().getCurrentVersion().startsWith("v1_8")) {
      if (event.getPlayer().getInventory().getItemInHand() == null && event.getPlayer()
          .getInventory().getItemInHand().getType() != creature.getItemMaterial()) {
        return;
      }
      inHand = player.getItemInHand();
    } else {
      if (event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND) {
        if (event.getPlayer().getInventory().getItemInOffHand() == null || event.getPlayer()
            .getInventory().getItemInOffHand().getType() != creature.getItemMaterial()) {
          if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer()
              .getInventory().getItemInMainHand().getType() == creature.getItemMaterial()) {
            event.setCancelled(true);
          }
          return;
        } else if (event.getPlayer().getInventory().getItemInOffHand() != null && event.getPlayer()
            .getInventory().getItemInOffHand().getType() == creature.getItemMaterial()) {
          inHand = player.getInventory().getItemInOffHand();
        }
      } else if (event.getHand() == org.bukkit.inventory.EquipmentSlot.HAND) {
        if (event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer()
            .getInventory().getItemInMainHand().getType() != creature.getItemMaterial()) {
          if (event.getPlayer().getInventory().getItemInOffHand() != null && event.getPlayer()
              .getInventory().getItemInOffHand().getType() == creature.getItemMaterial()) {
            event.setCancelled(true);
          }
          return;
        } else if (event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer()
            .getInventory().getItemInMainHand().getType() == creature.getItemMaterial()) {
          inHand = player.getInventory().getItemInMainHand();
        }
      }

    }

    if (!(inHand.getData() instanceof SpawnEgg)) {
      return;
    }

    if (!(Main.getInstance().getCurrentVersion().startsWith("v1_9")
        || Main.getInstance().getCurrentVersion().startsWith("v1_10"))
        && ((SpawnEgg) inHand.getData()).getSpawnedType() != EntityType.SHEEP) {
      return;
    }

    if (game.isSpectator(player)) {
      return;
    }

    Location startLocation = null;
    if (event.getClickedBlock() == null
        || event.getClickedBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
      startLocation = player.getLocation().getBlock()
          .getRelative(Utils.getCardinalDirection(player.getLocation())).getLocation();
    } else {
      startLocation = event.getClickedBlock().getRelative(BlockFace.UP).getLocation();
    }

    creature.setPlayer(player);
    creature.setGame(game);
    creature.run(startLocation, inHand);
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInteractOtherUser(PlayerInteractEntityEvent event) {
    if (event.getPlayer() == null) {
      return;
    }

    Player player = event.getPlayer();
    Game game = Main.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    if (event.getRightClicked() == null) {
      return;
    }

    if (event.getRightClicked() instanceof ITNTSheep) {
      event.setCancelled(true);
      return;
    }

    if (event.getRightClicked().getVehicle() != null
        && event.getRightClicked().getVehicle() instanceof ITNTSheep) {
      event.setCancelled(true);
      return;
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamageEntity(EntityDamageByEntityEvent event) {
    if (event.getCause().equals(DamageCause.CUSTOM) || event.getCause().equals(DamageCause.VOID)
        || event.getCause().equals(DamageCause.FALL)) {
      return;
    }

    if (event.getEntity() instanceof ITNTSheep) {
      event.setDamage(0.0);
      return;
    }

    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (!(event.getDamager() instanceof TNTPrimed)) {
      return;
    }

    TNTPrimed damager = (TNTPrimed) event.getDamager();

    if (!(damager.getSource() instanceof Player)) {
      return;
    }

    Player damagerPlayer = (Player) damager.getSource();
    Player player = (Player) event.getEntity();
    Game game = Main.getInstance().getGameManager().getGameOfPlayer(player);

    if (game == null) {
      return;
    }

    if (game.getState() != GameState.RUNNING) {
      return;
    }

    if (game.isSpectator(damagerPlayer) || game.isSpectator(player)) {
      event.setCancelled(true);
      return;
    }

    Team damagerTeam = game.getPlayerTeam(damagerPlayer);
    Team team = game.getPlayerTeam(player);

    if (damagerTeam.equals(team) && !damagerTeam.getScoreboardTeam().allowFriendlyFire()) {
      event.setCancelled(true);
      return;
    }
  }

}