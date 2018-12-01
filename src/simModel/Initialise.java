package simModel;

import simulationModelling.ScheduledAction;

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

		int x = 0;
		while(x < model.rcCastingStation.length){
			if(numSpit > 0){
				model.rcCastingStation[x] = new CastingStation();
				model.rcCastingStation[x].type = model.constants.SPITFIRE;
				model.rcCastingStation[x].status = model.constants.IDLE;
				model.rcCastingStation[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[x].bin = new Bin();
				model.rcCastingStation[x].bin.type = model.constants.SPITFIRE;
				model.rcCastingStation[x].bin.n = model.constants.EMPTY;
				x++;
				numSpit--;
			}
			if(numF16 > 0){
				model.rcCastingStation[x] = new CastingStation();
				model.rcCastingStation[x].type = model.constants.F16;
				model.rcCastingStation[x].status = model.constants.IDLE;
				model.rcCastingStation[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[x].bin = new Bin();
				model.rcCastingStation[x].bin.type = model.constants.F16;
				model.rcCastingStation[x].bin.n = model.constants.EMPTY;
				x++;
				numF16--;
			}
			if(numConcorde > 0){
				model.rcCastingStation[x] = new CastingStation();
				model.rcCastingStation[x].type = model.constants.CONCORDE;
				model.rcCastingStation[x].status = model.constants.IDLE;
				model.rcCastingStation[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.rcCastingStation[x].bin = new Bin();
				model.rcCastingStation[x].bin.type = model.constants.CONCORDE;
				model.rcCastingStation[x].bin.n = model.constants.EMPTY;
				x++;
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
		for (int i = 0; i < totalNumberOfCastingStations; i++){
			model.qIOArea[model.constants.CAST][model.constants.OUT][i] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all cutting/grinding stations with their input/output areas
		for (int j = 0; j < model.numCuttingGrindingStations; j++){
			model.rProcessingStation[model.constants.CUT - 1][j] = new ProcessingStation();
			model.rProcessingStation[model.constants.CUT - 1][j].status = model.constants.IDLE;
			model.rProcessingStation[model.constants.CUT - 1][j].bin = Constants.NO_BIN;
			model.qIOArea[model.constants.CUT][model.constants.IN][j] = new IOArea(model.constants.IN_OUT_CAP);
			model.qIOArea[model.constants.CUT][model.constants.OUT][j] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all coating stations with their input/output areas
		for (int k = 0; k < model.numCoatingStations; k++){
			model.rProcessingStation[model.constants.COAT - 1][k] = new ProcessingStation();
			model.rProcessingStation[model.constants.COAT - 1][k].status = model.constants.IDLE;
			model.rProcessingStation[model.constants.COAT - 1][k].bin = Constants.NO_BIN;
			model.qIOArea[model.constants.COAT][model.constants.IN][k] = new IOArea(model.constants.IN_OUT_CAP);
			model.qIOArea[model.constants.COAT][model.constants.OUT][k] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all insp/packaging stations with it's input area
		for (int l = 0; l < model.numInspectionPackagingStations; l++){
			model.rProcessingStation[model.constants.INSP - 1][l] = new ProcessingStation();
			model.rProcessingStation[model.constants.INSP - 1][l].status = model.constants.IDLE;
			model.rProcessingStation[model.constants.INSP - 1][l].bin = Constants.NO_BIN;
			model.qIOArea[model.constants.INSP][model.constants.IN][l] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// creating all qMover
		for (int moverId = 0; moverId < model.rgMover.length; moverId++){
			model.rgMover[moverId] = new Mover();
			model.rgMover[moverId].trolley = new Bin[model.constants.MOVER_CAP];
			model.rgMover[moverId].n = model.constants.EMPTY;
			model.qMoverLines[model.constants.CAST][model.constants.OUT].add(moverId);
		}

		// setting maintenance person to available
		model.rMaintenancePerson.available = true;

		// creating casting repair queue
		model.qCastingRepairQueue = new CastingRepairQueue();
	}
	

}
