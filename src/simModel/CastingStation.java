package simModel;

public class CastingStation extends ProcessingStation {
    private static int uniqueId = 1;
    public int stationId;
    public IOArea input = null;

    public CastingStation(int type){
        super();
        this.bin = new Bin(type);
        this.stationId = uniqueId++;

    }

}
