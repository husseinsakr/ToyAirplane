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
		int indexForCastingStations = 0;
		for (int x = 0; x < 3; x++){
			for (int y = 0; y < model.castingStations[x].length; y++){ // creating objects for casting stations
				model.castingStations[x][y] = new CastingStation(x);
				//System.out.println("Size of model.inputoutputqueues" + model.inputOutputQueues[0][1].length);
				model.castingStations[x][y].output = model.inputOutputQueues[0][1][indexForCastingStations];
				indexForCastingStations++;
			}

			for (int z = 0; z < model.processingStations[x].length; z++) { // creating objects for all other stations
				model.processingStations[x][z] = new ProcessingStation();
				model.processingStations[x][z].input = model.inputOutputQueues[x+1][0][z];
					if (x != 2)
						model.processingStations[x][z].output = model.inputOutputQueues[x + 1][1][z];

			}
		}


	}
	

}
