package oneShot;
import java.io.Serializable;
import java.util.ArrayList;

import utilities.Chips;


abstract class Player1Shot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final int BELIEFTYPE_ABSOLUTE = 0;
	public static final int BELIEFTYPE_RELATIVE = 1;
	public static final int AGENTTYPE_RANDOM = 0;
	public static final int AGENTTYPE_SEMIRANDOM = 1;
	public static final int AGENTTYPE_BELIEF25 = 2;
	public static final int AGENTTYPE_BELIEF5 = 3;
	public static final int AGENTTYPE_REACT_ON_RANDOM = 4;
	public static final int AGENTTYPE_BELIEF101 = 5;
	public static final int AGENTTYPE_BELIEF9 = 6;
	public static final int AGENTTYPE_FULLBELIEFS = 7;
	public static final int AGENTTYPE_NULLBELIEFS = 8;
	
	
	// The set of chips is represented as an index, the score resulting from the set of chips
	// is represented as an entry in the utilityFunction array
	int chips;
	int playerID;
	int agentType = AGENTTYPE_RANDOM;
	int beliefType = BELIEFTYPE_ABSOLUTE;
	
	BeliefStructure structure;
	
	double learningSpeed = 0.1;
	Setting1Shot setting;

	/**
	 * Gives the agent's offer 
	 * @return the offer made to responder.
	 *              That is, if accepted, responder gets the returned value.
	 */
	abstract public int makeOffer();
	abstract public ArrayList<Integer> selectOffer();

	public void setType(int newType) {
		if (this.agentType != newType && newType > AGENTTYPE_SEMIRANDOM) {
			switch(newType) {
			case AGENTTYPE_BELIEF25:
				structure = new Structure25();
				break;
			case AGENTTYPE_BELIEF5:
				structure = new Structure5();
				break;
			case AGENTTYPE_BELIEF9:
				structure = new Structure9();
				break;
			case AGENTTYPE_BELIEF101:
				structure = new Structure101();
				break;
			case AGENTTYPE_REACT_ON_RANDOM:
				structure = new StructureNull();
				break;
			case AGENTTYPE_FULLBELIEFS:
				structure = new StructureFull();
				((StructureFull)structure).init(setting, playerID);
				break;
			case AGENTTYPE_NULLBELIEFS:
				structure = new StructureNull();
				structure.init();
				break;
			}
			structure.init();
		}
		this.agentType = newType;
		init(chips);
	}

	public Player1Shot(int playerID, Setting1Shot setting, int nrColors, int nrChipsPerPlayer) {
		this.playerID = playerID;
		this.setting = setting;
	}
	
	/**
	 * Initializes beliefs for a new negotiations. Note that only beliefs about colors are reset,
	 * while beliefs about offer types are kept.
	 */
	public void init(int chips) {
		this.chips = chips;
	}
	
	/**
	 * Returns the set of chips as an index.
	 * @return the set of chips as an index
	 */
	public int getChips() {
		return chips;
	}
	
	/**
	 * Sets the chipset to a new value, for example as a result of negotiation
	 * @param newValue new index for the set of chips
	 */
	public void setChips(int newValue) {
		chips = newValue;
	}
	
	/**
	 * Returns the subjective probability that the partner would accept a given offer
	 * @param offer offer to make to the partner
	 * @return subjective probability that the offer is accepted
	 */
	protected double getBelief(int offer) {
		if (agentType == AGENTTYPE_FULLBELIEFS) {
//			System.out.println(((StructureFull)structure).getBelief(offer));
			return ((StructureFull)structure).getBelief(offer);
		}
		return structure.getBelief(Chips.getDifference(setting.allocatorChipset[playerID], offer, setting.getBinMax(playerID)));
	}
	
	protected void updateBeliefsOfferRejected(int playerID, int offer) {
		if (agentType == AGENTTYPE_FULLBELIEFS) {
			if (playerID == this.playerID) {
				((StructureFull)structure).observeRejection(offer);
			}
		} else if (agentType > AGENTTYPE_SEMIRANDOM) {
			structure.observeRejection(Chips.getDifference(setting.allocatorChipset[playerID], offer, setting.getBinMax(playerID)));
		}
		if (playerID == this.playerID) {
//			decreaseColorBelief(offer);
		}
	}

	protected void updateBeliefsOfferAccepted(int playerID, int offer) {
		if (agentType == AGENTTYPE_FULLBELIEFS) {
			if (playerID == this.playerID) {
				((StructureFull)structure).observeAcceptance(offer);
			}
		} else if (agentType > AGENTTYPE_SEMIRANDOM) {
			structure.observeAcceptance(Chips.getDifference(setting.allocatorChipset[playerID], offer, setting.getBinMax(playerID)));
		}
		if (playerID == this.playerID) {
//			decreaseColorBelief(offer);
		}
	}

	/**
	 * Returns the expected value of an offer (from the perspective of this player) 
	 * @param offer the chips that this player wants to *keep*
	 * @return
	 */
	protected double getExpectedValue(int offer) {
		if (offer == setting.allocatorChipset[playerID]) {
			return setting.getAllocatorUtility(playerID, offer);
		}
		switch (agentType) {
		case AGENTTYPE_RANDOM:
			return 1.0;
		case AGENTTYPE_SEMIRANDOM:
			return (setting.getAllocatorUtility(playerID, offer) > 0?1.0:0.0);
		case AGENTTYPE_REACT_ON_RANDOM:
			return setting.getAllocatorUtility(playerID, offer);
		}
		return getBelief(offer)*(setting.getAllocatorUtility(playerID, offer));
	}
	
	public String toString() {
		int i;
		int[] bins = Chips.getBins(chips, setting.getBinMax(playerID));
		String retVal =  "";
		for (i = 0; i < bins.length; ++i) {
			retVal += bins[i]+"\t";
		}
		if (agentType == 2) {
			return structure.toString();
		}
		return agentType+"\t"+retVal;

	}
	
	public void setLearningSpeed(double newLearningSpeed) {
		learningSpeed = newLearningSpeed;
	}
	
	public double getLearningSpeed() {
		return learningSpeed;
	}
	
}
