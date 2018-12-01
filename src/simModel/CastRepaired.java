package simModel;

import simulationModelling.Activity;

public class CastRepaired extends Activity {
    ToyManufacturingModel ToyManufacturingModel;
    int stationId;

    protected CastRepaired(ToyManufacturingModel ToyManufacturingModel) {
        this.ToyManufacturingModel = ToyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model) {
        boolean returnValue = false;
        if (!model.qCastingRepairQueue.isEmpty() && model.rMaintenancePerson.available
            && model.getClock() < model.endTime){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent() {
        stationId = ToyManufacturingModel.qCastingRepairQueue.pop();
        ToyManufacturingModel.rMaintenancePerson.available = false;
    }

    @Override
    public double duration() {
        return ToyManufacturingModel.rvp.uCastingRepairTime();
    }

    @Override
    public void terminatingEvent() {
        ToyManufacturingModel.rcCastingStation[stationId].status = Constants.IDLE;
        ToyManufacturingModel.rcCastingStation[stationId].timeToNextBreak = ToyManufacturingModel.rvp.uCastingBreakTime();
        ToyManufacturingModel.rMaintenancePerson.available = true;
        //System.out.println("Maintenance person has finished repairing " + stationId);
        ToyManufacturingModel.printAllVariablesForDebuggingPurposes();
    }

}
