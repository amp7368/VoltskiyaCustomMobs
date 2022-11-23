package apple.voltskiya.custom_mobs.leap.basic;

import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import apple.utilities.json.gson.GsonBuilderDynamic;
import apple.voltskiya.custom_mobs.leap.LeapModule;
import apple.voltskiya.custom_mobs.leap.parent.targeting.TargetingConfigType;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import com.voltskiya.lib.pmc.FileIOServiceNow;
import java.util.ArrayList;
import java.util.Collection;

public class LeapBasicSpawner implements SpawnListenerHolder {

    private final Collection<SpawnListener> configs = new ArrayList<>();

    public LeapBasicSpawner() {
        AppleAJDTyped<LeapBasicConfig> manager = AppleAJD.createTyped(LeapBasicConfig.class, LeapModule.get().getFile("Basic"),
            FileIOServiceNow.get().taskCreator());
        manager.setSerializingJson(TargetingConfigType.register(new GsonBuilderDynamic()).create());
        Collection<LeapBasicConfig> basics = manager.loadFolderNow();
        this.configs.addAll(basics);
        if (basics.stream().noneMatch(config -> config.tag.equals("basic"))) {
            LeapBasicConfig config = new LeapBasicConfig("basic");
            manager.saveInFolder(config);
            this.configs.add(config);
        }
        basics.forEach(manager::saveInFolderNow);
        registerListeners();
    }

    @Override
    public Collection<SpawnListener> getListeners() {
        return configs;
    }
}
