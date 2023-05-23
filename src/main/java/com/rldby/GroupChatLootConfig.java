package com.rldby;

import lombok.AllArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("groupchatloot")
public interface GroupChatLootConfig extends Config
{
    @AllArgsConstructor
    enum PluginFonts {
        NORMAL("Runescape", "Normal"),
        SMALL("Runescape Small", "Small");


        private final String fontName;
        private final String description;

        public String getFontName()
        {
            return fontName;
        }

        @Override
        public String toString()
        {
            return description;
        }
    }

    @AllArgsConstructor
    enum HistoryOverlayLineExpiry {
        NEVER(0, "Never"),
        THREE_SECONDS(3, "3 seconds"),
        FIVE_SECONDS(5, "5 seconds"),
        SEVEN_SECONDS(7, "7 seconds"),
        TEN_SECONDS(10, "10 seconds"),
        FIFTEEN_SECONDS(15, "15 seconds"),
        THIRTY_SECONDS(30, "30 seconds"),
        ONE_MINUTE(60, "1 minute"),
        TWO_MINUTES(60 * 2, "2 minutes"),
        FIVE_MINUTES(60 * 5, "5 minutes"),
        TEN_MINUTES(60 * 10, "10 minutes"),
        FIFTEEN_MINUTES(60 * 15, "15 minutes"),
        TWENTY_MINUTES(60 * 20, "20 minutes"),
        THIRTY_MINUTES(60 * 30, "30 minutes"),
        ONE_HOUR(60 * 60, "1 hour"),
        TWO_HOURS(60 * 60 * 2, "2 hours");


        private final int seconds;
        private final String description;

        @Override
        public String toString()
        {
            return description;
        }

        public boolean hasExpiry()
        {
            return seconds > 0;
        }

        public int getSeconds()
        {
            return seconds;
        }
    }

    @ConfigSection(
            name = "Chatbox Configuration",
            description = "Configure how the plugin handles loot chatbox messages.",
            position = 80
    )
    String chatConfigSection = "chatConfigSection";

    @ConfigItem(
            position = 15,
            keyName = "hideChatLines",
            name = "Hide all drops in chatbox",
            description = "Enabling this option will prevent group drop messages from being shown in the chatbox.",
            section = chatConfigSection
    )
    default boolean hideChatLines() {
        return true;
    }

    @ConfigItem(
            position = 20,
            keyName = "chatMinValue",
            name = "Minimum price",
            description = "Configures the minimum price an item must be for it to appear in the chatbox.",
            section = chatConfigSection
    )
    default int chatMinValue() {
        return 0;
    }

    @ConfigItem(
            position = 30,
            keyName = "chatIgnoredItems",
            name = "Ignored items",
            description = "Enter a comma separated list of item names to be excluded from the chatbox. Asterisk wildcard characters may be used. E.g. 'ashes,*bones'. A blank value disables any filtering based on item name.",
            section = chatConfigSection
    )
    default String chatIgnoredItems() {
        return "";
    }

    @ConfigSection(
            name = "Overlay Configuration",
            description = "Configure the plugin's overlay.",
            position = 90
    )
    String overlayConfigSection = "overlayConfigSection";

    @ConfigItem(
            position = 5,
            keyName = "enableHistoryOverlay",
            name = "Enable overlay",
            description = "Enables an overlay which displays recent group drops.",
            section = overlayConfigSection
    )
    default boolean enableHistoryOverlay() {
        return true;
    }

    @Range(
            min = 1,
            max = 15
    )
    @ConfigItem(
            position = 10,
            keyName = "numberToDisplay",
            name = "Maximum lines",
            description = "Controls the number of most recent drops that will appear in the overlay",
            section = overlayConfigSection
    )
    default int numberToDisplay() {
        return 7;
    }

    @ConfigItem(
            position = 16,
            keyName = "historyOverlayMinValue",
            name = "Minimum price",
            description = "Configures the minimum price for a drop to be shown in the overlay.",
            section = overlayConfigSection
    )
    default int historyOverlayMinValue() {
        return 0;
    }

    //HistoryOverlayLineExpiry

    @ConfigItem(
            position = 17,
            keyName = "historyOverlayExpiration",
            name = "Drop expiration",
            description = "Configures the maximum length of time that a drop can appear on the overlay.",
            section = overlayConfigSection
    )
    default HistoryOverlayLineExpiry historyOverlayExpiration() {
        return HistoryOverlayLineExpiry.TEN_MINUTES;
    }

    @ConfigItem(
            position = 18,
            keyName = "historyOverlayIncludePrices",
            name = "Display prices",
            description = "Disabling this option will stop the overlay from showing item price values alongside item names.",
            section = overlayConfigSection
    )
    default boolean historyOverlayIncludePrices() {
        return true;
    }

    @ConfigItem(
            position = 19,
            keyName = "reverseOrder",
            name = "Order new drops at top",
            description = "Enabling this option will order the overlay so that newer drops appear at the top instead of the bottom.",
            section = overlayConfigSection
    )
    default boolean reverseOrder() {
        return false;
    }

    @ConfigItem(
            position = 35,
            keyName = "timestampFormat",
            name = "Timestamp format",
            description = "Configures the format for timestamps in the overlay. A blank value disables timestamps.",
            section = overlayConfigSection
    )
    default String timestampFormat() {
        return "[HH:mm]";
    }

    @ConfigItem(
            position = 40,
            keyName = "overlayIgnoredItems",
            name = "Ignored items",
            description = "Enter a comma separated list of item names to be excluded from the overlay. Asterisk wildcard characters may be used. E.g. 'ashes,*bones'. A blank value disables any filtering based on item name.",
            section = overlayConfigSection
    )
    default String overlayIgnoredItems() {
        return "";
    }

    @ConfigSection(
            name = "Overlay Appearance",
            description = "Configure how the plugin's overlay appears.",
            position = 97,
            closedByDefault = true
    )
    String overlayAppearanceSection = "overlayAppearanceSection";

    @ConfigItem(
            position = 20,
            keyName = "fontSize",
            name = "Font size",
            description = "Default: 16",
            section = overlayAppearanceSection
    )
    default int fontSize() {
        return 16;
    }

    @ConfigItem(
            position = 30,
            keyName = "fontFamily",
            name = "Font family",
            description = "Default: Small",
            section = overlayAppearanceSection
    )
    default PluginFonts fontFamily() {
        return PluginFonts.SMALL;
    }

    @Alpha
    @ConfigItem(
            position = 40,
            keyName = "overlayTextColor",
            name = "Text colour",
            description = "Configures the text colour for the overlay.",
            section = overlayAppearanceSection
    )
    default Color overlayTextColor() {
        return new Color(255, 255, 255, 255);
    }

    @Alpha
    @ConfigItem(
            position = 50,
            keyName = "overlayBackColor",
            name = "Background colour",
            description = "Configures the background colour for the overlay.",
            section = overlayAppearanceSection
    )
    default Color overlayBackColor() {
        return new Color(0, 0, 0, 100);
    }

    @ConfigItem(
            position = 60,
            keyName = "historyOverlayTextShadowEnabled",
            name = "Text shadow",
            description = "If enabled, text on the overlay will have a shadow.",
            section = overlayAppearanceSection
    )
    default boolean historyOverlayTextShadowEnabled() {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 70,
            keyName = "historyOverlayTextShadowColour",
            name = "Text shadow colour",
            description = "Configures the shadow colour for text on the overlay.",
            section = overlayAppearanceSection
    )
    default Color historyOverlayTextShadowColour() {
        return new Color(0, 0, 0, 255);
    }

    @Range(
            max = 16
    )
    @ConfigItem(
            position = 80,
            keyName = "historyOverlayPadding",
            name = "Padding",
            description = "Configures the amount of padding in the overlay.",
            section = overlayAppearanceSection
    )
    default int historyOverlayPadding() {
        return 6;
    }

    @Range(
            max = 10
    )
    @ConfigItem(
            position = 90,
            keyName = "historyOverlayRowSpacing",
            name = "Row spacing",
            description = "Configures the amount of vertical space between each drop displayed in the overlay.",
            section = overlayAppearanceSection
    )
    default int historyOverlayRowSpacing()
    {
        return 2;
    }

    @ConfigSection(
            name = "Overlay Price Formatting",
            description = "Configure how the plugin's overlay formats item names and values based on price.",
            position = 98,
            closedByDefault = true
    )
    String valueFormattingSection = "valueFormattingSection";

    @ConfigItem(
            position = 5,
            keyName = "applyValueFormatting",
            name = "Enable price formatting",
            description = "Enables colour formatting of items and prices in the overlay, based on a drop's price.",
            section = valueFormattingSection
    )
    default boolean applyValueFormatting()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
            position = 7,
            keyName = "priceFormat_junkValueColor",
            name = "Junk value colour",
            description = "Configures the text colour for drops that do not meet any of the price value tiers.",
            section = valueFormattingSection
    )
    default Color priceFormat_junkValueColor() {
        return new Color(180, 180, 180, 255);
    }

    @ConfigItem(
            position = 8,
            keyName = "priceFormat_normalValuePrice",
            name = "Normal value price",
            description = "Configures the start price for normal value items",
            section = valueFormattingSection
    )
    default int priceFormat_normalValuePrice() {
        return 1_000;
    }

    @Alpha
    @ConfigItem(
            position = 9,
            keyName = "priceFormat_normalValueColor",
            name = "Normal value colour",
            description = "Configures the text colour for normal value drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_normalValueColor() {
        return new Color(255, 255, 255, 255);
    }

    @ConfigItem(
            position = 10,
            keyName = "priceFormat_t1ValuePrice",
            name = "Tier 1 min. price",
            description = "Configures the start price for tier 1 valued items.",
            section = valueFormattingSection
    )
    default int priceFormat_t1ValuePrice() {
        return 10_000;
    }

    @Alpha
    @ConfigItem(
            position = 12,
            keyName = "priceFormat_t1ValueColor",
            name = "Tier 1 colour",
            description = "Configures the text colour for tier 1 valued drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_t1ValueColor() {
        return new Color(55, 194, 21, 255);
    }

    @ConfigItem(
            position = 20,
            keyName = "priceFormat_t2ValuePrice",
            name = "Tier 2 min. price",
            description = "Configures the start price for tier 2 valued drops.",
            section = valueFormattingSection
    )
    default int priceFormat_t2ValuePrice() {
        return 100_000;
    }

    @Alpha
    @ConfigItem(
            position = 25,
            keyName = "priceFormat_t2ValueColor",
            name = "Tier 2 colour",
            description = "Configures the text colour for tier 2 valued drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_t2ValueColor() {
        return new Color(0x65, 0x7F, 0xD7, 255);
    }


    @ConfigItem(
            position = 30,
            keyName = "priceFormat_t3ValuePrice",
            name = "Tier 3 min. price",
            description = "Configures the start price for tier 3 valued drops.",
            section = valueFormattingSection
    )
    default int priceFormat_t3ValuePrice() {
        return 1_000_000;
    }

    @Alpha
    @ConfigItem(
            position = 35,
            keyName = "priceFormat_t3ValueColor",
            name = "Tier 3 colour",
            description = "Configures the text colour for tier 3 valued drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_t3ValueColor() {
        return new Color(161, 50, 218, 255);
    }

    @ConfigItem(
            position = 40,
            keyName = "priceFormat_t4ValuePrice",
            name = "Tier 4 min. price",
            description = "Configures the start price for tier 4 valued drops.",
            section = valueFormattingSection
    )
    default int priceFormat_t4ValuePrice() {
        return 10_000_000;
    }

    @Alpha
    @ConfigItem(
            position = 45,
            keyName = "priceFormat_t4ValueColor",
            name = "Tier 4 colour",
            description = "Configures the text colour for tier 4 valued drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_t4ValueColor() {
        return new Color(187, 211, 11, 255);
    }

    @ConfigItem(
            position = 50,
            keyName = "priceFormat_t5ValuePrice",
            name = "Tier 5 min. price",
            description = "Configures the start price for tier 5 valued drops.",
            section = valueFormattingSection
    )
    default int priceFormat_t5ValuePrice() {
        return 100_000_000;
    }

    @Alpha
    @ConfigItem(
            position = 55,
            keyName = "priceFormat_t5ValueColor",
            name = "Tier 5 colour",
            description = "Configures the text colour for tier 5 valued drops.",
            section = valueFormattingSection
    )
    default Color priceFormat_t5ValueColor() {
        return new Color(229, 142, 9, 255);
    }
}
