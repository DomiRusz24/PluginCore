package me.domirusz24.plugincore.core.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.domirusz24.plugincore.core.protocollib.AbstractPacket;

public class WrapperPlayServerMultiBlockChange extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.MULTI_BLOCK_CHANGE;

    public WrapperPlayServerMultiBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMultiBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve a copy of the record data as a block change array.
     *
     * @return The copied block change array.
     */
    public WrappedBlockData[] getRecords() {
        return handle.getBlockDataArrays().read(0);
    }

    /**
     * Set the record data using the given helper array.
     *
     * @param value - new value
     */
    public void setRecords(WrappedBlockData[] value) {
        handle.getBlockDataArrays().write(0, value);
    }

    // ------------

    public void setSection(BlockPosition position) {
        handle.getSectionPositions().write(0, position);
    }

    public BlockPosition getSection() {
        return handle.getSectionPositions().read(0);
    }

    // ------------

    public void setTrustEdges(boolean edge) {
        handle.getBooleans().write(0, edge);
    }

    public boolean getTrustEdges() {
        return handle.getBooleans().read(0);
    }

    // ------------

    public void setLocations(short[] locations) {
        handle.getShortArrays().write(0, locations);
    }

    public short[] getBlockAmount() {
        return handle.getShortArrays().read(0);
    }
}
