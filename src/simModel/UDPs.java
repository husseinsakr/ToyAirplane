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
					&& model.castingStations[stationID].bin.n < model.constants.BIN_CAP
					&& model.getClock() < model.endTime){
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
		for (int areaID = Constants.CUT - 1; areaID < Constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.processingStations[areaID].length; stationID++){
				if(model.processingStations[areaID][stationID].status == Constants.IDLE
						&& model.processingStations[areaID][stationID].bin == null
						&& !model.inputOutputQueues[areaID + 1][Constants.IN][stationID].isEmpty())
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

		//Cutting/Grinding, Coating and INSP stations
		for (int areaID = model.constants.CUT - 1; areaID < model.constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.processingStations[areaID].length; stationID++){
				if(model.processingStations[areaID][stationID].bin != null
					&& model.processingStations[areaID][stationID].status == Constants.IDLE
					&& (areaID == Constants.COAT || model.inputOutputQueues[areaID + 1][model.constants.OUT][stationID].size() < model.constants.IN_OUT_CAP))
				{ // && model.processingStations[areaID][stationID].bin.n == model.constants.BIN_CAP, I DONT THINK WE NEED THIS ANYMORE
					areaIdAndStationId[0] = areaID + 1;
					areaIdAndStationId[1] = stationID;
					return areaIdAndStationId;
				}
			}
		}

		// inspection station ?? update output or some shit
		return null;
	}

	public int canStartMovingBins(){ // need more explanation on this one
		Mover mover;
		int numberOfBinsCanPickup;
		for(int areaId = Constants.CAST; areaId < Constants.INSP; areaId++){
			numberOfBinsCanPickup = 0;
			if(!model.moverLines[areaId][Constants.OUT].isEmpty()) {
				for (int stationId = 0; stationId < model.inputOutputQueues[areaId][Constants.OUT].length; stationId++) {
					mover = model.movers[model.moverLines[areaId][Constants.OUT].peek()];
					numberOfBinsCanPickup += model.inputOutputQueues[areaId][Constants.OUT][stationId].size();
					if (numberOfBinsCanPickup >= Constants.MOVER_CAP - mover.n){
						return areaId;
					} else if (numberOfBinsCanPickup > 0 && model.getClock() < model.endTime){
						return areaId;
					}
				}
			}
		}
		/*
		for(int areaId = Constants.CAST; areaId < Constants.INSP; areaId++){
			if(!model.moverLines[areaId][Constants.OUT].isEmpty()) {
				for (int stationId = 0; stationId < model.inputOutputQueues[areaId][Constants.OUT].length; stationId++) {
					mover = model.movers[model.moverLines[areaId][Constants.OUT].peek()];
					int x = model.inputOutputQueues[areaId][Constants.OUT][stationId].size();
					if (model.inputOutputQueues[areaId][Constants.OUT][stationId].size() >= Constants.MOVER_CAP - mover.n){
						areaIdAndStationId = new int[2];
						areaIdAndStationId[0] = areaId;
						areaIdAndStationId[1] = stationId;
						return areaIdAndStationId;
					}
				}
			}
		}
		*/
		return Constants.NONE;
	}

	public int fillTrolley(int moverId, int areaId){
		int destinationArea = model.dvp.uNextStation(areaId);

		for(int i = 0; i < Constants.MOVER_CAP; i++){ // fill trolley by also making sure that we don't overwrite an existing bin
			int[] stationOutputLengths = getMaxOutputsInStations(areaId);
			int stationId = indexOfBiggestInteger(stationOutputLengths);
			if(model.getClock() < model.endTime && model.movers[moverId].trolley[i] == null){
				model.movers[moverId].trolley[i] = model.inputOutputQueues[areaId][Constants.OUT][stationId].poll();
				model.movers[moverId].n++;
			} else if(model.inputOutputQueues[areaId][Constants.OUT][stationId].size() > 0) {
				model.movers[moverId].trolley[i] = model.inputOutputQueues[areaId][Constants.OUT][stationId].poll();
				model.movers[moverId].n++;
			}
		}
		if(areaId == Constants.CUT) {
			boolean allSpitfireBins = true;
			for (Bin bin : model.movers[moverId].trolley){
				if (bin != null && bin.type != Constants.SPITFIRE){
					allSpitfireBins = false;
					break;
				}
			}
			if (allSpitfireBins)
				destinationArea = model.dvp.uNextStation(destinationArea); // skip coating
		}
		return destinationArea;
	}

	//returns the index of the smallest int in an array
	public int indexOfBiggestInteger(int[] array){
		int max = array[0];
		int index = 0;
		for(int i = 0; i < array.length; i++){
			if(max < array[i]){
				max = array[i];
				index = i;
			}
		}
		return index;
	}

	public int canDistributeBins(){
		for(int areaId = Constants.CUT; areaId <= Constants.INSP; areaId++){
			if(!model.moverLines[areaId][Constants.IN].isEmpty()){
				int numOfStationsAtAreaId = model.inputOutputQueues[areaId][Constants.IN].length;
				int canFit = 0;
				for(IOArea ioArea : model.inputOutputQueues[areaId][Constants.IN]){
					canFit += ioArea.remainingCapacity();
				}
				if(areaId == Constants.COAT){
					int moverId = model.moverLines[areaId][Constants.IN].peek();
					for(Bin bin : model.movers[moverId].trolley){
						if(model.getClock() < model.endTime && bin.type == Constants.SPITFIRE){
							canFit++;
						} else if(model.getClock() > model.endTime){
							if(bin != null && bin.type == Constants.SPITFIRE){
								canFit++;
							}
						}
					}
				}
				if (canFit >= Constants.MOVER_CAP)
					return areaId;
			}
		}
		return Constants.NONE;
	}

	private int[] getMaxOutputsInStations(int areaId){
		int[] stationsAtAreaId = new int[model.inputOutputQueues[areaId][Constants.OUT].length];
		for(int i = 0; i < stationsAtAreaId.length; i++){
			stationsAtAreaId[i] = model.inputOutputQueues[areaId][Constants.OUT][i].size();
		}
		return stationsAtAreaId;
	}
	
	
}
