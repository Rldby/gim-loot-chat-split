package com.rldby;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class GroupLootSession
{
    private static final int lastDropBufferCapacity = 200;

    private List<GroupLootDrop> lastDrops = new ArrayList<>(lastDropBufferCapacity);

    void LogDrop(GroupLootDrop drop)
    {
        if (lastDrops.size() == 0)
        {
            // The history is currently empty, so we can blindly add the first entry without worry.

            lastDrops.add(drop);
        }
        else
        {
            // The loot history list has items, so we'll need to find where to insert it based on age.
            // Considering the length of the list is limited by lastDropBufferCapacity, an item may not
            // be eligible to be added if it is too old.

            // When adding to the list, the newest drops get inserted at the start.

            // First we loop through the list until we reach a Drop instance that is older than the one being added now.

            for (int dropIndex = 0; dropIndex < lastDrops.size(); dropIndex++)
            {
                GroupLootDrop existingDrop = lastDrops.get(dropIndex);

                if (existingDrop.getMessageNode().getId() == drop.getMessageNode().getId())
                {
                    return;
                }

                if (existingDrop.IsOlderThanDrop(drop))
                {
                    // We've hit the first Drop in the list that is older. Add the incoming Drop to the list
                    // in front of the one currently at this index.

                    // First check to see if the history is full. If so, remove the last item.

                    if (lastDrops.stream().count() == lastDropBufferCapacity)
                    {
                        lastDrops.remove(lastDropBufferCapacity - 1);
                    }

                    lastDrops.add(dropIndex, drop);

                    return;
                }

                if (dropIndex == lastDrops.stream().count() - 1)
                {
                    // The incoming Drop is older than all Drops in the history. If the list has space, we
                    // can add the incoming Drop to the end. However, if the list is full then the incoming
                    // Drop will be discarded.

                    if (lastDrops.size() < lastDropBufferCapacity)
                    {
                        lastDrops.add(drop);
                    }
                }
            }
        }
    }
}
