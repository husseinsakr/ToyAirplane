package simModel;

import simulationModelling.AOSimulationModel;
import simulationModelling.Behaviour;

import java.util.ArrayList;
import java.util.LinkedList;

//
// The Simulation model Class
public class ToyManufacturingModel extends AOSimulationModel
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
	public CastingStation[] rcCastingStation;
	public ProcessingStation[][] rProcessingStation;
	public ArrayList<Bin>[][][] qIOArea = new ArrayList[4][2][]; // [areaID][IN OR OUT][stationId]
	public Mover[] rgMover;
	public LinkedList<Integer>[][] qMoverLines = new LinkedList[4][2]; // [areaID][IN or OUT]

	// Define the reference variables to the various 
	// entities with scope Set and Unary
	public MaintenancePerson rMaintenancePerson;
	public LinkedList<Integer> qCastingRepairQueue;
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
	public ToyManufacturingModel(double t0time, double tftime, int numCastingStationsSpitfire,
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
		rcCastingStation = new CastingStation[totalNumberOfCastingStations];

		//Cutting/Grinding, Coating and Inspection/Packaging stations
		//Cutting = 0, Coating = 1, Insp/Pack = 2
		rProcessingStation = new ProcessingStation[4][];
		// We subtract by 1 because casting station is not part of rProcessingStation
		rProcessingStation[constants.CUT] = new ProcessingStation[numCuttingGrindingStations];
		rProcessingStation[constants.COAT] = new ProcessingStation[numCoatingStations];
		rProcessingStation[constants.INSP] = new ProcessingStation[numInspectionPackagingStations];

		// Input and Output Queues
		qIOArea[constants.CAST][constants.OUT] = new ArrayList[totalNumberOfCastingStations];
		qIOArea[constants.CUT][constants.IN] = new ArrayList[numCuttingGrindingStations];
		qIOArea[constants.CUT][constants.OUT] = new ArrayList[numCuttingGrindingStations];
		qIOArea[constants.COAT][constants.IN] = new ArrayList[numCoatingStations];
		qIOArea[constants.COAT][constants.OUT] = new ArrayList[numCoatingStations];
		qIOArea[constants.INSP][constants.IN] = new ArrayList[numInspectionPackagingStations];

		// qMover
		rgMover = new Mover[numMovers];

		// Maintenance person
		rMaintenancePerson = new MaintenancePerson();
		
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
				System.out.println("Output bin from station " + act.stationId + " with a station type " + act.areaId + " now has an output queue of size: " + act.ToyManufacturingModel.qIOArea[act.areaId][1][act.stationId].size());
			else
				System.out.println("Output bin from inspection station");
				*/

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
					+ act.ToyManufacturingModel.rcCastingStation[act.stationId].bin.n + " planes.");
			*/
		}

		if (CastNeedsMaintenance.precondition(this) == true)
		{
			CastNeedsMaintenance act = new CastNeedsMaintenance(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
			//System.out.println("Casting station " + act.stationId + " requires maintenance");

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
				System.out.println("Station started operating with type " + (act.areaId+1) + " and stationid " + act.stationId + " and has an output size of " + act.ToyManufacturingModel.qIOArea[(act.areaId + 1) % Constants.INSP][1][act.stationId].size());
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

			System.out.println("             MOVER LINE");
			System.out.println("                     INPUT AREA      STATION            OUTPUT AREA");
			System.out.println("                                                                 MOVER LINE");

			//Print status of Casting Stations
			for (int i = 0; i < rcCastingStation.length; i++) {

				String stat = "";
				switch(rcCastingStation[i].status){
					case IDLE: stat = "IDLE"; break;
					case BUSY: stat = "BUSY"; break;
					case NEEDS_MAINTENANCE: stat = "NMAINT"; break;
				}

				StringBuilder outq = new StringBuilder();
				for (int m = 0; m < qIOArea[0][1][i].size(); m++){
					switch(qIOArea[0][1][i].get(m).type){
						case SPITFIRE: outq.append("S"); break;
						case CONCORDE: outq.append("C"); break;
						case F16: outq.append("F"); break;
					}
				}

				System.out.printf("\n           %-8s             %-4s %-2s: %-6s [%-2s]    {%-5s}       TTB = %-6.2f min", rcCastingStation[i].type, "CAST", i, stat, rcCastingStation[i].bin.n, outq, rcCastingStation[i].timeToNextBreak);
			}


			//Print status of Moverline - CAST OUTPUT
			StringBuilder mcastout = new StringBuilder();
			for (int m = 0; m < qMoverLines[0][1].size(); m++){
				mcastout.append("M");
			}
			System.out.printf("\n                                                           {%-20s} %-2s", mcastout.toString(), qMoverLines[0][1].size());
			System.out.println("\n");






			//Print status of Moverline - CUT INPUT
			StringBuilder mcutin = new StringBuilder();
			for (int m = 0; m < qMoverLines[1][0].size(); m++){
				mcutin.append("M");
			}
			System.out.printf("\n%-2s {%20s}", qMoverLines[1][0].size(), mcutin.toString());


			//Print status of Cutting/Grinding Stations
			for (int j = 0; j < numCuttingGrindingStations; j++) {

				String bntyp = "";
				if(rProcessingStation[1][j].status == Constants.StationStatus.BUSY){
					switch(rProcessingStation[1][j].bin.type){
						case SPITFIRE: bntyp = "S "; break;
						case CONCORDE: bntyp = "C "; break;
						case F16: bntyp = "F "; break;
					}
				} else {
					bntyp = "  ";
				}

				StringBuilder inq = new StringBuilder();
				for (int m = 0; m < qIOArea[1][0][j].size(); m++){
					switch(qIOArea[1][0][j].get(m).type){
						case SPITFIRE: inq.append("S"); break;
						case CONCORDE: inq.append("C"); break;
						case F16: inq.append("F"); break;
					}
				}

				StringBuilder outq = new StringBuilder();
				for (int m = 0; m < qIOArea[1][1][j].size(); m++){
					switch(qIOArea[1][1][j].get(m).type){
						case SPITFIRE: outq.append("S"); break;
						case CONCORDE: outq.append("C"); break;
						case F16: outq.append("F"); break;
					}
				}

				System.out.printf("\n                     {%5s}    %-4s %-2s: %-6s [%-2s]    {%-5s}", inq, "CUT ", j, rProcessingStation[1][j].status, bntyp, outq);
			}


			//Print status of Moverline - CUT OUTPUT
			StringBuilder mcutout = new StringBuilder();
			for (int m = 0; m < qMoverLines[1][1].size(); m++){
				mcutout.append("M");
			}
			System.out.printf("\n                                                           {%-20s} %-2s", mcutout.toString(), qMoverLines[1][1].size());
			System.out.println("\n");






			//Print status of Moverline - COAT INPUT
			StringBuilder mcoatin = new StringBuilder();
			for (int m = 0; m < qMoverLines[2][0].size(); m++){
				mcoatin.append("M");
			}
			System.out.printf("\n%-2s {%20s}", qMoverLines[2][0].size(), mcoatin.toString());


			//Print status of Coating Stations
			for (int k = 0; k < numCoatingStations; k++) {

				String bntyp = "";
				if(rProcessingStation[2][k].status == Constants.StationStatus.BUSY){
					switch(rProcessingStation[2][k].bin.type){
						case SPITFIRE: bntyp = "S "; break;
						case CONCORDE: bntyp = "C "; break;
						case F16: bntyp = "F "; break;
					}
				} else {
					bntyp = "  ";
				}

				StringBuilder inq = new StringBuilder();
				for (int m = 0; m < qIOArea[2][0][k].size(); m++){
					switch(qIOArea[2][0][k].get(m).type){
						case SPITFIRE: inq.append("S"); break;
						case CONCORDE: inq.append("C"); break;
						case F16: inq.append("F"); break;
					}
				}

				StringBuilder outq = new StringBuilder();
				for (int m = 0; m < qIOArea[2][1][k].size(); m++){
					switch(qIOArea[2][1][k].get(m).type){
						case SPITFIRE: outq.append("S"); break;
						case CONCORDE: outq.append("C"); break;
						case F16: outq.append("F"); break;
					}
				}

				System.out.printf("\n                     {%5s}    %-4s %-2s: %-6s [%-2s]    {%-5s}", inq, "COAT", k, rProcessingStation[2][k].status, bntyp, outq);
			}


			//Print sntatus of Moverline - COAT OUTPUT
			StringBuilder mcoatout = new StringBuilder();
			for (int m = 0; m < qMoverLines[2][1].size(); m++){
				mcoatout.append("M");
			}
			System.out.printf("\n                                                           {%-20s} %-2s", mcoatout.toString(), qMoverLines[2][1].size());
			System.out.println("\n");






			//Print sntatus of Moverline - INSP INPUT
			StringBuilder minspin = new StringBuilder();
			for (int m = 0; m < qMoverLines[3][0].size(); m++){
				minspin.append("M");
			}
			System.out.printf("\n%-2s {%20s}", qMoverLines[3][0].size(), minspin.toString());


			//Print status of Inspection/Packing Stations
			for (int l = 0; l < numInspectionPackagingStations; l++) {

				String bntyp = "";
				if(rProcessingStation[3][l].status == Constants.StationStatus.BUSY){
					switch(rProcessingStation[3][l].bin.type){
						case SPITFIRE: bntyp = "S "; break;
						case CONCORDE: bntyp = "C "; break;
						case F16: bntyp = "F "; break;
					}
				} else {
					bntyp = "  ";
				}

				StringBuilder inq = new StringBuilder();
				for (int m = 0; m < qIOArea[3][0][l].size(); m++){
					switch(qIOArea[3][0][l].get(m).type){
						case SPITFIRE: inq.append("S"); break;
						case CONCORDE: inq.append("C"); break;
						case F16: inq.append("F"); break;
					}
				}

				System.out.printf("\n                     {%5s}    %-4s %-2s: %-6s [%-2s]", inq, "INSP", l, rProcessingStation[3][l].status, bntyp);
			}
			System.out.println("\n");






			//Print status of the Maintenance Person
			String mpavail = "";
			if(rMaintenancePerson.available){
				mpavail = "AVAILABLE";
			} else {
				mpavail = "NOT_AVAILABLE";
			}
			System.out.printf("\nMAINTPER: " + mpavail);


			//Print status of the Casting Repair Queue
			StringBuilder rpairq = new StringBuilder();
			for (int stationId : qCastingRepairQueue){
				if(rpairq == null || rpairq.length() == 0){
					rpairq.append(stationId);
				} else {
					rpairq.append(", " + stationId);
				}

			}
			System.out.printf("\nREPAIRQ:  {%-20s}", rpairq.toString());
			System.out.println("\n");



			System.out.println("\n");
			System.out.println("\n_________________________________________________________________________________________");
			System.out.println("\n");
			System.out.println("\n");
		}
	}

}


