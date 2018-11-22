package simModel;

class DVPs
{
	ModelName model;  // for accessing the clock

	// Constructor
	protected DVPs(ModelName model) { this.model = model; }


	protected double uMovingStationsTime(int currentAreaId,int destinationAreaId) {
		double movingTime;
		if(currentAreaId==Constants.CAST && destinationAreaId==Constants.CUT) {
			movingTime = 0.85;
		} else if(currentAreaId==Constants.CUT && destinationAreaId==Constants.COAT) {
			movingTime = 0.43;
		} else if (currentAreaId==Constants.COAT && destinationAreaId==Constants.INSP) {
			movingTime = 0.41;
		} else if(currentAreaId==Constants.INSP && destinationAreaId==Constants.CAST) {
			movingTime = 1.35;
		} else { //CUT -> INSP
			movingTime = 0.43 + 0.41;
		}
		return model.getClock()+movingTime;
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
}
