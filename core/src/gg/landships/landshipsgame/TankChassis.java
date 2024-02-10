package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

public class TankChassis extends GameObject implements Serializable {
    public float reloadSpeed = 1f;
    // 0.2 is very slow, and 4 is very fast

    public float turnSpeed = 1f;
    // degrees per frame the hull turns

    public float driveSpeed = 5f;
    // how much the velocity increases by

    public float frictionBase = 0.9f;
    // 0 = 100% friction, instant stop, 1 = no stop

    public float topSpeed = 12f;
    // this is the maximum speed cap for this tank;

    public float progress = 0.0f;
    public TankTurret turret;

    TankChassis() {
        super(new Texture("chassistemplate.png"));
        turret = new TankTurret();
        turret.think();

        LandshipsGame.renderLayer1.add(turret);

        if(LandshipsGame.updateList.contains(this))
            LandshipsGame.updateList.add(turret);
    }

    private void updateVariables() {
        direction = getDirection();

        if(velocity.x > topSpeed)
            velocity.x = topSpeed;

        if(velocity.y > topSpeed)
            velocity.y = topSpeed;

        sprite.translate(velocity.x, velocity.y);
        turret.sprite.setPosition(sprite.getX(), sprite.getY() + turret.sprite.getHeight() / 4);
    }

    private void controls() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprite.rotate(turnSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprite.rotate(-turnSpeed);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.x += driveSpeed * Gdx.graphics.getDeltaTime() * direction.x;
            velocity.y += driveSpeed * Gdx.graphics.getDeltaTime() * direction.y;
        } else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.x -= driveSpeed * Gdx.graphics.getDeltaTime() * direction.x;
            velocity.y -= driveSpeed * Gdx.graphics.getDeltaTime() * direction.y;
        } else {
            velocity.x *= frictionBase;
            velocity.y *= frictionBase;
        }

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            // if we are reloaded reset the reload progress
            // and shoot the shell in the direction of the turret
            if(progress == 1f) {
                progress = 0;

                Vector2 tDirection = turret.direction;
                Vector2 tOrigin = turret.getTruePos();
                float bSpeed = 2500f;

                TankShell shell = new TankShell(tDirection, tOrigin, bSpeed);
                LandshipsGame.renderLayer1.add(shell);
                LandshipsGame.updateList.add(shell);
            }
        }
    }

    @Override
    public void think() {
        updateVariables();
        controls();
    }
}
