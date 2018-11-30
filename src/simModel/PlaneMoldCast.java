package simModel;

import simulationModelling.Activity;

public class PlaneMoldCast extends Activity {
    ModelName modelName;
    int stationId;

    public PlaneMoldCast(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if(model.udp.castingStationReadyForProcessing() != model.constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        stationId = modelName.udp.castingStationReadyForProcessing(); // casting station ready to cast
        modelName.castingStations[stationId].status = modelName.constants.BUSY;
    }

    @Override
    public double duration(){
        return modelName.rvp.uOperationTime(modelName.constants.CAST);
    }

    @Override
    public void terminatingEvent(){
        modelName.castingStations[stationId].status = modelName.constants.IDLE;
        if(modelName.castingStations[stationId].bin == null){
            modelName.castingStations[stationId].bin = new Bin();
            modelName.castingStations[stationId].bin.type = modelName.castingStations[stationId].type;
            modelName.castingStations[stationId].bin.n = Constants.EMPTY;
        }
        modelName.castingStations[stationId].bin.n += 6;
        modelName.castingStations[stationId].timeToNextBreak -= modelName.constants.CASTING_TIME;
        //System.out.println("Casting station with id " + stationId + " has finished casting. ");
        modelName.printAllVariablesForDebuggingPurposes();
    }
}
