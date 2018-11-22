package simModel;

import simulationModelling.OutputSequence;

class Output
{
	ModelName model;
	private final double PRO_TAR_SPITFIRE=1300;
	private final double PRO_TAR_F16=1950;
	private final double PRO_TAR_CONCORDE=2340;
	private final double MINUTES_PERDAY=480.0;

	//protected Output(ModelName md) { model = md; }
	// Use OutputSequence class to define Trajectory and Sample Sequences
	//DSOVs

	protected double percentTimeCastBlocked() {
		trjCastBlocked.computeTrjDSOVs(model.getTime0(),model.getTimef());
		return trjCastBlocked.getSum()/MINUTES_PERDAY;
	}

	protected double percentTimeCutBlocked() {
		trjCutBlocked.computeTrjDSOVs(model.getTime0(),model.getTimef());
		return trjCutBlocked.getSum()/MINUTES_PERDAY;
	}

	protected double percentTimeCoatBlocked() {
		trjCoatBlocked.computeTrjDSOVs(model.getTime0(),model.getTimef());
		return trjCoatBlocked.getSum()/MINUTES_PERDAY;
	}




	// Trajectory Sequences

	OutputSequence trjCastBlocked=new OutputSequence("PercentTimeCastingStationBlocked");
	OutputSequence trjCutBlocked=new OutputSequence("PercentTimeCuttingStationBlocked");
	OutputSequence trjCoatBlocked=new OutputSequence("PercentTimeCoatingStationBlocked");

	//Last value saved in the trajectory set

	double lastPerCastBlocked;
	double lastPerCutBlocked;
	double lastPerCoatBlocked;

	//Constructor
	protected Output(ModelName model) {
		this.model=model;
		//First points in trajectory sequences-range=0.0

		lastPerCastBlocked=0.0;
		lastPerCutBlocked=0.0;
		lastPerCoatBlocked=0.0;

		lastProSpitfire=0.0;
		lastProF16=0.0;
		lastProConcorde=0.0;
		lastMinOvertime=0.0;

		samPerSpitfire.put(0.0,lastProSpitfire);
		samPerF16.put(0.0,lastProF16);
		samPerConcorde.put(0.0,lastProConcorde);
		samMinOvertime.put(0.0,lastMinOvertime);

		trjCastBlocked.put(0.0,lastPerCastBlocked);
		trjCutBlocked.put(0.0,lastPerCutBlocked);
		trjCoatBlocked.put(0.0,lastPerCoatBlocked);
	}

	//update the trajectory sequences
	//curTime-the currenct time
	protected void updateSequences() {
		//update TRJ[CasterDown]
		//double currentPerProSpitfire;
		//double currentPerProF16;
		//double currentPerProConcorde;
		//double currentTimeMoversWaiting;

		double currentTimeCastBlocked=0.0;
		double currentTimeCutBlocked=0.0;
		double currentTimeCoatBlocked=0.0;

		//casting blocked
		//model.castingStations[castingStationId].bin.n == model.constants.BIN_CAP
		//					&& model.inputOutputQueues[model.constants.CAST][model.constants.OUT][castingStationId].size() < model.constants.IN_OUT_CAP
		for(int i=0;i < model.numCastingStationsConcorde + model.numCastingStationsF16 + model.numCastingStationsSpitfire;i++) {
			if(model.castingStations[i].status == Constants.IDLE && model.castingStations[i].bin.n == 24) currentTimeCastBlocked++;
			if(currentTimeCastBlocked!=lastPerCastBlocked) {
				lastPerCastBlocked=currentTimeCastBlocked;
				trjCastBlocked.put(model.getClock(),currentTimeCastBlocked);
			}
		}


		//cutting/Grinding blocked
		//(model.processingStations[areaID][stationID].bin != null
		//					&& model.processingStations[areaID][stationID].status == Constants.IDLE
		//					&& (areaID == Constants.COAT || model.inputOutputQueues[areaID + 1][model.constants.OUT][stationID].size() < model.constants.IN_OUT_CAP))
		for(int i=0;i<model.numCuttingGrindingStations;i++) {
			if(model.processingStations[Constants.CUT][i].status == Constants.IDLE && model.processingStations[Constants.CUT][i].bin != null) currentTimeCutBlocked++;
			if(currentTimeCutBlocked!=lastPerCutBlocked) {
				lastPerCutBlocked=currentTimeCutBlocked;
				trjCutBlocked.put(model.getClock(),currentTimeCutBlocked);
			}
		}

		//coating blocked
		for(int i=0;i<model.numCoatingStations;i++) {
			if(model.processingStations[Constants.COAT][i].status==Constants.IDLE && model.processingStations[Constants.COAT][i].bin != null) currentTimeCoatBlocked++;
			if(currentTimeCoatBlocked!=lastPerCoatBlocked) {
				lastPerCoatBlocked=currentTimeCoatBlocked;
				trjCoatBlocked.put(model.getClock(),currentTimeCoatBlocked);
			}
		}
	}

	// Sample Sequences
	// DSOVs available in the OutputSequence objects
	// If seperate methods required to process Trajectory or Sample
	// Sequences - add them here

	// SSOVs
	protected double perProSpitfire() {
		samPerSpitfire.computePhiDSOVs();
		return samPerSpitfire.getSum()/PRO_TAR_SPITFIRE;
	}

	protected double perProF16() {
		samPerF16.computePhiDSOVs();
		return samPerF16.getSum()/PRO_TAR_F16;
	}

	protected double perProConcorde() {
		samPerConcorde.computePhiDSOVs();
		return samPerConcorde.getSum()/PRO_TAR_CONCORDE;
	}

	protected double minutesOvertime() {
		samMinOvertime.computePhiDSOVs();
		return samMinOvertime.getSum();
	}

	OutputSequence samPerSpitfire=new OutputSequence("PercentTargetProductionSpitfire");
	OutputSequence samPerF16=new OutputSequence("PercentTargetProductionF16");
	OutputSequence samPerConcorde=new OutputSequence("PercentTargetProductionConcorde");
	OutputSequence samMinOvertime=new OutputSequence("MinutesOvertime");

	double lastProSpitfire;
	double lastProF16;
	double lastProConcorde;
	double lastMinOvertime;
}