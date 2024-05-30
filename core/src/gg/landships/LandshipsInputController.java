package gg.landships;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

// decrepit class here all it does is handle some of the events
// that we cannot otherwise catch on a case by case basis, i mean really,
// this is just here for some kinds of mouse moves and mouse scrolling
// you could use it for more though..
public class LandshipsInputController implements InputProcessor {

    public boolean keyDown(int keycode) {return false;}
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            Game.camera.setIsFocused(!Game.camera.getIsFocused());
        }
        return false;
    }
    public boolean keyTyped(char character) {return false;}
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {return false;}
    public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
    public boolean mouseMoved(int screenX, int screenY) {return false;}

    @Override
    public boolean scrolled(float amountX, float amountY) {
        LandshipsCamera cam = Game.renderer.getCamera();

        cam.zoom += amountY;
        if(cam.zoom > cam.maxZoom) {
            cam.zoom = cam.maxZoom;
        }

        if(cam.zoom < 1f) {
            cam.zoom = 1f;
        }
        return false;
    }
    
    public static Vector2 getRealMousePos() {
        Vector3 mouse3 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        Vector3 real3 = Game.camera.unproject(mouse3);
        return new Vector2(real3.x, real3.y);
    }
}
