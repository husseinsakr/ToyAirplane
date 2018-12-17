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
        if (model.udp.binReadyForOutput() != Constants.NO_RESULT){ //we use Constants.NO_RESULT instead of NONE because it returns a NULL when no result is found
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaIdAndStationId = toyManufacturingModel.udp.binReadyForOutput(); //get areaId and stationId where u could output a bin from station
        areaId = areaIdAndStationId[0];
        stationId = areaIdAndStationId[1];
        if(areaId == Constants.CAST){ //if we at casting station, we create a new bin for the casting station after we output current one
            toyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(toyManufacturingModel.rcCastingStation[stationId].bin);
            toyManufacturingModel.rcCastingStation[stationId].bin = new Bin();
            toyManufacturingModel.rcCastingStation[stationId].bin.type = toyManufacturingModel.rcCastingStation[stationId].type;
            toyManufacturingModel.rcCastingStation[stationId].bin.n = 0;
        } else if(areaId != Constants.INSP){ //if we at any other station then inspection, put the bin in output and set the station to NO_BIN afterwards in station
            Bin igBin = toyManufacturingModel.rProcessingStation[areaId][stationId].bin;
            toyManufacturingModel.qIOArea[areaId][Constants.OUT][stationId].add(igBin);
            toyManufacturingModel.rProcessingStation[areaId][stationId].bin = Constants.NO_BIN;
        }
    }

}
