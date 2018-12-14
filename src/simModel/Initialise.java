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
                // Add initilisation instructions

		//create all casting stations
		int totalNumberOfCastingStations = model.numCastingStationsSpitfire + model.numCastingStationsF16
											+ model.numCastingStationsConcorde;
		int numSpit = model.numCastingStationsSpitfire;
		int numF16 = model.numCastingStationsF16;
		int numConcorde = model.numCastingStationsConcorde;

		int num = 0;
		while(num < model.rcCastingStation.length){
			if(numSpit > 0){
				model.rcCastingStation[num] = new CastingStation();
				model.rcCastingStation[num].type = Constants.PlaneType.SPITFIRE;
				model.rcCastingStation[num].status = Constants.StationStatus.IDLE;
				model.rcCastingStation[num].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[num].bin = new Bin();
				model.rcCastingStation[num].bin.type = Constants.PlaneType.SPITFIRE;
				model.rcCastingStation[num].bin.n = model.constants.EMPTY;
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
				model.rcCastingStation[num].bin.n = model.constants.EMPTY;
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
				model.rcCastingStation[num].bin.n = model.constants.EMPTY;
				num++;
				numConcorde--;
			}
		}

		/*
		for(int x = 0; x < model.numCastingStationsSpitfire; x++){
			model.rcCastingStation[x] = new CastingStation();
			model.rcCastingStation[x].type = model.constants.SPITFIRE;
			model.rcCastingStation[x].status = model.constants.IDLE;
			model.rcCastingStation[x].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.rcCastingStation[x].bin = new Bin();
			model.rcCastingStation[x].bin.type = model.constants.SPITFIRE;
			model.rcCastingStation[x].bin.n = model.constants.EMPTY;
		}

		int startingIndexOfF16 = model.numCastingStationsSpitfire;
		int endingIndexOfF16 = model.numCastingStationsSpitfire + model.numCastingStationsF16;
		for(int y = startingIndexOfF16; y < endingIndexOfF16; y++){
			model.rcCastingStation[y] = new CastingStation();
			model.rcCastingStation[y].type = model.constants.F16;
			model.rcCastingStation[y].status = model.constants.IDLE;
			model.rcCastingStation[y].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.rcCastingStation[y].bin = new Bin();
			model.rcCastingStation[y].bin.type = model.constants.F16;
			model.rcCastingStation[y].bin.n = model.constants.EMPTY;
		}

		int startingIndexOfConcorde = model.numCastingStationsSpitfire + model.numCastingStationsF16;
		int endingIndexOfConcorde = totalNumberOfCastingStations;
		for(int z = startingIndexOfConcorde; z < endingIndexOfConcorde; z++){
			model.rcCastingStation[z] = new CastingStation();
			model.rcCastingStation[z].type = model.constants.CONCORDE;
			model.rcCastingStation[z].status = model.constants.IDLE;
			model.rcCastingStation[z].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.rcCastingStation[z].bin = new Bin();
			model.rcCastingStation[z].bin.type = model.constants.CONCORDE;
			model.rcCastingStation[z].bin.n = model.constants.EMPTY;
		}

		*/

		// create all queue objects for casting stations
		for (int castId = 0; castId < totalNumberOfCastingStations; castId++){
			model.qIOArea[model.constants.CAST][model.constants.OUT][castId] = new ArrayList();
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
			model.rgMover[moverId].trolley = new Bin[model.constants.MOVER_CAP];
			model.rgMover[moverId].n = Constants.EMPTY;
			model.qMoverLines[Constants.CAST][Constants.OUT].add(moverId);
		}

		// setting maintenance person to available
		model.rMaintenancePerson.available = true;

		// creating casting repair queue
		model.qCastingRepairQueue = new LinkedList<>();

	}
	

}
