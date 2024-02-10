package gg.landships.landshipsgame;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class NetworkUpdateMessage extends NetworkMessage {
    NetworkUpdateMessage() {
        messageType = 0;
    }

    Vector2 chassisPos;
    Vector2 turretPos;

    float chassisRot;
    float turretRot;
}
