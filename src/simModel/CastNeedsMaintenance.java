package simModel;

import simulationModelling.Activity;

public class CastNeedsMaintenance extends Activity {
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

    @Override
    public void startingEvent() {
        stationId = toyManufacturingModel.udp.castingAboutToBreak();
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.NEEDS_MAINTENANCE;
    }

    @Override
    public double duration() {
        return toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak;
    }

    @Override
    public void terminatingEvent() {
        toyManufacturingModel.qCastingRepairQueue.add(stationId);
    }

}
