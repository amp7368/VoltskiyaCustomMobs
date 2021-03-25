package apple.voltskiya.custom_mobs.turrets;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

import static apple.voltskiya.custom_mobs.turrets.TurretMob.*;

public class TurretManagerTicker implements Listener {
    private static TurretManagerTicker instance;
    private HashMap<Integer, TurretMob> turrets = new HashMap<>();

    public TurretManagerTicker() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, VoltskiyaPlugin.get());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity.getScoreboardTags().contains(TURRET_TAG)) {
            final net.minecraft.server.v1_16_R3.Entity possibleTurret = ((CraftEntity) entity).getHandle();
            NBTTagCompound nbt = new NBTTagCompound();
            possibleTurret.save(nbt);
            if (nbt.hasKey(TURRET_UID)) {
                System.out.println("PlayerInteractAtEntityEvent");
                int uid = nbt.getInt(TURRET_UID);
                @Nullable TurretMob turret = turrets.get(uid);
                if(turret!=null){
                    turret.damage(event.getDamage());
                }
            }
        }
    }

    public static TurretManagerTicker get() {
        return instance;
    }
}
