package apple.voltskiya.custom_mobs;

import apple.voltskiya.custom_mobs.custom_model.CustomModelPlugin;
import apple.voltskiya.custom_mobs.dungeon.PluginDungeon;
import apple.voltskiya.custom_mobs.gui.InventoryGuiPlugin;
import apple.voltskiya.custom_mobs.leaps.LeapPlugin;
import apple.voltskiya.custom_mobs.mobs.PluginNmsMobs;
import apple.voltskiya.custom_mobs.mobs.abilities.MobTickPlugin;
import apple.voltskiya.custom_mobs.old_turrets.TurretPlugin;
import apple.voltskiya.custom_mobs.reload.PluginDisable;
import apple.voltskiya.custom_mobs.reload.PluginEnable;
import apple.voltskiya.custom_mobs.ticking.Ticking;
import apple.voltskiya.custom_mobs.util.PluginUtils;
import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class VoltskiyaPlugin extends JavaPlugin {
    private static VoltskiyaPlugin instance;
    private PaperCommandManager commandManager;
    private final List<VoltskiyaModule> modules = new ArrayList<>();
    private final List<String> loadedJars = new ArrayList<>();
    private LuckPerms luckPerms;

    @Override
    public void onLoad() {
        instance = this;
        manuallyLoadModules();
    }


    @Override
    public void onEnable() {
        loadDependencies();
        setupACF();
        setupLuckPerms();
        enableModules();
//        registerModules();
        new Snowball();
    }

    @Override
    public void onDisable() {
        for (VoltskiyaModule module : modules) {
            if (module.shouldEnable()) {
                module.onDisable();
            }
        }
    }

    private void manuallyLoadModules() {
        final VoltskiyaModule[] modules = new VoltskiyaModule[]{
                new PluginDisable(),
                new PluginUtils(),
                new Ticking(), // this has to go first
                new MobTickPlugin(),
                new LeapPlugin(),
                new TurretPlugin(),
                new CustomModelPlugin(),
                new PluginNmsMobs(),
                new PluginDungeon(),
                new InventoryGuiPlugin(),
                new PluginEnable()
        };
        for (VoltskiyaModule module : modules) {
            registerModule(module);
            if (module.shouldEnable()) {
                loadModule(module);
            }
        }
    }

    private void enableModules() {
        for (VoltskiyaModule module : modules) {
            if (module.shouldEnable()) {
                enableModule(module);
            }
        }
    }

    private void loadDependencies() {
        getLogger().log(Level.INFO, "Starting dynamic dependency loading");
        File dependencies = new File(getDataFolder(), "dependencies");
        if (!dependencies.exists()) dependencies.mkdirs();
        try {
            Files.walk(dependencies.toPath()).forEach(childPath -> {
                File child = childPath.toFile();
                if (!child.getName().endsWith(".jar")) return;
                String depend = child.getName().replace(".jar", "");
                try {
                    loadDependency(child);
                    loadedJars.add(depend);
                    getLogger().log(Level.INFO, "Loaded dependency: " + depend);
                } catch (Exception e) {
                    e.printStackTrace();
                    getLogger().log(Level.WARNING, "Failed to load dependency: " + depend);
                }
            });
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Unexpected issue occurred while loading dependencies.");
            e.printStackTrace();
        }
        getLogger().log(Level.INFO, "Finished dynamic dependency loading, loaded " + loadedJars.size() + " jars.");
    }

    private void loadDependency(File file) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        URLClassLoader loader = (URLClassLoader) getClass().getClassLoader();
        if (!method.canAccess(loader)) method.setAccessible(true);
        method.invoke(loader, file.getAbsoluteFile().toURI().toURL());
    }

    public boolean isLoaded(String dependency) {
        return loadedJars.contains(dependency);
    }

    public List<String> getLoadedJars() {
        return loadedJars;
    }

    private void registerModules() {
        Reflections reflections = new Reflections("apple.voltskiya.custom_mobs", new SubTypesScanner(true));
        reflections.getSubTypesOf(VoltskiyaModule.class).forEach(moduleClass -> {
            VoltskiyaModule module;
            try {
                module = (VoltskiyaModule) moduleClass.getConstructors()[0].newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            registerModule(module);
            if (module.shouldEnable()) {
                enableModule(module);
            } else {
                getLogger().log(Level.WARNING, "Failed to enable Voltskiya Module " + module.getName());
            }
        });
        getLogger().log(Level.INFO, "Loaded " + (int) modules.stream().filter(VoltskiyaModule::isEnabled).count() + " Voltskiya modules.");
        getLogger().log(Level.INFO, "Failed to load " + (int) modules.stream().filter(module -> !module.isEnabled()).count() + " Voltskiya modules.");
    }


    private void registerModule(VoltskiyaModule module) {
        modules.add(module);
        module.setLogger(getLogger());
        module.setEnabled(false);
    }

    private void loadModule(VoltskiyaModule module) {
        module.init();
    }

    public void enableModule(VoltskiyaModule module) {
        module.setEnabled(true);
        module.enable();
        getLogger().log(Level.INFO, "Enabled Voltskiya Module: " + module.getName());
    }

    private void setupACF() {
        commandManager = new PaperCommandManager(this);
    }

    private void setupLuckPerms() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPerms = LuckPermsProvider.get();
        } else {
            getLogger().log(Level.WARNING, "LuckPerms is not enabled, some functions may be disabled.");
        }
    }

    public static VoltskiyaPlugin get() {
        return instance;
    }

    public @NotNull LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }


}
