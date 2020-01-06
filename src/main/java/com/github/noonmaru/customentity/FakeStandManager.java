/*
 * Copyright (c) 2020 Noonmaru
 *
 * Licensed under the General Public License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.noonmaru.customentity;

import com.github.noonmaru.collections.mut.LinkedNodeList;
import org.bukkit.World;

import java.util.ArrayDeque;

/**
 * @author Nemo
 */
public class FakeStandManager
{

    private final LinkedNodeList<FakeStand> stands = new LinkedNodeList<>();

    private final ArrayDeque<FakeStand> queue = new ArrayDeque<>();

    private final ArrayDeque<FakeStand> trackerQueue = new ArrayDeque<>();

    private double trackerCount;

    private double trackerUpdatePerTick;

    public FakeStand create(World world, double x, double y, double z, float yaw, float pitch)
    {
        FakeStand newStand = new FakeStand(this, world, x, y, z, yaw, pitch);
        newStand.node = stands.addFirstNode(newStand);
        newStand.updateTrackers();

        return newStand;
    }

    void enqueue(FakeStand stand)
    {
        this.queue.offer(stand);
    }

    /**
     * 이 메서드를 Tick마다 호출해주세요!
     */
    public void update()
    {
        updateStands();
        updateTrackers();
    }

    private void updateStands()
    {
        FakeStand stand;

        while ((stand = queue.poll()) != null)
        {
            if (stand.isValid())
                stand.onUpdate();
        }
    }

    private void recalculateUpdatePerTick()
    {
        trackerCount = 0.0D;
        trackerUpdatePerTick = stands.size() / 40.0D; /*Math.max(1.0D, 1.0 - Math.pow(0.5, (stands.size() - 20) / 20.0D));*/
    }

    private void updateTrackers()
    {
        if (trackerQueue.isEmpty())
        {
            recalculateUpdatePerTick();
            trackerQueue.addAll(stands);
        }

        int count = (int) (trackerCount += trackerUpdatePerTick);
        trackerCount -= count;
        FakeStand stand;

        while (count > 0 && (stand = trackerQueue.poll()) != null)
        {
            if (stand.isValid())
            {
                count--;
                stand.updateTrackers();
            }
        }
    }
}
