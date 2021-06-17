package apple.voltskiya.custom_mobs.custom_model;

import apple.voltskiya.custom_mobs.VoltskiyaPlugin;
import apple.voltskiya.custom_mobs.util.DistanceUtils;
import apple.voltskiya.custom_mobs.util.minecraft.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class CustomModelGui implements InventoryHolder {
    // left click repeats the last action
    // right click with the gui tool reopens the gui
    // gui's are only loaded until a reload
    // gui tool is the way to interact and open and modify the gui settings


    private final static Map<Integer, ModelGuiPage> pageMap = new HashMap<>() {{
        put(0, new Page1());
    }};
    private static final long SHOW_DELAY = 100L;

    private final Location selectionCenter;
    private double selectionRadius;
    private final Consumer<CustomModelGui> runOnSave;
    private int page = 0;
    private final ArrayList<Inventory> inventory = new ArrayList<>();
    private Runnable action = null;

    public CustomModelGui(Player player, Vector direction, Double startingRadius, Consumer<CustomModelGui> runOnSave) {
        this.runOnSave = runOnSave;
        this.selectionCenter = player.getLocation().setDirection(direction);
        this.selectionRadius = startingRadius == null ? 0.5 : startingRadius;
        for (int i = 0; i < pageMap.size(); i++) {
            inventory.add(pageMap.get(i).createInventory(this));
        }
        player.openInventory(getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory.get(page);
    }

    /**
     * executes whatever the last action assigned was
     */
    public void execute() {
        if (action != null) {
            action.run();
        }
    }

    private void updateImage(ShowSection showMe) {
        switch (showMe) {
            case SELECTION_CENTER:
                spawnArmorStand(selectionCenter.clone().setDirection(new Vector(1, 0, 0)), Material.BLACK_CONCRETE);
                break;
            case SELECTION_RADIUS:
                double phi = Math.PI * (3d - Math.sqrt(5d));
                final int UPDATE_PIXELS = 300;
                for (int i = 0; i < UPDATE_PIXELS; i++) {
                    double y = 1 - i / (UPDATE_PIXELS - 1d) * 2;
                    double radiusAtY = Math.sqrt(1 - y * y);
                    double theta = phi * i; // golden angle increment
                    double x = Math.cos(theta) * radiusAtY;
                    double z = Math.sin(theta) * radiusAtY;
                    spawnArmorStand(selectionCenter.clone().add(
                            x * selectionRadius, y * selectionRadius, z * selectionRadius
                    ), Material.OAK_BUTTON);
                }
        }
    }

    private void spawnArmorStand(Location locationToShow, Material head) {
        locationToShow.getWorld().spawnEntity(locationToShow.clone().subtract(0, 1.5 / 4, 0), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM,
                (e) -> {
                    @Nullable EntityEquipment equipment = ((ArmorStand) e).getEquipment();
                    if (equipment != null) equipment.setHelmet(new ItemStack(head));
                    ((ArmorStand) e).setAI(false);
                    ((ArmorStand) e).setSmall(true);
                    ((ArmorStand) e).setInvisible(true);
                    e.setInvulnerable(true);
                    e.setGravity(false);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(VoltskiyaPlugin.get(), e::remove, SHOW_DELAY);
                }
        );
    }

    public void click(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory != null)
            if (clickedInventory.getHolder() instanceof CustomModelGui) {
                ModelGuiPage p = pageMap.get(page);
                if (p != null) p.dealWithClick(event.getSlot(), event.getWhoClicked(), this);
            }
        event.setCancelled(true);
    }

    public List<Entity> getEntities() {
        @NotNull Collection<Entity> entities = selectionCenter.getNearbyEntities(selectionRadius, selectionRadius, selectionRadius);
        List<Entity> validEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (DistanceUtils.distance(entity.getLocation(), selectionCenter) <= selectionRadius)
                validEntities.add(entity);
        }
        return validEntities;
    }

    public Location getCenter() {
        return selectionCenter;
    }

    private static class Page1 implements ModelGuiPage {
        private static final Map<Integer, InventorySelection> slotToAction = new HashMap<>();
        private static final double INCREASE_RADIUS_INTERVAL = 0.3;
        private static final double CENTER_MOVE_INTERVAL = 0.2;


        public Inventory createInventory(CustomModelGui holder) {
            final Inventory inventory = Bukkit.createInventory(holder, 54);
            inventory.setItem(4, InventoryUtils.makeItem(Material.LIME_TERRACOTTA, 1, "Page 1 Overview",
                    Arrays.asList(
                            "This page deals with the center of the model.",
                            "You can increase the radius of the selection.",
                            "You can also move the center around.",
                            "Note: This only impacts the selection of entities."
                    )
            ));
            inventory.setItem(InventorySelection.NEXT_PAGE.index, InventoryUtils.makeItem(Material.ARROW, 1, "Page 2", null));

            // radius
            inventory.setItem(10, InventoryUtils.makeItem(Material.OAK_SIGN, 1, "Radius",
                    Arrays.asList(
                            "This section increases or decreases the selection size",
                            "Note: Only sphere selections can be made"
                    )
            ));
            inventory.setItem(InventorySelection.INCREASE_RADIUS.index, InventoryUtils.makeItem(Material.IRON_BLOCK, 1, "Increase Radius", null));
            inventory.setItem(InventorySelection.DECREASE_RADIUS.index, InventoryUtils.makeItem(Material.IRON_INGOT, 1, "Decrease Radius", null));
            inventory.setItem(InventorySelection.SHOW_RADIUS.index, InventoryUtils.makeItem(Material.BLACK_TERRACOTTA, 1, "Radius",
                    Collections.singletonList(
                            "Radius: " + holder.selectionRadius
                    )
            ));
            // center xz
            inventory.setItem(21, InventoryUtils.makeItem(Material.OAK_SIGN, 1, "Center",
                    Arrays.asList(
                            "This section changes the center location of the selection.",
                            "Note: Only movements in the x z plane can be made here",
                            "Note: These movements are absolute movements",
                            "and are not relative to facing direction"
                    )
            ));
            inventory.setItem(InventorySelection.PLUS_X.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, "+X", null));
            inventory.setItem(InventorySelection.MINUS_Z.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, "-Z", null));
            inventory.setItem(InventorySelection.PLUS_Z.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, "+Z", null));
            inventory.setItem(InventorySelection.MINUS_X.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, "-X", null));
            inventory.setItem(InventorySelection.SHOW_CENTER.index, InventoryUtils.makeItem(Material.BLACK_TERRACOTTA, 1, "Center",
                    Arrays.asList(
                            "x: " + holder.selectionCenter.getX(),
                            "y: " + holder.selectionCenter.getY(),
                            "z: " + holder.selectionCenter.getZ()
                    )
            ));

            // center y
            inventory.setItem(24, InventoryUtils.makeItem(Material.OAK_SIGN, 1, "Center",
                    Arrays.asList(
                            "This section only changes the center of the selection.",
                            "Note: only movements along the y axis can be made here."
                    )
            ));
            inventory.setItem(InventorySelection.PLUS_Y.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, " +Y", null));
            inventory.setItem(InventorySelection.MINUS_Y.index, InventoryUtils.makeItem(Material.BLUE_TERRACOTTA, 1, " -Y", null));
            inventory.setItem(InventorySelection.SAVE.index, InventoryUtils.makeItem(Material.GREEN_CONCRETE, 1, " Save", null));
            return inventory;
        }

        @Override
        public void dealWithClick(int slot, @NotNull HumanEntity player, CustomModelGui gui) {
            InventorySelection.dealWithClick(slot, player, gui);
        }

        private enum InventorySelection {
            NEXT_PAGE(8,
                    (player, gui) -> {
                        gui.page++;
                    }),
            INCREASE_RADIUS(19,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionRadius += INCREASE_RADIUS_INTERVAL;
                        gui.updateImage(ShowSection.SELECTION_RADIUS);
                    }),
            DECREASE_RADIUS(28,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionRadius -= INCREASE_RADIUS_INTERVAL;
                        gui.updateImage(ShowSection.SELECTION_RADIUS);
                    }),
            PLUS_X(30,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(CENTER_MOVE_INTERVAL, 0, 0);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            MINUS_X(48,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(-CENTER_MOVE_INTERVAL, 0, 0);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            PLUS_Z(40,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(0, 0, CENTER_MOVE_INTERVAL);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            MINUS_Z(38,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(0, 0, -CENTER_MOVE_INTERVAL);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            PLUS_Y(33,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(0, CENTER_MOVE_INTERVAL, 0);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            MINUS_Y(51,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.selectionCenter.add(0, -CENTER_MOVE_INTERVAL, 0);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            SHOW_RADIUS(37,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.updateImage(ShowSection.SELECTION_RADIUS);
                    }),
            SHOW_CENTER(39,
                    (player, gui) -> {
                        Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                        gui.updateImage(ShowSection.SELECTION_CENTER);
                    }),
            SAVE(53, (player, gui) -> {
                Bukkit.getScheduler().runTask(VoltskiyaPlugin.get(), (@NotNull Runnable) player::closeInventory);
                gui.runOnSave.accept(gui);
            });

            private final int index;
            private final BiConsumer<HumanEntity, CustomModelGui> dealWithClick;

            InventorySelection(int index, BiConsumer<HumanEntity, CustomModelGui> dealWithClick) {
                this.index = index;
                this.dealWithClick = dealWithClick;
                slotToAction.put(index, this);
            }

            public static void dealWithClick(int slot, @NotNull HumanEntity player, CustomModelGui gui) {
                InventorySelection action = slotToAction.get(slot);
                if (action != null) {
                    gui.action = () -> action.dealWithClick.accept(player, gui);
                    gui.action.run();
                }
            }
        }
    }

    private interface ModelGuiPage {
        Inventory createInventory(CustomModelGui holder);

        void dealWithClick(int slot, @NotNull HumanEntity player, CustomModelGui gui);
    }

    private enum ShowSection {
        SELECTION_RADIUS,
        SELECTION_CENTER
    }

}
