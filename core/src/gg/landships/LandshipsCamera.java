package gg.landships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

//
// LandshipsCamera
//
// This is our camera, it handles all of the stuff that a camera has to do
// It extends OrthographicCamera so we can use all of the same methods and so that
// it actually works in the LibGDX system.
//

public class LandshipsCamera extends OrthographicCamera {
    private boolean isFocused = false;
    private LandshipsEntity focus;

    public float maxZoom = 4f;

    LandshipsCamera() {
        super(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());           // because we simply extend call super
        setToOrtho(false);                                            // truly no idea what this does 
        //                                                                  // but it has to happen

        // make sure the focus system is reset
        release();
    }

    @Override
    public void update() {
        if(!getIsFocused()) {
            // Camera is not set to follow anything, so let's let the user
            // drag the camera around.
            if(Gdx.input.isButtonPressed(1)) {
                float sens = 1.8f * zoom;
                translate(-(Gdx.input.getDeltaX() * sens), Gdx.input.getDeltaY() * sens);
            }
        } else {
            // Camera IS set to follow something, so let's focus on it
            // Check if it's null, just in case
            if(focus != null) {
                Vector2 fPos = new Vector2(focus.getPos());
                Vector3 cPos = new Vector3(fPos.x, fPos.y, 0);
                position.set(cPos);
            }
        }
        super.update();
    }

    public boolean getIsFocused() {
        return isFocused;
    }

    public void setIsFocused(boolean iF) {
        isFocused = iF;
    }

    public void setFocus(LandshipsEntity foc) {
        focus = foc;
    }

    public void focus(LandshipsEntity foc) {
        focus = foc;
        isFocused = true;
    }

    public void release() {
        focus = null;
        isFocused = false;
    }
}
