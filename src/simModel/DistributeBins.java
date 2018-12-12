package simModel;

import simulationModelling.ConditionalAction;

public class DistributeBins extends ConditionalAction {
    ToyManufacturingModel toyManufacturingModel;
    int areaId;
    Mover mover;

    public DistributeBins(ToyManufacturingModel toyManufacturingModel){
        this.toyManufacturingModel = toyManufacturingModel;
    }

    public static boolean precondition(ToyManufacturingModel model){
        boolean returnValue = false;
        if(model.udp.canDistributeBins() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaId = toyManufacturingModel.udp.canDistributeBins();
        int moverId = toyManufacturingModel.qMoverLines[areaId][Constants.IN].pop();
        mover = toyManufacturingModel.rgMover[moverId];
        for (int trolleyIndex = 0; trolleyIndex < Constants.MOVER_CAP; trolleyIndex++){
                Bin igBin = new Bin();
                if(mover.trolley[trolleyIndex] != null){
                    igBin.type = mover.trolley[trolleyIndex].type;
                    igBin.n = mover.trolley[trolleyIndex].n;
                } else {
                    continue;
                }
            if(areaId != Constants.COAT || (areaId == Constants.COAT && igBin.type != Constants.PlaneType.SPITFIRE)) {
                int[] queueLengths = new int[toyManufacturingModel.qIOArea[areaId][Constants.IN].length];
                for(int i = 0; i < queueLengths.length; i++){
                    queueLengths[i] = toyManufacturingModel.qIOArea[areaId][Constants.IN][i].size();
                }
                toyManufacturingModel.qIOArea[areaId][Constants.IN][indexOfSmallestInteger(queueLengths)].add(igBin);
                mover.trolley[trolleyIndex] = null;
                mover.n--;
            }
        }
        toyManufacturingModel.qMoverLines[areaId % Constants.INSP][Constants.OUT].add(moverId);
    }

    //returns the index of the smallest int in an array
    public int indexOfSmallestInteger(int[] array){
        int min = array[0];
        int index = 0;
        for(int i = 0; i < array.length; i++){
            if(min > array[i]){
                min = array[i];
                index = i;
            }
        }
        return index;
    }

}
