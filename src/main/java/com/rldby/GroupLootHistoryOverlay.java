package com.rldby;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
class GroupLootHistoryOverlay extends Overlay
{
    private static final int nameItemSpacing = 3;

    private final GroupChatLootPlugin plugin;
    private final GroupChatLootConfig config;


    @Inject
    private GroupLootHistoryOverlay(GroupChatLootPlugin plugin, GroupChatLootConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        setPriority(OverlayPriority.LOW);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        GroupLootSession session = plugin.getSession();

        if (session != null && session.getLastDrops().size() > 0)
        {
            List<GroupLootDrop> dropsToRender = getDropsToShow(session);

            if (dropsToRender.size() > 0)
            {
                int padding = config.historyOverlayPadding();
                int rowSpacing = config.historyOverlayRowSpacing();
                SimpleDateFormat timestampFormatter = getFormatter(config);
                Font userFont = new Font(config.fontFamily().getFontName(), Font.PLAIN, config.fontSize());
                FontMetrics fontMetrics = graphics.getFontMetrics(userFont);

                graphics.setFont(userFont);

                MeasuredGroupLootList measuredDrops = new MeasuredGroupLootList(
                        dropsToRender, config, fontMetrics, timestampFormatter);

                Dimension dimension = new Dimension(
                        measuredDrops.getMaximumLineWidth() + nameItemSpacing + (padding * 2),
                        (measuredDrops.getDrops().size() * measuredDrops.getMaximumLineHeight()) + ((measuredDrops.getDrops().size() - 1) * rowSpacing) + (padding * 2));

                FillOverlayBack(graphics, dimension);

                for (int i = 0; i < measuredDrops.getDrops().size(); i++)
                {
                    MeasuredGroupLootDrop measuredDrop = measuredDrops.GetAtIndex(i, config.reverseOrder());
                    GroupLootDrop drop = measuredDrop.getDrop();

                    int linePosY = padding +
                            ((i + 1) * measuredDrops.getMaximumLineHeight()) +
                            (i * rowSpacing);

                    DrawString(
                            graphics,
                            drop.getTimestampNameLabel(timestampFormatter),
                            config.overlayTextColor(),
                            padding,
                            linePosY
                    );

                    Color itemNameTextColor = GetItemValueColour(drop);

                    DrawString(
                            graphics,
                            drop.getItemNameValueLabel(config.historyOverlayIncludePrices()),
                            itemNameTextColor,
                            padding + measuredDrop.getLeftPartWidth() + nameItemSpacing,
                            linePosY
                    );
                }

                return dimension;
            }
        }

        return null;
    }

    private SimpleDateFormat getFormatter(GroupChatLootConfig config)
    {
        SimpleDateFormat timestampFormatter = null;

        if (!config.timestampFormat().isEmpty())
        {
            try
            {
                timestampFormatter = new SimpleDateFormat(config.timestampFormat());
            }
            catch (Exception e)
            {
                timestampFormatter = new SimpleDateFormat("[HH:mm]");
            }
        }

        return timestampFormatter;
    }

    private List<GroupLootDrop> getDropsToShow(GroupLootSession session)
    {
        List<GroupLootDrop> drops = new ArrayList<>();

        for (GroupLootDrop drop : session.getLastDrops())
        {
            if (drop.NameMatchesAnyPattern(plugin.getOverlayIgnorePatterns()))
            {
                continue;
            }

            if (config.historyOverlayExpiration().hasExpiry())
            {
                if (drop.getAgeInSeconds() > config.historyOverlayExpiration().getSeconds())
                {
                    continue;
                }
            }

            if (drop.getItemValue() > config.historyOverlayMinValue())
            {
                drops.add(drop);

                if (drops.size() == config.numberToDisplay())
                {
                    break;
                }
            }
        }

        return drops;
    }

    private void FillOverlayBack(Graphics2D graphics, Dimension dimension)
    {
        graphics.setColor(config.overlayBackColor());
        graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.setColor(config.overlayTextColor());
    }

    private void DrawString(Graphics2D graphics, String content, Color color, int x, int y)
    {
        if (config.historyOverlayTextShadowEnabled())
        {
            graphics.setColor(config.historyOverlayTextShadowColour());
            graphics.drawString(content, x + 1, y + 1);
        }

        graphics.setColor(color);
        graphics.drawString(content, x, y);
    }

    private Color GetItemValueColour(GroupLootDrop drop)
    {
        if (config.applyValueFormatting())
        {
            if (drop.getItemValue() >= config.priceFormat_t5ValuePrice())
            {
                return config.priceFormat_t5ValueColor();
            }

            if (drop.getItemValue() >= config.priceFormat_t4ValuePrice())
            {
                return config.priceFormat_t4ValueColor();
            }

            if (drop.getItemValue() >= config.priceFormat_t3ValuePrice())
            {
                return config.priceFormat_t3ValueColor();
            }

            if (drop.getItemValue() >= config.priceFormat_t2ValuePrice())
            {
                return config.priceFormat_t2ValueColor();
            }

            if (drop.getItemValue() >= config.priceFormat_t1ValuePrice())
            {
                return config.priceFormat_t1ValueColor();
            }

            if (drop.getItemValue() >= config.priceFormat_normalValuePrice())
            {
                return config.priceFormat_normalValueColor();
            }

            return config.priceFormat_junkValueColor();
        }

        return config.overlayTextColor();
    }
}

