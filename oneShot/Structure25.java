package oneShot;

import utilities.Chips;

public class Structure25 implements BeliefStructure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int[][] counts, accepted;
	
	@Override
	public double getBelief(int[] differences) {
		int pos = Chips.getPositiveAmount(differences);
		int neg = Chips.getNegativeAmount(differences);
		return ((double)accepted[pos][neg])/counts[pos][neg];
	}

	@Override
	public void observeRejection(int[] differences) {
		int pos = Chips.getPositiveAmount(differences);
		int neg = Chips.getNegativeAmount(differences);
		counts[pos][neg]++;
	}

	@Override
	public void observeAcceptance(int[] differences) {
		int pos = Chips.getPositiveAmount(differences);
		int neg = Chips.getNegativeAmount(differences);
		counts[pos][neg]++;
		accepted[pos][neg]++;
	}

	@Override
	public void init() {
		int nrChipsPerPlayer = 5;
		int i,j;
		counts = new int[nrChipsPerPlayer+1][nrChipsPerPlayer+1];
		accepted = new int[nrChipsPerPlayer+1][nrChipsPerPlayer+1];
		for (i = 0; i < counts.length; ++i) {
			for (j = 0; j < counts.length; ++j) {
				counts[i][j] = 5;
				accepted[i][j] = 5;
			}
		}
	}
	
	public String toString() { 
		int i, j;
		String output = "";
		for (i = 0; i < counts[0].length; ++i) {
			output += "\t"+i;
		}
		for (i = 0; i < counts.length; ++i) {
			output += "\n"+i;
			for (j = 0; j < counts[0].length; ++j) {
				output += "\t"+accepted[i][j]+"/"+counts[i][j];
			}
		}
		return output;
	}
}
