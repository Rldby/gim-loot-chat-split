package com.rldby;

import lombok.Getter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class MeasuredGroupLootList
{
    @Getter
    private final List<MeasuredGroupLootDrop> drops = new ArrayList<>();

    @Getter
    private final int maximumLineWidth;

    @Getter
    private final int maximumLineHeight;

    MeasuredGroupLootList(List<GroupLootDrop> drops, GroupChatLootConfig config, FontMetrics fontMetrics, SimpleDateFormat timestampFormatter)
    {
        int maximumLineWidth = -1;
        int maximumLineHeight = -1;

        for (GroupLootDrop drop : drops)
        {
            MeasuredGroupLootDrop measuredGroupLootDrop = new MeasuredGroupLootDrop(
                    drop,
                    fontMetrics,
                    timestampFormatter,
                    config.historyOverlayIncludePrices());

            maximumLineWidth = Math.max(maximumLineWidth, measuredGroupLootDrop.getTotalWidth());
            maximumLineHeight = Math.max(maximumLineHeight, measuredGroupLootDrop.getLineHeight());

            this.drops.add(measuredGroupLootDrop);
        }

        this.maximumLineWidth = maximumLineWidth;
        this.maximumLineHeight = maximumLineHeight;
    }

    public MeasuredGroupLootDrop GetAtIndex(int index, boolean reverseOrder)
    {
        if (reverseOrder)
        {
            return drops.get(index);
        }
        else
        {
            return drops.get(drops.size() - 1 - index);
        }
    }
}
