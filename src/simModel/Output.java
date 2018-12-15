package simModel;

class Output
{
	ToyManufacturingModel model;
	public int numSpitfireProduced = 0; // number of spitfire planes produced
	public int numF16Produced = 0; // number of f16 planes produced
	public int numConcordeProduced = 0; // number of concorde planes produced

	public Output(ToyManufacturingModel toyManufacturingModel){this.model = toyManufacturingModel;}

}