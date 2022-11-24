package apple.voltskiya.custom_mobs.leap.parent;

import apple.utilities.database.ajd.AppleAJD;
import apple.utilities.database.ajd.AppleAJDTyped;
import apple.utilities.json.gson.GsonBuilderDynamic;
import apple.utilities.util.Pretty;
import apple.voltskiya.custom_mobs.leap.LeapModule;
import apple.voltskiya.custom_mobs.leap.parent.config.LeapConfig;
import apple.voltskiya.custom_mobs.leap.parent.targeting.TargetingConfigType;
import apple.voltskiya.mob_manager.listen.SpawnListener;
import apple.voltskiya.mob_manager.listen.SpawnListenerHolder;
import com.google.gson.Gson;
import com.voltskiya.lib.pmc.FileIOServiceNow;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class LeapSpawner<Config extends LeapConfig & SpawnListener> implements SpawnListenerHolder {

    private final Collection<SpawnListener> configs = new ArrayList<>();

    public LeapSpawner(CreateLeapConfig<Config> createConfig, Class<Config> type, String prefix) {
        File file = LeapModule.get().getFile(Pretty.upperCaseFirst(prefix.isEmpty() ? "Basic" : prefix));
        AppleAJDTyped<Config> manager = AppleAJD.createTyped(type, file,
            FileIOServiceNow.get().taskCreator());
        manager.setSerializingJson(TargetingConfigType.register(new GsonBuilderDynamic()).create());
        Collection<Config> loaded = manager.loadFolderNow();
        this.configs.addAll(loaded);
        if (loaded.stream().noneMatch(config -> config.tag.equals("basic"))) {
            Config config = createConfig.create(prefix, "basic");
            manager.saveInFolder(config);
            this.configs.add(config);
        }
        loaded.forEach(manager::saveInFolderNow);
        registerListeners();
    }


    @Override
    public Collection<SpawnListener> getListeners() {
        return configs;
    }

}
