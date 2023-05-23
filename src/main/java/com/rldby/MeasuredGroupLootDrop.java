package com.rldby;

import lombok.Getter;

import java.awt.*;
import java.text.SimpleDateFormat;

@Getter
class MeasuredGroupLootDrop
{
    private GroupLootDrop drop;
    private int leftPartWidth;
    private int rightPartWidth;
    private int lineHeight;

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
