package haveric.recipeManager.recipes.campfire;

import haveric.recipeManager.RecipeManager;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.campfire.data.RMCampfireData;
import haveric.recipeManager.recipes.campfire.data.RMCampfires;
import haveric.recipeManager.tools.ToolsItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RMCampfireEvents implements Listener {
    public RMCampfireEvents() { }

    public void clean() {
        HandlerList.unregisterAll(this);
    }

    public static void reload() {
        HandlerList.unregisterAll(RecipeManager.getRMCampfireEvents());
        Bukkit.getPluginManager().registerEvents(RecipeManager.getRMCampfireEvents(), RecipeManager.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void rmCampfirePlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();

            if (item != null && item.getType() != Material.AIR) {
                Block block = event.getClickedBlock();

                if (block != null && block.getType() == Material.CAMPFIRE) {
                    Campfire campfire = (Campfire) block.getState();

                    int slot = -1;
                    for (int i = 0; i <= 3; i++) {
                        ItemStack currentIngredient = campfire.getItem(i);

                        if (currentIngredient == null) {
                            slot = i;
                            break;
                        }
                    }

                    if (slot != -1) {
                        RMCampfireData data = RMCampfires.get(campfire.getLocation());
                        Player player = event.getPlayer();

                        data.setItemId(slot, player.getUniqueId());

                        /* TODO: Figure out a way to properly set random cook time. Possibly wait for new campfire api event
                        RMCampfireRecipe recipe = RecipeManager.getRecipes().getRMCampfireRecipe(item);
                        if (recipe != null && recipe.hasRandomTime()) {
                            runCampfireUpdateLater(campfire, slot, recipe.getCookTicks());
                        }
                        */
                    }
                }
            }
        }
    }

    /*
    private void runCampfireUpdateLater(Campfire campfire, int slot, int cookTime) {
        new BukkitRunnable() {
            @Override
            public void run() {
                campfire.setCookTimeTotal(slot, cookTime);
            }
        }.runTaskLater(RecipeManager.getPlugin(), 0);
    }
    */

    @EventHandler(priority = EventPriority.LOW)
    public void rmCampfireCookEvent(BlockCookEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.CAMPFIRE) {
            ItemStack ingredient = event.getSource();
            RMCampfireRecipe recipe = RecipeManager.getRecipes().getRMCampfireRecipe(ingredient);

            if (recipe != null) {
                Campfire campfire = (Campfire) block.getState();

                int slot = -1;
                for (int i = 0; i <= 3; i++) {
                    ItemStack currentIngredient = campfire.getItem(i);

                    if (currentIngredient != null && campfire.getCookTime(i) == recipe.getCookTicks()) {
                        slot = i;
                        break;
                    }
                }

                if (slot != -1) {
                    RMCampfireData data = RMCampfires.get(campfire.getLocation());
                    UUID playerUUID = data.getItemUUID(slot);
                    data.setItemId(slot, null);
                    if (data.allSlotsEmpty()) {
                        RMCampfires.remove(campfire.getLocation());
                    }

                    Args a = Args.create().player(playerUUID).recipe(recipe).build();
                    if (!recipe.checkFlags(a)) {
                        event.setCancelled(true);
                        return;
                    }

                    ItemResult result = recipe.getResult(a);

                    a = Args.create().player(playerUUID).recipe(recipe).result(result).build();

                    if (!result.checkFlags(a)) {
                        event.setCancelled(true);
                        return;
                    }

                    a.clear();

                    boolean recipeCraftSuccess = recipe.sendCrafted(a);
                    if (recipeCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().get("flag.prefix.recipe"));
                    }

                    a.clear();

                    boolean resultCraftSuccess = result.sendCrafted(a);
                    if (resultCraftSuccess) {
                        a.sendEffects(a.player(), Messages.getInstance().parse("flag.prefix.result", "{item}", ToolsItem.print(result)));
                    }

                    if (recipeCraftSuccess && resultCraftSuccess) {
                        event.setResult(a.result());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void rmCampfireBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.CAMPFIRE) {
            RMCampfires.remove(block.getLocation());
        }
    }
}
