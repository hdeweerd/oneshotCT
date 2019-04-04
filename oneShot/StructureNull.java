package oneShot;

public class StructureNull implements BeliefStructure {

	/**
	 * Dummy belief structure
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public double getBelief(int[] differences) {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void observeRejection(int[] differences) {
		// TODO Auto-generated method stub

	}

	@Override
	public void observeAcceptance(int[] differences) {
		// TODO Auto-generated method stub

	}

}
