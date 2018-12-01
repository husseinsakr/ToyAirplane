package simModel;

import simulationModelling.ConditionalAction;

public class OutputBinFromStation extends ConditionalAction{

    ToyManufacturingModel ToyManufacturingModel;
    int[] areaIdAndStationId;
    int areaId, stationId;

    public OutputBinFromStation(ToyManufacturingModel ToyManufacturingModel){
        this.ToyManufacturingModel = ToyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if (model.udp.binReadyForOutput() != null){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaIdAndStationId = ToyManufacturingModel.udp.binReadyForOutput();
        areaId = areaIdAndStationId[0];
        stationId = areaIdAndStationId[1];
        if(areaId == Constants.CAST){
            ToyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(ToyManufacturingModel.rcCastingStation[stationId].bin);
            ToyManufacturingModel.rcCastingStation[stationId].bin = new Bin();
            ToyManufacturingModel.rcCastingStation[stationId].bin.type = ToyManufacturingModel.rcCastingStation[stationId].type;
            ToyManufacturingModel.rcCastingStation[stationId].bin.n = 0;
        } else if(areaId != Constants.INSP){
            Bin igBin = new Bin();
            igBin.type = ToyManufacturingModel.rProcessingStation[areaId - 1][stationId].bin.type;
            igBin.n = ToyManufacturingModel.rProcessingStation[areaId - 1][stationId].bin.n;
            ToyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(igBin);
            ToyManufacturingModel.rProcessingStation[areaId - 1][stationId].bin = null;
        } else { // We need to do something for when we at INSP station I am guessing
            ToyManufacturingModel.rProcessingStation[areaId - 1][stationId].bin = null;
        }
    }

}
