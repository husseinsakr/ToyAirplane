package simModel;

import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;

//
// The Simulation model Class
public class ModelName extends AOSimulationModel
{
	// Constants available from Constants class
	public Constants constants = new Constants();
	public boolean log;
	public static int x = 0;
	public double endTime;
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

	public int getNumSptfireProduced(){
		return output.numSpitfireProduced;
	}

	public int getNumF16Produced(){
		return output.numF16Produced;
	}

	public int getNumConcordeProduced(){
		return output.numConcordeProduced;
	}
	
	// Output values - define the public methods that return values
	// required for experimentation.


	// Constructor
	public ModelName(double t0time, double tftime, int numCastingStationsSpitfire,
					 int numCastingStationsF16, int numCastingStationsConcorde, int numCuttingGrindingStations,
					 int numCoatingStations, int numInspectionPackagingStations, int numMovers, Seeds sd, boolean log)
	{
		this.log = log;
		// Initialise parameters here
		this.endTime = tftime;
		this.numCastingStationsSpitfire = numCastingStationsSpitfire;
		this.numCastingStationsF16 = numCastingStationsF16;
		this.numCastingStationsConcorde = numCastingStationsConcorde;
		this.numCuttingGrindingStations = numCuttingGrindingStations;
		this.numCoatingStations = numCoatingStations;
		this.numInspectionPackagingStations = numInspectionPackagingStations;
		this.numMovers = numMovers;

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
		initAOSimulModel(t0time);

		     // Schedule the first arrivals and employee scheduling
		Initialise init = new Initialise(this);
		scheduleAction(init);  // Should always be first one scheduled.
		//showSBL();
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

			/*
			if(act.areaId != Constants.INSP)
				System.out.println("Output bin from station " + act.stationId + " with a station type " + act.areaId + " now has an output queue of size: " + act.modelName.inputOutputQueues[act.areaId][1][act.stationId].size());
			else
				System.out.println("Output bin from inspection station");
				*/

		}

		if (CastNeedsMaintenance.precondition(this) == true)
		{
			CastNeedsMaintenance act = new CastNeedsMaintenance(this); // Generate instance
			act.actionEvent();
			statusChanged = true;
			//System.out.println("Casting station " + act.stationId + " requires maintenance");

		}


		if(DistributeBins.precondition(this) == true)
		{
			DistributeBins distributeBins = new DistributeBins(this); // Generate instance
			distributeBins.actionEvent();
			statusChanged = true;
			//System.out.println("Distribute bins at areaId " + distributeBins.areaId);

		}

		// Conditional Activities
		if (PlaneMoldCast.precondition(this) == true)
		{
			PlaneMoldCast act = new PlaneMoldCast(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			/*
			System.out.println("Casting station starts operating with stationId " + act.stationId + " and holding "
					+ act.modelName.castingStations[act.stationId].bin.n + " planes.");
			*/
		}

		if (CastRepaired.precondition(this) == true)
		{
			CastRepaired act = new CastRepaired(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			//System.out.println("Casting station got repaired with stationId " + act.stationId);

		}

		if (StationProcessing.precondition(this) == true)
		{
			StationProcessing act = new StationProcessing(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			/*
			if(act.areaId+1 != Constants.INSP)
				System.out.println("Station started operating with type " + (act.areaId+1) + " and stationid " + act.stationId + " and has an output size of " + act.modelName.inputOutputQueues[(act.areaId + 1) % Constants.INSP][1][act.stationId].size());
			else
				System.out.println("Station started operating with type " + (act.areaId+1) + " and stationid " + act.stationId);
			*/
		}


		if (MoveBins.precondition(this) == true)
		{
			MoveBins act = new MoveBins(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;

			//System.out.println("Starting to move bins from " + act.currentArea + " with stationid to " + act.destinationArea);
		}

		return statusChanged;
	}

	public boolean implicitStopCondition(){
		//System.out.println("getClock() in implicit stop condition: " + getClock());
		if(sbl.isEmpty() && getClock() > endTime){
			//System.out.println("Current time at ending: " + getClock());
			return true;
		}
		return false;
	}

	public void eventOccured()
	{
		printAllVariablesForDebuggingPurposes();
 	}

 	/*
	// Standard Procedure to start Sequel Activities with no parameters
	protected void spStart(SequelActivity seqAct)
	{
		seqAct.startingEvent();
		scheduleActivity(seqAct);
	}
	*/

	public void printAllVariablesForDebuggingPurposes(){
		if(log) {
			System.out.printf("Clock = %10.4f\n", getClock());
			showSBL();
			
			for (int i = 0; i < castingStations.length; i++) {
				System.out.println("Casting stationId= " + i + "; type="
						+ castingStations[i].type + "; n=" + castingStations[i].bin.n
						+ "; status=" + castingStations[i].status +
						"; timetobreak=" + castingStations[i].timeToNextBreak
						+ "; output: " + inputOutputQueues[0][1][i].size()
						+ "; planeType: " + castingStations[i].type + ";");
			}

			for (int j = 0; j < numCuttingGrindingStations; j++) {
				System.out.print("Cutting stationId= " + j
						+ "; status= " + processingStations[0][j].status + ";");
				if(processingStations[0][j].status == Constants.BUSY)
					System.out.print(" bin.n= " + processingStations[0][j].bin.n
							+"; binType=" +processingStations[0][j].bin.type) ;
				else
					System.out.print(" bin=NOBIN");
				System.out.print("; input= " + inputOutputQueues[1][0][j].size()
						+ "; output= " + inputOutputQueues[1][1][j].size() + ";\n");
			}

			for (int k = 0; k < numCoatingStations; k++) {
				System.out.print("Coating stationId= " + k
						+ "; status= " + processingStations[1][k].status + ";");
				if(processingStations[1][k].status == Constants.BUSY)
					System.out.print(" bin.n= " + processingStations[1][k].bin.n
							+"; binType=" +processingStations[1][k].bin.type);
				else
					System.out.print(" bin=NOBIN");
				System.out.print("; input= " + inputOutputQueues[2][0][k].size()
						+ "; output= " + inputOutputQueues[2][1][k].size() +";\n");
			}

			for (int l = 0; l < numInspectionPackagingStations; l++) {
				System.out.print("Insp stationId= " + l + "; status= "
						+ processingStations[2][l].status + ";");
				if(processingStations[2][l].status == Constants.BUSY)
					System.out.print(" bin.n= " + processingStations[2][l].bin.n
							+"; binType=" +processingStations[2][l].bin.type) ;
				else
					System.out.print(" bin=NOBIN");
				System.out.print("; input= " + inputOutputQueues[3][0][l].size()+";\n");
			}

			System.out.println("MoverLine at casting output: " + moverLines[0][1].size());
			System.out.println("MoverLine at cutting input: " + moverLines[1][0].size());
			System.out.println("MoverLine at cutting output: " + moverLines[1][1].size());
			System.out.println("MoverLine at coating input: " + moverLines[2][0].size());
			System.out.println("MoverLine at coating output: " + moverLines[2][1].size());
			System.out.println("MoverLine at inspection input: " + moverLines[3][0].size());
			System.out.println("Maintenance person is available: " + maintenancePerson.available);
			System.out.print("Casting repair queue: ");
			for (int stationId : castingRepairQueue)
				System.out.print(stationId + " ");
			System.out.println("\n--------------------------------------------------------------");
		}
	}

}


