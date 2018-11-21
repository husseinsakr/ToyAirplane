package simModel;

import cern.jet.random.engine.RandomSeedGenerator;

public class Seeds 
{
	int cuttingTime;   //cutting
	int packingTime;   //packing
	int breakTime;   //die casting breaking down
	int repairTime;   // repair time

	public Seeds(RandomSeedGenerator rsg)
	{
		cuttingTime=rsg.nextSeed();
		packingTime=rsg.nextSeed();
		breakTime=rsg.nextSeed();
		repairTime=rsg.nextSeed();
	}
}
