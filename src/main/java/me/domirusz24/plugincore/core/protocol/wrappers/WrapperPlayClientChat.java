package me.domirusz24.plugincore.core.protocol.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.domirusz24.plugincore.core.protocol.AbstractPacket;

public class WrapperPlayClientChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CHAT;

    public WrapperPlayClientChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientChat(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Message.
     *
     * @return The current Message
     */
    public String getMessage() {
        return handle.getStrings().read(0);
    }

    /**
     * Set Message.
     *
     * @param value - new value.
     */
    public void setMessage(String value) {
        handle.getStrings().write(0, value);
    }

}
