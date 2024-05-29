package gg.landships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

//
//  TankTurret
//
//  The TankTurret class is here to be the visual representation of the tank's turret.
//  That's basically all it does, it won't have any collisions or anything.
//

public class TankTurret extends LandshipsEntity {
    private TankBase base;

    // Define the floats for the center of the turret itself
    private float w2;
    private float h4;

    TankTurret(TankBase b) {
        super(new Texture("tank_turret.png"));
        base = b;

        // Calculate the coordinates for the center of the turret
        // Not the sprite all in all the turret itself
        w2 = getSprite().getTexture().getWidth() / 2;
        h4 = getSprite().getTexture().getHeight() / 4;

        getSprite().setCenter(w2, h4);
        getSprite().setOrigin(w2, h4);
    }

    @Override
    public Vector2 getPos() {
        return getTruePos().add(new Vector2(w2, h4));
    }

    @Override
    public void think() {
        // Save our sprite so we don't call over and over
        Sprite sprite = getSprite();
        Sprite baseSprite = base.getSprite();

        // Set the position to be centered on the base
        float y = baseSprite.getY() + baseSprite.getTexture().getHeight() / 4;
        sprite.setPosition(baseSprite.getX(), y);
    }

    // Because this entity will never occupy the renderer's entity list,
    // it has to handle its own drawing, which will be coordinated by the
    // TankBase entity that owns this TankTurret.
    public void draw() {
        LandshipsRenderer renderer = Game.renderer;
        SpriteBatch batch = renderer.batch;
        
        // Render it
        renderer.begin();
        getSprite().draw(batch);
        renderer.end();

        onDrawFinished();
    }

    public void onDrawFinished() {
        rotateToMouse();
    }

    public void rotateToMouse() {
        Vector2 mousePos = LandshipsInputController.getRealMousePos();
        Vector2 pos = getPos();
        double ang = Math.atan2(
            mousePos.y - pos.y,
            mousePos.x - pos.x
        ) * 180.0d / Math.PI;
        ang -= 90d;

        float dt = Gdx.graphics.getDeltaTime();
        float interp = dt * 5f;
        float nextAng = MathUtils.lerpAngleDeg(getSprite().getRotation(), (float)ang, interp);;
        getSprite().setRotation(nextAng);
    }
}
