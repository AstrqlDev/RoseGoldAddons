package rosegoldaddons;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import rosegoldaddons.commands.*;
import rosegoldaddons.features.*;
import rosegoldaddons.utils.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Mod(modid = "timechanger", name = "RoseGoldAddons", version = "2.1")
public class Main {
    public static GuiScreen display = null;
    public static Config configFile = Config.INSTANCE;
    public static KeyBinding[] keyBinds = new KeyBinding[20];
    public static boolean endermanMacro = false;
    public static boolean powderMacro = false;
    public static boolean AOTSMacro = false;
    public static boolean SoulWhipMacro = false;
    public static boolean GhostMacro = false;
    public static boolean gemNukeToggle = false;
    public static boolean mithrilNuker = false;
    public static boolean autoUseItems = false;
    public static boolean autoHardStone = false;
    public static boolean forageOnIsland = false;
    public static boolean necronAimbot = false;
    public static boolean bloodTriggerBot = false;
    public static boolean brewingMacro = false;
    public static boolean nukeCrops = false;
    public static boolean nukeWood = false;
    public static boolean placeCane = false;
    private static boolean firstLoginThisSession = true;
    private static boolean oldanim = false;
    public static boolean init = false;
    public static boolean mithrilMacro = false;
    private boolean issue = false;
    public static boolean pauseCustom = false;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static JsonObject rga;

    String info = "Hello decompiler! this is just some funny stuff, you do not have to worry about it!";
    private String[] cumsters = null;
    private String[] ILILILLILILLILILILL = null;

    public static HashMap<String, String> nameCache = new HashMap<>();
    public static HashMap<String, String> rankCache = new HashMap<>();
    public static ArrayList<String> hashedCache = new ArrayList<>();
    public static HashMap<String, String> names = new HashMap<>();
    public static HashMap<String, String> ranks = new HashMap<>();

    @Mod.EventHandler
    public void onFMLInitialization(FMLPreInitializationEvent event) {
        File directory = new File(event.getModConfigurationDirectory(), "rosegoldaddons");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            rga = getJson("https://gist.githubusercontent.com/RoseGoldIsntGay/2d15ef10d53629455a40f5c027db1dfb/raw/").getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            issue = true;
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (issue) return;
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new AutoReady());
        MinecraftForge.EVENT_BUS.register(new OpenSkyblockGui());
        MinecraftForge.EVENT_BUS.register(new EndermanMacro());
        MinecraftForge.EVENT_BUS.register(new AutoArrowAlign());
        MinecraftForge.EVENT_BUS.register(new PowderMacro());
        MinecraftForge.EVENT_BUS.register(new SwordSwapping());
        MinecraftForge.EVENT_BUS.register(new PartyUntransfer());
        MinecraftForge.EVENT_BUS.register(new GhostMacro());
        MinecraftForge.EVENT_BUS.register(new CustomItemMacro());
        MinecraftForge.EVENT_BUS.register(new HardstoneAura());
        MinecraftForge.EVENT_BUS.register(new ForagingIslandMacro());
        MinecraftForge.EVENT_BUS.register(new NecronAimbot());
        MinecraftForge.EVENT_BUS.register(new BloodTriggerBot());
        MinecraftForge.EVENT_BUS.register(new EntityReach());
        MinecraftForge.EVENT_BUS.register(new GemstoneAura());
        MinecraftForge.EVENT_BUS.register(new PingWorldChange());
        MinecraftForge.EVENT_BUS.register(new BrewingMacro());
        MinecraftForge.EVENT_BUS.register(new CropNuker());
        MinecraftForge.EVENT_BUS.register(new SexAura());
        MinecraftForge.EVENT_BUS.register(new MithrilNuker());
        MinecraftForge.EVENT_BUS.register(new ForagingNuker());
        MinecraftForge.EVENT_BUS.register(new AutoSlayer());
        MinecraftForge.EVENT_BUS.register(new PlayerUtils());
        MinecraftForge.EVENT_BUS.register(new ArmorStandESPs());
        MinecraftForge.EVENT_BUS.register(new PinglessMining());
        MinecraftForge.EVENT_BUS.register(new MithrilMacro());
        MinecraftForge.EVENT_BUS.register(new AutoGhostBlock());
        MinecraftForge.EVENT_BUS.register(new ShadyRotation());
        MinecraftForge.EVENT_BUS.register(new DungeonESP());
        MinecraftForge.EVENT_BUS.register(new ScoreboardUtils());
        configFile.initialize();
        ClientCommandHandler.instance.registerCommand(new OpenSettings());
        ClientCommandHandler.instance.registerCommand(new Rosedrobe());
        ClientCommandHandler.instance.registerCommand(new LobbySwap());
        ClientCommandHandler.instance.registerCommand(new Backpack());
        ClientCommandHandler.instance.registerCommand(new UseCooldown());
        ClientCommandHandler.instance.registerCommand(new Rosepet());
        ClientCommandHandler.instance.registerCommand(new AllEntities());
        ClientCommandHandler.instance.registerCommand(new SexPlayer());

        JsonArray funnynames = rga.get("funnynames").getAsJsonArray();
        cumsters = new String[funnynames.size()];
        Iterator<JsonElement> fn = funnynames.iterator();
        int count = 0;
        while(fn.hasNext()) {
            JsonElement name = fn.next();
            cumsters[count] = name.getAsString();
            count++;
        }
        JsonArray funnymessages = rga.get("funnymessages").getAsJsonArray();
        ILILILLILILLILILILL = new String[funnymessages.size()];
        Iterator<JsonElement> fm = funnymessages.iterator();
        count = 0;
        while(fm.hasNext()) {
            JsonElement message = fm.next();
            ILILILLILILLILILILL[count] = message.getAsString();
            count++;
        }

        JsonObject replacions = rga.get("replacions").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> set = replacions.entrySet();

        set.forEach(stringJsonElementEntry -> {
            names.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString().replace("&", "§"));
            System.out.println(stringJsonElementEntry.getKey()+": "+stringJsonElementEntry.getValue().getAsString().replace("&", "§"));
        });

        replacions = rga.get("ranks").getAsJsonObject();
        set = replacions.entrySet();

        set.forEach(stringJsonElementEntry -> {
            ranks.put(stringJsonElementEntry.getKey(), stringJsonElementEntry.getValue().getAsString().replace("&", "§"));
            System.out.println(stringJsonElementEntry.getKey()+": "+stringJsonElementEntry.getValue().getAsString().replace("&", "§"));
        });
        init = true;

        try {
            Reader reader = Files.newBufferedReader(Paths.get("./config/rosegoldaddons/rcmacros.json"));
            int data = reader.read();
            String str = "";
            while(data != -1) {
                str += (char) data;
                data = reader.read();
            }
            str = str.replace("\"","").replace("{","").replace("}","");
            String[] arr = str.split(",");
            for(int i = 0; i < arr.length; i++) {
                String[] arr2 = arr[i].split(":");
                System.out.println(arr2[0]+" "+arr2[1]);
                UseCooldown.RCitems.put(arr2[0], Integer.parseInt(arr2[1]));
            }
            reader.close();
            Reader reader2 = Files.newBufferedReader(Paths.get("./config/rosegoldaddons/lcmacros.json"));
            int data2 = reader2.read();
            String str2 = "";
            while(data2 != -1) {
                str2 += (char) data2;
                data2 = reader.read();
            }
            str2 = str2.replace("\"","").replace("{","").replace("}","");
            String[] arr3 = str2.split(",");
            for(int i = 0; i < arr3.length; i++) {
                String[] arr4 = arr3[i].split(":");
                System.out.println(arr4[0]+" "+arr4[1]);
                UseCooldown.LCitems.put(arr4[0], Integer.parseInt(arr4[1]));
            }
            reader2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        keyBinds[0] = new KeyBinding("Custom Item Macro Toggle", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[1] = new KeyBinding("Toggle Enderman Macro", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[2] = new KeyBinding("Arrow Align Aura", Keyboard.KEY_NONE, "RoseGoldAddons - Dungeons");
        keyBinds[3] = new KeyBinding("Powder Macro Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Mining");
        keyBinds[4] = new KeyBinding("AOTS SS Toggle", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[5] = new KeyBinding("Soul Whip SS Toggle", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[6] = new KeyBinding("Ghost Macro Toggle", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[7] = new KeyBinding("Gemstone Nuker Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Mining");
        keyBinds[8] = new KeyBinding("Hardstone Nuker Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Mining");
        keyBinds[9] = new KeyBinding("Foraging Island Macro", Keyboard.KEY_NONE, "RoseGoldAddons - Foraging");
        keyBinds[10] = new KeyBinding("Necron Aimbot Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Dungeons");
        keyBinds[11] = new KeyBinding("Blood Room Triggerbot Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Dungeons");
        keyBinds[12] = new KeyBinding("Brewing Macro Toggle", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[13] = new KeyBinding("Open Trades Menu", Keyboard.KEY_NONE, "RoseGoldAddons");
        keyBinds[14] = new KeyBinding("Crop Nuker Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Farming");
        keyBinds[15] = new KeyBinding("Mithril Nuker Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Mining");
        keyBinds[16] = new KeyBinding("Foraging Nuker Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Foraging");
        keyBinds[17] = new KeyBinding("Cane Placer Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Farming");
        keyBinds[18] = new KeyBinding("Mithril Macro Toggle", Keyboard.KEY_NONE, "RoseGoldAddons - Mining");
        keyBinds[19] = new KeyBinding("Peek Custom Names", Keyboard.KEY_NONE, "RoseGoldAddons");

        for (KeyBinding keyBind : keyBinds) {
            ClientRegistry.registerKeyBinding(keyBind);
        }
    }

    @Mod.EventHandler
    public void post(FMLPostInitializationEvent event) {
        Loader.instance().getActiveModList().forEach(modContainer -> {
            if (modContainer.getModId().equals("oldanimations")) {
                oldanim = true;
            }
        });
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        if(forageOnIsland || nukeWood || nukeCrops || mithrilNuker || gemNukeToggle || mithrilMacro) {
            ChatUtils.sendMessage("§cDetected World Change, Stopping All Macros");
            forageOnIsland = false;
            nukeWood = false;
            nukeCrops = false;
            mithrilNuker = false;
            gemNukeToggle = false;
            mithrilMacro = false;
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if(mc.thePlayer == null || mc.theWorld == null) return;
        if(firstLoginThisSession) {
            ChatComponentText msg1 = new ChatComponentText("§0§7Thanks to ShadyAddons:§b https://cheatersgetbanned.me");
            msg1.setChatStyle(ChatUtils.createClickStyle(ClickEvent.Action.OPEN_URL, "https://cheatersgetbanned.me"));
            ChatComponentText msg2 = new ChatComponentText("§0§7Thanks to Harry282 (SBClient):§b https://github.com/Harry282/Skyblock-Client");
            msg2.setChatStyle(ChatUtils.createClickStyle(ClickEvent.Action.OPEN_URL, "https://github.com/Harry282/Skyblock-Client"));
            ChatComponentText msg3 = new ChatComponentText("§0§7Thanks to pizza boy (Pizza Client):§b https://github.com/PizzaboiBestLegit/Pizza-Client");
            msg3.setChatStyle(ChatUtils.createClickStyle(ClickEvent.Action.OPEN_URL, "https://github.com/PizzaboiBestLegit/Pizza-Client"));
            ChatComponentText msg4 = new ChatComponentText("§0§7Check out the RoseGoldAddons §bDiscord Server!");
            msg4.setChatStyle(ChatUtils.createClickStyle(ClickEvent.Action.OPEN_URL, "https://discord.gg/Tmk2hwzdxm"));
            mc.thePlayer.addChatMessage(msg1);
            mc.thePlayer.addChatMessage(msg2);
            mc.thePlayer.addChatMessage(msg3);
            mc.thePlayer.addChatMessage(msg4);
            if(oldanim) {
                ChatUtils.sendMessage("§l§4Old Animations Mod was detected in your mods folder. This mod breaks some key RoseGoldAddons features, please uninstall it before asking for support as it is known to cause a lot of issues. Thanks.");
            }
            firstLoginThisSession = false;
        }
        if(mc.gameSettings.limitFramerate == 1) {
            mc.gameSettings.setOptionFloatValue(GameSettings.Options.FRAMERATE_LIMIT, 260.0F);
        }
        if (display != null) {
            try {
                mc.displayGuiScreen(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
            display = null;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        pauseCustom = keyBinds[19].isKeyDown();
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        int rnd = new Random().nextInt(configFile.skiblock);
        if(rnd == 0 && configFile.funnyStuff) {
            mc.thePlayer.addChatMessage(new ChatComponentText((cumsters[new Random().nextInt(cumsters.length)].replace("&","§")+"§7: "+i(ILILILLILILLILILILL[new Random().nextInt(ILILILLILILLILILILL.length)]))));
        }
        if (keyBinds[0].isPressed()) {
            autoUseItems = !autoUseItems;
            String str = autoUseItems ? "§aCustom Item Macro Activated" : "§cCustom Item Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[1].isPressed()) {
            endermanMacro = !endermanMacro;
            String str = endermanMacro ? "§aZealot Macro Activated" : "§cZealot Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[2].isPressed()) {
            AutoArrowAlign.cheat();
        } else if (keyBinds[3].isPressed()) {
            powderMacro = !powderMacro;
            String str = powderMacro ? "§aPowder Macro Activated" : "§cPowder Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[4].isPressed()) {
            AOTSMacro = !AOTSMacro;
            String str = AOTSMacro ? "§aAOTS Macro Activated" : "§cAOTS Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[5].isPressed()) {
            SoulWhipMacro = !SoulWhipMacro;
            String str = SoulWhipMacro ? "§aSoul Whip Macro Activated" : "§cSoul Whip Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[6].isPressed()) {
            GhostMacro = !GhostMacro;
            String str = GhostMacro ? "§aGhost Macro Activated" : "§cGhost Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[7].isPressed()) {
            gemNukeToggle = !gemNukeToggle;
            String str = gemNukeToggle ? "§aGemstone Nuke Activated" : "§cGemstone Nuke Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[8].isPressed()) {
            autoHardStone = !autoHardStone;
            String str = autoHardStone ? "§aHardstone Nuker Activated" : "§cHardstone Nuker Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[9].isPressed()) {
            forageOnIsland = !forageOnIsland;
            String str = forageOnIsland ? "§aForaging Macro Activated" : "§cForaging Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[10].isPressed()) {
            necronAimbot = !necronAimbot;
            String str = necronAimbot ? "§aNecron Aimbot Activated" : "§cNecron Aimbot Deactivated";
            ChatUtils.sendMessage(str);
        } else if (keyBinds[11].isPressed()) {
            bloodTriggerBot = !bloodTriggerBot;
            String str = bloodTriggerBot ? "§aBlood Room Triggerbot Activated" : "§cBlood Room Triggerbot Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[12].isPressed()) {
            brewingMacro = !brewingMacro;
            String str = brewingMacro ? "§aBrewing Macro Activated" : "§cBrewing Macro Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[13].isPressed()) {
            OpenSkyblockGui.openTradesMenu();
        } else if(keyBinds[14].isPressed()) {
            nukeCrops = !nukeCrops;
            String str = nukeCrops ? "§aCrop Nuker Activated" : "§cCrop Nuker Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[15].isPressed()) {
            mithrilNuker = !mithrilNuker;
            String str = mithrilNuker ? "§aMithril Nuker Activated" : "§cMithril Nuker Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[16].isPressed()) {
            nukeWood = !nukeWood;
            String str = nukeWood ? "§aForaging Nuker Activated" : "§cForaging Nuker Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[17].isPressed()) {
            placeCane = !placeCane;
            String str = placeCane ? "§aCane Placer Activated" : "§cCane Placer Deactivated";
            ChatUtils.sendMessage(str);
        } else if(keyBinds[18].isPressed()) {
            mithrilMacro = !mithrilMacro;
            String str = mithrilMacro ? "§aMithril Macro Activated" : "§cMithril Macro Deactivated";
            ChatUtils.sendMessage(str);
        }
    }

    public static String i(String s) {
        String str = "Error Getting Abraham Lincoln Quote. Check Your Internet Connection And Try Again.";
        try {
            str = new String(Base64.getDecoder().decode(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static JsonElement getJson(String jsonUrl) {
        return (new JsonParser()).parse(Objects.requireNonNull(getInputStream(jsonUrl)));
    }

    public static InputStreamReader getInputStream(String url) {
        try {
            URLConnection conn = (new URL(url)).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            return new InputStreamReader(conn.getInputStream());
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }


    private static String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }

            bufferedReader.close();
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return content.toString();
    }

}
