package simModel;

import simulationModelling.ConditionalAction;

public class CastNeedsMaintenance extends ConditionalAction {
    ModelName modelName;
    int stationId;

    public CastNeedsMaintenance(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if(model.udp.castingAboutToBreak() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        stationId = modelName.udp.castingAboutToBreak();
        modelName.castingStations[stationId].status = Constants.NEEDS_MAINTENANCE;
        modelName.castingRepairQueue.add(stationId);
    }
}
