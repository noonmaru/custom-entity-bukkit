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

import com.github.noonmaru.customentity.CustomEntityPacket;
import com.github.noonmaru.tap.command.ArgumentList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandCEScale extends CommandCE
{

	public CommandCEScale()
	{
		super("<X> <Y> <Z> [Duration]", "크기를 적용합니다.", "customentity.scale", 3);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String componentLabel, int entityId, ArgumentList args)
	{
		try
		{
			float scaleX = Float.parseFloat(args.next());
			float scaleY = Float.parseFloat(args.next());
			float scaleZ = Float.parseFloat(args.next());
			int duration = args.hasNext() ? Math.max(0, Integer.parseInt(args.next())) : 0;
			
			CustomEntityPacket.scale(entityId, scaleX, scaleY, scaleZ, duration).sendAll();
			broadcast(sender, label, componentLabel, String.format("%d -> %.1f %.1f %.1f %d", entityId, scaleX, scaleY, scaleZ, duration));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}

}
