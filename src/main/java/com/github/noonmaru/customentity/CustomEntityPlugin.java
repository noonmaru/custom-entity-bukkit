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

package com.github.noonmaru.customentity;

import com.github.noonmaru.tap.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.noonmaru.customentity.cmd.CommandCEColor;
import com.github.noonmaru.customentity.cmd.CommandCEColorAndScale;
import com.github.noonmaru.customentity.cmd.CommandCERegister;
import com.github.noonmaru.customentity.cmd.CommandCEScale;
import com.github.noonmaru.customentity.cmd.CommandCEUnregister;

public final class CustomEntityPlugin extends JavaPlugin
{
	private static CustomEntityPlugin instance;

	public static CustomEntityPlugin getInstance()
	{
		return instance;
	}

	@Override
	public void onEnable()
	{
		getServer().getMessenger().registerOutgoingPluginChannel(this, CustomEntityPacket.CHANNEL);

		instance = this;

		registerCommands();
	}

	private void registerCommands()
	{
		new CommandManager().addHelp("help").addComponent("register", new CommandCERegister()).addComponent("unregister", new CommandCEUnregister())
				.addComponent("color", new CommandCEColor()).addComponent("scale", new CommandCEScale()).addComponent("colorAndScale", new CommandCEColorAndScale())
				.register(getCommand("customentity"));
	}
}
