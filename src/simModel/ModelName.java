package simModel;

import com.sun.tools.javac.Main;
import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;
import simulationModelling.SequelActivity;

//
// The Simulation model Class
public class ModelName extends AOSimulationModel
{
	// Constants available from Constants class
	public Constants constants = new Constants();
	/* Parameter */
        // Define the parameters
	public int numCastingStationsSpitfire;
	public int numCastingStationsF16;
	public int numCastingStationsConcorde;
	public int numCuttingGrindingStations;
	public int numCoatingStations;
	public int numInspectionPackagingStations;
	public int numMovers;

	/*-------------Entity Data Structures-------------------*/
	/* Group and Queue Entities */
	public CastingStation[] castingStations;
	public ProcessingStation[][] processingStations;
	public IOArea[][][] inputOutputQueues = new IOArea[4][2][]; // [areaID][IN OR OUT][stationId]
	public Mover[] movers;
	public MoversLine[][] moverLines = new MoversLine[4][2]; // [areaID][IN or OUT]

	// Define the reference variables to the various 
	// entities with scope Set and Unary
	public MaintenancePerson maintenancePerson;
	public CastingRepairQueue castingRepairQueue;
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
		this.numMovers = 25;

		// Create RVP object with given seed
		rvp = new RVPs(this,sd);

		// Instantiating entities here

		//casting stations
		int totalNumberOfCastingStations = numCastingStationsSpitfire + numCastingStationsF16 + numCastingStationsConcorde;
		castingStations = new CastingStation[totalNumberOfCastingStations];

		//Cutting/Grinding, Coating and Inspection/Packaging stations
		int types = 3; //types of stations
		processingStations = new ProcessingStation[types][];
		// We subtract by 1 because casting station is not part of processingStations
		processingStations[constants.CUT - 1] = new ProcessingStation[numCuttingGrindingStations];
		processingStations[constants.COAT - 1] = new ProcessingStation[numCoatingStations];
		processingStations[constants.INSP - 1] = new ProcessingStation[numInspectionPackagingStations];

		// Input and Output Queues
		inputOutputQueues[constants.CAST][constants.OUT] = new IOArea[totalNumberOfCastingStations];
		inputOutputQueues[constants.CUT][constants.IN] = new IOArea[numCuttingGrindingStations];
		inputOutputQueues[constants.CUT][constants.OUT] = new IOArea[numCuttingGrindingStations];
		inputOutputQueues[constants.COAT][constants.IN] = new IOArea[numCoatingStations];
		inputOutputQueues[constants.COAT][constants.OUT] = new IOArea[numCoatingStations];
		inputOutputQueues[constants.INSP][constants.IN] = new IOArea[numInspectionPackagingStations];

		// Movers
		movers = new Mover[numMovers];

		// Maintenance person
		maintenancePerson = new MaintenancePerson();

		// CastingRepairQueue
		castingRepairQueue = new CastingRepairQueue();

		// MoverLine Queues
		moverLines[constants.CAST][constants.OUT] = new MoversLine();
		moverLines[constants.CUT][constants.IN] = new MoversLine();
		moverLines[constants.CUT][constants.OUT] = new MoversLine();
		moverLines[constants.COAT][constants.IN] = new MoversLine();
		moverLines[constants.COAT][constants.OUT] = new MoversLine();
		moverLines[constants.INSP][constants.IN] = new MoversLine();
		
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


