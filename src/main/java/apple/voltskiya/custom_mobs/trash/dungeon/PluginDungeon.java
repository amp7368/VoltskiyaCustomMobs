package apple.voltskiya.custom_mobs.trash.dungeon;

import apple.voltskiya.custom_mobs.trash.dungeon.patrols.PatrolWandListener;
import plugin.util.plugin.plugin.util.plugin.PluginManagedModule;

public class PluginDungeon extends PluginManagedModule {
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
