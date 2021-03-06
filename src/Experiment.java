// File: Experiment.java
// Description:

import simModel.*;
import cern.jet.random.engine.*;

// Main Method: Experiments
// 
class Experiment
{
    public static boolean foundNumberOfSpitfireCastingStations = false;
    public static boolean foundNumberOfCastingStations = false;
    public static boolean foundNumberOfF16CastingStations = false;
    public static boolean foundNumberOfConcordeCastingStations = false;
    public static boolean foundNumberOfCuttingGrindingStations = false;
    public static boolean foundNumberOfCoatingStations = false;
    public static boolean foundNumberOfInspectionPackagingStations = false;
    public static boolean foundNumberOfMovers = false;
    public static final int maxNumberOfStationsPerType = 20;
    public static final int minNumberOfStations = 1;
    public static int simulationNumber = 1;

   public static void main(String[] args) {
       int i, NUMRUNS = 40; //change as you like
       double startTime = 0.0, endTime = 480.0;
       Seeds[] sds = new Seeds[NUMRUNS];
       ToyManufacturingModel mname;  // Simulation object
       double percentProductionIncrease = 30.0 / 100.0;
       boolean showLog = true; // change this to TRUE to show log and false to find optimal parameters!

       int spitfireProduction = 1000;
       int f16Production = 1500;
       int concordeProduction = 1800;
       double spitfireProductionGoal = spitfireProduction + (spitfireProduction * percentProductionIncrease);
       double f16ProductionGoal = f16Production + (f16Production * percentProductionIncrease);
       double concordeProductionGoal = concordeProduction + (concordeProduction * percentProductionIncrease);

       // Lets get a set of uncorrelated seeds
       RandomSeedGenerator rsg = new RandomSeedGenerator();
       for (i = 0; i < NUMRUNS; i++) sds[i] = new Seeds(rsg);

       int numCastingStationsSpitfire = minNumberOfStations;
       int numCastingStationsF16 = minNumberOfStations;
       int numCastingStationsConcorde = minNumberOfStations;
       int numCuttingGrindingStations = maxNumberOfStationsPerType;
       int numCoatingStations = maxNumberOfStationsPerType;
       int numInspectionPackagingStations = maxNumberOfStationsPerType;
       int numMovers = maxNumberOfStationsPerType; //ideal amount for now

       // Case 1

       if(showLog) { // change as you like the values to find result of simulation!
           NUMRUNS = 1;
           numCastingStationsSpitfire = minNumberOfStations;
           numCastingStationsF16 = minNumberOfStations;
           numCastingStationsConcorde = minNumberOfStations;
           numCuttingGrindingStations = minNumberOfStations;
           numCoatingStations = minNumberOfStations;
           numInspectionPackagingStations = minNumberOfStations;
           numMovers = maxNumberOfStationsPerType; //ideal amount for now
       } else {
           System.out.println("Finding best parameters to reach desired outputs!\n");
       }

       while (true)
       {
           double avgNumberOfSpitfireProducedDaily = 0.0;
           double avgNumberOfF16ProducedDaily = 0.0;
           double avgNumberOfConcordeProducedDaily = 0.0;

           // Loop for NUMRUN simulation runs for each case
           for (i = 0; i < NUMRUNS; i++) {

               mname = new ToyManufacturingModel(startTime, endTime, numCastingStationsSpitfire,
                       numCastingStationsF16, numCastingStationsConcorde, numCuttingGrindingStations, numCoatingStations,
                       numInspectionPackagingStations, numMovers, sds[i], showLog);
               mname.runSimulation();

               avgNumberOfSpitfireProducedDaily += mname.getNumSptfireProduced();
               avgNumberOfF16ProducedDaily += mname.getNumF16Produced();
               avgNumberOfConcordeProducedDaily += mname.getNumConcordeProduced();
           }


           avgNumberOfSpitfireProducedDaily /= NUMRUNS;
           avgNumberOfF16ProducedDaily /= NUMRUNS;
           avgNumberOfConcordeProducedDaily /= NUMRUNS;

           double percentSpitfireProduced = (double) avgNumberOfSpitfireProducedDaily / spitfireProductionGoal;
           double percentF16Produced = (double) avgNumberOfF16ProducedDaily / f16ProductionGoal;
           double percentConcordeProduced = (double) avgNumberOfConcordeProducedDaily / concordeProductionGoal;

           int totalNumberOfCastingStations = numCastingStationsConcorde + numCastingStationsF16 + numCastingStationsSpitfire;

           if(!showLog) {
               System.out.print("Simulation " + simulationNumber++ + " with parameters: ");
               System.out.print(numCastingStationsSpitfire + ", ");
               System.out.print(numCastingStationsF16 + ", ");
               System.out.print(numCastingStationsConcorde + ", ");
               System.out.print(numCuttingGrindingStations + ", ");
               System.out.print(numCoatingStations + ", ");
               System.out.print(numInspectionPackagingStations + ", ");
               System.out.println(numMovers);
               System.out.println("---------------------------------------------------------------");
               System.out.println("Outputs:");
               System.out.println("Num F16 produced: " + avgNumberOfF16ProducedDaily +
                       "(" + String.format("%.2f", percentF16Produced * 100) + "%)");
               System.out.println("Num Concorde produced: " + avgNumberOfConcordeProducedDaily +
                       "(" + String.format("%.2f", percentConcordeProduced * 100) + "%)");
               System.out.println("Num Spitfire produced: " + avgNumberOfSpitfireProducedDaily +
                       "(" + String.format("%.2f", percentSpitfireProduced * 100) + "%)");
               System.out.println("---------------------------------------------------------------\n");
           } else {
               break;
           }

           if(!foundNumberOfCastingStations) {
               if (avgNumberOfSpitfireProducedDaily > spitfireProductionGoal) {
                   foundNumberOfSpitfireCastingStations = true;
               } else if (!foundNumberOfSpitfireCastingStations) {
                   if (totalNumberOfCastingStations + 1 > 20) {
                       numCastingStationsSpitfire = 0;
                       numMovers++;
                   }
                   numCastingStationsSpitfire++;
                   continue;
               } else if (avgNumberOfSpitfireProducedDaily < spitfireProductionGoal) {
                   foundNumberOfSpitfireCastingStations = false;
                   continue;
               }

               if (avgNumberOfF16ProducedDaily > f16ProductionGoal) {
                   foundNumberOfF16CastingStations = true;
               } else if (!foundNumberOfF16CastingStations) {
                   if (totalNumberOfCastingStations + 1 > 20) {
                       numMovers++;
                       numCastingStationsF16 = 0;
                       foundNumberOfSpitfireCastingStations = false;
                   }
                   numCastingStationsF16++;
                   continue;
               } else if (avgNumberOfF16ProducedDaily < f16ProductionGoal) {
                   foundNumberOfF16CastingStations = false;
                   continue;
               }

               if (avgNumberOfConcordeProducedDaily > concordeProductionGoal) {
                   foundNumberOfConcordeCastingStations = true;
               } else if (!foundNumberOfConcordeCastingStations) {
                   if (totalNumberOfCastingStations + 1 > 20) {
                       numMovers++;
                       numCastingStationsConcorde = 0;
                   }
                   numCastingStationsConcorde++;
                   continue;
               } else if (avgNumberOfConcordeProducedDaily < concordeProductionGoal) {
                   foundNumberOfConcordeCastingStations = false;
                   continue;
               }
           }

           foundNumberOfCastingStations = true;

           if(!foundNumberOfCoatingStations
                   && avgNumberOfConcordeProducedDaily > spitfireProductionGoal
                   && avgNumberOfF16ProducedDaily > f16ProductionGoal
                   && avgNumberOfConcordeProducedDaily > concordeProductionGoal)
           {
               numCoatingStations--;
               continue;
           } else if (!foundNumberOfCoatingStations){
               numCoatingStations++;
               foundNumberOfCoatingStations = true;
               continue;
           }

           if(!foundNumberOfCuttingGrindingStations
                   && avgNumberOfConcordeProducedDaily > spitfireProductionGoal
                   && avgNumberOfF16ProducedDaily > f16ProductionGoal
                   && avgNumberOfConcordeProducedDaily > concordeProductionGoal)
           {
               numCuttingGrindingStations--;
               continue;
           } else if(!foundNumberOfCuttingGrindingStations){
               numCuttingGrindingStations++;
               foundNumberOfCuttingGrindingStations = true;
               continue;
           }

           if(!foundNumberOfInspectionPackagingStations
                   && avgNumberOfConcordeProducedDaily > spitfireProductionGoal
                   && avgNumberOfF16ProducedDaily > f16ProductionGoal
                   && avgNumberOfConcordeProducedDaily > concordeProductionGoal){
               numInspectionPackagingStations--;
               continue;
           } else if(!foundNumberOfInspectionPackagingStations) {
               numInspectionPackagingStations++;
               foundNumberOfInspectionPackagingStations = true;
               continue;
           }

           if(!foundNumberOfMovers
                   && avgNumberOfConcordeProducedDaily > spitfireProductionGoal
                   && avgNumberOfF16ProducedDaily > f16ProductionGoal
                   && avgNumberOfConcordeProducedDaily > concordeProductionGoal){
               numMovers--;
               continue;
           } else if(!foundNumberOfMovers){
               numMovers++;
               foundNumberOfMovers = true;
               continue;
           }


           if(foundNumberOfCastingStations && foundNumberOfCuttingGrindingStations
                   && foundNumberOfCoatingStations && foundNumberOfInspectionPackagingStations
                   && foundNumberOfMovers)
           {
               System.out.println("Found optimal parameters!");
               break;
           }
           // See examples for hints on collecting output
           // and developing code for analysis
       }
   }
}
