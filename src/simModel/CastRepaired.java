package simModel;

import simulationModelling.Activity;

public class CastRepaired extends Activity {
    ModelName modelName;
    int stationId;

    protected CastRepaired(ModelName modelName) {
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model) {
        boolean returnValue = false;
        if (!model.castingRepairQueue.isEmpty() && model.maintenancePerson.available
            && model.getClock() < model.endTime){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent() {
        stationId = modelName.castingRepairQueue.pop();
        modelName.maintenancePerson.available = false;
    }

    @Override
    public double duration() {
        return modelName.rvp.uCastingRepairTime();
    }

    @Override
    public void terminatingEvent() {
        modelName.castingStations[stationId].status = Constants.IDLE;
        modelName.castingStations[stationId].timeToNextBreak = modelName.rvp.uCastingBreakTime();
        modelName.maintenancePerson.available = true;
        System.out.println("Maintenance person has finished repairing " + stationId);
        modelName.printAllVariablesForDebuggingPurposes();
    }

}
