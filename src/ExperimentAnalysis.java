import cern.jet.random.engine.RandomSeedGenerator;
import outputAnalysis.ConfidenceInterval;
import simModel.Seeds;
import simModel.ToyManufacturingModel;

public class ExperimentAnalysis {
    public static void main(String[] args) {
        int i, NUMRUNS = 100;
        final double CONFIDENCE_LEVEL = 0.99;
        double startTime=0;
        double endTime = 480; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        ToyManufacturingModel mname; // Simulation object
        boolean showLog = false;
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        double [] valuesF16Case1 = new double[NUMRUNS];
        double [] valuesConcordeCase1 = new double[NUMRUNS];
        double [] valuesSpitfireCase1 = new double[NUMRUNS];

        int numMovers = 19,
        	numCastingStationsF16 = 4,
            numCastingStationsConcorde = 5,
            numCastingStationsSpitfire = 3,
            numCuttingGrindingStations = 5,
            numCoatingStations = 4,
            numInspectionPackagingStations = 3;

        for (i = 0; i < NUMRUNS; i++) {
        	mname = new ToyManufacturingModel(startTime, endTime, numCastingStationsSpitfire,
        			numCastingStationsF16, numCastingStationsConcorde, numCuttingGrindingStations,  numCoatingStations,
        			numInspectionPackagingStations, numMovers, sds[i], showLog);
            mname.runSimulation();

            valuesConcordeCase1[i] = mname.getNumConcordeProduced();
            valuesF16Case1[i]      = mname.getNumF16Produced();
            valuesSpitfireCase1[i] = mname.getNumSptfireProduced();
        }

        ConfidenceInterval cfConcorde = new ConfidenceInterval(valuesConcordeCase1, CONFIDENCE_LEVEL);
        ConfidenceInterval cfF16 = new ConfidenceInterval(valuesF16Case1, CONFIDENCE_LEVEL);
        ConfidenceInterval cfSpitfire = new ConfidenceInterval(valuesSpitfireCase1, CONFIDENCE_LEVEL);
        // Create the table
        System.out.printf("Planes    Point estimate(ybar(n))  s(n)     zeta   CI Min   CI Max     |zeta/ybar(n)|\n");
        System.out.printf("-------------------------------------------------------------------------------------\n");
        System.out.printf("Concorde %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfConcorde.getPointEstimate(), cfConcorde.getVariance(), cfConcorde.getZeta(),
                  cfConcorde.getCfMin(), cfConcorde.getCfMax(),
                  Math.abs(cfConcorde.getZeta()/cfConcorde.getPointEstimate()));
        System.out.printf("F16      %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfF16.getPointEstimate(), cfF16.getVariance(), cfF16.getZeta(),
                  cfF16.getCfMin(), cfF16.getCfMax(),
                  Math.abs(cfF16.getZeta()/cfF16.getPointEstimate()));
        System.out.printf("Spitfire %13.3f %18.3f %8.3f %8.3f %8.3f %14.3f\n",
                  cfSpitfire.getPointEstimate(), cfSpitfire.getVariance(), cfSpitfire.getZeta(),
                  cfSpitfire.getCfMin(), cfSpitfire.getCfMax(),
                  Math.abs(cfSpitfire.getZeta()/cfSpitfire.getPointEstimate()));
        System.out.printf("-------------------------------------------------------------------------------------\n");
    }
}
