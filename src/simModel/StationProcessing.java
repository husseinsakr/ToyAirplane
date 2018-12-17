package simModel;

import simulationModelling.ConditionalActivity;

public class StationProcessing extends ConditionalActivity {
    ToyManufacturingModel toyManufacturingModel;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public StationProcessing(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if (model.udp.stationReadyForOperation() != Constants.NO_RESULT){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        areaIdAndStationId = toyManufacturingModel.udp.stationReadyForOperation(); //returns areaID and stationID of station ready for processing
        this.areaId = areaIdAndStationId[0];
        this.stationId = areaIdAndStationId[1];
        toyManufacturingModel.rProcessingStation[areaId][stationId].status = Constants.StationStatus.BUSY; //set station to busy
        toyManufacturingModel.rProcessingStation[areaId][stationId].bin = toyManufacturingModel.qIOArea[areaId][Constants.IN][stationId].remove(0); //get bin from input area to start processing!
    }

    @Override
    public double duration(){
        return toyManufacturingModel.rvp.uOperationTime(areaId);
    }

    @Override
    public void terminatingEvent(){
        toyManufacturingModel.rProcessingStation[areaId][stationId].status = Constants.StationStatus.IDLE; //set station to IDLE after processing is done
        if ((areaId) == Constants.INSP){ //if we at inspection station, we need to update our output!
            for(int i = 0; i < Constants.BIN_CAP; i++){
                if(toyManufacturingModel.rvp.uNumPlanesAccepted()){
                    switch (toyManufacturingModel.rProcessingStation[areaId][stationId].bin.type) {
                        case SPITFIRE:
                            toyManufacturingModel.output.numSpitfireProduced++;
                            break;
                        case F16:
                            toyManufacturingModel.output.numF16Produced++;
                            break;
                        case CONCORDE:
                            toyManufacturingModel.output.numConcordeProduced++;
                            break;
                    }
                }
            }
            toyManufacturingModel.rProcessingStation[areaId][stationId].bin = Constants.NO_BIN; //remove bin from inspection station
        }
    }
}
