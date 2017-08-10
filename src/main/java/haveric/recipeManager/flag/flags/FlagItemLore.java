package haveric.recipeManager.flag.flags;

import haveric.recipeManager.flag.Flag;
import haveric.recipeManager.flag.FlagType;
import haveric.recipeManager.flag.args.Args;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class FlagItemLore extends Flag {

    @Override
    public String getFlagType() {
        return FlagType.ITEM_LORE;
    }

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <text>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Adds a line to result's lore (description)",
            "",
            "Supports colors (e.g. <red>, <blue>, &4, &F, etc).",
            "",
            "You can also use these variables:",
            "  {player}         = crafter's name or '(nobody)' if not available",
            "  {playerdisplay}  = crafter's display name or '(nobody)' if not available",
            "  {result}         = the result item name or '(nothing)' if recipe failed.",
            "  {recipename}     = recipe's custom or autogenerated name or '(unknown)' if not available",
            "  {recipetype}     = recipe type or '(unknown)' if not available",
            "  {inventorytype}  = inventory type or '(unknown)' if not available",
            "  {world}          = world name of event location or '(unknown)' if not available",
            "  {x}              = event location's X coord or '(?)' if not available",
            "  {y}              = event location's Y coord or '(?)' if not available",
            "  {z}              = event location's Z coord or '(?)' if not available",
            "",
            "Allows quotes to prevent spaces being trimmed.", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} <red>Awesome item",
            "{flag} <magic>some scrambled text on line 2",
            "{flag} <gray>Crafted at {world}:{x},{y},{z}",
            "{flag} \"  Extra space  \" // Quotes at the beginning and end will be removed, but spaces will be kept.", };
    }


    private List<String> lore = new ArrayList<>();

    public FlagItemLore() {
    }

    public FlagItemLore(FlagItemLore flag) {
        lore.addAll(flag.lore);
    }

    @Override
    public FlagItemLore clone() {
        return new FlagItemLore((FlagItemLore) super.clone());
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> newLore) {
        Validate.notNull(newLore, "The 'lore' argument must not be null!");

        lore.clear();

        for (String value : newLore) {
            addLore(value);
        }
    }

    public void addLore(String value) {
        lore.add(RMCUtil.parseColors(value, false));
    }

    @Override
    public boolean onParse(String value) {
        if (value == null) {
            value = ""; // convert empty flag to blank line
        }

        value = RMCUtil.trimExactQuotes(value);

        addLore(value);

        return true;
    }

    @Override
    public void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Need result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();
        List<String> newLore = meta.getLore();

        if (newLore == null) {
            newLore = new ArrayList<>();
        }

        for (String line : lore) {
            newLore.add(a.parseVariables(line));
        }

        meta.setLore(newLore);

        a.result().setItemMeta(meta);
    }
}
