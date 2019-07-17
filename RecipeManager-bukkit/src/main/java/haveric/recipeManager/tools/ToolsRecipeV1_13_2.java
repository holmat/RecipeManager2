package haveric.recipeManager.tools;

import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.List;

/**
 * Minecraft v1.13 NMS solution for matching if a RM recipe is already represented by an MC recipe.
 *
 * Basically duplicates the "internal" matching code.
 **/
public class ToolsRecipeV1_13_2 extends BaseToolsRecipe {
    @Override
    public boolean matchesShaped(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack[] matrix, org.bukkit.inventory.ItemStack[] matrixMirror, int width, int height) {
        if (bukkitRecipe instanceof ShapedRecipe) {
            ShapedRecipe shapedRecipe = (ShapedRecipe) bukkitRecipe;

            return RMBukkitTools.compareShapedRecipeToMatrix(shapedRecipe, matrix, matrixMirror);
        }

        return false;
    }

    @Override
    public boolean matchesShapeless(Recipe bukkitRecipe, List<org.bukkit.inventory.ItemStack> ingredientItems) {
        if (bukkitRecipe instanceof ShapelessRecipe) {
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) bukkitRecipe;

            return RMBukkitTools.compareIngredientList(ingredientItems, shapelessRecipe.getIngredientList());
        }

        return false;
    }


    @Override
    public boolean matchesFurnace(Recipe bukkitRecipe, org.bukkit.inventory.ItemStack furnaceIngredient) {
        if (bukkitRecipe instanceof org.bukkit.inventory.FurnaceRecipe) {
            org.bukkit.inventory.FurnaceRecipe furnaceRecipe = (org.bukkit.inventory.FurnaceRecipe) bukkitRecipe;

            return RMBukkitTools.isSameItemFromChoice(furnaceRecipe.getInputChoice(), furnaceIngredient);
        }

        return false;
    }
}