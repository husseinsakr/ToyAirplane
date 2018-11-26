// File: Experiment.java
// Description:

import simModel.*;
import cern.jet.random.engine.*;

// Main Method: Experiments
// 
class Experiment
{
    public static boolean foundNumberOfSpitfireCastingStations = false;
    public static boolean foundNumberOfF16CastingStations = false;
    public static boolean foundNumberOfConcordeCastingStations = false;
    public static boolean foundNumberOfCuttingGrindingStations = false;
    public static boolean foundNumberOfCoatingStations = false;
    public static boolean foundNumberOfInspectionPackagingStations = false;
    public static boolean foundNumberOfMovers = false;
    public static final int maxNumberOfStationsPerType = 20;

   public static void main(String[] args) {
       int i, NUMRUNS = 1; //change later
       double startTime = 0.0, endTime = 480.0;
       Seeds[] sds = new Seeds[NUMRUNS];
       ModelName mname;  // Simulation object
       double percentProductionIncrease = 30.0 / 100.0;

       int spitfireProduction = 1000;
       int f16Production = 1500;
       int concordeProduction = 1800;
       double spitfireProductionGoal = spitfireProduction + (spitfireProduction * percentProductionIncrease);
       double f16ProductionGoal = f16Production + (f16Production * percentProductionIncrease);
       double concordeProductionGoal = concordeProduction + (concordeProduction * percentProductionIncrease);
       //System.out.println("spitFireProductionGoal " + spitfireProductionGoal);
       //System.out.println("f16ProductionGoal " + f16ProductionGoal);
       //System.out.println("concordeProductionGoal " + concordeProductionGoal);

       // Lets get a set of uncorrelated seeds
       RandomSeedGenerator rsg = new RandomSeedGenerator();
       for (i = 0; i < NUMRUNS; i++) sds[i] = new Seeds(rsg);

       int numCastingStationsSpitfire = 2;
       int numCastingStationsF16 = 0;
       int numCastingStationsConcorde = 0;
       int numCuttingGrindingStations = 1;
       int numCoatingStations = 1;
       int numInspectionPackagingStations = 1;
       int numMovers = 200;

       // Case 1
       int[] parameters = new int[]{numCastingStationsSpitfire, numCastingStationsF16, numCastingStationsConcorde,
               numCuttingGrindingStations, numCoatingStations, numInspectionPackagingStations, numMovers};
        /*
       while (true)
       {
       */
           double avgNumberOfSpitfireProducedDaily = 0.0;
           double avgNumberOfF16ProducedDaily = 0.0;
           double avgNumberOfConcordeProducedDaily = 0.0;

           // Loop for NUMRUN simulation runs for each case
           for (i = 0; i < NUMRUNS; i++) {
               mname = new ModelName(startTime, endTime, numCastingStationsSpitfire,
                       numCastingStationsF16, numCastingStationsConcorde, numCuttingGrindingStations, numCoatingStations,
                       numInspectionPackagingStations, numMovers, sds[i]);
               mname.runSimulation();

               avgNumberOfSpitfireProducedDaily += mname.getNumSptfireProduced();
               avgNumberOfF16ProducedDaily += mname.getNumF16Produced();
               avgNumberOfConcordeProducedDaily += mname.getNumConcordeProduced();

               System.out.println("getNumSpitfireProduced(): " + mname.getNumSptfireProduced());

               System.out.println("getNumF16Produced(): " + mname.getNumF16Produced());
               System.out.println("getNumConcordeProduced(): " + mname.getNumConcordeProduced());
           }
            /*
           avgNumberOfSpitfireProducedDaily /= NUMRUNS;
           avgNumberOfF16ProducedDaily /= NUMRUNS;
           avgNumberOfConcordeProducedDaily /= NUMRUNS;

           double percentSpitfireProduced = (double) avgNumberOfSpitfireProducedDaily / spitfireProductionGoal;
           double percentF16Produced = (double) avgNumberOfF16ProducedDaily / f16ProductionGoal;
           double percentConcordeProduced = (double) avgNumberOfConcordeProducedDaily / concordeProductionGoal;

           System.out.println("Num Spitfire produced: " + avgNumberOfSpitfireProducedDaily +
                   "(" + String.format("%.2f", percentSpitfireProduced * 100) + "%)");
           System.out.println("Num F16 produced: " + avgNumberOfF16ProducedDaily +
                   "(" + String.format("%.2f", percentF16Produced * 100) + "%)");
           System.out.println("Num Concorde produced: " + avgNumberOfConcordeProducedDaily +
                   "(" + String.format("%.2f", percentConcordeProduced * 100) + "%)");

           System.out.println("number of spitfire stations: " + numCastingStationsSpitfire);
           System.out.println("number of f16 stations: " + numCastingStationsF16);
           System.out.println("number of concorde stations: " + numCastingStationsConcorde);
           System.out.println("Number of movers: " + numMovers);



           if(avgNumberOfSpitfireProducedDaily > spitfireProductionGoal){
               System.out.println("Found spitfire: " + numCastingStationsSpitfire);
               foundNumberOfSpitfireCastingStations = true;
           } else if (!foundNumberOfSpitfireCastingStations){
               if(numCastingStationsSpitfire == 20){
                   System.out.println("Wtf spitfire, I am going to add more movers");
                   numCastingStationsSpitfire = 0;
                   numCastingStationsF16 = 0;
                   numCastingStationsConcorde = 0;
                   numMovers++;
               }
               numCastingStationsSpitfire++;
               continue;
           } else if(avgNumberOfSpitfireProducedDaily < spitfireProductionGoal){
               foundNumberOfSpitfireCastingStations = false;
               continue;
           }

           if(avgNumberOfF16ProducedDaily > f16ProductionGoal){
               System.out.println("Found f16: " + numCastingStationsF16);
               foundNumberOfF16CastingStations = true;
           } else if(!foundNumberOfF16CastingStations){
               if(numCastingStationsF16 == 20){
                   System.out.println("Wtf f16, I am going to add more movers");
                   numMovers++;
                   numCastingStationsSpitfire = 0;
                   numCastingStationsF16 = 0;
                   numCastingStationsConcorde = 0;
                   foundNumberOfSpitfireCastingStations = false;
               }
               numCastingStationsF16++;
               continue;
           } else if(avgNumberOfF16ProducedDaily < f16ProductionGoal){
               foundNumberOfF16CastingStations = false;
               continue;
           }

           if(avgNumberOfConcordeProducedDaily > concordeProductionGoal){
               System.out.println("Found concorde: " + numCastingStationsConcorde);
               foundNumberOfConcordeCastingStations = true;
           } else if(!foundNumberOfConcordeCastingStations){
               if(numCastingStationsConcorde == 20){
                   System.out.println("Wtf concorde, I am going to add more movers and restart everyone");
                   numMovers++;
                   numCastingStationsSpitfire = 0;
                   numCastingStationsF16 = 0;
                   numCastingStationsConcorde = 0;
                   foundNumberOfSpitfireCastingStations = false;
                   foundNumberOfF16CastingStations = false;
               }
               numCastingStationsConcorde++;
               continue;
           } else if(avgNumberOfConcordeProducedDaily < concordeProductionGoal){
               foundNumberOfConcordeCastingStations = false;
               continue;
           }

           if(avgNumberOfSpitfireProducedDaily < spitfireProductionGoal){
               foundNumberOfSpitfireCastingStations = false;
           }

           if(foundNumberOfSpitfireCastingStations && foundNumberOfF16CastingStations && foundNumberOfConcordeCastingStations){
               if(!foundNumberOfCoatingStations && avgNumberOfConcordeProducedDaily >= spitfireProduction
                       && avgNumberOfF16ProducedDaily >= f16ProductionGoal
                       && avgNumberOfConcordeProducedDaily >= concordeProductionGoal)
               {
                   numCoatingStations--;
                   continue;
               } else if (!foundNumberOfCoatingStations){
                   numCoatingStations++;
                   foundNumberOfCoatingStations = true;
                   continue;
               }

               if(!foundNumberOfCuttingGrindingStations && avgNumberOfConcordeProducedDaily >= spitfireProduction
                       && avgNumberOfF16ProducedDaily >= f16ProductionGoal
                       && avgNumberOfConcordeProducedDaily >= concordeProductionGoal)
               {
                   numCuttingGrindingStations--;
                   continue;
               } else if(!foundNumberOfCuttingGrindingStations){
                   numCuttingGrindingStations++;
                   foundNumberOfCuttingGrindingStations = true;
                   continue;
               }
               if(!foundNumberOfInspectionPackagingStations && avgNumberOfConcordeProducedDaily >= spitfireProduction
                       && avgNumberOfF16ProducedDaily >= f16ProductionGoal
                       && avgNumberOfConcordeProducedDaily >= concordeProductionGoal){
                   numInspectionPackagingStations--;
                   continue;
               } else if(!foundNumberOfInspectionPackagingStations) {
                   numInspectionPackagingStations++;
                   foundNumberOfInspectionPackagingStations = true;
                   continue;
               }
               if(!foundNumberOfMovers && avgNumberOfConcordeProducedDaily >= spitfireProduction
                       && avgNumberOfF16ProducedDaily >= f16ProductionGoal
                       && avgNumberOfConcordeProducedDaily >= concordeProductionGoal){
                   numMovers--;
                   continue;
               } else if(!foundNumberOfMovers){
                   numMovers++;
                   foundNumberOfMovers = true;
                   break;
               }
           }
           System.out.println("Number of spitfire casting stations: " + numCastingStationsSpitfire);
           System.out.println("Number of F16 casting stations: " + numCastingStationsF16);
           System.out.println("Number of concorde casting stations: " + numCastingStationsConcorde);
           System.out.println("Number of cutting/grinding stations: " + numCuttingGrindingStations);
           System.out.println("Number of coating stations: " + numCoatingStations);
           System.out.println("Number of inspection stations: " + numInspectionPackagingStations);
           System.out.println("Number of movers: " + numMovers);

           System.out.println("Num F16 produced: " + avgNumberOfF16ProducedDaily +
                   "(" + String.format("%.2f", percentF16Produced * 100) + "%)");
           System.out.println("Num Concorde produced: " + avgNumberOfConcordeProducedDaily +
                   "(" + String.format("%.2f", percentConcordeProduced * 100) + "%)");
           System.out.println("Num Spitfire produced: " + avgNumberOfSpitfireProducedDaily +
                   "(" + String.format("%.2f", percentSpitfireProduced * 100) + "%)");
           if(foundNumberOfSpitfireCastingStations && foundNumberOfF16CastingStations && foundNumberOfConcordeCastingStations
                   && foundNumberOfCuttingGrindingStations && foundNumberOfCoatingStations && foundNumberOfInspectionPackagingStations
                   && foundNumberOfMovers)
           {
               System.out.println("In here");
               System.out.println("Number of spitfire casting stations: " + numCastingStationsSpitfire);
               System.out.println("Number of F16 casting stations: " + numCastingStationsF16);
               System.out.println("Number of concorde casting stations: " + numCastingStationsConcorde);
               System.out.println("Number of cutting/grinding stations: " + numCuttingGrindingStations);
               System.out.println("Number of coating stations: " + numCoatingStations);
               System.out.println("Number of inspection stations: " + numInspectionPackagingStations);
               System.out.println("Number of movers: " + numMovers);

               System.out.println("Num F16 produced: " + avgNumberOfF16ProducedDaily +
                       "(" + String.format("%.2f", percentF16Produced * 100) + "%)");
               System.out.println("Num Concorde produced: " + avgNumberOfConcordeProducedDaily +
                       "(" + String.format("%.2f", percentConcordeProduced * 100) + "%)");
               System.out.println("Num Spitfire produced: " + avgNumberOfSpitfireProducedDaily +
                       "(" + String.format("%.2f", percentSpitfireProduced * 100) + "%)");
               break;
           }

           // See examples for hints on collecting output
           // and developping code for analysis
       }
       */
   }
}
