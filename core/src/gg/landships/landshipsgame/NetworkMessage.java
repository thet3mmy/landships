package gg.landships.landshipsgame;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class NetworkMessage implements Serializable {
    int messageType;
    int clientId;
}
