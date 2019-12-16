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

package com.github.noonmaru.customentity.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import com.github.noonmaru.customentity.CustomEntityPacket;
import com.github.noonmaru.tap.packet.Packet;
import com.google.common.base.Charsets;


public final class PacketBuilder implements DataOutput
{
	private static final PacketBuilder INSTANCE = new PacketBuilder();

	public static PacketBuilder getInstance()
	{
		INSTANCE.reset();

		return INSTANCE;
	}

	private final ByteArrayOutputStream bOut;
	
	private final DataOutputStream dOut;

	private PacketBuilder()
	{
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		this.bOut = bOut;
		this.dOut = new DataOutputStream(bOut);
	}

	@Override
	public void write(int b)
	{
		this.bOut.write(b);
	}

	@Override
	public void write(byte[] b)
	{
		try
		{
			this.bOut.write(b);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void write(byte[] b, int off, int len)
	{
		this.bOut.write(b, off, len);
	}

	@Override
	public void writeBoolean(boolean v)
	{
		try
		{
			this.dOut.writeBoolean(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeByte(int v)
	{
		try
		{
			this.dOut.writeByte(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeShort(int v)
	{
		try
		{
			this.dOut.writeShort(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeChar(int v)
	{
		try
		{
			this.dOut.writeChar(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeInt(int v)
	{
		try
		{
			this.dOut.writeInt(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeLong(long v)
	{
		try
		{
			this.dOut.writeLong(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeFloat(float v)
	{
		try
		{
			this.dOut.writeFloat(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeDouble(double v)
	{
		try
		{
			this.dOut.writeDouble(v);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeBytes(String s)
	{
		try
		{
			this.dOut.writeBytes(s);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeChars(String s)
	{
		try
		{
			this.dOut.writeChars(s);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	@Override
	public void writeUTF(String s)
	{
		try
		{
			this.dOut.writeUTF(s);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	public void writeString(String s)
	{
		if (s == null)
		{
			try
			{
				this.dOut.writeShort(0);
			}
			catch (IOException e)
			{
				throw new AssertionError(e);
			}
			return;
		}
		
		byte[] bytes = s.getBytes(Charsets.UTF_8);
				
		try
		{
			this.dOut.writeShort(bytes.length);
			this.dOut.write(bytes);
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	public void reset()
	{
		this.bOut.reset();
	}

	public byte[] toByteArray()
	{
		return this.bOut.toByteArray();
	}
	
	public Packet build()
	{
		return Packet.CUSTOM.payload(CustomEntityPacket.CHANNEL, toByteArray());
	}
}
