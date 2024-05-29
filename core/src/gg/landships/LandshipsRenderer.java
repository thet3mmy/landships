package gg.landships;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

//
// LandshipsRenderer
//
// This class is resaponsible for drawing all of the shapes, and sprites, of the game
// It has batches and renderers are screen spaced and also transformed with the camera
// It also contains an entity list where all of the rendered ents are stored in it
// Remember, that if you try to render anything without calling ui_begin() or begin(),
// an exception will be thrown
//

public class LandshipsRenderer {
    public SpriteBatch batch;                           // Batch that transforms by camera
    public SpriteBatch uiBatch;                         // Batch that is static to screen coordinates
    public ShapeRenderer srend;                         // ShapeRenderer that transforms by camera
    public ShapeRenderer uisrend;                       // ShapeRenderer that is static to screen coordinates
    
    private LinkedList<LandshipsEntity> entities;       // All of our entities to draw
    private LandshipsCamera cam;                        // Our bound camera
    private boolean isRenderingB = false;
    private boolean isRenderingUi = false;
    
    LandshipsRenderer(LandshipsCamera camera) {
        batch = new SpriteBatch();              // create normal SB
        uiBatch = new SpriteBatch();            // create ui SB
        srend = new ShapeRenderer();            // create normal SR
        uisrend = new ShapeRenderer();          // create ui SR
        entities = new LinkedList<>();          // create ent list
        cam = camera;                           // remember the provided camera

        // Set the projection matrixes for the renderers
        batch.setProjectionMatrix(cam.combined);
        srend.setProjectionMatrix(cam.combined);
    }

    public void update() {
        if(cam == null)
            throw new RendererException("Camera is null");

        cam.update();
        batch.setProjectionMatrix(cam.combined);
    }

    public void render() {
        if(!isRenderingB) {
            throw new RendererException("Rendering without batch active");
        }

        for(LandshipsEntity e: new LinkedList<>(entities)) {
            e.getSprite().draw(batch);
        }
    }

    public void updateEnts() {
        for(LandshipsEntity e: new LinkedList<>(entities)) {
            e.think();
        }
    }

    public void begin() {
        if(isRenderingB) {
            throw new RendererException("Already rendering");
        }

        batch.setProjectionMatrix(cam.combined);    // keep it updated
        batch.begin();                              // start rendering
        isRenderingB = true;
    }

    public void end() {
        if(!isRenderingB) {
            throw new RendererException("Already not rendering");
        }

        batch.end();                                // stop rendering
        isRenderingB = false;
    }

    public void ui_begin() {
        if(isRenderingUi) {
            throw new RendererException("Already rendering UI");
        }

        uiBatch.begin();                            // start rendering, remember DONT set proj matrix
    }

    public void ui_end() {
        if(!isRenderingUi) {
            throw new RendererException("Already not rendering UI");
        }

        uiBatch.end();
    }

    public void addEntity(LandshipsEntity e) {
        entities.add(e);
    }
    
    public void removeEntity(LandshipsEntity e) {
        entities.remove(e);
    }

    public int getNumEntities() {
        return entities.size();
    }

    public LinkedList<LandshipsEntity> getEntities() {
        return new LinkedList<>(entities);
    }

    public LandshipsCamera getCamera() {
        return cam;
    }
}
