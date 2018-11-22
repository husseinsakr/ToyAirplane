package simModel;

import simulationModelling.ConditionalAction;

public class DistributeBins extends ConditionalAction {
    ModelName modelName;
    int areaId;
    Mover mover;

    public DistributeBins(ModelName modelName){
        this.modelName = modelName;
    }

    public static boolean precondition(ModelName model){
        boolean returnValue = false;
        if(model.udp.canDistributeBins() != Constants.NONE){
            returnValue = true;
        }
        return returnValue;
    }

    public void actionEvent(){
        areaId = modelName.udp.canDistributeBins();
        int moverId = modelName.moverLines[areaId][Constants.IN].pop();
        mover = modelName.movers[moverId];
        for (int trolleyIndex = 0; trolleyIndex < Constants.MOVER_CAP; trolleyIndex++){
            Bin bin = new Bin();
            bin.type = mover.trolley[trolleyIndex].type;
            bin.n = mover.trolley[trolleyIndex].n;
            if(areaId != Constants.COAT || (areaId == Constants.COAT && bin.type != Constants.SPITFIRE)) {
                int[] queueLengths = new int[modelName.inputOutputQueues[areaId][Constants.IN].length];
                for(int i = 0; i < queueLengths.length; i++){
                    queueLengths[i] = modelName.inputOutputQueues[areaId][Constants.IN][i].size();
                }
                modelName.inputOutputQueues[areaId][Constants.IN][indexOfSmallestInteger(queueLengths)].add(bin);
                mover.trolley[trolleyIndex] = null;
                mover.n--;
            }
        }
            modelName.moverLines[areaId][Constants.OUT].add(moverId);
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
