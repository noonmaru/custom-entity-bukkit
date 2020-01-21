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

import com.github.noonmaru.collections.Node;
import com.github.noonmaru.math.Vector;
import com.github.noonmaru.tap.Tap;
import com.github.noonmaru.tap.entity.EntitySelectors;
import com.github.noonmaru.tap.entity.TapArmorStand;
import com.github.noonmaru.tap.item.TapItemStack;
import com.github.noonmaru.tap.math.BoundingBox;
import com.github.noonmaru.tap.math.RayTracer;
import com.github.noonmaru.tap.packet.Packet;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Nemo
 */
public final class FakeStand
{
    private final FakeStandManager manager;

    private final IdentityHashMap<Player, Tracker> trackers = new IdentityHashMap<>();

    private final TapArmorStand stand;

    private final Vector pos = new Vector();

    private final Vector prevPos = new Vector();

    Node<FakeStand> node;

    private float yaw, pitch;

    private float scaleX, scaleY, scaleZ;

    private TapItemStack item;

    private boolean enqueued;

    private double trackingRange = 128;

    private int trackingTick;

    private BoundingBox trackerBox;

    private boolean updatePos, updateMeta, updateItem;

    public FakeStand(FakeStandManager manager, World world, double x, double y, double z, float yaw, float pitch)
    {
        this.manager = manager;
        stand = Tap.ENTITY.createEntity(ArmorStand.class);
        stand.setMarker(true);
        stand.setInvisible(true);
        stand.setHeadPose(0, 0, 0);
        stand.setBukkitWorld(world);
        stand.setPositionAndRotation(x, y, z, yaw, pitch);

        this.yaw = yaw;
        this.pitch = pitch;
        this.scaleX = this.scaleY = this.scaleZ = 1.0F;

        prevPos.set(pos.set(x, y, z));
    }

    void onUpdate()
    {
        if (updatePos)
        {
            stand.setPositionAndRotation(pos.x, pos.y, pos.z, yaw, pitch);

            Vector move = pos.copy().subtract(prevPos);

            if (move.length() > 3.9)
            {
                sendPacket(Packet.ENTITY.teleport(stand.getBukkitEntity(), pos.x, pos.y, pos.z, yaw, pitch, false));
            }
            else
            {
                sendPacket(Packet.ENTITY.relativeMoveLook(stand.getId(), move.x, move.y, move.z, yaw, pitch, false));
            }

            prevPos.set(pos);
            updatePos = false;
        }
        if (updateMeta)
        {
            sendPacket(Packet.ENTITY.metadata(stand.getBukkitEntity()));
            updateMeta = false;
        }
        if (updateItem)
        {
            sendPacket(Packet.ENTITY.equipment(stand.getId(), EquipmentSlot.HEAD, item));
            updateItem = false;
        }

        enqueued = false;
    }

    void updateTrackers()
    {
        int trackingTick = ++this.trackingTick;
        BoundingBox trackerBox = getTrackerBox();
        World world = stand.getBukkitWorld();
        List<Player> newTrackers = trackerBox.getEntities(world, null, EntitySelectors.PLAYER.and(entity -> ((Player) entity).getListeningPluginChannels().contains(CustomEntityPacket.CHANNEL)));

        for (Player newTracker : newTrackers)
        {
            Tracker tracker = trackers.computeIfAbsent(newTracker, player -> new Tracker());

            if (tracker.trackingCount == 0) //처음 등록될때
            {
                ArmorStand bukkitEntity = stand.getBukkitEntity();
                int id = stand.getId();
                Packet.ENTITY.spawnMob(bukkitEntity).sendTo(newTrackers);
                Packet.ENTITY.metadata(bukkitEntity).sendTo(newTrackers);
                Packet.ENTITY.equipment(id, EquipmentSlot.HEAD, item).sendTo(newTrackers);
                CustomEntityPacket.register(id).sendTo(newTrackers);
                CustomEntityPacket.scale(id, scaleX, scaleY, scaleZ, 0).sendTo(newTrackers);
            }

            tracker.trackingCount = trackingTick; //트래킹 카운트 업데이트
        }

        Iterator<Map.Entry<Player, Tracker>> iterator = trackers.entrySet().iterator();

        while (iterator.hasNext())
        {
            Map.Entry<Player, Tracker> entry = iterator.next();

            if (trackingTick != entry.getValue().trackingCount) //범위 밖에 있어서 트래킹 카운트가 올라가지 않았을경우 제거
            {
                Packet.ENTITY.destroy(stand.getId()).sendTo(entry.getKey());
                iterator.remove();
            }
        }
    }

    private BoundingBox getTrackerBox()
    {
        BoundingBox box = this.trackerBox;

        if (box != null)
            return box;

        Vector pos = this.pos;
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;
        double w = this.trackingRange;

        return this.trackerBox = Tap.MATH.newBoundingBox(x - w, y - w, z - w, x + w, y + w, z + w);
    }

    public void setCustomName(String name)
    {
        stand.setCustomName(name);

        updateMeta();
    }

    public void setCustomNameVisible(boolean visible)
    {
        stand.setCustomNameVisible(visible);

        updateMeta();
    }

    public void setGlowingTo(boolean glowing, Iterable<? extends Player> players)
    {
        boolean origin = isGlowing();

        if (origin == glowing)
        {
            Packet.ENTITY.metadata(stand.getBukkitEntity()).sendTo(players);
        }
        else
        {
            stand.setGlowing(glowing);
            Packet.ENTITY.metadata(stand.getBukkitEntity()).sendTo(players);
            stand.setGlowing(origin);
        }
    }

    public boolean isGlowing()
    {
        return stand.isGlowing();
    }

    public void setGlowing(boolean glowing)
    {
        stand.setGlowing(glowing);

        updateMeta();
    }

    public TapItemStack getItem()
    {
        return item == null ? null : item.copy();
    }

    public void setItem(TapItemStack item)
    {
        this.item = item == null ? null : item.copy();

        updateItem();
    }

    public void setPosition(double x, double y, double z, float yaw, float pitch)
    {
        pos.set(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
        this.trackerBox = null;

        updatePos = true;
        enqueue();
    }

    public Vector getPosition()
    {
        return pos.copy();
    }

    public RayTracer createRayTracer()
    {
        return Tap.MATH.newRayTraceCalculator(prevPos, pos);
    }

    private void enqueue()
    {
        if (enqueued)
            return;

        enqueued = true;
        manager.enqueue(this);
    }

    public void setPose(float x, float y, float z)
    {
        stand.setHeadPose(x, y, z);

        updateMeta();
    }

    private void updateMeta()
    {
        updateMeta = true;
        enqueue();
    }

    public float getScaleX()
    {
        return scaleX;
    }

    public float getScaleY()
    {
        return scaleY;
    }

    public float getScaleZ()
    {
        return scaleZ;
    }

    public void setScale(float scaleX, float scaleY, float scaleZ, int period)
    {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;

        sendPacket(CustomEntityPacket.scale(stand.getId(), scaleX, scaleY, scaleZ, period));
    }

    public void setScale(float scale, int period)
    {
        setScale(scale, scale, scale, period);
    }

    public void setTrackerRange(double trackingRange)
    {
        this.trackingRange = trackingRange;
    }

    public boolean isValid()
    {
        return node != null;
    }

    public void remove()
    {
        if (!isValid())
            return;

        sendPacket(Packet.ENTITY.destroy(stand.getId()));
        sendPacket(CustomEntityPacket.unregister(stand.getId()));
        trackers.clear();
        node.clear();
        node = null;
    }

    private void updateItem()
    {
        updateItem = true;
        enqueue();
    }

    private void sendPacket(Packet packet)
    {
        packet.sendTo(trackers.keySet());
    }

    private static class Tracker
    {
        int trackingCount;
    }
}
