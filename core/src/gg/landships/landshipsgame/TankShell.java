package gg.landships.landshipsgame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;
import java.util.LinkedList;

public class TankShell extends GameObject implements Serializable {
    Vector2 direction;
    Vector2 originPos;
    float bulletSpeed;
    float penetration = 80f;
    float damage = 150f;
    long lifetime;
    TankChassis owner;

    TankShell(Vector2 dir, Vector2 origin, float bSpeed, TankChassis own) {
        super(new Texture("shelltemplate.png"));

        this.direction = dir;
        originPos = origin;
        bulletSpeed = bSpeed;
        owner = own;

        velocity = new Vector2();
        velocity.x = bSpeed * dir.x * Gdx.graphics.getDeltaTime();
        velocity.y = bSpeed * dir.y * Gdx.graphics.getDeltaTime();

        sprite.setPosition(origin.x, origin.y);
    }

    private Polygon getPolygon() {
        // this is HORRIBLE do not do this!!!!!!!!!!!!!
        Polygon thisPoly;

        float x = sprite.getX();
        float y = sprite.getY();
        float w = sprite.getWidth();
        float h = sprite.getHeight();

        float[] verts = new float[8];
        verts[0] = x;
        verts[1] = y;
        verts[2] = x + w;
        verts[3] = y;
        verts[4] = x + w;
        verts[5] = y + h;
        verts[6] = x;
        verts[7] = y + h;

        thisPoly = new Polygon(verts);
        // end of very bad stuff

        return thisPoly;
    }

    @Override
    public void think() {
        lifetime++;
        sprite.translate(velocity.x, velocity.y);

        if(lifetime > 60*5)
            delete();

        for(GameObject o: new LinkedList<>(LandshipsGame.renderLayer0)) {
            if(!o.equals(this)) {
                if(!o.equals(owner)) {
                    if(o instanceof TankChassis) {
                        Polygon shellPoly = getPolygon();
                        Polygon targetPolyFront = ((TankChassis)o).getFrontHitPolygon();
                        Polygon targetPolyRear = ((TankChassis)o).getRearHitPolygon();
                        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();

                        // Test if the shell collides with the front hitbox
                        if(Intersector.overlapConvexPolygons(shellPoly.getTransformedVertices(),
                                targetPolyFront.getTransformedVertices(), mtv)) {
                            System.out.println("Colliding with the front");

                            if(o.equals(LandshipsGame.thisTank)) {
                                if(penetration > ((TankChassis) o).armorFront) {
                                    LandshipsGame.networkSystem.hitMessage(damage, LandshipsGame.tanks.indexOf(owner), owner.myShells.indexOf(this));
                                } else {
                                    System.out.println("No penetration on the front");
                                }
                            }

                            delete();
                            return;
                        }

                        // Test if the shell collides with the rear hitbox
                        if(Intersector.overlapConvexPolygons(shellPoly.getTransformedVertices(),
                                targetPolyRear.getTransformedVertices(), mtv)) {
                            System.out.println("Colliding with the rear");

                            if(o.equals(LandshipsGame.thisTank)) {
                                if(penetration > ((TankChassis) o).armorRear) {
                                    LandshipsGame.networkSystem.hitMessage(damage, LandshipsGame.tanks.indexOf(owner), owner.myShells.indexOf(this));
                                } else {
                                    System.out.println("No penetration on the rear");
                                }
                            }

                            delete();
                            return;
                        }

                        // If we reach this point we did not collide with anything
                    }
                }
            }
        }
    }

    public void delete() {
        LandshipsGame.updateList.remove(this);
        LandshipsGame.renderLayer0.remove(this);
        owner.myShells.remove(this);
    }
}
