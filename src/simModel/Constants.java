package simModel;

class Constants 
{
	/* Constants */
	// Define constants as static
	// Example: protected final static double realConstant = 8.0;
    public final static int CAST = 0;
    public final static int CUT = 1;
    public final static int COAT = 2;
    public final static int INSP = 3;
    public final static int IN = 0;
    public final static int OUT = 1;
    public final static int BIN_CAP = 24;
    public final static int MOVER_CAP = 3;
    public final static int IN_OUT_CAP = 5;
    public final static int CASTING_TIME = 3;
    public final static int COATING_TIME = 12;
    public final static int EMPTY = 0;
    public final static int NONE = -1;
    public final static Bin NO_BIN = null;
    public final static Object NULL = null;

    enum PlaneType {SPITFIRE, CONCORDE, F16};
    enum StationStatus {IDLE, BUSY, NEEDS_MAINTENANCE};

}
