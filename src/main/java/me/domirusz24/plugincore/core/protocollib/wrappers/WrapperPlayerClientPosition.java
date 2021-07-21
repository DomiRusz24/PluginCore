package me.domirusz24.plugincore.core.protocollib.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.domirusz24.plugincore.core.protocollib.AbstractPacket;

public class WrapperPlayerClientPosition extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.POSITION;

    public WrapperPlayerClientPosition() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayerClientPosition(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve X.
     * <p>
     * Notes: absolute position
     *
     * @return The current X
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Set X.
     *
     * @param value - new value.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieve FeetY.
     * <p>
     * Notes: absolute feet position, normally HeadY - 1.62. Used to modify the
     * players bounding box when going up stairs, crouching, etc…
     *
     * @return The current FeetY
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Set FeetY.
     *
     * @param value - new value.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieve Z.
     * <p>
     * Notes: absolute position
     *
     * @return The current Z
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Set Z.
     *
     * @param value - new value.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieve On Ground.
     * <p>
     * Notes: true if the client is on the ground, False otherwise
     *
     * @return The current On Ground
     */
    public boolean getOnGround() {
        return handle.getBooleans().read(0);
    }

    /**
     * Set On Ground.
     *
     * @param value - new value.
     */
    public void setOnGround(boolean value) {
        handle.getBooleans().write(0, value);
    }

}
