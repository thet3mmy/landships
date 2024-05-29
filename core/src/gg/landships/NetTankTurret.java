package gg.landships;

//
// NetTankTurret
//
// In networking we are going to make false tank entities that look identical to
// the real ones, but are not actually updated by the game (instead by the netcode)
//

public class NetTankTurret extends TankTurret {
    NetTankTurret(NetTankBase b) {
        super(b);
    }

    // Make this method do nothing
    // This is because we don't want the networked turrets trying to turn themselves
    @Override
    public void onDrawFinished() {}
}
