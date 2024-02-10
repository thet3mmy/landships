package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TankTurret extends GameObject {
    private float rotationSpeed = 1f;

    TankTurret(Texture t) {
        super(t);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 4);
    }

    private float calculateTargetAngle() {
        Vector3 mousePos3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 realPos3 = LandshipsGame.camera.unproject(mousePos3);
        Vector2 realPos = new Vector2(realPos3.x, realPos3.y);

        float angleRad = MathUtils.atan2(realPos.y - getTruePos().y, realPos.x - getTruePos().x);
        float angleDeg = angleRad * MathUtils.radDeg;

        return angleDeg - 90;
    }

    private void aimTurret() {
        float interpolation = rotationSpeed * Gdx.graphics.getDeltaTime();
        float interpolatedRotation = MathUtils.lerpAngleDeg(sprite.getRotation(), calculateTargetAngle(), interpolation);
        sprite.setRotation(interpolatedRotation);
    }

    @Override
    public Vector2 getTruePos() {
        return new Vector2(sprite.getX() + sprite.getWidth() / 2, sprite.getY() + sprite.getHeight() / 4);
    }

    @Override
    public void think() {
        aimTurret();
        direction = getDirection();
    }
}
