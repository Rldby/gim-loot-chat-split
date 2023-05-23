package com.rldby;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
        name = "Group Chat Loot"
)
public class GroupChatLootPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private GroupChatLootConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ItemManager itemManager;

    @Inject
    private GroupLootHistoryOverlay lootOverlay;

    @Getter
    private GroupLootSession session;

    @Getter
    private List<Pattern> chatIgnorePatterns;

    @Getter
    private List<Pattern> overlayIgnorePatterns;

    @Provides
    GroupChatLootConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GroupChatLootConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        if (config.enableHistoryOverlay())
        {
            overlayManager.add(lootOverlay);
        }

        session = new GroupLootSession();
        RefreshChatboxIgnoredItemPatterns();
        RefreshOverlayIgnoredItemPatterns();
        RefreshChatBox();
    }

    @Override
    protected void shutDown() throws Exception
    {
        if (config.enableHistoryOverlay())
        {
            overlayManager.remove(lootOverlay);
        }

        session = null;
        RefreshChatBox();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN ||
                gameStateChanged.getGameState() == GameState.HOPPING)
        {
            if (session != null && session.getLastDrops().size() > 0)
            {
                session.getLastDrops().clear();
            }
        }
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event)
    {
        if ("chatFilterCheck".equals(event.getEventName()))
        {
            ProcessChatMessage();
        }
    }

    private void ProcessChatMessage()
    {
        GroupLootDrop drop = GroupLootDrop.FromCurrentMessageInStack(client);

        if (drop != null)
        {
            session.LogDrop(drop);

            if (config.hideChatLines() ||
                    drop.getItemValue() < config.chatMinValue() ||
                    drop.NameMatchesAnyPattern(chatIgnorePatterns))
            {
                int[] intStack = client.getIntStack();
                int intStackSize = client.getIntStackSize();

                intStack[intStackSize - 3] = 0;
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (Objects.equals(event.getGroup(), "grouplootchatsplit"))
        {
            if (Objects.equals(event.getKey(), "hideChatLines") || Objects.equals(event.getKey(), "chatMinValue"))
            {
                RefreshChatBox();
            }
            else if (Objects.equals(event.getKey(), "enableHistoryOverlay"))
            {
                if (config.enableHistoryOverlay())
                {
                    overlayManager.add(lootOverlay);
                }
                else
                {
                    overlayManager.remove(lootOverlay);
                }
            }
            else if (Objects.equals(event.getKey(), "overlayIgnoredItems"))
            {
                RefreshOverlayIgnoredItemPatterns();
            }
            else if (Objects.equals(event.getKey(), "chatIgnoredItems"))
            {
                RefreshChatboxIgnoredItemPatterns();
                RefreshChatBox();
            }
        }
    }

    private void RefreshChatBox()
    {
        clientThread.invoke(() -> client.runScript(ScriptID.SPLITPM_CHANGED));
    }

    private void RefreshChatboxIgnoredItemPatterns()
    {
        chatIgnorePatterns = CompilePatternsFromString(config.chatIgnoredItems());
    }

    private void RefreshOverlayIgnoredItemPatterns()
    {
        overlayIgnorePatterns = CompilePatternsFromString(config.overlayIgnoredItems());
    }

    private List<Pattern> CompilePatternsFromString(String input)
    {
        String[] ignoredItems = null;

        if (input != null && !input.trim().isEmpty())
        {
            ignoredItems = input.split(",");
        }

        List<Pattern> patterns = new ArrayList<>();

        if (ignoredItems != null && ignoredItems.length > 0)
        {
            for (String ignoredItem : ignoredItems)
            {
                if (!ignoredItem.trim().isEmpty())
                {
                    patterns.add(
                            Pattern.compile(
                                    ignoredItem.trim().replace("*", ".*"),
                                    Pattern.CASE_INSENSITIVE));
                }
            }

            return patterns;
        }

        return null;
    }
}
