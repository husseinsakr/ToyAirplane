package simModel;

import simulationModelling.ConditionalActivity;

import java.util.ArrayList;
import java.util.Iterator;

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
			if (model.rcCastingStation[stationID].status == Constants.StationStatus.IDLE
					&& model.rcCastingStation[stationID].bin.n < model.constants.BIN_CAP
					&& model.getClock() + Constants.CASTING_TIME < model.endTime
					&& model.rcCastingStation[stationID].timeToNextBreak > Constants.CASTING_TIME){
				return stationID;
			}
		}
		return Constants.NONE;
	}

	/*
	 * Returns the identifier stationID of a casting station that is about to break
	 */
	public int castingAboutToBreak(){
		for (int stationID = 0; stationID < model.rcCastingStation.length; stationID++){
			if (model.rcCastingStation[stationID].timeToNextBreak < model.constants.CASTING_TIME
					&& model.rcCastingStation[stationID].status == Constants.StationStatus.IDLE){
				return stationID;
			}
		}
		return Constants.NONE;
	}



	/*
	 * Returns the identifiers areaID and stationID of stations that are ready for operation
	 */
	public int[] stationReadyForOperation() {
		int[] areaIdAndStationId = new int[2];
		//checking all rProcessingStation if they can operate
		for (int areaID = Constants.CUT; areaID <= Constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.rProcessingStation[areaID].length; stationID++){
				if(model.rProcessingStation[areaID][stationID].status == Constants.StationStatus.IDLE
						&& model.rProcessingStation[areaID][stationID].bin == null
						&& model.qIOArea[areaID][Constants.IN][stationID].size() != 0)
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
					&& model.rcCastingStation[castingStationId].status == Constants.StationStatus.IDLE
					&& model.qIOArea[model.constants.CAST][model.constants.OUT][castingStationId].size() < model.constants.IN_OUT_CAP)
			{
				areaIdAndStationId[0] = Constants.CAST;
				areaIdAndStationId[1] = castingStationId;
				return areaIdAndStationId;
			}
		}

		//Cutting/Grinding, Coating and INSP stations
		for (int areaID = Constants.CUT; areaID < Constants.INSP; areaID++){
			for (int stationID = 0; stationID < model.rProcessingStation[areaID].length; stationID++){
				if(model.rProcessingStation[areaID][stationID].bin != Constants.NO_BIN
						&& model.rProcessingStation[areaID][stationID].status == Constants.StationStatus.IDLE
						&& (areaID == Constants.INSP || model.qIOArea[areaID][Constants.OUT][stationID].size() < Constants.IN_OUT_CAP))
				{
					areaIdAndStationId[0] = areaID;
					areaIdAndStationId[1] = stationID;
					return areaIdAndStationId;
				}
			}
		}
		return null;
	}


	public int canStartMovingBins(){ // need more explanation on this one
		Mover mover;
		int numberOfBinsCanPickup;
		for (int areaId = Constants.CAST; areaId < Constants.INSP; areaId++) {
			numberOfBinsCanPickup = 0;
			if (!model.qMoverLines[areaId][Constants.OUT].isEmpty()) {
				mover = model.rgMover[model.qMoverLines[areaId][Constants.OUT].peek()];
				for (int stationId = 0; stationId < model.qIOArea[areaId][Constants.OUT].length; stationId++) {
					numberOfBinsCanPickup += model.qIOArea[areaId][Constants.OUT][stationId].size();
					if (numberOfBinsCanPickup >= Constants.MOVER_CAP - mover.n) {
						return areaId;
					}
					/*else if (numberOfBinsCanPickup > 0
							&& model.getClock() > model.endTime) {
						if(areaId == Constants.CAST){
							if(model.rcCastingStation[stationId].status == Constants.StationStatus.IDLE)
								return areaId;
						} else {
							if(model.rProcessingStation[areaId][stationId].status == Constants.StationStatus.IDLE)
								return areaId;
						}
						return areaId;
					}
					*/
				}
			}
		}
		return Constants.NONE;
	}

	public void emptyTrolley(int moverId, int areaId){
		Mover mover = model.rgMover[moverId];
		for (int trolleyIndex = 0; trolleyIndex < Constants.MOVER_CAP; trolleyIndex++){
			Bin igBin = new Bin();
			if(mover.trolley[trolleyIndex] != null){
				igBin.type = mover.trolley[trolleyIndex].type;
				igBin.n = mover.trolley[trolleyIndex].n;
			} else {
				continue;
			}
			if(areaId != Constants.COAT || (areaId == Constants.COAT && igBin.type != Constants.PlaneType.SPITFIRE)) {
				int[] queueLengths = new int[model.qIOArea[areaId][Constants.IN].length];
				for(int i = 0; i < queueLengths.length; i++){
					queueLengths[i] = model.qIOArea[areaId][Constants.IN][i].size();
				}
				model.qIOArea[areaId][Constants.IN][findLeastBusyStation(areaId)].add(igBin);
				mover.trolley[trolleyIndex] = null;
				mover.n--;
			}
		}
	}

	//returns the index of the smallest int in an array
	public int findLeastBusyStation(int areaId){
		int[] queueLengths = new int[model.qIOArea[areaId][Constants.IN].length];
		for(int i = 0; i < queueLengths.length; i++){
			queueLengths[i] = model.qIOArea[areaId][Constants.IN][i].size();
		}
		int min = queueLengths[0];
		int index = 0;
		ArrayList<Integer> emptyStations = new ArrayList<>();
		for(int i = 0; i < queueLengths.length; i++){
			if(min > queueLengths[i]){
				min = queueLengths[i];
				index = i;
			} else if(min == 0 && queueLengths[i] == 0){
				emptyStations.add(i);
			}
		}
		Iterator iterator = emptyStations.iterator();
		while (iterator.hasNext()){
			int stationId = (int)iterator.next();
			if(model.rProcessingStation[areaId][stationId].status == Constants.StationStatus.IDLE){
				index = stationId;
				break;
			}
		}
		return index;
	}

	public int fillTrolley(int moverId, int areaId){
		int destinationArea = uNextStation(areaId);

		for(int i = 0; i < Constants.MOVER_CAP; i++){ // fill trolley by also making sure that we don't overwrite an existing bin
			int[] stationOutputLengths = getMaxOutputsInStations(areaId);
			int stationId = indexOfBiggestInteger(stationOutputLengths);
			if(model.rgMover[moverId].trolley[i] == Constants.NO_BIN){
				model.rgMover[moverId].trolley[i] = model.qIOArea[areaId][Constants.OUT][stationId].remove(0);
				model.rgMover[moverId].n++;
			}
		}
		if(areaId == Constants.CUT) {
			boolean allSpitfireBins = true;
			for (Bin igBin : model.rgMover[moverId].trolley){
				if (igBin != null && igBin.type != Constants.PlaneType.SPITFIRE){
					allSpitfireBins = false;
					break;
				}
			}
			if (allSpitfireBins)
				destinationArea = uNextStation(destinationArea); // skip coating
		}
		return destinationArea;
	}

	protected int uNextStation(int currentAreaId) {
		switch(currentAreaId) {
			case Constants.CAST:return Constants.CUT;
			case Constants.CUT:return Constants.COAT;
			case Constants.COAT:return Constants.INSP;
			case Constants.INSP:return Constants.CAST;
			default:return Constants.NONE;
		}
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
				for(ArrayList ioArea : model.qIOArea[areaId][Constants.IN]){
					canFit += Constants.IN_OUT_CAP - ioArea.size();
				}
				if(areaId == Constants.COAT){
					int moverId = model.qMoverLines[areaId][Constants.IN].peek();
					for(Bin igBin : model.rgMover[moverId].trolley){
						if(model.getClock() < model.endTime && igBin.type == Constants.PlaneType.SPITFIRE){
							canFit++;
						} else if(model.getClock() > model.endTime){
							if(igBin != null && igBin.type == Constants.PlaneType.SPITFIRE){
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
