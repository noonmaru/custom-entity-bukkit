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

public class CommandCEColorAndScale extends CommandCE
{

	public CommandCEColorAndScale()
	{
		super("<Color> <X> <Y> <Z> [Duration]", "색상과 크기를 적용합니다.", "customentity.colorandscale");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String componentLabel, int entityId, ArgumentList args)
	{
		try
		{
			int color = Integer.decode("0x" + args.next());
			int r = (color >> 16) & 0xFF;
			int g = (color >> 8) & 0xFF;
			int b = color & 0xFF;
			float scaleX = args.nextFloat();
			float scaleY = args.nextFloat();
			float scaleZ = args.nextFloat();
			int duration = args.hasNext() ? Math.max(0, Integer.parseInt(args.next())) : 0;

			CustomEntityPacket.colorAndScale(entityId, r, g, b, scaleX, scaleY, scaleZ, duration).sendAll();
			broadcast(sender, label, componentLabel, String.format("%d -> %02X%02X%02X %.1f %.1f %.1f %d", entityId, r, g, b, scaleX, scaleY, scaleZ, duration));
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}

}
