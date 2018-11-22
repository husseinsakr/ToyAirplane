package simModel;

import simulationModelling.ConditionalAction;

public class OutputBinFromStation extends ConditionalAction{

    ModelName modelName;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public OutputBinFromStation(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if (model.udp.binReadyForOutput() != null){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaIdAndStationId = modelName.udp.binReadyForOutput();
        areaId = areaIdAndStationId[0];
        stationId = areaIdAndStationId[1];
        if(areaId == Constants.CAST){
            modelName.inputOutputQueues[areaId][Constants.OUT][stationId].add(modelName.castingStations[stationId].bin);
            modelName.castingStations[stationId].bin = new Bin();
            modelName.castingStations[stationId].bin.type = modelName.castingStations[stationId].type;
            modelName.castingStations[stationId].bin.n = 0;
        } else if(areaId != Constants.INSP){
            modelName.inputOutputQueues[areaId][Constants.OUT][stationId].add(modelName.processingStations[areaId - 1][stationId].bin);
            modelName.processingStations[areaId][stationId].bin = null;
        } else { // We need to do something for when we at INSP station I am guessing

        }
    }

}
