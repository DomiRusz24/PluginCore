package me.domirusz24.plugincore.core.protocol.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.domirusz24.plugincore.core.protocol.AbstractPacket;

import java.util.UUID;

public class WrapperPlayClientSpectate extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SPECTATE;

    public WrapperPlayClientSpectate() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientSpectate(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Target Player.
     *
     * @return The current Target Player
     */
    public UUID getTargetPlayer() {
        return handle.getUUIDs().read(0);
    }

    /**
     * Set Target Player.
     *
     * @param value - new value.
     */
    public void setTargetPlayer(UUID value) {
        handle.getUUIDs().write(0, value);
    }

}
