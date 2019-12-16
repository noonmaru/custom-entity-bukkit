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

import com.github.noonmaru.customentity.util.PacketBuilder;
import com.github.noonmaru.tap.packet.Packet;


public final class CustomEntityPacket
{
	public static final String CHANNEL = "CustomEntity";
	
	private static final int REGISTER = 0;
	
	private static final int UNREGISTER = 1;
	
	private static final int COLOR = 2;
	
	private static final int SCALE = 3;
	
	private static final int COLOR_AND_SCALE = 4;	
	
	private static final int FAKE_ENTITY = 5;
	
	public static Packet register(int entityId)
	{
		PacketBuilder builder = PacketBuilder.getInstance();
		
		builder.write(REGISTER);
		builder.writeInt(entityId);
		
		return builder.build();
	}
	
	public static Packet unregister(int... entityIds)
	{
		PacketBuilder builder = PacketBuilder.getInstance();
		
		builder.write(UNREGISTER);
		int length = entityIds.length;
		builder.writeShort(length);
		
		for (int i = 0; i < length; i++)
			builder.writeInt(entityIds[i]);
		
		return builder.build();
	}
	
	public static Packet scale(int entityId, float scaleX, float scaleY, float scaleZ, int duration)
	{
		PacketBuilder builder = PacketBuilder.getInstance();

		builder.write(SCALE);
		builder.writeInt(entityId);
		builder.writeFloat(scaleX);
		builder.writeFloat(scaleY);
		builder.writeFloat(scaleZ);
		builder.writeInt(duration);

		return builder.build();
	}
	
	public static Packet color(int entityId, int colorR, int colorG, int colorB, int duration)
	{
		PacketBuilder builder = PacketBuilder.getInstance();
		
		builder.write(COLOR);
		builder.writeInt(entityId);
		builder.writeByte(colorR);
		builder.writeByte(colorG);
		builder.writeByte(colorB);
		builder.writeInt(duration);
		
		return builder.build();
	}
	
	public static Packet colorAndScale(int entityId, int colorR, int colorG, int colorB, float scaleX, float scaleY, float scaleZ, int duration)
	{
		PacketBuilder builder = PacketBuilder.getInstance();
		
		builder.write(COLOR_AND_SCALE);
		builder.writeInt(entityId);
		builder.writeByte(colorR);
		builder.writeByte(colorG);
		builder.writeByte(colorB);
		builder.writeFloat(scaleX);
		builder.writeFloat(scaleY);
		builder.writeFloat(scaleZ);
		builder.writeInt(duration);
		
		return builder.build();
	}

	public static Packet fakeEntity(int entityId, int typeId)
	{
		PacketBuilder builder = PacketBuilder.getInstance();

		builder.write(FAKE_ENTITY);
		builder.writeInt(entityId);
		builder.writeByte(typeId);
		
		return builder.build();
	}
}
