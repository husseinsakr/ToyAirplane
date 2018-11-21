package simModel;

import simulationModelling.ConditionalActivity;

class UDPs
{
	ModelName model;  // for accessing the clock
	
	// Constructor
	protected UDPs(ModelName model) { this.model = model; }

	/*
	 * Returns the identifier stationID of a casting station that is ready for processing
	 */
	public int castingStationReadyForProcessing(){
		for (int stationID = 0; stationID < model.castingStations.length; stationID++){
			if (model.castingStations[stationID].status == model.constants.IDLE
					&& model.castingStations[stationID].bin. n < model.constants.BIN_CAP){
				return stationID;
			}
		}
		return model.constants.NONE;
	}

	/*
	 * Returns the identifier stationID of a casting station that is about to break
	 */
	public int castingAboutToBreak(){
		for (int stationID = 0; stationID < model.castingStations.length; stationID++){
			if (model.castingStations[stationID].timeToNextBreak < model.constants.CASTING_TIME
					&& model.castingStations[stationID].status == model.constants.IDLE){
				return stationID;
			}
		}
		return model.constants.NONE;
	}

	/*
	 * Returns the identifiers areaID and stationID of stations that are ready for operation
	 */
	public int[] stationReadyForOperation() {
		int[] areaIdAndStationId = new int[2];
		//checking all ProcessingStations if they can operate
		for (int areaID = model.constants.CUT - 1; areaID < model.constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.processingStations[areaID].length; stationID++){
				if(model.processingStations[areaID][stationID].status == model.constants.IDLE
						&& model.processingStations[areaID][stationID].bin == null
						&& !model.inputOutputQueues[areaID + 1][model.constants.IN][stationID].isEmpty())
				{
					areaIdAndStationId[0] = areaID;
					areaIdAndStationId[1] = stationID;
					return areaIdAndStationId;
				}
			}
		}
		return null;
	}

	public int[] binReadyForOutput(){
		int[] areaIdAndStationId = new int[2];

		//Casting station
		for (int castingStationId = 0; castingStationId < model.castingStations.length; castingStationId++){
			if(model.castingStations[castingStationId].bin.n == model.constants.BIN_CAP
					&& model.inputOutputQueues[model.constants.CAST][model.constants.OUT][castingStationId].size() < model.constants.IN_OUT_CAP)
			{
				areaIdAndStationId[0] = model.constants.CAST;
				areaIdAndStationId[1] = castingStationId;
				return areaIdAndStationId;
			}
		}

		//Cutting/Grinding and Coating stations
		for (int areaID = model.constants.CUT - 1; areaID < model.constants.COAT; areaID++){
			for (int stationID = 0; stationID < model.processingStations[areaID].length; stationID++){
				if(model.processingStations[areaID][stationID].bin.n == model.constants.BIN_CAP
					&& model.inputOutputQueues[areaID + 1][model.constants.OUT][stationID].size() < model.constants.IN_OUT_CAP)
				{
					areaIdAndStationId[0] = areaID;
					areaIdAndStationId[1] = stationID;
					return areaIdAndStationId;
				}
			}
		}

		// inspection station ?? update output or some shit
		return null;
	}

	public int[] canStartMovingBins(){ // need more explanation on this one
		int[] areaIdAndStationId = null;
		Mover mover;
		for(int areaId = Constants.CAST; areaId < Constants.INSP; areaId++){
			if(!model.moverLines[areaId][Constants.OUT].isEmpty()) {
				for (int stationId = 0; stationId < model.inputOutputQueues[areaId][Constants.OUT].length; stationId++) {
					mover = model.movers[model.moverLines[areaId][Constants.OUT].peek()];
					if (model.inputOutputQueues[areaId][Constants.OUT][stationId].size() <= mover.n){
						areaIdAndStationId = new int[2];
						areaIdAndStationId[0] = areaId;
						areaIdAndStationId[1] = stationId;
					}
				}
			}
		}
		return areaIdAndStationId;
	}

	public int fillTrolley(int moverId, int areaId, int stationId){
		int destinationArea = areaId;
		for(int i = 0; i < Constants.MOVER_CAP; i++){ // fill trolley by also making sure that we don't overwrite an existing bin
			if(model.movers[moverId].trolley[i] != null){
				model.movers[moverId].trolley[i] = model.inputOutputQueues[areaId][Constants.OUT][stationId].poll();
				model.movers[moverId].n++;
			}
		}
		destinationArea++;
		if(areaId == Constants.CUT) {
			boolean allSpitfireBins = true;
			for (Bin bin : model.movers[moverId].trolley){
				if (bin.type != Constants.SPITFIRE){
					allSpitfireBins = false;
					break;
				}
			}
			if (allSpitfireBins)
				destinationArea++;
		}
		return destinationArea;
	}

	public int canDistributeBins(){
		for(int areaId = Constants.CUT; areaId <= Constants.INSP; areaId++){
			if(!model.moverLines[areaId][Constants.IN].isEmpty()){
				return areaId;
			}
		}
		return Constants.NONE;
	}
	
	
}
