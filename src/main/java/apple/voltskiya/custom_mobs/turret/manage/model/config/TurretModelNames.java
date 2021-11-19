package apple.voltskiya.custom_mobs.turret.manage.model.config;

import apple.utilities.util.FileFormatting;
import apple.voltskiya.custom_mobs.custom_model.handling.ModelName;
import apple.voltskiya.custom_mobs.turret.PluginTurret;

import java.io.File;

public enum TurretModelNames implements ModelName {
    GM("GM"),
    PLAYER("Player");

    private final File file;

    TurretModelNames(String fileName) {
        File file = PluginTurret.get().getFile("model", FileFormatting.extensionYml(fileName));
        if (!file.exists()) {
            file = getDefaultFile();
        }
        this.file = file;

    }

    private File getDefaultFile() {
        return PluginTurret.get().getFile("model", FileFormatting.extensionYml("default"));
    }

    @Override
    public File getFile() {
        return file;
    }
}
