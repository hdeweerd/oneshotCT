package oneShot;

import java.util.Arrays;

public class Structure101 implements BeliefStructure {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int[][][][][] counts, accepted;
	
	private int[] getList(int[] differences) {
		int i;
		int[] d = differences.clone();
		Arrays.sort(d);
		for (i = 0; i < d.length; ++i) {
			d[i] += 4;
		}
		return d;
	}
	
	@Override
	public double getBelief(int[] differences) {
		int[] d = getList(differences);
		return ((double)accepted[d[0]][d[1]][d[2]][d[3]][d[4]])/counts[d[0]][d[1]][d[2]][d[3]][d[4]];
	}

	@Override
	public void observeRejection(int[] differences) {
		int[] d = getList(differences);
		counts[d[0]][d[1]][d[2]][d[3]][d[4]]++;
	}

	@Override
	public void observeAcceptance(int[] differences) {
		int[] d = getList(differences);
		counts[d[0]][d[1]][d[2]][d[3]][d[4]]++;
		accepted[d[0]][d[1]][d[2]][d[3]][d[4]]++;
	}

	@Override
	public void init() {
		int nrChipsPerPlayer = 5;
		int i,j,k,l,m;
		counts = new int[2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1];
		accepted = new int[2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1][2*nrChipsPerPlayer+1];
		for (i = 0; i < counts.length; ++i) {
			for (j = 0; j < counts.length; ++j) {
				for (k = 0; k < counts.length; ++k) {
					for (l = 0; l < counts.length; ++l) {
						for (m = 0; m < counts.length; ++m) {
							counts[i][j][k][l][m] = 5;
							accepted[i][j][k][l][m] = 5;
						}
					}
				}
			}
		}
	}

}
