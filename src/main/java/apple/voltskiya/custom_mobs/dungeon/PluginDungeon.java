package apple.voltskiya.custom_mobs.dungeon;

import apple.voltskiya.custom_mobs.VoltskiyaModule;
import apple.voltskiya.custom_mobs.dungeon.patrols.PatrolWandListener;

public class PluginDungeon extends VoltskiyaModule {
    private static PluginDungeon instance;

    public static PluginDungeon get() {
        return instance;
    }

    @Override
    public void init() {
        instance = this;
    }

    @Override
    public void enable() {
        new DungeonCommand();
        new PatrolWandListener();
    }

    @Override
    public String getName() {
        return "Dungeon";
    }
}
