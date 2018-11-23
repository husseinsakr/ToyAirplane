package simModel;

import simulationModelling.ConditionalActivity;

public class StationProcessing extends ConditionalActivity {
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
        modelName.processingStations[areaId][stationId].bin = modelName.inputOutputQueues[areaId + 1][Constants.IN][stationId].poll();
        //System.out.println("Processing a bin of type: " +  modelName.processingStations[areaId][stationId].bin.type);
    }

    @Override
    public double duration(){
        return modelName.rvp.uOperationTime(areaId+1);
    }

    @Override
    public void terminatingEvent(){
        modelName.processingStations[areaId][stationId].status = Constants.IDLE;
        if ((areaId+1) == Constants.INSP){ // needs to be worked on
            //update output here for leaving planes
           // System.out.println("I have a bin type of: " + modelName.processingStations[areaId][stationId].bin.type);
            switch (modelName.processingStations[areaId][stationId].bin.type){
                case Constants.SPITFIRE:
                    modelName.output.numSpitfireProduced += Constants.BIN_CAP;
                    break;
                case Constants.F16:
                    modelName.output.numF16Produced += Constants.BIN_CAP;
                    break;
                case Constants.CONCORDE:
                    modelName.output.numConcordeProduced += Constants.BIN_CAP;
                    break;
                default:
                    System.out.println("Error: Invalid plane type. Check this!");
                    break;
            }
        }
        //System.out.println("Station with type " + (areaId+1) + " and stationId " + stationId + " have finished processing");
        modelName.printAllVariablesForDebuggingPurposes();

    }
}
