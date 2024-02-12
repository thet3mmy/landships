package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class TankChassis extends GameObject implements Serializable {
    public float reloadSpeed = 0.8f;
    // 0.2 is very slow, and 4 is very fast

    public float turnSpeed = 1f;
    // degrees per frame the hull turns

    public float driveSpeed = 5f;
    // how much the velocity increases by

    public float frictionBase = 0.92f;
    // 0 = 100% friction, instant stop, 1 = no stop

    public float topSpeed = 12f;
    // this is the maximum speed cap for this tank;

    public float hp = 1000f;
    // this is how much health remains in this tank;

    // armor values for the tank's sections
    public float armorFront = 85f;
    public float armorRear = 35f;

    // reload progress
    public float progress = 1.0f;

    // our turret
    public TankTurret turret;

    // ratio of rear to front hitbox
    public static final float hitboxRatio = 6f;

    public LinkedList<TankShell> myShells;

    TankChassis(Texture t) {
        super(t);
        turret = new TankTurret(new Texture("turrettemplate.png"));
        turret.think();

        LandshipsGame.renderLayer1.add(turret);

        if(LandshipsGame.updateList.contains(this))
            LandshipsGame.updateList.add(turret);

        myShells = new LinkedList<>();
    }

    private void updateVariables() {
        direction = getDirection();

        if(velocity.x > topSpeed)
            velocity.x = topSpeed;

        if(velocity.y > topSpeed)
            velocity.y = topSpeed;

        sprite.translate(velocity.x, velocity.y);
    }

    public void updateTurretPosition() {
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

        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            // if we are reloaded reset the reload progress
            // and shoot the shell in the direction of the turret
            if(progress == 1f) {
                progress = 0;

                Vector2 tDirection = turret.direction.nor();
                Vector2 tOrigin = turret.getTruePos();
                float bSpeed = 7500f;

                LandshipsGame.networkSystem.shootBullet(tDirection, tOrigin, bSpeed);
            }
        }
    }

    public Polygon getFrontHitPolygon() {
        Polygon polygon;
        float[] vertices = new float[8];

        float x = LandshipsGame.thisTank.sprite.getX();
        float y = LandshipsGame.thisTank.sprite.getY();
        float w = LandshipsGame.thisTank.sprite.getWidth();
        float h = LandshipsGame.thisTank.sprite.getHeight();

        vertices[0] = x;
        vertices[1] = (y + h) - (hitboxRatio * 2f);

        vertices[2] = x + w;
        vertices[3] = (y + h) - (hitboxRatio * 2f);

        vertices[4] = x + w;
        vertices[5] = y + h;

        vertices[6] = x;
        vertices[7] = y + h;

        polygon = new Polygon(vertices);
        polygon.setRotation(LandshipsGame.thisTank.sprite.getRotation());

        Vector2 origin = getTruePos();
        polygon.setOrigin(origin.x, origin.y);
        return polygon;
    }

    public Polygon getRearHitPolygon() {
        Polygon polygon;
        float[] vertices = new float[8];

        float x = LandshipsGame.thisTank.sprite.getX();
        float y = LandshipsGame.thisTank.sprite.getY();
        float w = LandshipsGame.thisTank.sprite.getWidth();
        float h = LandshipsGame.thisTank.sprite.getHeight();

        vertices[0] = x;
        vertices[1] = y;

        vertices[2] = x + w;
        vertices[3] = y;

        vertices[4] = x + w;
        vertices[5] = (y + h) - (hitboxRatio * 2f);

        vertices[6] = x;
        vertices[7] = (y + h) - (hitboxRatio * 2f);

        polygon = new Polygon(vertices);
        polygon.setRotation(LandshipsGame.thisTank.sprite.getRotation());

        Vector2 origin = getTruePos();
        polygon.setOrigin(origin.x, origin.y);
        return polygon;
    }

    @Override
    public void think() {
        updateVariables();
        controls();
    }
}
