package simModel;

import simulationModelling.ConditionalAction;

public class CastNeedsMaintenance extends ConditionalAction {
    ToyManufacturingModel toyManufacturingModel;
    int stationId;

    public CastNeedsMaintenance(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.castingAboutToBreak() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        stationId = toyManufacturingModel.udp.castingAboutToBreak();
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.NEEDS_MAINTENANCE;
        toyManufacturingModel.qCastingRepairQueue.add(stationId);
    }
}
