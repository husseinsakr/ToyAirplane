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

	public boolean log; // if set to true shows the log
	public double endTime; // simulation end time

	/* Parameters */
	public int numCastingStationsSpitfire;
	public int numCastingStationsF16;
	public int numCastingStationsConcorde;
	public int numCuttingGrindingStations;
	public int numCoatingStations;
	public int numInspectionPackagingStations;
	public int numMovers;

	/*-------------Entity Data Structures-------------------*/
	/* Group and Queue Entities */
	public CastingStation[] rcCastingStation; // [stationId]
	public ProcessingStation[][] rProcessingStation; // [areaId][stationId]
	public ArrayList<Bin>[][][] qIOArea = new ArrayList[4][2][]; // [areaId][IN OR OUT][stationId]
	public Mover[] rgMover; // [moverId]
	public LinkedList<Integer>[][] qMoverLines = new LinkedList[4][2]; // [areaId][IN or OUT]

	// Entities with scope Set and Unary
	public MaintenancePerson rMaintenancePerson;
	public LinkedList<Integer> qCastingRepairQueue;

	/* Input Variables */
	// References to RVP, DVP and UDP objects
	protected RVPs rvp;  // Reference to rvp object - object created in constructor
	protected DVPs dvp = new DVPs(this);  // Reference to dvp object
	protected UDPs udp = new UDPs(this); // Reference to udp object

	// Output object
	protected Output output = new Output(this);

	// Output values
	public int getNumSptfireProduced(){
		return output.numSpitfireProduced;
	}

	public int getNumF16Produced(){
		return output.numF16Produced;
	}

	public int getNumConcordeProduced(){
		return output.numConcordeProduced;
	}

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
		//Cutting = 1, Coating = 2, Insp/Pack = 3
		//We used array of size 4 but we only use arrays 1-3
		rProcessingStation = new ProcessingStation[4][];
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
		
		// Initialise the simulation model
		initAOSimulModel(t0time);

		Initialise init = new Initialise(this);
		scheduleAction(init);  // Always first one scheduled.
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
	}

	public boolean scanPreconditions(){
		boolean statusChanged = false;

		// Conditional Actions

		//checks if we can output a bin from a station that is done processing
		if (OutputBinFromStation.precondition(this) == true)
		{
			OutputBinFromStation act = new OutputBinFromStation(this); // Generate instance																// instance
			act.actionEvent();
			statusChanged = true;
		}

		//checks if we have movers in input queues of areas to distribute bins evenly around all stations
		if(DistributeBins.precondition(this) == true)
		{
			DistributeBins distributeBins = new DistributeBins(this); // Generate instance
			distributeBins.actionEvent();
			statusChanged = true;
		}

		// Conditional Activities

		//Checks if a casting station can start operating
		if (PlaneMoldCast.precondition(this) == true)
		{
			PlaneMoldCast act = new PlaneMoldCast(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}

		//Checks if a casting station requires maintenance
		if (CastNeedsMaintenance.precondition(this) == true)
		{
			CastNeedsMaintenance act = new CastNeedsMaintenance(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}

		//Checks if we can repairing a casting station
		if (CastRepaired.precondition(this) == true)
		{
			CastRepaired act = new CastRepaired(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}

		//Checks if a station can start operating
		if (StationProcessing.precondition(this) == true)
		{
			StationProcessing act = new StationProcessing(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}

		//Checks if we can start moving bins from a station
		if (MoveBins.precondition(this) == true)
		{
			MoveBins act = new MoveBins(this); // Generate instance
			act.startingEvent();
			scheduleActivity(act);
			statusChanged = true;
		}

		return statusChanged;
	}

	public boolean implicitStopCondition(){
		if(sbl.isEmpty() && getClock() > endTime){
			for(CastingStation castingStation: rcCastingStation)
				castingStation.bin.n = 0;
			printAllVariablesForDebuggingPurposes();
			return true;
		}
		return false;
	}

	public void eventOccured()
	{
		printAllVariablesForDebuggingPurposes();
 	}

	//Method made to show behaviour of simulation before and after events
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


