package simModel;

import simulationModelling.ConditionalAction;

public class CastNeedsMaintenance extends ConditionalAction {
    ToyManufacturingModel ToyManufacturingModel;
    int stationId;

    public CastNeedsMaintenance(ToyManufacturingModel ToyManufacturingModel){
        this.ToyManufacturingModel = ToyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.castingAboutToBreak() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        stationId = ToyManufacturingModel.udp.castingAboutToBreak();
        ToyManufacturingModel.rcCastingStation[stationId].status = Constants.NEEDS_MAINTENANCE;
        ToyManufacturingModel.qCastingRepairQueue.add(stationId);
    }
}
