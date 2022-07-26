package rosegoldaddons.features;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import rosegoldaddons.Main;
import rosegoldaddons.utils.ChatUtils;
import rosegoldaddons.utils.DiscordWebhook;

import java.io.IOException;

public class PingWorldChange {
    private int countdown = 0;

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        if(Main.configFile.pingworldchange && countdown == 0) {
            String url = Main.configFile.hookurl;
            String id = Main.configFile.discordid;
            if(url.contains("https://") && (url.contains("discord.com/api/webhooks/") || url.contains("discordapp.com/api/webhooks/"))) {
                if(!id.equals("")) {
                    DiscordWebhook webhook = new DiscordWebhook(url);
                    webhook.setContent("<@" + id + "> Detected World Change.");
                    try {
                        countdown = 100;
                        webhook.execute();
                    } catch (IOException e) {
                        ChatUtils.sendMessage("Ping sender seemed to have crashed: "+e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    ChatUtils.sendMessage("Invalid Discord ID");
                }
            } else {
                ChatUtils.sendMessage("Invalid Webhook URL");
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            if(countdown > 0) {
                countdown--;
            }
        }
    }
}
