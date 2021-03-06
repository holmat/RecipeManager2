package haveric.recipeManager.flag;

import haveric.recipeManager.*;
import haveric.recipeManager.messages.MessageSender;
import haveric.recipeManager.messages.Messages;
import haveric.recipeManager.messages.TestMessageSender;
import org.bukkit.Bukkit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.UUID;

import static haveric.recipeManager.Files.FILE_MESSAGES;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({Settings.class, MessageSender.class, Bukkit.class, RecipeManager.class})
public class FlagBaseTest {
    protected Settings settings;
    protected TestUnsafeValues unsafeValues;
    protected File workDir;
    protected String baseResourcesPath;
    protected String baseRecipePath;
    protected UUID testUUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Before
    public void setupBase() {
        mockStatic(Bukkit.class);
        unsafeValues = new TestUnsafeValues();
        when(Bukkit.getUnsafe()).thenReturn(unsafeValues);

        mockStatic(Settings.class);
        settings = mock(Settings.class);
        when(Settings.getInstance()).thenReturn(settings);
        when(settings.getMultithreading()).thenReturn(false);

        mockStatic(MessageSender.class);
        when(MessageSender.getInstance()).thenReturn(TestMessageSender.getInstance());


        TestItemFactory itemFactory = new TestItemFactory();
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);

        TestOfflinePlayer player = new TestOfflinePlayer();
        when(Bukkit.getOfflinePlayer(testUUID)).thenReturn(player);

        new FlagLoader();
        FlagFactory.getInstance().init();

        File baseSrcDir = new File("src");
        String baseSrcPath = baseSrcDir.getAbsolutePath().replace(".idea\\modules\\", "") + "/";
        String baseTestPath = baseSrcPath + "test/";

        workDir = new File(baseTestPath + "work/");
        workDir.delete();
        workDir.mkdirs();

        baseResourcesPath = baseTestPath + "resources/";
        baseRecipePath = baseResourcesPath + "recipes/";

        File messagesFile = new File(baseSrcPath + "/main/resources/" + FILE_MESSAGES);
        Messages.getInstance().loadMessages(null, messagesFile);

        Recipes recipes = new Recipes();

        mockStatic(RecipeManager.class);
        when(RecipeManager.getRecipes()).thenReturn(recipes);
    }
}
