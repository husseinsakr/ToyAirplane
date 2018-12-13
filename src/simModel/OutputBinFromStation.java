package simModel;

import simulationModelling.ConditionalAction;

public class OutputBinFromStation extends ConditionalAction{

    ToyManufacturingModel toyManufacturingModel;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public OutputBinFromStation(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if (model.udp.binReadyForOutput() != Constants.NULL){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaIdAndStationId = toyManufacturingModel.udp.binReadyForOutput();
        areaId = areaIdAndStationId[0];
        stationId = areaIdAndStationId[1];
        if(areaId == Constants.CAST){
            toyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(toyManufacturingModel.rcCastingStation[stationId].bin);
            toyManufacturingModel.rcCastingStation[stationId].bin = new Bin();
            toyManufacturingModel.rcCastingStation[stationId].bin.type = toyManufacturingModel.rcCastingStation[stationId].type;
            toyManufacturingModel.rcCastingStation[stationId].bin.n = 0;
        } else if(areaId != Constants.INSP){
            Bin igBin = toyManufacturingModel.rProcessingStation[areaId][stationId].bin;
            toyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(igBin);
            toyManufacturingModel.rProcessingStation[areaId][stationId].bin = Constants.NO_BIN;
        } else { // We need to do something for when we at INSP station I am guessing
            toyManufacturingModel.rProcessingStation[areaId][stationId].bin = Constants.NO_BIN;
        }
    }

}
