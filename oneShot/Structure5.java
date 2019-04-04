package oneShot;

public class Structure5 implements BeliefStructure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int[] counts, accepted;
	
	private int getIndex(int[] differences) {
		int i, acc;
		acc = 0;
		for (i = 0; i < differences.length; ++i) {
			acc += differences[i];
		}
		return Math.abs(acc);
	}

	@Override
	public double getBelief(int[] differences) {
		int i = getIndex(differences);
		return ((double)accepted[i])/counts[i];
	}

	@Override
	public void observeRejection(int[] differences) {
		int i = getIndex(differences);
		counts[i]++;
	}

	@Override
	public void observeAcceptance(int[] differences) {
		int i = getIndex(differences);
		counts[i]++;
		accepted[i]++;
	}

	@Override
	public void init() {
		int nrChipsPerPlayer = 5;
		int i;
		counts = new int[nrChipsPerPlayer+1];
		accepted = new int[nrChipsPerPlayer+1];
		for (i = 0; i < counts.length; ++i) {
			counts[i] = 5;
			accepted[i] = 5;
		}
	}

}
