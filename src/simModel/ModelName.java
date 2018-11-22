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
	public static int x = 0;
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
		this.numCastingStationsSpitfire = 1;
		this.numCastingStationsF16 = 1;
		this.numCastingStationsConcorde = 1;
		this.numCuttingGrindingStations = 1;
		this.numCoatingStations = 1;
		this.numInspectionPackagingStations = 1;
		this.numMovers = 5;

		// Create RVP object with given seed
		rvp = new RVPs(this,sd);

		// Instantiating entities here

		//casting stations
		int totalNumberOfCastingStations = numCastingStationsSpitfire + numCastingStationsF16 + numCastingStationsConcorde;
		castingStations = new CastingStation[totalNumberOfCastingStations];

		//Cutting/Grinding, Coating and Inspection/Packaging stations
		//Cutting = 0, Coating = 1, Insp/Pack = 2
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
		while (scanPreconditions() == true)/* repeat */;

		// Check preconditions of Interruptions in Extended Activities
	}

	public boolean scanPreconditions(){
		boolean statusChanged = false;

		// Conditional Actions
		if (OutputBinFromStation.precondition(this) == true)
		{
			OutputBinFromStation act = new OutputBinFromStation(this); // Generate instance																// instance
			act.actionEvent();
			statusChanged = true;
			if(act.areaId != Constants.INSP)
				System.out.println("Output bin from station " + act.stationId + " with a station type " + act.areaId + " now has an output queue of size: " + act.modelName.inputOutputQueues[act.areaId][1][act.stationId].size());
			else
				System.out.println("Output bin from inspection station");
			printAllVariablesForDebuggingPurposes();
		}

		if (CastNeedsMaintenance.precondition(this) == true)
		{
			CastNeedsMaintenance act = new CastNeedsMaintenance(this); // Generate instance
			act.actionEvent();
			statusChanged = true;
			System.out.println("Casting station " + act.stationId + " requires maintenance");
			printAllVariablesForDebuggingPurposes();
		}


		if(DistributeBins.precondition(this) == true)
		{
			DistributeBins distributeBins = new DistributeBins(this); // Generate instance
			distributeBins.actionEvent();
			statusChanged = true;
			System.out.println("Distribute bins at areaId " + distributeBins.areaId);
			printAllVariablesForDebuggingPurposes();
		}

		// Conditional Activities
		if (PlaneMoldCast.precondition(this) == true)
		{
			PlaneMoldCast act = new PlaneMoldCast(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			System.out.println("Casting station starts operating with stationId " + act.stationId + " and holding "
					+ act.modelName.castingStations[act.stationId].bin.n + " planes.");
			printAllVariablesForDebuggingPurposes();
		}

		if (CastRepaired.precondition(this) == true)
		{
			CastRepaired act = new CastRepaired(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			System.out.println("Casting station got repaired with stationId " + act.stationId);
			printAllVariablesForDebuggingPurposes();
		}

		if (StationProcessing.precondition(this) == true)
		{
			StationProcessing act = new StationProcessing(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			if(act.areaId+1 != Constants.INSP)
				System.out.println("Station started operating with type " + (act.areaId+1) + " and stationid " + act.stationId + " and has an output size of " + act.modelName.inputOutputQueues[(act.areaId + 1) % Constants.INSP][1][act.stationId].size());
			else
				System.out.println("Station started operating with type " + (act.areaId+1) + " and stationid " + act.stationId);
			printAllVariablesForDebuggingPurposes();
		}


		if (MoveBins.precondition(this) == true)
		{
			MoveBins act = new MoveBins(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			System.out.println("Starting to move bins from " + act.currentArea + " with stationid " + act.stationId + " to " + act.destinationArea);
			printAllVariablesForDebuggingPurposes();
		}

		return statusChanged;
	}
	
	public void eventOccured()
	{
		//this.showSBL();
		// Can add other debug code to monitor the status of the system
		// See examples for suggestions on setup logging

		// Setup an updateTrjSequences() method in the Output class
		// and call here if you have Trajectory Sets
		// updateTrjSequences()
		output.updateSequences();
 	}

	// Standard Procedure to start Sequel Activities with no parameters
	protected void spStart(SequelActivity seqAct)
	{
		seqAct.startingEvent();
		scheduleActivity(seqAct);
	}

	public void printAllVariablesForDebuggingPurposes(){
		System.out.println("--------------------------------------------------------------");
		for(int i = 0; i < castingStations.length; i++){
			System.out.println("Casting stationId: " + i + "; type=" + castingStations[i].type + " n=" + castingStations[i].bin.n + " status=" + castingStations[i].status + " timetobreak=" + castingStations[i].timeToNextBreak);
			System.out.println("Casting output: " + inputOutputQueues[0][1][i].size());
		}
		System.out.println("Cutting input: " + inputOutputQueues[1][0][0].size());
		System.out.println("Cutting output: " + inputOutputQueues[1][1][0].size());
		System.out.println("Coating input: " + inputOutputQueues[2][0][0].size());
		System.out.println("Coating output: " + inputOutputQueues[2][1][0].size());
		System.out.println("Insp input: " + inputOutputQueues[3][0][0].size());
		System.out.println("MoverLine at casting output: " + moverLines[0][1].size());
		System.out.println("MoverLine at cutting input: " + moverLines[1][0].size());
		System.out.println("MoverLine at cutting output: " + moverLines[1][1].size());
		System.out.println("MoverLine at coating input: " + moverLines[2][0].size());
		System.out.println("MoverLine at coating output: " + moverLines[2][1].size());
		System.out.println("MoverLine at inspection input: " + moverLines[3][0].size());
		System.out.println("Maintenance person is available: " + maintenancePerson.available);
		System.out.print("Casting repair queue: ");
		for(int stationId : castingRepairQueue)
			System.out.print(stationId + " ");
		System.out.println("\n--------------------------------------------------------------");


	}

}


