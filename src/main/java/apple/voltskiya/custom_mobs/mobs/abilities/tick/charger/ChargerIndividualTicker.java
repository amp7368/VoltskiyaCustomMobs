package apple.voltskiya.custom_mobs.mobs.abilities.tick.charger;

import apple.voltskiya.custom_mobs.mobs.abilities.tick.Tickable;
import apple.voltskiya.custom_mobs.util.UpdatedPlayerList;
import apple.voltskiya.custom_mobs.util.ticking.TickGiverable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import voltskiya.apple.utilities.util.DistanceUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ChargerIndividualTicker implements Tickable {
    private final TickGiverable giver;
    private final ArrayList<Charger> chargers = new ArrayList<>();
    private final ChargerManagerTicker.Closeness closeness;
    private boolean isTicking = false;
    private long myTickerUid = -1;
    private boolean isCharging = false;
    private final Random random = new Random();


    public ChargerIndividualTicker(TickGiverable giver, ChargerManagerTicker.Closeness closeness) {
        this.giver = giver;
        this.closeness = closeness;
    }

    @Override
    public synchronized void tick() {
        Iterator<Charger> chargerUidIterator = chargers.iterator();
        boolean trim = false;
        while (chargerUidIterator.hasNext()) {
            Charger charger = chargerUidIterator.next();
            if (charger.getEntity().isDead()) {
                // remove this charger D:
                chargerUidIterator.remove();
                trim = true;
            } else {
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

    private synchronized boolean tickCharger(Charger charger) {
        if (!isCharging) return false;
        if (charger.isChargeable()) {
            if (random.nextDouble() < charger.getType().getChargeChance() * giver.getTickSpeed()) {
                Location chargerLocation = charger.getEntity().getLocation();
                Player playerToChargeAt = null;
                double chargeError = Double.MAX_VALUE;
                for (Player player : UpdatedPlayerList.getPlayers()) {
                    if (player.getGameMode() == GameMode.SURVIVAL && chargerLocation.getWorld().getUID().equals(player.getWorld().getUID())) {
                        // check that the player is in the facing direction of the charger
                        Location playerLocation = player.getLocation();
                        Location change = playerLocation.clone().subtract(chargerLocation);
                        final double dx = change.getX();
                        final double dy = change.getY();
                        final double dz = change.getZ();
                        final double changeMagnitude = DistanceUtils.magnitude(dx, dy, dz);
                        if (changeMagnitude > charger.getType().getTooCloseToCharge()) {
                            // multiply the chargerFacing times the magnitude
                            // to see if the charge is reasonable
                            @NotNull Vector chargerFacing = change.toVector().normalize();
                            chargerFacing.multiply(changeMagnitude + 6);
                            Location result = chargerLocation.clone().add(chargerFacing);
                            double error = DistanceUtils.magnitude(result.subtract(playerLocation));
                            if (error < chargeError && error < charger.getType().getMarginOfError()) {
                                playerToChargeAt = player;
                                chargeError = error;
                            }
                        }
                    }
                }

                if (playerToChargeAt != null) {
                    Player player = UpdatedPlayerList.getClosestPlayerPlayer(playerToChargeAt.getLocation());
                    if (player != null && DistanceUtils.distance(player.getLocation(), playerToChargeAt.getLocation()) < 3) {
                        // say we charged
                        charger.chargeNow();
                        charger.getType().construct(charger, playerToChargeAt);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public synchronized void giveCharger(Charger charger) {
        this.chargers.add(charger);
        if (!isTicking) {
            isTicking = true;
            this.myTickerUid = closeness.getGiver().add(this::tick);
        }
    }

    public void setChargeTick() {
        this.isCharging = true;
    }
}
