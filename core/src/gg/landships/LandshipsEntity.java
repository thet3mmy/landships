package gg.landships;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

//
// LandshipsEntity
//
// this is the basic class from which all of the game objects
// will be deriving from. This should have asprite and whatever else
// is needed to run the game logic like method definitions.
//

public class LandshipsEntity {
    private Sprite sprite;                                      // sprite representing our entity
    public Vector2 dir;
    
    LandshipsEntity(Texture t) {
        sprite = new Sprite(t);                                 // create it using a texture provided
    }

    // most likely, the most important method of any entity
    // this is called every single frame that the game runs and it
    // is probably going to be responsible for handling all of this
    // object's logic, including any key presses or math or whatever
    public void think() {

    }

    public Sprite getSprite() {
        return sprite;                                          // return it so it can be read
    }

    public Vector2 getTruePos() {
        return new Vector2(sprite.getX(), sprite.getY());       // turn its position into a Vector2
    }

    public Vector2 getPos() {
        float w2 = sprite.getTexture().getWidth() / 2;
        float h2 = sprite.getTexture().getHeight() / 2;
        return getTruePos().add(new Vector2(w2, h2));
    }

    public Vector2 getDir() {
        // Calculate the direction vector based on the angle of the sprite
        // This requires a lot of trig so this is a slow ass method
        float angRad = (getSprite().getRotation() + 90)* MathUtils.degRad;
        return new Vector2(
            (float)Math.cos(angRad),
            (float)Math.sin(angRad)
        );
    }
}
