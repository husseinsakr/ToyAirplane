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
        areaId = toyManufacturingModel.udp.canDistributeBins(); //returns the areaId where u could distribute bins
        int moverId = toyManufacturingModel.qMoverLines[areaId][Constants.IN].pop();
        mover = toyManufacturingModel.rgMover[moverId]; //get the mover with a full trolley to distribute bins
        toyManufacturingModel.udp.emptyTrolley(moverId, areaId); //udp used to empty out trolley and distribute bins evenly
        toyManufacturingModel.qMoverLines[areaId % Constants.INSP][Constants.OUT].add(moverId); //send mover to output area after distributing
    }



}
