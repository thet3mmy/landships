package gg.landships.landshipsgame;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class NetworkUpdateMessage extends NetworkMessage {
    NetworkUpdateMessage() {
        messageType = 0;
    }

    float chassisPosX;
    float chassisPosY;
    float turretPosX;
    float turretPosY;

    float chassisRot;
    float turretRot;
}
