package com.rldby;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
class GroupLootDrop
{
    private static final Pattern nameMatchPattern = Pattern.compile("^(([0-9]|,)+[\\s][x][\\s])");


    @Getter
    private MessageNode messageNode;

    @Getter
    private int timestamp;

    @Getter
    private String playerName;

    @Getter
    private String itemName;

    @Getter
    private String rawItemValue;

    @Getter
    private int itemValue;

    @Getter
    private ZonedDateTime zonedTimestamp;

    @Getter
    private String itemNameNoQuantity;

    private GroupLootDrop(MessageNode messageNode, String playerName, String itemName, String rawItemValue)
    {
        this.messageNode = messageNode;
        this.playerName = playerName;
        this.itemName = itemName;

        Matcher nameMatcher = nameMatchPattern.matcher(itemName);

        if (nameMatcher.find())
        {
            itemNameNoQuantity = nameMatcher.replaceAll("");
        }
        else
        {
            itemNameNoQuantity = itemName;
        }

        setRawItemValue(rawItemValue);
        setTimestamp(messageNode.getTimestamp());
    }

    public static GroupLootDrop FromCurrentMessageInStack(Client client)
    {
        int[] intStack = client.getIntStack();
        int intStackSize = client.getIntStackSize();
        String[] stringStack = client.getStringStack();
        int stringStackSize = client.getStringStackSize();

        final int messageType = intStack[intStackSize - 2];
        final int messageId = intStack[intStackSize - 1];
        String message = stringStack[stringStackSize - 1];
        ChatMessageType chatMessageType = ChatMessageType.of(messageType);
        final MessageNode messageNode = client.getMessages().get(messageId);

        if (chatMessageType == ChatMessageType.CLAN_MESSAGE)
        {
            boolean containsReceivedDrop = message.contains(" received a drop: ");
            boolean nameIsEmpty = "".equals(messageNode.getName());

            if (containsReceivedDrop && nameIsEmpty)
            {
                int receivedIndex = message.indexOf(" received a drop: ");
                int colonIndex = message.indexOf(":");
                int valueStartIndex = message.lastIndexOf("(");

                String groupMemberName = message.substring(0, receivedIndex);

                if (groupMemberName.startsWith("|"))
                {
                    groupMemberName = groupMemberName.substring(1);
                }

                String itemName;
                String itemValue;

                if (valueStartIndex > -1)
                {
                    itemName = message.substring(colonIndex + 2, valueStartIndex - 1);
                    itemValue = message.substring(valueStartIndex + 1, message.indexOf(" ", valueStartIndex));
                }
                else
                {
                    itemName = message.substring(colonIndex + 2, message.length() - 2);
                    itemValue = "1";
                }

                return new GroupLootDrop(messageNode, groupMemberName, itemName, itemValue);
            }
        }

        return null;
    }

    private void setRawItemValue(String value)
    {
        rawItemValue = value;

        try
        {
            itemValue = Integer.parseInt(rawItemValue.replace(",", ""));
        }
        catch (NumberFormatException e)
        {
            itemValue = 1;
        }
    }

    private void setTimestamp(int timestamp)
    {
        this.timestamp = timestamp;

        zonedTimestamp = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault());
    }

    public int getAgeInSeconds()
    {
        return (int) ChronoUnit.SECONDS.between(zonedTimestamp, ZonedDateTime.now());
    }

    public String getTimestampNameLabel(SimpleDateFormat timeFormatter)
    {
        String timestampPart = "";

        if (timeFormatter != null)
        {
            timestampPart = timeFormatter.format(Date.from(zonedTimestamp.toInstant()));

            if (!timestampPart.equals(""))
            {
                timestampPart += " ";
            }
        }

        return timestampPart + playerName + ":";
    }

    public String getItemNameValueLabel(boolean includePrice)
    {
        if (includePrice)
        {
            return itemName + " (" + rawItemValue + ")";
        }
        else
        {
            return itemName;
        }
    }

    public boolean IsOlderThanDrop(GroupLootDrop otherDrop)
    {
        return getTimestamp() < otherDrop.getTimestamp() ||
                (
                        getTimestamp() == otherDrop.getTimestamp() &&
                        getMessageNode().getId() < otherDrop.getMessageNode().getId()
                );
    }

    public boolean NameMatchesAnyPattern(List<Pattern> patterns)
    {
        if (patterns != null && patterns.size() > 0)
        {
            for (Pattern ignorePattern : patterns)
            {
                if (ignorePattern.matcher(getItemNameNoQuantity()).matches())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
