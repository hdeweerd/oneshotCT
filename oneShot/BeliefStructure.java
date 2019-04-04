package oneShot;

import java.io.Serializable;

public interface BeliefStructure extends Serializable {
	
	public void init();
	public double getBelief(int[] differences);
	public void observeRejection(int[] differences);
	public void observeAcceptance(int[] differences);
	

}
