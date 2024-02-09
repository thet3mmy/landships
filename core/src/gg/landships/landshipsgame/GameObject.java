package gg.landships.landshipsgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class GameObject implements Serializable {
    public transient Sprite sprite;
    public Vector2 direction;
    public Vector2 velocity;

    GameObject(Texture t) {
        sprite = new Sprite(t);

        direction = new Vector2();
        velocity = new Vector2();
    }

    public Vector2 getDirection() {
        Vector2 dir = new Vector2();
        float angleDeg = sprite.getRotation() + 90;
        float angleRad = angleDeg * MathUtils.degRad;

        dir.x = (float)Math.cos(angleRad);
        dir.y = (float)Math.sin(angleRad);

        return dir.nor();
    }

    public Vector2 getTruePos() {
        return new Vector2(sprite.getX() + (sprite.getWidth() / 2), sprite.getY() + (sprite.getHeight() / 2));
    }

    public void think() {
        sprite.translate(velocity.x, velocity.y);
    }
}
