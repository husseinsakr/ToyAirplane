package simModel;

import simulationModelling.ConditionalActivity;

public class MoveBins extends ConditionalActivity {
    ToyManufacturingModel ToyManufacturingModel;
    int currentArea, destinationArea, moverId;

    public MoveBins(ToyManufacturingModel ToyManufacturingModel){
        this.ToyManufacturingModel = ToyManufacturingModel;
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
        //int[] areaIdAndStationId = ToyManufacturingModel.udp.canStartMovingBins();
        this.currentArea = ToyManufacturingModel.udp.canStartMovingBins();
        //currentArea = areaIdAndStationId[0];
        //stationId = areaIdAndStationId[1];
        moverId = ToyManufacturingModel.gMoverLines[currentArea][Constants.OUT].poll();
        destinationArea = ToyManufacturingModel.udp.fillTrolley(moverId, currentArea);
    }

    @Override
    public double duration(){
        return ToyManufacturingModel.dvp.uMovingStationsTime(currentArea, destinationArea);
    }

    @Override
    public void terminatingEvent(){
        ToyManufacturingModel.gMoverLines[destinationArea][Constants.IN].add(moverId);
            //System.out.println("Finished moving bins from " + currentArea + " with stationId to " + destinationArea );
        ToyManufacturingModel.printAllVariablesForDebuggingPurposes();
    }
}
