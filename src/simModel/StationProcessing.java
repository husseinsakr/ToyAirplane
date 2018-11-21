package simModel;

import simulationModelling.Activity;

public class StationProcessing extends Activity {
    ModelName modelName;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public StationProcessing(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if (model.udp.stationReadyForOperation() != null){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        areaIdAndStationId = modelName.udp.stationReadyForOperation();
        this.areaId = areaIdAndStationId[0];
        this.stationId = areaIdAndStationId[1];
        modelName.processingStations[areaId][stationId].status = Constants.BUSY;
        modelName.processingStations[areaId][stationId].bin = modelName.inputOutputQueues[areaId][Constants.IN][stationId].poll();
    }

    @Override
    public double duration(){
        return modelName.rvp.uOperationTime(areaId);
    }

    @Override
    public void terminatingEvent(){
        modelName.processingStations[areaId][stationId].status = Constants.IDLE;
        if (areaId == Constants.INSP){ // needs to be worked on
            //update output here for leaving planes
             modelName.processingStations[areaId][stationId].bin = null;

        }

    }
}
