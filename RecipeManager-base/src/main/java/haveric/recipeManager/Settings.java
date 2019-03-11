package haveric.recipeManager;

import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.tools.Tools;
import haveric.recipeManager.tools.Version;
import haveric.recipeManagerCommon.util.RMCUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RecipeManager's settings loaded from its config.yml, values are read-only.
 */
public class Settings {
    private static final boolean SPECIAL_REPAIR_DEFAULT = true;
    private static final boolean SPECIAL_REPAIR_METADATA_DEFAULT = false;

    private static final boolean SPECIAL_LEATHER_DYE_DEFAULT = true;
    private static final boolean SPECIAL_FIREWORKS_DEFAULT = true;
    private static final boolean SPECIAL_FIREWORK_STAR_DEFAULT = true;
    private static final boolean SPECIAL_MAP_CLONING_DEFAULT = true;
    private static final boolean SPECIAL_MAP_EXTENDING_DEFAULT = true;
    private static final boolean SPECIAL_BOOK_CLONING_DEFAULT = true;
    private static final boolean SPECIAL_BANNER_DEFAULT = true;
    private static final boolean SPECIAL_SHIELD_BANNER_DEFAULT = true;
    private static final boolean SPECIAL_TIPPED_ARROWS_DEFAULT = true;
    private static final boolean SPECIAL_SHULKER_DYE_DEFAULT = true;

    private static final boolean SOUNDS_REPAIR_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_DEFAULT = true;
    private static final boolean SOUNDS_FAILED_CLICK_DEFAULT = true;

    private static final boolean FIX_MOD_RESULTS_DEFAULT = false;
    private static final boolean UPDATE_BOOKS_DEFAULT = true;
    private static final boolean COLOR_CONSOLE_DEFAULT = true;

    private static final String FURNACE_SHIFT_CLICK_DEFAULT = "f";

    private static final boolean MULTITHREADING_DEFAULT = true;

    private static final boolean CLEAR_RECIPES_DEFAULT = false;

    private static final boolean UPDATE_CHECK_ENABLED_DEFAULT = true;
    private static final int UPDATE_CHECK_FREQUENCY_DEFAULT = 6;

    private static Material MATERIAL_FAIL_DEFAULT;
    private static final Material MATERIAL_SECRET_DEFAULT = Material.CHEST;
    private static final Material MATERIAL_MULTIPLE_RESULTS_DEFAULT = Material.CHEST;

    private static final boolean DISABLE_OVERRIDE_WARNINGS_DEFAULT = false;

    private static List<String> RECIPE_COMMENT_CHARACTERS_DEFAULT;

    private static FileConfiguration fileConfig;
    private static FileConfiguration itemDatasConfig;
    private static FileConfiguration itemAliasesConfig;
    private static FileConfiguration enchantAliasesConfig;
    private static Settings instance;

    private static Map<Material, Short> itemDatas;

    private static Map<String, Material> materialNames;
    private static Map<Material, Map<String, Short>> materialDataNames;
    private static Map<String, Enchantment> enchantNames;

    private static Map<Material, String> materialPrint;
    private static Map<Material, Map<Short, String>> materialDataPrint;
    private static Map<Enchantment, String> enchantPrint;

    protected Settings() {
       // Exists only to defeat instantiation.
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();

            init();
        }

        return instance;
    }

    public static void clean() {
        instance = null;
    }

    private static void init() {
        RECIPE_COMMENT_CHARACTERS_DEFAULT = new ArrayList<>();
        RECIPE_COMMENT_CHARACTERS_DEFAULT.add("//");
        RECIPE_COMMENT_CHARACTERS_DEFAULT.add("#");

        if (Version.has1_8Support()) {
            MATERIAL_FAIL_DEFAULT = Material.BARRIER;
        } else {
            MATERIAL_FAIL_DEFAULT = Material.FIRE;
        }

        itemDatas = new HashMap<>();
        materialNames = new HashMap<>();
        materialDataNames = new HashMap<>();
        enchantNames = new HashMap<>();
        materialPrint = new HashMap<>();
        materialDataPrint = new HashMap<>();
        enchantPrint = new HashMap<>();

        // Load/reload/generate config.yml
        fileConfig = loadYML(Files.FILE_CONFIG);
    }

    public void reload(CommandSender sender) {
        init();

        String lastChanged = fileConfig.getString("lastchanged");

        if (!Files.LASTCHANGED_CONFIG.equals(lastChanged)) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_CONFIG + "' file is outdated, please delete it to allow it to be generated again.");
        }

        MessageSender.getInstance().log("config.yml settings:");
        MessageSender.getInstance().log("    special-recipes.repair: " + getSpecialRepair());
        MessageSender.getInstance().log("    special-recipes.repair-metadata: " + getSpecialRepairMetadata());
        MessageSender.getInstance().log("    special-recipes.leather-dye: " + getSpecialLeatherDye());
        MessageSender.getInstance().log("    special-recipes.fireworks: " + getSpecialFireworks());
        MessageSender.getInstance().log("    special-recipes.firework-star: " + getSpecialFireworkStar());
        MessageSender.getInstance().log("    special-recipes.map-cloning: " + getSpecialMapCloning());
        MessageSender.getInstance().log("    special-recipes.map-extending: " + getSpecialMapExtending());
        MessageSender.getInstance().log("    special-recipes.book-cloning: " + getSpecialBookCloning());
        MessageSender.getInstance().log("    special-recipes.banner: " + getSpecialBanner());
        MessageSender.getInstance().log("    special-recipes.shield-banner: " + getSpecialShieldBanner());
        MessageSender.getInstance().log("    special-recipes.tipped-arrows: " + getSpecialTippedArrows());
        MessageSender.getInstance().log("    special-recipes.shulker-dye: " + getSpecialShulkerDye());
        MessageSender.getInstance().log("    sounds.failed: " + getSoundsFailed());
        MessageSender.getInstance().log("    sounds.failed_click: " + getSoundsFailedClick());
        MessageSender.getInstance().log("    sounds.repair: " + getSoundsRepair());
        MessageSender.getInstance().log("    update-books: " + getUpdateBooks());
        MessageSender.getInstance().log("    color-console: " + getColorConsole());
        MessageSender.getInstance().log("    furnace-shift-click: " + getFurnaceShiftClick());
        MessageSender.getInstance().log("    multithreading: " + getMultithreading());
        MessageSender.getInstance().log("    fix-mod-results: " + getFixModResults());
        MessageSender.getInstance().log("    clear-recipes: " + getClearRecipes());
        MessageSender.getInstance().log("    update-check.enabled: " + getUpdateCheckEnabled());
        MessageSender.getInstance().log("    update-check.frequency: " + getUpdateCheckFrequency());
        MessageSender.getInstance().log("    material.fail: " + getFailMaterial());
        MessageSender.getInstance().log("    material.secret: " + getSecretMaterial());
        MessageSender.getInstance().log("    material.multiple-results: " + getMultipleResultsMaterial());
        MessageSender.getInstance().log("    disable-override-warnings: " + getDisableOverrideWarnings());
        MessageSender.getInstance().log("    recipe-comment-characters: " + getRecipeCommentCharacters());


        itemAliasesConfig = loadYML(Files.FILE_ITEM_ALIASES);

        if (!Files.LASTCHANGED_ITEM_ALIASES.equals(itemAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = Material.matchMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid material definition: " + arg);
                continue;
            }

            Object value = itemAliasesConfig.get(arg);

            if (value instanceof String) {
                parseMaterialNames(sender, (String) value, material);
            } else if (value instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) value;

                for (String key : section.getKeys(false)) {
                    if (key.equals("names")) {
                        parseMaterialNames(sender, section.getString(key), material);
                    } else {
                        try {
                            parseMaterialDataNames(sender, section.getString(key), Short.valueOf(key), material);
                        } catch (NumberFormatException e) {
                            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data value number: " + key + " for material: " + material);
                        }
                    }
                }
            } else {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has invalid data type at: " + arg);
            }
        }

        itemDatasConfig = loadYML(Files.FILE_ITEM_DATAS);

        if (!Files.LASTCHANGED_ITEM_DATAS.equals(itemDatasConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ITEM_DATAS + "' file is outdated, please delete it to allow it to be generated again.");
        }

        for (String arg : itemDatasConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Material material = getMaterial(arg);

            if (material == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_DATAS + "' has invalid material definition: " + arg);
                continue;
            }

            String value = itemDatasConfig.getString(arg);
            try {
                itemDatas.put(material, Short.valueOf(value));
            } catch (NumberFormatException e) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_DATAS + "' has invalid data value number: " + value + " for material: " + material);
            }
        }

        enchantAliasesConfig = loadYML(Files.FILE_ENCHANT_ALIASES);

        if (!Files.LASTCHANGED_ENCHANT_ALIASES.equals(enchantAliasesConfig.getString("lastchanged"))) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>NOTE: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' file is outdated, please delete it to allow it to be generated again.");
        }


        /* TODO remove
        for (Enchantment e : Enchantment.values()) {
            enchantNames.put(String.valueOf(e.getId()), e);
            enchantNames.put(Tools.parseAliasName(e.toString()), e); enchantPrint.put(e, Tools.parseAliasPrint(e.toString()));
        } */

        for (String arg : enchantAliasesConfig.getKeys(false)) {
            if (arg.equals("lastchanged")) {
                continue;
            }

            Enchantment enchant = Enchantment.getByName(arg.toUpperCase());

            if (enchant == null) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has invalid enchant definition: " + arg);
                continue;
            }

            String names = enchantAliasesConfig.getString(arg);
            String[] split = names.split(",");

            for (String str : split) {
                str = str.trim();
                String parsed = RMCUtil.parseAliasName(str);

                if (enchantNames.containsKey(parsed)) {
                    MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ENCHANT_ALIASES + "' has duplicate enchant alias '" + str + "' for enchant " + enchant);
                    continue;
                }

                enchantNames.put(parsed, enchant);

                if (!enchantPrint.containsKey(enchant)) {
                    enchantPrint.put(enchant, Tools.parseAliasPrint(str));
                }
            }
        }


        String failString = fileConfig.getString("material.fail", MATERIAL_FAIL_DEFAULT.toString());
        Material failMaterial = Material.matchMaterial(failString);
        if (failMaterial == null) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.fail has invalid material definition: " + failString + ". Defaulting to " + MATERIAL_FAIL_DEFAULT.toString() + ".");
        }

        String secretString = fileConfig.getString("material.secret", MATERIAL_SECRET_DEFAULT.toString());
        Material secretMaterial = Material.matchMaterial(secretString);
        if (secretMaterial == null) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.secret has invalid material definition: " + secretString + ". Defaulting to " + MATERIAL_SECRET_DEFAULT.toString() + ".");
        }

        String multipleResultsString = fileConfig.getString("material.multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());
        Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);
        if (multipleResultsMaterial == null) {
            MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + "material.multiple-results has invalid material definition: " + multipleResultsString + ". Defaulting to " + MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString() + ".");
        }
    }

    private void parseMaterialNames(CommandSender sender, String names, Material material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            String parsed = RMCUtil.parseAliasName(str);

            if (materialNames.containsKey(parsed)) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate material alias '" + str + "' for material " + material);
                continue;
            }

            materialNames.put(parsed, material);

            if (!materialPrint.containsKey(material)) {
                materialPrint.put(material, Tools.parseAliasPrint(str));
            }
        }
    }

    private void parseMaterialDataNames(CommandSender sender, String names, short data, Material material) {
        if (names == null) {
            return;
        }

        String[] split = names.split(",");

        for (String str : split) {
            str = str.trim();
            Map<String, Short> dataMap = materialDataNames.get(material);

            if (dataMap == null) {
                dataMap = new HashMap<>();
                materialDataNames.put(material, dataMap);
            }

            String parsed = RMCUtil.parseAliasName(str);

            if (dataMap.containsKey(parsed)) {
                MessageSender.getInstance().sendAndLog(sender, "<yellow>WARNING: <reset>'" + Files.FILE_ITEM_ALIASES + "' has duplicate data alias '" + str + "' for material " + material + " and data value " + data);
                continue;
            }

            dataMap.put(parsed, data);

            Map<Short, String> printMap = materialDataPrint.get(material);

            if (printMap == null) {
                printMap = new HashMap<>();
                materialDataPrint.put(material, printMap);
            }

            if (!printMap.containsKey(data)) {
                printMap.put(data, Tools.parseAliasPrint(str));
            }
        }
    }

    private static FileConfiguration loadYML(String fileName) {
        File file = new File(RecipeManager.getPlugin().getDataFolder() + File.separator + fileName);

        if (!file.exists()) {
            RecipeManager.getPlugin().saveResource(fileName, false);
            MessageSender.getInstance().log("Generated and loaded '" + fileName + "' file.");
        } else {
            MessageSender.getInstance().log("Loaded '" + fileName + "' file.");
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public boolean getSpecialRepair() {
        return fileConfig.getBoolean("special-recipes.repair", SPECIAL_REPAIR_DEFAULT);
    }

    public boolean getSpecialRepairMetadata() {
        return fileConfig.getBoolean("special-recipes.repair-metadata", SPECIAL_REPAIR_METADATA_DEFAULT);
    }

    public boolean getSpecialLeatherDye() {
        return fileConfig.getBoolean("special-recipes.leather-armor-dye", SPECIAL_LEATHER_DYE_DEFAULT);
    }

    public boolean getSpecialFireworks() {
        return fileConfig.getBoolean("special-recipes.fireworks", SPECIAL_FIREWORKS_DEFAULT);
    }

    public boolean getSpecialFireworkStar() {
        return fileConfig.getBoolean("special-recipes.firework-star", SPECIAL_FIREWORK_STAR_DEFAULT);
    }

    public boolean getSpecialMapCloning() {
        return fileConfig.getBoolean("special-recipes.map-cloning", SPECIAL_MAP_CLONING_DEFAULT);
    }

    public boolean getSpecialMapExtending() {
        return fileConfig.getBoolean("special-recipes.map-extending", SPECIAL_MAP_EXTENDING_DEFAULT);
    }

    public boolean getSpecialBookCloning() {
        return fileConfig.getBoolean("special-recipes.book-cloning", SPECIAL_BOOK_CLONING_DEFAULT);
    }

    public boolean getSpecialBanner() {
        return fileConfig.getBoolean("special-recipes.banner", SPECIAL_BANNER_DEFAULT);
    }

    public boolean getSpecialShieldBanner() {
        return fileConfig.getBoolean("special-recipes.shield-banner", SPECIAL_SHIELD_BANNER_DEFAULT);
    }

    public boolean getSpecialTippedArrows() {
        return fileConfig.getBoolean("special-recipes.tipped-arrows", SPECIAL_TIPPED_ARROWS_DEFAULT);
    }

    public boolean getSpecialShulkerDye() {
        return fileConfig.getBoolean("special-recipes.shulker-dye", SPECIAL_SHULKER_DYE_DEFAULT);
    }

    public boolean getSoundsRepair() {
        return fileConfig.getBoolean("sounds.repair", SOUNDS_REPAIR_DEFAULT);
    }

    public boolean getSoundsFailed() {
        return fileConfig.getBoolean("sounds.failed", SOUNDS_FAILED_DEFAULT);
    }

    public boolean getSoundsFailedClick() {
        return fileConfig.getBoolean("sounds.failed_click", SOUNDS_FAILED_CLICK_DEFAULT);
    }

    public boolean getFixModResults() {
        return fileConfig.getBoolean("fix-mod-results", FIX_MOD_RESULTS_DEFAULT);
    }

    public boolean getUpdateBooks() {
        return fileConfig.getBoolean("update-books", UPDATE_BOOKS_DEFAULT);
    }

    public boolean getColorConsole() {
        return fileConfig.getBoolean("color-console", COLOR_CONSOLE_DEFAULT);
    }

    public char getFurnaceShiftClick() {
        return fileConfig.getString("furnace-shift-click", FURNACE_SHIFT_CLICK_DEFAULT).charAt(0);
    }

    public boolean getMultithreading() {
        return fileConfig.getBoolean("multithreading", MULTITHREADING_DEFAULT);
    }

    public boolean getClearRecipes() {
        return fileConfig.getBoolean("clear-recipes", CLEAR_RECIPES_DEFAULT);
    }

    public boolean getUpdateCheckEnabled() {
        return fileConfig.getBoolean("update-check.enabled", UPDATE_CHECK_ENABLED_DEFAULT);
    }

    public int getUpdateCheckFrequency() {
        return Math.max(fileConfig.getInt("update-check.frequency", UPDATE_CHECK_FREQUENCY_DEFAULT), 0);
    }

    public Material getFailMaterial() {
        String failString = fileConfig.getString("material.fail", MATERIAL_FAIL_DEFAULT.toString());

        Material failMaterial = Material.matchMaterial(failString);

        if (failMaterial == null) {
            failMaterial = MATERIAL_FAIL_DEFAULT;
        }

        return failMaterial;
    }

    public Material getSecretMaterial() {
        String secretString = fileConfig.getString("material.secret", MATERIAL_SECRET_DEFAULT.toString());

        Material secretMaterial = Material.matchMaterial(secretString);

        if (secretMaterial == null) {
            secretMaterial = MATERIAL_SECRET_DEFAULT;
        }

        return secretMaterial;
    }

    public Material getMultipleResultsMaterial() {
        String multipleResultsString = fileConfig.getString("material.multiple-results", MATERIAL_MULTIPLE_RESULTS_DEFAULT.toString());

        Material multipleResultsMaterial = Material.matchMaterial(multipleResultsString);

        if (multipleResultsMaterial == null) {
            multipleResultsMaterial = MATERIAL_MULTIPLE_RESULTS_DEFAULT;
        }

        return multipleResultsMaterial;
    }

    public boolean getDisableOverrideWarnings() {
        return fileConfig.getBoolean("disable-override-warnings", DISABLE_OVERRIDE_WARNINGS_DEFAULT);
    }

    public String getRecipeCommentCharacters() {
        String allComments = "";
        List<String> comments = getRecipeCommentCharactersAsList();

        for (int i = 0; i < comments.size(); i++) {
            if (i > 0) {
                allComments += ",";
            }

            allComments += comments.get(i);
        }

        return allComments;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRecipeCommentCharactersAsList() {
        return (List<String>) fileConfig.getList("recipe-comment-characters", RECIPE_COMMENT_CHARACTERS_DEFAULT);
    }


    public Enchantment getEnchantment(String name) {
        return enchantNames.get(name);
    }

    public static Short getCustomData(Material material) {
        Short data = null;

        if (itemDatas.containsKey(material)) {
            data = itemDatas.get(material);
        }

        if (data == null) {
            data = material.getMaxDurability();
        }

        return data;
    }

    public Material getMaterial(String name) {
        return materialNames.get(RMCUtil.parseAliasName(name));
    }

    public Map<String, Short> getMaterialDataNames(Material material) {
        return materialDataNames.get(material);
    }

    public String getMaterialPrint(Material material) {
        return materialPrint.get(material);
    }

    public Map<Short, String> getMaterialDataPrint(Material material) {
        return materialDataPrint.get(material);
    }

    public String getEnchantPrint(Enchantment enchant) {
        return enchantPrint.get(enchant);
    }
}