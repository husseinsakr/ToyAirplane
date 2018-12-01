package simModel;

import simulationModelling.ConditionalActivity;

class UDPs
{
	ToyManufacturingModel model;  // for accessing the clock
	
	// Constructor
	protected UDPs(ToyManufacturingModel model) { this.model = model; }

	/*
	 * Returns the identifier stationID of a casting station that is ready for processing
	 */
	public int castingStationReadyForProcessing(){
		for (int stationID = 0; stationID < model.rcCastingStation.length; stationID++){
			if (model.rcCastingStation[stationID].status == model.constants.IDLE
					&& model.rcCastingStation[stationID].bin.n < model.constants.BIN_CAP
					&& model.getClock() + Constants.CASTING_TIME < model.endTime){
				return stationID;
			}
		}
		return model.constants.NONE;
	}

	/*
	 * Returns the identifier stationID of a casting station that is about to break
	 */
	public int castingAboutToBreak(){
		for (int stationID = 0; stationID < model.rcCastingStation.length; stationID++){
			if (model.rcCastingStation[stationID].timeToNextBreak < model.constants.CASTING_TIME
					&& model.rcCastingStation[stationID].status == model.constants.IDLE){
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
		//checking all rProcessingStation if they can operate
		for (int areaID = Constants.CUT - 1; areaID < Constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.rProcessingStation[areaID].length; stationID++){
				if(model.rProcessingStation[areaID][stationID].status == Constants.IDLE
						&& model.rProcessingStation[areaID][stationID].bin == null
						&& !model.qIOArea[areaID + 1][Constants.IN][stationID].isEmpty())
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
		for (int castingStationId = 0; castingStationId < model.rcCastingStation.length; castingStationId++){
			if(model.rcCastingStation[castingStationId].bin.n == model.constants.BIN_CAP
					&& model.rcCastingStation[castingStationId].status == Constants.IDLE
					&& model.qIOArea[model.constants.CAST][model.constants.OUT][castingStationId].size() < model.constants.IN_OUT_CAP)
			{
				areaIdAndStationId[0] = model.constants.CAST;
				areaIdAndStationId[1] = castingStationId;
				return areaIdAndStationId;
			}
		}

		//Cutting/Grinding, Coating and INSP stations
		for (int areaID = model.constants.CUT - 1; areaID < model.constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.rProcessingStation[areaID].length; stationID++){
				if(model.rProcessingStation[areaID][stationID].bin != null
					&& model.rProcessingStation[areaID][stationID].status == Constants.IDLE
					&& (areaID == Constants.COAT || model.qIOArea[areaID + 1][model.constants.OUT][stationID].size() < model.constants.IN_OUT_CAP))
				{ // && model.rProcessingStation[areaID][stationID].bin.n == model.constants.BIN_CAP, I DONT THINK WE NEED THIS ANYMORE
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
		for (int areaId = Constants.CAST; areaId < Constants.INSP; areaId++) {
			numberOfBinsCanPickup = 0;
			if (!model.qMoverLines[areaId][Constants.OUT].isEmpty()) {
				for (int stationId = 0; stationId < model.qIOArea[areaId][Constants.OUT].length; stationId++) {
					mover = model.rgMover[model.qMoverLines[areaId][Constants.OUT].peek()];
					numberOfBinsCanPickup += model.qIOArea[areaId][Constants.OUT][stationId].size();
					if (numberOfBinsCanPickup >= Constants.MOVER_CAP - mover.n) {
						return areaId;
					}
				}
			}
		}
		return Constants.NONE;
	}

	public int fillTrolley(int moverId, int areaId){
		int destinationArea = model.dvp.uNextStation(areaId);

		for(int i = 0; i < Constants.MOVER_CAP; i++){ // fill trolley by also making sure that we don't overwrite an existing bin
			int[] stationOutputLengths = getMaxOutputsInStations(areaId);
			int stationId = indexOfBiggestInteger(stationOutputLengths);
			if(model.rgMover[moverId].trolley[i] == Constants.NO_BIN){
				model.rgMover[moverId].trolley[i] = model.qIOArea[areaId][Constants.OUT][stationId].poll();
				model.rgMover[moverId].n++;
			}
		}
		if(areaId == Constants.CUT) {
			boolean allSpitfireBins = true;
			for (Bin igBin : model.rgMover[moverId].trolley){
				if (igBin != null && igBin.type != Constants.SPITFIRE){
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
			if(!model.qMoverLines[areaId][Constants.IN].isEmpty()){
				int numOfStationsAtAreaId = model.qIOArea[areaId][Constants.IN].length;
				int canFit = 0;
				for(IOArea ioArea : model.qIOArea[areaId][Constants.IN]){
					canFit += ioArea.remainingCapacity();
				}
				if(areaId == Constants.COAT){
					int moverId = model.qMoverLines[areaId][Constants.IN].peek();
					for(Bin igBin : model.rgMover[moverId].trolley){
						if(model.getClock() < model.endTime && igBin.type == Constants.SPITFIRE){
							canFit++;
						} else if(model.getClock() > model.endTime){
							if(igBin != null && igBin.type == Constants.SPITFIRE){
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
		int[] stationsAtAreaId = new int[model.qIOArea[areaId][Constants.OUT].length];
		for(int i = 0; i < stationsAtAreaId.length; i++){
			stationsAtAreaId[i] = model.qIOArea[areaId][Constants.OUT][i].size();
		}
		return stationsAtAreaId;
	}
	
	
}
