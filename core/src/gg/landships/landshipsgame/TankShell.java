package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class TankShell extends GameObject implements Serializable {
    Vector2 direction;
    Vector2 originPos;
    float bulletSpeed;

    TankShell(Vector2 dir, Vector2 origin, float bSpeed) {
        super(new Texture("shelltemplate.png"));

        this.direction = dir;
        originPos = origin;
        bulletSpeed = bSpeed;

        velocity = new Vector2();
        velocity.x = bSpeed * dir.x * Gdx.graphics.getDeltaTime();
        velocity.y = bSpeed * dir.y * Gdx.graphics.getDeltaTime();

        sprite.setPosition(origin.x, origin.y);
    }
}
