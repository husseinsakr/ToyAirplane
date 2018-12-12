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
        if (!model.qCastingRepairQueue.isEmpty() && model.rMaintenancePerson.available){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent() {
        stationId = toyManufacturingModel.qCastingRepairQueue.pop();
        toyManufacturingModel.rMaintenancePerson.available = false;
    }

    @Override
    public double duration() {
        return toyManufacturingModel.rvp.uCastingRepairTime();
    }

    @Override
    public void terminatingEvent() {
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.IDLE;
        toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak = toyManufacturingModel.rvp.uCastingBreakTime();
        toyManufacturingModel.rMaintenancePerson.available = true;
        //System.out.println("Maintenance person has finished repairing " + stationId);
        //toyManufacturingModel.printAllVariablesForDebuggingPurposes();
    }

}
