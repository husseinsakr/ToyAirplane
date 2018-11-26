package simModel;

import simulationModelling.ConditionalActivity;

public class MoveBins extends ConditionalActivity {
    ModelName modelName;
    int currentArea, destinationArea, moverId;

    public MoveBins(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if(model.udp.canStartMovingBins() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        //int[] areaIdAndStationId = modelName.udp.canStartMovingBins();
        this.currentArea = modelName.udp.canStartMovingBins();
        //currentArea = areaIdAndStationId[0];
        //stationId = areaIdAndStationId[1];
        moverId = modelName.moverLines[currentArea][Constants.OUT].poll();
        destinationArea = modelName.udp.fillTrolley(moverId, currentArea);
    }

    @Override
    public double duration(){
        return modelName.dvp.uMovingStationsTime(currentArea, destinationArea);
    }

    @Override
    public void terminatingEvent(){
        modelName.moverLines[destinationArea][Constants.IN].add(moverId);
        System.out.println("Finished moving bins from " + currentArea + " with stationId to " + destinationArea );
        modelName.printAllVariablesForDebuggingPurposes();
    }
}
