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

import com.github.noonmaru.customentity.cmd.*;
import com.github.noonmaru.tap.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

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

		//debug code
		/*FakeStandManager manager = new FakeStandManager();
		Map<Player, FakeStand> stands = new HashMap<>();

		getServer().getPluginManager().registerEvents(new Listener()
		{
			@EventHandler
			public void onPlayerJoin(PlayerJoinEvent event)
			{
				Player player = event.getPlayer();
				Location loc = player.getEyeLocation();
				loc.add(loc.getDirection().multiply(10));
				stands.put(player, manager.create(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0));

			}

			@EventHandler
			public void onPlayerInteract(PlayerInteractEvent event)
			{
				if (event.getHand() == EquipmentSlot.HAND)
				{
					if (event.getAction() == Action.RIGHT_CLICK_AIR)
					{

						Player player = event.getPlayer();
						Location loc = player.getEyeLocation();
						loc.add(loc.getDirection().multiply(10));
						stands.put(player, manager.create(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0));
					}
					else if (event.getAction() == Action.LEFT_CLICK_AIR)
					{
						stands.get(event.getPlayer()).setScale(2.0F, 6.0F, 2.0F, 10);
					}
				}
			}
		}, this);

		getServer().getScheduler().runTaskTimer(this, () -> {
			stands.keySet().removeIf(player -> !player.isOnline());

			for (Map.Entry<Player, FakeStand> entry : stands.entrySet())
			{
				Player player = entry.getKey();
				FakeStand stand = entry.getValue();
				Location loc = player.getEyeLocation();
				loc.add(loc.getDirection().multiply(10));
				stand.setPosition(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 0);
				stand.setItem(TapPlayer.wrapPlayer(player).getHeldItemMainHand());
			}
			manager.update();

		}, 0, 1);*/
	}

	private void registerCommands()
	{
		new CommandManager().addHelp("help").addComponent("register", new CommandCERegister()).addComponent("unregister", new CommandCEUnregister())
				.addComponent("color", new CommandCEColor()).addComponent("scale", new CommandCEScale()).addComponent("colorAndScale", new CommandCEColorAndScale())
				.register(getCommand("customentity"));
	}
}
