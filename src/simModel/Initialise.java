package simModel;

import simulationModelling.ScheduledAction;

class Initialise extends ScheduledAction
{
	ModelName model;

	// Constructor
	protected Initialise(ModelName model) { this.model = model; }

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
		while(x < model.castingStations.length){
			if(numSpit > 0){
				model.castingStations[x] = new CastingStation();
				model.castingStations[x].type = model.constants.SPITFIRE;
				model.castingStations[x].status = model.constants.IDLE;
				model.castingStations[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.castingStations[x].bin = new Bin();
				model.castingStations[x].bin.type = model.constants.SPITFIRE;
				model.castingStations[x].bin.n = model.constants.EMPTY;
				x++;
				numSpit--;
			}
			if(numF16 > 0){
				model.castingStations[x] = new CastingStation();
				model.castingStations[x].type = model.constants.F16;
				model.castingStations[x].status = model.constants.IDLE;
				model.castingStations[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.castingStations[x].bin = new Bin();
				model.castingStations[x].bin.type = model.constants.F16;
				model.castingStations[x].bin.n = model.constants.EMPTY;
				x++;
				numF16--;
			}
			if(numConcorde > 0){
				model.castingStations[x] = new CastingStation();
				model.castingStations[x].type = model.constants.CONCORDE;
				model.castingStations[x].status = model.constants.IDLE;
				model.castingStations[x].timeToNextBreak = model.rvp.uCastingBreakTime();
				model.castingStations[x].bin = new Bin();
				model.castingStations[x].bin.type = model.constants.CONCORDE;
				model.castingStations[x].bin.n = model.constants.EMPTY;
				x++;
				numConcorde--;
			}
		}

		/*
		for(int x = 0; x < model.numCastingStationsSpitfire; x++){
			model.castingStations[x] = new CastingStation();
			model.castingStations[x].type = model.constants.SPITFIRE;
			model.castingStations[x].status = model.constants.IDLE;
			model.castingStations[x].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.castingStations[x].bin = new Bin();
			model.castingStations[x].bin.type = model.constants.SPITFIRE;
			model.castingStations[x].bin.n = model.constants.EMPTY;
		}

		int startingIndexOfF16 = model.numCastingStationsSpitfire;
		int endingIndexOfF16 = model.numCastingStationsSpitfire + model.numCastingStationsF16;
		for(int y = startingIndexOfF16; y < endingIndexOfF16; y++){
			model.castingStations[y] = new CastingStation();
			model.castingStations[y].type = model.constants.F16;
			model.castingStations[y].status = model.constants.IDLE;
			model.castingStations[y].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.castingStations[y].bin = new Bin();
			model.castingStations[y].bin.type = model.constants.F16;
			model.castingStations[y].bin.n = model.constants.EMPTY;
		}

		int startingIndexOfConcorde = model.numCastingStationsSpitfire + model.numCastingStationsF16;
		int endingIndexOfConcorde = totalNumberOfCastingStations;
		for(int z = startingIndexOfConcorde; z < endingIndexOfConcorde; z++){
			model.castingStations[z] = new CastingStation();
			model.castingStations[z].type = model.constants.CONCORDE;
			model.castingStations[z].status = model.constants.IDLE;
			model.castingStations[z].timeToNextBreak = model.rvp.uCastingBreakTime();
			model.castingStations[z].bin = new Bin();
			model.castingStations[z].bin.type = model.constants.CONCORDE;
			model.castingStations[z].bin.n = model.constants.EMPTY;
		}

		*/

		// create all queue objects for casting stations
		for (int i = 0; i < totalNumberOfCastingStations; i++){
			model.inputOutputQueues[model.constants.CAST][model.constants.OUT][i] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all cutting/grinding stations with their input/output areas
		for (int j = 0; j < model.numCuttingGrindingStations; j++){
			model.processingStations[model.constants.CUT - 1][j] = new ProcessingStation();
			model.processingStations[model.constants.CUT - 1][j].status = model.constants.IDLE;
			model.inputOutputQueues[model.constants.CUT][model.constants.IN][j] = new IOArea(model.constants.IN_OUT_CAP);
			model.inputOutputQueues[model.constants.CUT][model.constants.OUT][j] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all coating stations with their input/output areas
		for (int k = 0; k < model.numCoatingStations; k++){
			model.processingStations[model.constants.COAT - 1][k] = new ProcessingStation();
			model.processingStations[model.constants.COAT - 1][k].status = model.constants.IDLE;
			model.inputOutputQueues[model.constants.COAT][model.constants.IN][k] = new IOArea(model.constants.IN_OUT_CAP);
			model.inputOutputQueues[model.constants.COAT][model.constants.OUT][k] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// create all insp/packaging stations with it's input area
		for (int l = 0; l < model.numInspectionPackagingStations; l++){
			model.processingStations[model.constants.INSP - 1][l] = new ProcessingStation();
			model.processingStations[model.constants.INSP - 1][l].status = model.constants.IDLE;
			model.inputOutputQueues[model.constants.INSP][model.constants.IN][l] = new IOArea(model.constants.IN_OUT_CAP);
		}

		// creating all movers
		for (int moverId = 0; moverId < model.movers.length; moverId++){
			model.movers[moverId] = new Mover();
			model.movers[moverId].trolley = new Bin[model.constants.MOVER_CAP];
			model.movers[moverId].n = model.constants.EMPTY;
			model.moverLines[model.constants.CAST][model.constants.OUT].add(moverId);
		}

		// setting maintenance person to available
		model.maintenancePerson.available = true;
	}
	

}
