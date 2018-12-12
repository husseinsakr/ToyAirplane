package simModel;

public class ProcessingStation {
    protected Constants.StationStatus status = Constants.StationStatus.IDLE; //status of station
    protected Bin bin = Constants.NO_BIN; //reference variable of bin in station
}