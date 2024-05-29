package gg.landships;

public class NetTankBase extends TankBase {
    NetTankBase() {
        super();
        turret = new NetTankTurret(this);
    }

    @Override
    public void think() {
        thinkTurret();
    }
}
