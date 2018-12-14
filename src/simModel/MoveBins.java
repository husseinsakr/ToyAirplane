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
        this.currentArea = toyManufacturingModel.udp.canStartMovingBins();
        moverId = toyManufacturingModel.qMoverLines[currentArea][Constants.OUT].poll();
        destinationArea = toyManufacturingModel.udp.fillTrolley(moverId, currentArea);
    }

    @Override
    public double duration(){
        return toyManufacturingModel.dvp.uMovingStationsTime(currentArea, destinationArea);
    }

    @Override
    public void terminatingEvent(){
        toyManufacturingModel.qMoverLines[destinationArea][Constants.IN].add(moverId);
        toyManufacturingModel.printAllVariablesForDebuggingPurposes();
    }
}
