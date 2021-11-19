package apple.voltskiya.custom_mobs.turret.manage;

import apple.utilities.database.keyed.AppleJsonDatabaseKeyed;
import apple.utilities.database.keyed.AppleJsonDatabaseKeyedBuilder;
import apple.utilities.json.gson.GsonBuilderDynamic;
import apple.utilities.request.keyed.AppleRequestOnConflict;
import apple.utilities.request.keyed.lazy.AppleRequestLazyServiceSimpleVoid;
import apple.voltskiya.custom_mobs.turret.PluginTurret;
import apple.voltskiya.custom_mobs.turret.parent.TurretMob;
import com.google.gson.Gson;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import plugin.util.plugin.plugin.util.plugin.FileIOServiceNow;
import voltskiya.apple.utilities.util.storage.GsonTypeAdapterUtils;

public class TurretDatabase {
    private static AppleJsonDatabaseKeyed<TurretMob> databaseManager;

    public static void initialize() {
        GsonBuilderDynamic gsonBuilder = new GsonBuilderDynamic();
        GsonTypeAdapterUtils.registerLocationTypeAdapter(gsonBuilder,
                new GsonTypeAdapterUtils.LocationTypeAdapterOptions(true, true, true));
        GsonTypeAdapterUtils.registerNBTTagTypeAdapter(gsonBuilder);
        gsonBuilder.registerTypeHierarchyAdapter(TurretMob.class, TurretTypeIdentifier.createTypeMapper().getGsonSerializing(gsonBuilder));
        Gson gson = gsonBuilder.create();

        AppleRequestLazyServiceSimpleVoid turretServiceIO = new AppleRequestLazyServiceSimpleVoid(20, 0, 100, 5000);
        databaseManager = AppleJsonDatabaseKeyedBuilder.required(
                TurretMob.class,
                PluginTurret.get().getFile("mobs"),
                FileIOServiceNow.get(),
                turretServiceIO,
                AppleRequestOnConflict.REPLACE()
        ).withGson(gson).create();
    }


    public static void loadAll() {
        for (TurretMob<?> turretMob : databaseManager.loadAllNow()) {
            turretMob.initialize();
        }
    }

    public static void save(TurretMob<?> turret) {
        databaseManager.save(turret);
    }

    public static void delete(TurretMob<?> turret) {
        databaseManager.delete(turret);
    }

    public static boolean interact(Player player, Entity rightClicked) {
        return false;
    }
}
