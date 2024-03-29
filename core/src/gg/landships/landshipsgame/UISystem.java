package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class UISystem {
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    UISystem() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
    }

    public void renderUI() {
        LandshipsGame.thisTank.progress += Gdx.graphics.getDeltaTime() * LandshipsGame.thisTank.reloadSpeed;
        LandshipsGame.thisTank.progress = Math.min(Math.max(LandshipsGame.thisTank.progress, 0), 1);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float radius = Math.min(screenWidth, screenHeight) * 0.04f;

        float centerX = Gdx.input.getX();
        float centerY = -Gdx.input.getY() + Gdx.graphics.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.circle(centerX, centerY, radius, 50);

        shapeRenderer.setColor(1 - LandshipsGame.thisTank.progress, LandshipsGame.thisTank.progress, 0, 1);
        float innerRadius = radius * LandshipsGame.thisTank.progress;
        shapeRenderer.circle(centerX, centerY, innerRadius, 50);
        shapeRenderer.end();

        ShapeRenderer shapeRenderer2 = new ShapeRenderer();
        shapeRenderer2.setProjectionMatrix(LandshipsGame.camera.combined);

        shapeRenderer2.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer2.setColor(1.0f, 1.0f, 0.0f, 1f);
        shapeRenderer2.polygon(LandshipsGame.thisTank.getRearHitPolygon().getTransformedVertices());
        shapeRenderer2.setColor(0.0f, 1.0f, 1.0f, 1f);
        shapeRenderer2.polygon(LandshipsGame.thisTank.getFrontHitPolygon().getTransformedVertices());

        shapeRenderer2.end();

        LandshipsGame.uiBatch.begin();
        font.draw(LandshipsGame.uiBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight());
        LandshipsGame.uiBatch.end();
    }
}
