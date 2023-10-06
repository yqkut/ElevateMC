package com.elevatemc.elib.pidgin.packet.listener;

import com.elevatemc.elib.pidgin.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * A wrapper class that holds all the information needed to
 * identify and execute a message function.
 *
 */
@AllArgsConstructor
@Getter
public class PacketListenerData {

	private Object instance;
	private Method method;
	private Class packetClass;

	public boolean matches(Packet packet) {
		return this.packetClass == packet.getClass();
	}

}
