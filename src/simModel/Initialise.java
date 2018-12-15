package simModel;

import simulationModelling.ScheduledAction;

import java.util.ArrayList;
import java.util.LinkedList;

class Initialise extends ScheduledAction
{
	ToyManufacturingModel model;

	// Constructor
	protected Initialise(ToyManufacturingModel model) { this.model = model; }

	double [] ts = { 0.0, -1.0 }; // -1.0 ends scheduling
	int tsix = 0;  // set index to first entry.
	protected double timeSequence() 
	{
		return ts[tsix++];  // only invoked at t=0
	}

	protected void actionEvent() 
	{
		// System Initialisation
                // Initilisation instructions

		//create all casting stations
		int totalNumberOfCastingStations = model.numCastingStationsSpitfire + model.numCastingStationsF16
											+ model.numCastingStationsConcorde;
		int numSpit = model.numCastingStationsSpitfire;
		int numF16 = model.numCastingStationsF16;
		int numConcorde = model.numCastingStationsConcorde;

		int num = 0;
		while(num < totalNumberOfCastingStations){
			if(numSpit > 0){
				model.rcCastingStation[num] = new CastingStation();
				model.rcCastingStation[num].type = Constants.PlaneType.SPITFIRE;
				model.rcCastingStation[num].status = Constants.StationStatus.IDLE;
				model.rcCastingStation[num].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[num].bin = new Bin();
				model.rcCastingStation[num].bin.type = Constants.PlaneType.SPITFIRE;
				model.rcCastingStation[num].bin.n = 0;
				num++;
				numSpit--;
			}
			if(numF16 > 0){
				model.rcCastingStation[num] = new CastingStation();
				model.rcCastingStation[num].type = Constants.PlaneType.F16;
				model.rcCastingStation[num].status = Constants.StationStatus.IDLE;
				model.rcCastingStation[num].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[num].bin = new Bin();
				model.rcCastingStation[num].bin.type = Constants.PlaneType.F16;
				model.rcCastingStation[num].bin.n = 0;
				num++;
				numF16--;
			}
			if(numConcorde > 0){
				model.rcCastingStation[num] = new CastingStation();
				model.rcCastingStation[num].type = Constants.PlaneType.CONCORDE;
				model.rcCastingStation[num].status = Constants.StationStatus.IDLE;
				model.rcCastingStation[num].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[num].bin = new Bin();
				model.rcCastingStation[num].bin.type = Constants.PlaneType.CONCORDE;
				model.rcCastingStation[num].bin.n = 0;
				num++;
				numConcorde--;
			}
		}

		// create all output areas for casting stations
		for (int castId = 0; castId < totalNumberOfCastingStations; castId++){
			model.qIOArea[Constants.CAST][Constants.OUT][castId] = new ArrayList();
		}

		// create all cutting/grinding stations with their input/output areas
		for (int cutId = 0; cutId < model.numCuttingGrindingStations; cutId++){
			model.rProcessingStation[Constants.CUT][cutId] = new ProcessingStation();
			model.rProcessingStation[Constants.CUT][cutId].status = Constants.StationStatus.IDLE;
			model.rProcessingStation[Constants.CUT][cutId].bin = Constants.NO_BIN;
			model.qIOArea[Constants.CUT][Constants.IN][cutId] = new ArrayList();
			model.qIOArea[Constants.CUT][Constants.OUT][cutId] = new ArrayList();
		}

		// create all coating stations with their input/output areas
		for (int coatId = 0; coatId < model.numCoatingStations; coatId++){
			model.rProcessingStation[Constants.COAT][coatId] = new ProcessingStation();
			model.rProcessingStation[Constants.COAT][coatId].status = Constants.StationStatus.IDLE;
			model.rProcessingStation[Constants.COAT][coatId].bin = Constants.NO_BIN;
			model.qIOArea[Constants.COAT][Constants.IN][coatId] = new ArrayList();
			model.qIOArea[Constants.COAT][Constants.OUT][coatId] = new ArrayList();
		}

		// create all insp/packaging stations with it's input area
		for (int inspId = 0; inspId < model.numInspectionPackagingStations; inspId++){
			model.rProcessingStation[Constants.INSP][inspId] = new ProcessingStation();
			model.rProcessingStation[Constants.INSP][inspId].status = Constants.StationStatus.IDLE;
			model.rProcessingStation[Constants.INSP][inspId].bin = Constants.NO_BIN;
			model.qIOArea[Constants.INSP][Constants.IN][inspId] = new ArrayList();
		}

		// MoverLine Queues
		model.qMoverLines[Constants.CAST][Constants.OUT] = new LinkedList<Integer>();
		model.qMoverLines[Constants.CUT][Constants.IN] = new LinkedList<Integer>();
		model.qMoverLines[Constants.CUT][Constants.OUT] = new LinkedList<Integer>();
		model.qMoverLines[Constants.COAT][Constants.IN] = new LinkedList<Integer>();
		model.qMoverLines[Constants.COAT][Constants.OUT] = new LinkedList<Integer>();
		model.qMoverLines[Constants.INSP][Constants.IN] = new LinkedList<Integer>();

		// creating all qMover
		for (int moverId = 0; moverId < model.rgMover.length; moverId++){
			model.rgMover[moverId] = new Mover();
			model.rgMover[moverId].trolley = new Bin[Constants.MOVER_CAP];
			model.rgMover[moverId].n = 0;
			model.qMoverLines[Constants.CAST][Constants.OUT].add(moverId);
		}

		// setting maintenance person to available
		model.rMaintenancePerson.available = true;

		// creating casting repair queue
		model.qCastingRepairQueue = new LinkedList<>();

	}
	

}
