package oneShot;

public class StructureFull implements BeliefStructure {
	private static final long serialVersionUID = 1L;
	int[] counts, accepted;
	
	public void init(Setting1Shot setting, int playerID) {
		int i;
		counts = new int[setting.getActionSpace(playerID)];
		accepted = new int[counts.length];
		for (i = 0; i < counts.length; ++i) {
			counts[i] = 5;
			accepted[i] = 5;
		}
	}
	
	public void init() {
	}
	public double getBelief(int offer) {
		if (((double)accepted[offer])/counts[offer] <= 0) {
			System.out.println("ERROR! "+offer);
		}
		return ((double)accepted[offer])/counts[offer];
	}
	public double getBelief(int[] differences) {
		return 0.0;
	}
	public void observeRejection(int offer) {
		counts[offer]++;
	}
	public void observeRejection(int[] differences) {
		
	}
	public void observeAcceptance(int offer) {
		counts[offer]++;
		accepted[offer]++;
	}
	public void observeAcceptance(int[] differences) {
		
	}

}
