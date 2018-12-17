package simModel;

import cern.jet.random.*;
import cern.jet.random.engine.MersenneTwister;
import dataModelling.TriangularVariate;

class RVPs 
{
	ToyManufacturingModel model; // for accessing the clock
    // Data Models - i.e. random veriate generators for distributions
	// are created using Colt classes, define

	public final double CASTING_BREAK_MEAN = 60;
	public final double RT_MEAN_TIME_TO_REPAIR = 8;
	public final double RT_STD_DEV_TIME_TO_REPAIR = 2;
	public final double CT_LOWER_TIME_TO_CUT = 0.25;
	public final double CT_MODE_TIME_TO_CUT = 0.28;
	public final double CT_UPPER_TIME_TO_CUT = 0.35;
	public final double GT_ALPHA = 43;
	public final double GT_LAMBDA = 178;
	public final double IT_LOWER_TIME_TO_INSPECT = 0.27;
	public final double IT_MODE_TIME_TO_INSPECT = 0.30;
	public final double IT_UPPER_TIME_TO_INSPECT = 0.40;
	public final double PERCENT_PASS = 0.88;

	// RVPS

	public TriangularVariate cutTime; // cutting time

	public Gamma grindTime; //grind time

	public TriangularVariate inspTime; //inspection time

	public Normal castRepairTime; //cast repair time

	public Exponential castBreakTime; //cast break time

	public MersenneTwister typeRandGen; //random generator used for passing inspection


	// Constructor
	protected RVPs(ToyManufacturingModel model, Seeds sd) 
	{ 
		this.model = model; 
		// Distribution functions
		cutTime = new TriangularVariate(CT_LOWER_TIME_TO_CUT, CT_MODE_TIME_TO_CUT, CT_UPPER_TIME_TO_CUT, new MersenneTwister(sd.cuttingTime));
		grindTime = new Gamma(GT_ALPHA, GT_LAMBDA, new MersenneTwister(sd.grindingTime));
		inspTime = new TriangularVariate(IT_LOWER_TIME_TO_INSPECT, IT_MODE_TIME_TO_INSPECT, IT_UPPER_TIME_TO_INSPECT, new MersenneTwister(sd.packingTime));
		castRepairTime = new Normal(RT_MEAN_TIME_TO_REPAIR, RT_STD_DEV_TIME_TO_REPAIR, new MersenneTwister(sd.repairTime));
		castBreakTime = new Exponential(1.0/CASTING_BREAK_MEAN, new MersenneTwister(sd.breakTime));
		typeRandGen = new MersenneTwister();
	}
	
	/* Random Variate Procedure for Arrivals */

	public final double uCastingBreakTime(){
		return castBreakTime.nextDouble();
	}

	public final double uCastingRepairTime(){
		return castRepairTime.nextDouble();
	}

	//method that returns the operation time of each type of station
	public final double uOperationTime(int areaId){
		double operationTime = model.constants.NONE; // just a default value
		switch(areaId){
			case Constants.CAST:
				operationTime = Constants.CASTING_TIME;
				break;
			case Constants.CUT:
				operationTime = Constants.BIN_CAP * (cutTime.next() + grindTime.nextDouble());
				break;
			case Constants.COAT:
				operationTime = Constants.COATING_TIME;
				break;
			case Constants.INSP:
				operationTime = Constants.BIN_CAP * (inspTime.next());
				break;
			default:
				System.out.printf("invalid areaId");
		}
		return operationTime;
	}

	//method that returns TRUE if value lands under PERCENT_PASS and FALSE otherwise
	public final boolean uNumPlanesAccepted(){
		double randNum = typeRandGen.nextDouble();
		if (randNum < PERCENT_PASS)
			return true;
		else
			return false;
	}

}
