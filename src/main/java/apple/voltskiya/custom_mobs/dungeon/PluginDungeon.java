package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaModule;

public class PluginDungeon extends VoltskiyaModule {
    @Override
    public void enable() {
        new DungeonCommand();
    }

    @Override
    public String getName() {
        return "Dungeon";
    }
}
