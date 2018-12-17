package simModel;

class DVPs
{
	ToyManufacturingModel model;  // for accessing the clock

	// Constructor
	protected DVPs(ToyManufacturingModel model) { this.model = model; }


	//set times to move from stations
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
		return movingTime;
	}

}
