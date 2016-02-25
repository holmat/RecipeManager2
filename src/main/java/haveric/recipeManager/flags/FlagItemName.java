package haveric.recipeManager.flags;

import org.bukkit.inventory.meta.ItemMeta;

import haveric.recipeManagerCommon.util.RMCUtil;

public class FlagItemName extends Flag {

    @Override
    protected String[] getArguments() {
        return new String[] {
            "{flag} <text or false>", };
    }

    @Override
    protected String[] getDescription() {
        return new String[] {
            "Changes result's display name.",
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
            "  {z}              = event location's Z coord or '(?)' if not available", };
    }

    @Override
    protected String[] getExamples() {
        return new String[] {
            "{flag} <light_purple>Weird Item",
            "{flag} <yellow>{player}'s Sword", };
    }


    private String name;

    public FlagItemName() {
    }

    public FlagItemName(FlagItemName flag) {
        name = flag.name;
    }

    @Override
    public FlagItemName clone() {
        return new FlagItemName((FlagItemName) super.clone());
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    @Override
    protected boolean onParse(String value) {
        setName(value);
        return true;
    }

    @Override
    protected void onPrepare(Args a) {
        if (!a.hasResult()) {
            a.addCustomReason("Needs result!");
            return;
        }

        ItemMeta meta = a.result().getItemMeta();

        String displayName;
        if (getName() == null) {
            displayName = null;
        } else {
            displayName = RMCUtil.parseColors(a.parseVariables(getName()), false);
        }
        meta.setDisplayName(displayName);

        a.result().setItemMeta(meta);
    }
}
