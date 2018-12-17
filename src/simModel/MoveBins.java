package simModel;

import simulationModelling.ConditionalActivity;

public class MoveBins extends ConditionalActivity {
    ToyManufacturingModel toyManufacturingModel;
    int currentArea, destinationArea, moverId;

    public MoveBins(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.canStartMovingBins() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        this.currentArea = toyManufacturingModel.udp.canStartMovingBins(); //get current areaId where u could move bins
        moverId = toyManufacturingModel.qMoverLines[currentArea][Constants.OUT].poll(); //get the first mover in the queue
        destinationArea = toyManufacturingModel.udp.fillTrolley(moverId, currentArea); //fill the mover's trolley
    }

    @Override
    public double duration(){
        return toyManufacturingModel.dvp.uMovingStationsTime(currentArea, destinationArea);
    }

    @Override
    public void terminatingEvent(){
        toyManufacturingModel.qMoverLines[destinationArea][Constants.IN].add(moverId); //add mover to input queue of areaId he just reached
    }
}
