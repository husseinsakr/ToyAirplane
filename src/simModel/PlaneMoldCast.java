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
        if(model.udp.castingStationReadyForProcessing() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        stationId = toyManufacturingModel.udp.castingStationReadyForProcessing(); //get stationId that can cast
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.BUSY; //set station to busy
    }

    @Override
    public double duration(){
        return toyManufacturingModel.rvp.uOperationTime(Constants.CAST);
    }

    @Override
    public void terminatingEvent(){
        toyManufacturingModel.rcCastingStation[stationId].status = Constants.StationStatus.IDLE; //set station to IDLE
        toyManufacturingModel.rcCastingStation[stationId].bin.n += 6; //increment bin by 6 as we cast 6 planes at a time
        toyManufacturingModel.rcCastingStation[stationId].timeToNextBreak -= Constants.CASTING_TIME; //decrement timeToBreak so we know when station breaks
    }
}
