package simModel;

import simulationModelling.ConditionalActivity;

public class CastNeedsMaintenance extends ConditionalActivity {
    ToyManufacturingModel toyManufacturingModel;
    int stationId;

    public CastNeedsMaintenance(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){ //checks for pre condition
        boolean returnValue = false;
        if(model.udp.castingAboutToBreak() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent() {
        stationId = toyManufacturingModel.udp.castingAboutToBreak(); //gets the stationId of casting station that is about to break
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.NEEDS_MAINTENANCE; //changes status of station
    }

    @Override
    public double duration() {
        return toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak;
    }

    @Override
    public void terminatingEvent() {
        toyManufacturingModel.qCastingRepairQueue.add(stationId); //adds casting station to repairQueue when station breaks
    }

}
