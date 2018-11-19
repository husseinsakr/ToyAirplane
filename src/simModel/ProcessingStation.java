package simModel;

public class ProcessingStation {
    protected int status; //status of station
    protected Bin bin; //reference variable of bin in station
    protected int type;
    public IOArea input;
    public IOArea output;

    public ProcessingStation(){
        this.status = 1;
    }
}