package simModel;

import simulationModelling.Activity;

public class CastRepaired extends Activity {
    ToyManufacturingModel toyManufacturingModel;
    int stationId;

    protected CastRepaired(ToyManufacturingModel toyManufacturingModel) {
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model) {
        boolean returnValue = false;
        if (model.qCastingRepairQueue.size() != 0 && model.rMaintenancePerson.available){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent() {
        stationId = toyManufacturingModel.qCastingRepairQueue.pop(); //gets the casting station Id from the head of the casting repair queue
        toyManufacturingModel.rMaintenancePerson.available = false; //set maintenance person to busy
    }

    @Override
    public double duration() {
        return toyManufacturingModel.rvp.uCastingRepairTime();
    }

    @Override
    public void terminatingEvent() {
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.IDLE; //set station status to idle
        toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak = toyManufacturingModel.rvp.uCastingBreakTime(); //give the casting station a new timeToBreak
        toyManufacturingModel.rMaintenancePerson.available = true; //set maintenance person to available
    }

}
