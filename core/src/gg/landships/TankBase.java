package gg.landships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

//
// TankBase
//
// this class entity will be the base of the tank, and it will
// contain a reference to the turret entity as well. It also is
// responsible for handling its own controls in the think() method
//

public class TankBase extends LandshipsEntity {
    protected TankTurret turret;

    TankBase() {
        // Create the tankbase with this image as its texture
        super(new Texture("tank_base.png"));
        turret = new TankTurret(this);
    }

    // Our TankTurret is not in the renderer's entity list, so we
    // have to process and draw it ourselves.
    public void thinkTurret() {
        turret.think();
        turret.draw();
    }

    @Override
    public void think() {
        // Store the current sprite so we don't have to call for it all the time
        Sprite sprite = getSprite();

        // Get our current direction vector
        dir = getDir();

        // Process our vehicle inputs
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprite.rotate(1f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprite.rotate(-1f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            // Scale the direction by 3f so we can move faster
            Vector2 trn = dir.cpy().scl(3f);

            // Move forwards
            sprite.translate(trn.x, trn.y);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            // Set the scale to be negative (move back)
            Vector2 trn = dir.cpy().scl(-3f);

            // Move backwards
            sprite.translate(trn.x, trn.y);
        }
    
        // Run the turret's code, and pray
        thinkTurret();
    }
}
