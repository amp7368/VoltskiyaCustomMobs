package apple.voltskiya.custom_mobs.abilities.tick.charger;

import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.ticking.TickGiverable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ChargerIndividualTicker implements Tickable {
    private final TickGiverable giver;
    private final ArrayList<UUID> chargers = new ArrayList<>();
    private static HashMap<UUID, Long> chargersToLastCharge = new HashMap<>();
    private final static Object chargersToLastChargeSync = new Object();
    private final ChargerManagerTicker.Closeness closeness;
    private boolean isTicking = false;
    private long myTickerUid = -1;
    private final long callerUid = UpdatedPlayerList.callerUid();
    private boolean isCharging = false;


    public ChargerIndividualTicker(TickGiverable giver, ChargerManagerTicker.Closeness closeness) {
        this.giver = giver;
        this.closeness = closeness;
    }

    @Override
    public synchronized void tick() {
        Iterator<UUID> chargerUidIterator = chargers.iterator();
        boolean trim = false;
        while (chargerUidIterator.hasNext()) {
            UUID chargerUid = chargerUidIterator.next();
            Entity entity = Bukkit.getEntity(chargerUid);
            if (!(entity instanceof Mob)) {
                // remove this charger D:
                chargerUidIterator.remove();
                trim = true;
            } else {
                Mob charger = (Mob) entity;
                boolean isCharging = tickCharger(charger);
                if (isCharging || ChargerManagerTicker.get().amIGivingCharger(charger, closeness)) {
                    chargerUidIterator.remove();
                    trim = true;
                }
            }
        }
        if (trim) {
            chargers.trimToSize();
            if (isTicking && chargers.isEmpty()) {
                giver.remove(myTickerUid);
                isTicking = false;
            }
        }
    }

    private synchronized boolean tickCharger(Mob charger) {
        if (!isCharging) return false;
        synchronized (chargersToLastChargeSync) {
            long lastCharge = chargersToLastCharge.getOrDefault(charger.getUniqueId(), 0L);
            if (System.currentTimeMillis() - lastCharge < ChargerManagerTicker.CHARGE_COOLDOWN) {
                return false;
            }
        }
        if (new Random().nextDouble() < ChargerManagerTicker.CHARGE_CHANCE * giver.getTickSpeed()) {
            Location chargerLocation = charger.getLocation();
            Player playerToChargeAt = null;
            double chargeError = Double.MAX_VALUE;
            for (Player player : UpdatedPlayerList.getPlayers(callerUid)) {
                if (player.getGameMode() == GameMode.SURVIVAL && chargerLocation.getWorld().getUID().equals(player.getWorld().getUID())) {
                    // check that the player is in the facing direction of the charger
                    Location playerLocation = player.getLocation();
                    Location change = playerLocation.clone().subtract(chargerLocation);
                    final double dx = change.getX();
                    final double dy = change.getY();
                    final double dz = change.getZ();
                    final double changeMagnitude = DistanceUtils.magnitude(dx, dy, dz);
                    if (changeMagnitude > ChargerManagerTicker.TOO_CLOSE_TO_CHARGE) {
                        // multiply the chargerFacing times the magnitude
                        // to see if the charge is reasonable
                        @NotNull Vector chargerFacing = chargerLocation.getDirection();
                        chargerFacing.multiply(changeMagnitude);
                        Location result = chargerLocation.clone().add(chargerFacing);
                        double error = DistanceUtils.magnitude(result.subtract(playerLocation));
                        if (error < chargeError && error < ChargerManagerTicker.MARGIN_OF_ERROR) {
                            playerToChargeAt = player;
                            chargeError = error;
                        }
                    }
                }
            }

            if (playerToChargeAt != null) {
                Player player = UpdatedPlayerList.getClosestPlayer(playerToChargeAt.getLocation(), callerUid);
                if (player != null && DistanceUtils.distance(player.getLocation(), playerToChargeAt.getLocation()) < 3) {
                    synchronized (chargersToLastChargeSync) {
                        // say we charged
                        chargersToLastCharge.put(charger.getUniqueId(), System.currentTimeMillis());
                    }
                    new ChargerCharge(charger, playerToChargeAt.getLocation());
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized void giveCharger(Mob charger) {
        trimChargersToLastDamage();
        this.chargers.add(charger.getUniqueId());
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    private static void trimChargersToLastDamage() {
        synchronized (chargersToLastChargeSync) {
            long now = System.currentTimeMillis();
            chargersToLastCharge.values().removeIf(lastCharge -> now - lastCharge > ChargerManagerTicker.CHARGE_COOLDOWN);
            chargersToLastCharge = new HashMap<>(chargersToLastCharge);
        }
    }

    public void setChargeTick() {
        this.isCharging = true;
    }
}
