package gg.landships;

public class NetTankBase extends TankBase {
    NetTankBase() {
        super();
        turret = new NetTankTurret(this);

        // So this seems pretty weird, but we do this so that
        // we don't see the dummy tanks when they are not controlled by the net code.
        getSprite().setPosition(-999999, -999999);
    }

    @Override
    public void think() {
        thinkTurret();
    }
}
