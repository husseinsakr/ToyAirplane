package simModel;

import simulationModelling.ConditionalAction;

public class DistributeBins extends ConditionalAction {
    ToyManufacturingModel toyManufacturingModel;
    int areaId;
    Mover mover;

    public DistributeBins(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.canDistributeBins() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaId = toyManufacturingModel.udp.canDistributeBins();
        int moverId = toyManufacturingModel.qMoverLines[areaId][Constants.IN].pop();
        mover = toyManufacturingModel.rgMover[moverId];
        toyManufacturingModel.udp.emptyTrolley(moverId, areaId);
        toyManufacturingModel.qMoverLines[areaId % Constants.INSP][Constants.OUT].add(moverId);
    }



}
