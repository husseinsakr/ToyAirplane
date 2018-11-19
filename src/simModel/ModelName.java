package simModel;

import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;
import simulationModelling.SequelActivity;

//
// The Simulation model Class
public class ModelName extends AOSimulationModel
{
	// Constants available from Constants class
	private Constants constants = new Constants();
	/* Parameter */
        // Define the parameters
	private int numCastingStationsSpitfire;
	private int numCastingStationsF16;
	private int numCastingStationsConcorde;
	private int numCuttingGrindingStations;
	private int numCoatingStations;
	private int numInspectionPackagingStations;
	private int numMovers;

	/*-------------Entity Data Structures-------------------*/
	/* Group and Queue Entities */
	public CastingStation[][] castingStations;
	public ProcessingStation[][] processingStations;
	public IOArea[][][] inputOutputQueues = new IOArea[4][2][]; // [type][IN OR OUT][stationId]
	public Mover[] movers;

	// Define the reference variables to the various 
	// entities with scope Set and Unary
	// Objects can be created here or in the Initialise Action

	/* Input Variables */
	// Define any Independent Input Varaibles here
	
	// References to RVP and DVP objects
	protected RVPs rvp;  // Reference to rvp object - object created in constructor
	protected DVPs dvp = new DVPs(this);  // Reference to dvp object
	protected UDPs udp = new UDPs(this);

	// Output object
	protected Output output = new Output(this);
	
	// Output values - define the public methods that return values
	// required for experimentation.


	// Constructor
	public ModelName(double t0time, double tftime, /*define other args,*/ Seeds sd)
	{
		// Initialise parameters here
		this.numCastingStationsSpitfire = 2;
		this.numCastingStationsF16 = 2;
		this.numCastingStationsConcorde = 3;
		this.numCuttingGrindingStations = 1;
		this.numCoatingStations = 1;
		this.numInspectionPackagingStations = 1;
		this.numMovers = 1;

		// Create RVP object with given seed
		rvp = new RVPs(this,sd);

		// Instantiating entities here

		//casting stations
		castingStations = new CastingStation[3][];
		castingStations[constants.SPITFIRE] = new CastingStation[numCastingStationsSpitfire];
		castingStations[constants.F16] = new CastingStation[numCastingStationsF16];
		castingStations[constants.CONCORDE] = new CastingStation[numCastingStationsConcorde];

		//Cutting/Grinding, Coating and Inspection/Packaging stations
		processingStations = new ProcessingStation[3][];
		// we subtract by 1 because casting station is not part of processingStations
		processingStations[constants.CUT - 1] = new ProcessingStation[numCuttingGrindingStations];
		processingStations[constants.COAT - 1] = new ProcessingStation[numCoatingStations];
		processingStations[constants.INSP - 1] = new ProcessingStation[numInspectionPackagingStations];

		//Input and Output Queues
		int totalNumberOfCastingStations = numCastingStationsSpitfire + numCastingStationsF16 + numCastingStationsConcorde;
		inputOutputQueues[constants.CAST][constants.OUT] = new IOArea[totalNumberOfCastingStations];
		inputOutputQueues[constants.CUT][constants.IN] = new IOArea[numCuttingGrindingStations];
		inputOutputQueues[constants.CUT][constants.OUT] = new IOArea[numCuttingGrindingStations];
		inputOutputQueues[constants.COAT][constants.IN] = new IOArea[numCoatingStations];
		inputOutputQueues[constants.COAT][constants.OUT] = new IOArea[numCoatingStations];
		inputOutputQueues[constants.INSP][constants.IN] = new IOArea[numInspectionPackagingStations];

		for (int i = 0; i < totalNumberOfCastingStations; i++){ // create all queue objects for casting stations
			inputOutputQueues[constants.CAST][constants.OUT][i] = new IOArea(constants.IN_OUT_CAP);
		}

		for (int j = 0; j < numCuttingGrindingStations; j++){ // create all queue objects for cutting/grinding stations
			inputOutputQueues[constants.CUT][constants.IN][j] = new IOArea(constants.IN_OUT_CAP);
			inputOutputQueues[constants.CUT][constants.OUT][j] = new IOArea(constants.IN_OUT_CAP);
		}

		for (int k = 0; k < numCoatingStations; k++){ // create all queue objects for coating stations
			inputOutputQueues[constants.COAT][constants.IN][k] = new IOArea(constants.IN_OUT_CAP);
			inputOutputQueues[constants.COAT][constants.OUT][k] = new IOArea(constants.IN_OUT_CAP);
		}

		for (int l = 0; l < numInspectionPackagingStations; l++){ // create all queue objects for insp/packaging stations
			inputOutputQueues[constants.INSP][constants.IN][l] = new IOArea(constants.IN_OUT_CAP);
		}

		//Movers
		movers = new Mover[numMovers];

		
		// rgCounter and qCustLine objects created in Initalise Action
		
		// Initialise the simulation model
		initAOSimulModel(t0time,tftime);   

		     // Schedule the first arrivals and employee scheduling
		Initialise init = new Initialise(this);
		scheduleAction(init);  // Should always be first one scheduled.
		// Schedule other scheduled actions and acitvities here
	}

	/************  Implementation of Data Modules***********/	
	/*
	 * Testing preconditions
	 */
	protected void testPreconditions(Behaviour behObj)
	{
		reschedule (behObj);
		// Check preconditions of Conditional Activities

		// Check preconditions of Interruptions in Extended Activities
	}
	
	public void eventOccured()
	{
		//this.showSBL();
		// Can add other debug code to monitor the status of the system
		// See examples for suggestions on setup logging

		// Setup an updateTrjSequences() method in the Output class
		// and call here if you have Trajectory Sets
		// updateTrjSequences() 
	}

	// Standard Procedure to start Sequel Activities with no parameters
	protected void spStart(SequelActivity seqAct)
	{
		seqAct.startingEvent();
		scheduleActivity(seqAct);
	}	

}


