package simModel;

import simulationModelling.Activity;

public class PlaneMoldCast extends Activity {
    ToyManufacturingModel toyManufacturingModel;
    int stationId;

    public PlaneMoldCast(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.castingStationReadyForProcessing() != model.constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        stationId = toyManufacturingModel.udp.castingStationReadyForProcessing();
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.BUSY;
    }

    @Override
    public double duration(){
        return toyManufacturingModel.rvp.uOperationTime(toyManufacturingModel.constants.CAST);
    }

    @Override
    public void terminatingEvent(){
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.IDLE;
        toyManufacturingModel.rcCastingStation[stationId].bin.n += 6;
        toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak -= Constants.CASTING_TIME;
    }
}
