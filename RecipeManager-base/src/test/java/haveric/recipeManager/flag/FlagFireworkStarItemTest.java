package haveric.recipeManager.flag;

import haveric.recipeManager.RecipeProcessor;
import haveric.recipeManager.TestMetaCharge;
import haveric.recipeManager.flag.args.ArgBuilder;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManager.flag.flags.result.FlagFireworkStarItem;
import haveric.recipeManager.recipes.BaseRecipe;
import haveric.recipeManager.recipes.ItemResult;
import haveric.recipeManager.recipes.craft.CraftRecipe1_13;
import haveric.recipeManagerCommon.recipes.RMCRecipeInfo;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.junit.Test;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.*;

public class FlagFireworkStarItemTest extends FlagBaseTest {

    @Test
    public void onRecipeParse() {
        File file = new File(baseRecipePath + "flagFireworkChargeItem/");
        RecipeProcessor.reload(null, true, file.getPath(), workDir.getPath());

        Map<BaseRecipe, RMCRecipeInfo> queued = RecipeProcessor.getRegistrator().getQueuedRecipes();

        assertEquals(3, queued.size());

        for (Map.Entry<BaseRecipe, RMCRecipeInfo> entry : queued.entrySet()) {
            CraftRecipe1_13 recipe = (CraftRecipe1_13) entry.getKey();

            ItemResult result = recipe.getFirstResult();

            Args a = ArgBuilder.create().recipe(recipe).result(result).player(testUUID).build();

            FlagFireworkStarItem flag = (FlagFireworkStarItem) result.getFlag(FlagType.FIREWORK_STAR_ITEM);
            flag.onPrepare(a);

            TestMetaCharge meta = (TestMetaCharge) result.getItemMeta();
            String name = recipe.getName();

            if (name.equals("first")) {
                assertEquals(2, meta.getEffect().getColors().size());
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(0, 255, 0)));
                assertTrue(meta.getEffect().getFadeColors().isEmpty());

                assertTrue(meta.getEffect().hasTrail());
                assertFalse(meta.getEffect().hasFlicker());

                assertEquals(FireworkEffect.Type.BALL_LARGE, meta.getEffect().getType());
            } else if (name.equals("second") || name.equals("two")) {
                assertEquals(1, meta.getEffect().getColors().size());
                assertFalse(meta.getEffect().getColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getColors().contains(Color.fromRGB(0, 255, 0)));

                assertEquals(2, meta.getEffect().getFadeColors().size());
                assertTrue(meta.getEffect().getFadeColors().contains(Color.fromRGB(255, 0, 0)));
                assertTrue(meta.getEffect().getFadeColors().contains(Color.fromRGB(0, 255, 0)));

                assertFalse(meta.getEffect().hasTrail());
                assertTrue(meta.getEffect().hasFlicker());

                assertEquals(FireworkEffect.Type.CREEPER, meta.getEffect().getType());
            }
        }
    }
}