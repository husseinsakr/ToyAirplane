import cern.jet.random.engine.RandomSeedGenerator;
import outputAnalysis.ConfidenceInterval;
import simModel.Seeds;
import simModel.ToyManufacturingModel;

public class ExperimentAnalysis2 {
    static final int [] NUM_RUNS_ARRAY = {20, 30, 40, 60, 80, 100, 1000, 10000};
    static final double CONFIDENCE_LEVEL = 0.99;
    public static void main(String[] args) {
        int i, NUMRUNS = 10000;
        double startTime=0;
        double endTime = 480; // run for 8 hours
        Seeds[] sds = new Seeds[NUMRUNS];
        boolean showLog = false;
        ToyManufacturingModel mname; // Simulation object
        RandomSeedGenerator rsg = new RandomSeedGenerator();
        for (i = 0; i < NUMRUNS; i++)
            sds[i] = new Seeds(rsg);

        double[] allValuesF16 = new double[NUMRUNS];
        double[] allValuesConcorde = new double[NUMRUNS];
        double[] allValuesSpitfire = new double[NUMRUNS];

        int numMover = 19,
        	numCastingStationsF16 = 4,
        	numCastingStationsConcorde = 5,
        	numCastingStationsSpitfire = 3,
            numCuttingGrindingStations = 5,
            numCoatingStations = 4,
            numInspectionPackagingStations = 3;   
        for (i = 0; i < NUMRUNS; i++) {
        	mname = new ToyManufacturingModel(startTime, endTime, numCastingStationsSpitfire,
        			numCastingStationsF16, numCastingStationsConcorde, numCuttingGrindingStations,  numCoatingStations,
        			numInspectionPackagingStations, numMover, sds[i], showLog);
        	mname.runSimulation();

            allValuesConcorde[i] = mname.getNumConcordeProduced();
            allValuesF16[i]      = mname.getNumF16Produced();
            allValuesSpitfire[i] = mname.getNumSptfireProduced();
        }

        displayTable(allValuesConcorde, allValuesF16, allValuesSpitfire);
    }

    private static void displayTable(double[] allValuesConcorde, double[] allValuesF16, double[] allValuesSpitfire) {
           System.out.printf("                             Result (Concorde, F16, Spitfire):\n");
           System.out.printf("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
           System.out.printf("    n             y(n)                           s(n)                           zeta(n)                       CI Min                          CI Max                    zeta(n)/y(n)\n");
           System.out.printf("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
           for(int ix1 = 0; ix1 < NUM_RUNS_ARRAY.length; ix1++) {
               int numruns = NUM_RUNS_ARRAY[ix1];
               double[] valuesF16 = new double[numruns];
               double[] valuesConcorde = new double[numruns];
               double[] valuesSpitfire = new double[numruns];
               for(int ix2 = 0 ; ix2 < numruns; ix2++) {
                   valuesF16[ix2]      = allValuesF16[ix2];
                   valuesConcorde[ix2] = allValuesConcorde[ix2];
                   valuesSpitfire[ix2] = allValuesSpitfire[ix2];
               }
               ConfidenceInterval cfConcorde = new ConfidenceInterval(valuesConcorde, CONFIDENCE_LEVEL);
               ConfidenceInterval cfF16 = new ConfidenceInterval(valuesF16, CONFIDENCE_LEVEL);
               ConfidenceInterval cfSpitfire = new ConfidenceInterval(valuesSpitfire, CONFIDENCE_LEVEL);
               System.out.printf("%5d (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%8.3f, %8.3f, %8.3f) (%5.3f, %5.3f, %5.3f)\n",
                               numruns,
                               cfConcorde.getPointEstimate(), cfF16.getPointEstimate(), cfSpitfire.getPointEstimate(),
                               cfConcorde.getStdDev(), cfF16.getStdDev(), cfSpitfire.getStdDev(),
                               cfConcorde.getZeta(), cfF16.getZeta(), cfSpitfire.getZeta(),
                               cfConcorde.getCfMin(), cfF16.getCfMin(), cfSpitfire.getCfMin(),
                               cfConcorde.getCfMax(), cfF16.getCfMax(), cfSpitfire.getCfMax(),
                               cfConcorde.getZeta()/cfConcorde.getPointEstimate(), cfF16.getZeta()/cfF16.getPointEstimate(), cfSpitfire.getZeta()/cfSpitfire.getPointEstimate());
           }
           System.out.printf("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }
}
