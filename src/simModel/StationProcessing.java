package simModel;

import simulationModelling.ConditionalActivity;

public class StationProcessing extends ConditionalActivity {
    ToyManufacturingModel ToyManufacturingModel;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public StationProcessing(ToyManufacturingModel ToyManufacturingModel){
        this.ToyManufacturingModel = ToyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if (model.udp.stationReadyForOperation() != Constants.NULL){
            returnValue = true;
        }
        return returnValue;
    }

    @Override
    public void startingEvent(){
        areaIdAndStationId = ToyManufacturingModel.udp.stationReadyForOperation();
        this.areaId = areaIdAndStationId[0];
        this.stationId = areaIdAndStationId[1];
        ToyManufacturingModel.rProcessingStation[areaId][stationId].status = Constants.BUSY;
        ToyManufacturingModel.rProcessingStation[areaId][stationId].bin = ToyManufacturingModel.qIOArea[areaId + 1][Constants.IN][stationId].poll();
        //System.out.println("Processing a bin of type: " +  ToyManufacturingModel.rProcessingStation[areaId][stationId].bin.type);
    }

    @Override
    public double duration(){
        return ToyManufacturingModel.rvp.uOperationTime(areaId+1);
    }

    @Override
    public void terminatingEvent(){
        ToyManufacturingModel.rProcessingStation[areaId][stationId].status = Constants.IDLE;
        if ((areaId+1) == Constants.INSP){ // needs to be worked on
            //update output here for leaving planes
            //System.out.println("I have a bin type of: " + ToyManufacturingModel.rProcessingStation[areaId][stationId].bin.type);
            switch (ToyManufacturingModel.rProcessingStation[areaId][stationId].bin.type){
                case Constants.SPITFIRE:
                    for(int i = 0; i < Constants.BIN_CAP; i++) {
                        if(ToyManufacturingModel.rvp.uNumPlanesAccepted())
                            ToyManufacturingModel.output.numSpitfireProduced++;
                    }
                    break;
                case Constants.F16:
                    for(int i = 0; i < Constants.BIN_CAP; i++) {
                        if(ToyManufacturingModel.rvp.uNumPlanesAccepted())
                            ToyManufacturingModel.output.numF16Produced++;
                    }
                    break;
                case Constants.CONCORDE:
                    for(int i = 0; i < Constants.BIN_CAP; i++) {
                        if (ToyManufacturingModel.rvp.uNumPlanesAccepted())
                            ToyManufacturingModel.output.numConcordeProduced++;
                    }
                    break;
                default:
                    System.out.println("Error: Invalid plane type. Check this!");
                    break;
            }
        }
        //System.out.println("Station with type " + (areaId+1) + " and stationId " + stationId + " have finished processing");
        ToyManufacturingModel.printAllVariablesForDebuggingPurposes();

    }
}
