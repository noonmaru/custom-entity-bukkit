/*
 * Copyright (c) 2019 Noonmaru
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

package com.github.noonmaru.customentity.cmd;

import com.github.noonmaru.math.Vector;
import com.github.noonmaru.tap.Tap;
import com.github.noonmaru.tap.command.ArgumentList;
import com.github.noonmaru.tap.command.CommandComponent;
import com.github.noonmaru.tap.entity.EntitySelectors;
import com.github.noonmaru.tap.math.RayTraceResult;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;


public abstract class CommandCE extends CommandComponent
{
    public CommandCE(String usage, String description, String permission)
    {
        this(usage, description, permission, 0);
    }

    public CommandCE(String usage, String description, String permission, int argumentsLength)
    {
        super(usage == null ? "<target>|target" : "<target>|target " + usage, description, permission, Math.max(0, argumentsLength) + 1);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String componentLabel, ArgumentList args)
    {
        String token = args.next();

        try
        {
            return onCommand(sender, command, label, componentLabel, Integer.parseInt(token), args);
        }
        catch (NumberFormatException e)
        {}

        if ("target".equalsIgnoreCase(token))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                Location loc = player.getEyeLocation();
                org.bukkit.util.Vector vec = loc.getDirection().multiply(256D);
                Vector from = new Vector(loc.getX(), loc.getY(), loc.getZ());
                Vector to = from.copy().add(vec.getX(), vec.getY(), vec.getZ());
                RayTraceResult result = Tap.MATH.rayTrace(loc.getWorld(), player, from, to, 0, 1.0D, EntitySelectors.CREATURE);

                if (result != null)
                {
                    LivingEntity target = result.getEntity();

                    if (target != null)
                        return onCommand(sender, command, label, componentLabel, target.getEntityId(), args);
                }

                sender.sendMessage("적용할 엔티티를 바라보고 사용하세요.");
                return true;
            }
        }

        List<Entity> entities = matchEntities(sender, token, Entity.class);
        int cursor = args.getCursor();

        for (Entity entity : entities)
        {
            if (entity instanceof Player)
                continue;

            if (!onCommand(sender, command, label, componentLabel, entity.getEntityId(), args.setCursor(cursor)))
                return false;
        }

        return true;
    }

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String componentLabel, int entityId, ArgumentList args);

}
