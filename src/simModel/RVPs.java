package simModel;

import cern.jet.random.Exponential;
import cern.jet.random.engine.MersenneTwister;

class RVPs 
{
	ModelName model; // for accessing the clock
    // Data Models - i.e. random veriate generators for distributions
	// are created using Colt classes, define 
	// reference variables here and create the objects in the
	// constructor with seeds
	public final double CASTING_BREAK_MEAN = 30;
	public final double RT_MEAN_TIME_TO_REPAIR = 8;
	public final double RT_STD_DEV_TIME_TO_REPAIR = 2;
	public final double PROC_TIME_CAST = model.constants.CASTING_TIME;
	public final double CT_LOWER_TIME_TO_CUT = 0.25;
	public final double CT_MODE_TIME_TO_CUT = 0.28;
	public final double CT_UPPER_TIME_TO_CUT = 0.35;
	public final double GT_MEAN_TIME_TO_GRIND = 111; // NEEDS TO BE CHANGED
	public final double GT_STD_DEV_TIME_TO_GRIND = 111; // NEEDS TO BE CHANGED
	public final double PROC_TIME_COAT = model.constants.COATING_TIME;
	public final double IT_LOWER_TIME_TO_INSPECT = 0.27;
	public final double IT_MODE_TIME_TO_INSPECT = 0.30;
	public final double IT_UPPER_TIME_TO_INSPECT = 0.40;
	public final double PERCENT_PASS = 0.88;
	public final double PERCENT_REJECT = 0.12;




	// Constructor
	protected RVPs(ModelName model, Seeds sd) 
	{ 
		this.model = model; 
		// Set up distribution functions
		interArrDist = new Exponential(1.0/WMEAN1,  
				                       new MersenneTwister(sd.seed1));
	}
	
	/* Random Variate Procedure for Arrivals */
	private Exponential interArrDist;  // Exponential distribution for interarrival times
	private final double WMEAN1=10.0;
	protected double duInput()  // for getting next value of duInput
	{
	    double nxtInterArr;

        nxtInterArr = interArrDist.nextDouble();
	    // Note that interarrival time is added to current
	    // clock value to get the next arrival time.
	    return(nxtInterArr+model.getClock());
	}

	public final double uCastingBreakTime(){
		return 0.0;
	}

	public final double uCastingRepairTime(){
		return 0.0;
	}

	public final double uOperationTime(){
		return 0.0;
	}

	public final double uNumPlanesAccepted(){
		return 0.0;
	}

}
