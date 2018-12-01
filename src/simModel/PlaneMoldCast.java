package simModel;

import simulationModelling.Activity;

public class PlaneMoldCast extends Activity {
    ToyManufacturingModel ToyManufacturingModel;
    int stationId;

    public PlaneMoldCast(ToyManufacturingModel ToyManufacturingModel){
        this.ToyManufacturingModel = ToyManufacturingModel;
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
        stationId = ToyManufacturingModel.udp.castingStationReadyForProcessing(); // casting station ready to cast
        ToyManufacturingModel.rcCastingStation[stationId].status = ToyManufacturingModel.constants.BUSY;
    }

    @Override
    public double duration(){
        return ToyManufacturingModel.rvp.uOperationTime(ToyManufacturingModel.constants.CAST);
    }

    @Override
    public void terminatingEvent(){
        ToyManufacturingModel.rcCastingStation[stationId].status = ToyManufacturingModel.constants.IDLE;
        ToyManufacturingModel.rcCastingStation[stationId].bin.n += 6;
        ToyManufacturingModel.rcCastingStation[stationId].timeToNextBreak -= ToyManufacturingModel.constants.CASTING_TIME;
    }
}
