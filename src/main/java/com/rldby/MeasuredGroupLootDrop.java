package com.rldby;

import lombok.Getter;

import java.awt.*;
import java.text.SimpleDateFormat;

@Getter
class MeasuredGroupLootDrop
{
    private final GroupLootDrop drop;
    private final int leftPartWidth;
    private final int rightPartWidth;
    private final int lineHeight;

    MeasuredGroupLootDrop(GroupLootDrop drop, FontMetrics fontMetrics, SimpleDateFormat timestampFormatter, boolean includesPrice)
    {
        this.drop = drop;
        leftPartWidth = fontMetrics.stringWidth(drop.getTimestampNameLabel(timestampFormatter));
        rightPartWidth = fontMetrics.stringWidth(drop.getItemNameValueLabel(includesPrice));
        lineHeight = fontMetrics.getHeight();
    }

    int getTotalWidth()
    {
        return leftPartWidth + rightPartWidth;
    }
}
