package oneShot;

import java.util.ArrayList;


public class PlayerToM1Shot extends Player1Shot {

	private static final long serialVersionUID = 1L;
	PlayerToM1Shot allocatorModel = null;
	PlayerToM1Shot selfModel = null;
	double confidence = 1.0;
	double precision = 0.00000001;
	int order;

	boolean confidenceLocked = false;
	boolean startHigh = true;
	
	public PlayerToM1Shot(int playerID, int order, Setting1Shot setting, int nrColors, int nrChipsPerPlayer) {
		super(playerID, setting, nrColors, nrChipsPerPlayer);
		this.order = order;
		if (order > 0) {
			selfModel = new PlayerToM1Shot(playerID, order-1, setting, nrColors, nrChipsPerPlayer);
			allocatorModel = new PlayerToM1Shot(1 - playerID, order-1, setting, nrColors, nrChipsPerPlayer);
			allocatorModel.confidenceLocked = true;
		}
	}

	/**
	 * Initializes agent and partner models
	 */
	public void init(int chipsSelf, int chipsOther) {
		super.init(chipsSelf);
		if (this.order > 0) {
			selfModel.init(chipsSelf, chipsOther);
			allocatorModel.init(chipsOther, chipsSelf);
//			confidence = (confidenceLocked || startHigh? 1.0 : 0.0);
		}
	}
	
	protected double getAcceptanceRate(int offer) {
		int i, ownResponderUtility, otherResponderUtility;
		ArrayList<Integer> allocatorOffers;
		double curValue = 0.0;
		allocatorOffers = allocatorModel.selectOffer();
		ownResponderUtility = setting.getResponderUtility(playerID, offer);
		for (i = 0; i < allocatorOffers.size(); ++i) {
			otherResponderUtility = setting.getResponderUtility(1-playerID, allocatorOffers.get(i));
			if (otherResponderUtility == ownResponderUtility) {
				// Allocator's possible offer matches own offer. 50/50 odds of being accepted
				curValue += 0.5;
			} else if (otherResponderUtility < ownResponderUtility) {
				// Own offer is better than Allocator's offer. Responder will accept own offer
				curValue += 1.0;
			}
		}
		return curValue / allocatorOffers.size();
	}
	
	/**
	 * Returns the internal value of a given offer
	 * @param offerToSelf
	 * @return
	 */
	public double getValue(int offer) {
		int ownResponderUtility;
		double curValue = 0.0;
		if (order == 0) {
			return getExpectedValue(offer);
		}
		ownResponderUtility = setting.getResponderUtility(playerID, offer);
		if (ownResponderUtility <= 0) {
			return -1.0;
			// Responder won't accept an offer that reduces her score
		}
		curValue = getAcceptanceRate(offer) * (setting.getAllocatorUtility(playerID, offer));
		return curValue*confidence + (1 - confidence)*selfModel.getValue(offer);
	}

	public void setLearningSpeed(double newLearningSpeed) {
		super.setLearningSpeed(newLearningSpeed);
		if (order > 0) {
			selfModel.setLearningSpeed(newLearningSpeed);
			allocatorModel.setLearningSpeed(newLearningSpeed);
		}
	}

	/**
	 * Requests to make an offer
	 */
	public int makeOffer() 	{
		ArrayList<Integer> validOffers = selectOffer();
//		System.out.println(validOffers);
		int offer = validOffers.get((int)(Math.random()*validOffers.size())).intValue();
		return offer;
	}
	
	/**
	 * Returns the best possible value achievable
	 * @return
	 */
	public double getBestValue() {
		int i;
		double curValue, bestOfferValue;
		bestOfferValue = 0.0;
		for (i = 0; i < setting.getActionSpace(playerID); ++i) {
			curValue = getValue(i);
			if (curValue > bestOfferValue) {
				bestOfferValue = curValue;
			}
		}
		return bestOfferValue;
	}
	
	private ArrayList<Integer> selectOfferFast() {
		double[]  values = new double[setting.getActionSpace(playerID)];
		double diff, bestOfferValue;
		int i,j;
		for (i = 0; i < values.length; ++i) {
			if (setting.getResponderUtility(playerID, i) <= 0) {
				values[i] = -1;
			} else {
				values[i] = 0;
				for (j = 0; j < setting.getActionSpace(1-playerID); ++j) {
					diff =  setting.getResponderUtility(playerID, i) - setting.getResponderUtility(1 - playerID, j);
					if (diff == 0) {
						values[i] += 0.5;
					} else if (diff > 0) {
						values[i] += 1.0;
					}
				}
				values[i] = values[i]*setting.getAllocatorUtility(playerID, i)*confidence + (1 - confidence)*selfModel.getValue(i);
			}
		}
		ArrayList<Integer> bestOffer = new ArrayList<Integer>();
		bestOffer.add(new Integer(chips));
		bestOfferValue = 0.0;
		for (i = 0; i < setting.getActionSpace(playerID); ++i) {
			if (values[i] > bestOfferValue) {
				bestOffer.clear();
				bestOffer.add(new Integer(i));
				bestOfferValue = values[i];
			} else if (values[i] == bestOfferValue) {
				bestOffer.add(new Integer(i));
			}
		}
		return bestOffer;
	}

	/**
	 * Returns a list of offers that have the best possible expected value.
	 */
	public ArrayList<Integer> selectOffer() {
		int i;
		ArrayList<Integer> bestOffer = new ArrayList<Integer>();
		if (agentType == Player1Shot.AGENTTYPE_RANDOM) {
			if (order == 0) {
				for (i = 0; i < setting.getActionSpace(playerID); ++i) {
					bestOffer.add(new Integer(i));
				}
				return bestOffer;
			} else if (order == 1) {
				return selectOfferFast();
			}
		}
		double curValue, bestOfferValue;
		bestOffer.add(new Integer(chips));
		bestOfferValue = 0.0;
		for (i = 0; i < setting.getActionSpace(playerID); ++i) {
			curValue = getValue(i);
			if (curValue > bestOfferValue) {
				bestOffer.clear();
				bestOffer.add(new Integer(i));
				bestOfferValue = curValue;
			} else if (curValue == bestOfferValue && curValue > 0) {
				bestOffer.add(new Integer(i));
			}
		}
		return bestOffer;
	}
	
	/**
	 * Observes some player making some offer. 
	 * Note: making an offer does not observe that offer!
	 * @param playerID
	 * @param offer
	 */
	public void observeAction(int playerID, int offer) {
		double supposedValue;
		if (order > 0) {
			selfModel.observeAction(playerID, offer);
			if (playerID == 1 - this.playerID && !confidenceLocked) {
				confidence = (1-getLearningSpeed())*confidence;
				// Since we observe the opponent's behaviour, update confidence in opponent model
				allocatorModel.observeAction(playerID, offer);
				supposedValue = (allocatorModel.getValue(offer)+1)/(allocatorModel.getBestValue()+1);
				if (supposedValue > 0) {
					confidence += getLearningSpeed()*supposedValue;
				}
			}
		}
	}
	
	public void observeRejection(int playerID, int offer) {
		super.updateBeliefsOfferRejected(playerID, offer);
		if (order > 0) {
			selfModel.observeRejection(playerID, offer);
			allocatorModel.observeRejection(playerID, offer);
		}
	}
	
	public void observeAcceptance(int playerID, int offer) {
		if (playerID == this.playerID) {
			super.updateBeliefsOfferAccepted(playerID, offer);
		}
		if (order > 0) {
			selfModel.observeAcceptance(playerID, offer);
			allocatorModel.observeAcceptance(playerID, offer);
		}
	}
	
	public void setType(int newType) {
		super.setType(newType);
		if (order > 0) {
			selfModel.setType(newType);
			allocatorModel.setType(newType);
		}
	}
	public String getValuesString(int i) {
		if (order > 0) {
			return getValue(i)+"\t"+selfModel.getValuesString(i);
		}
		return getValue(i)+"";
	}
	
/*	public String toString() {
		int i, j;
		int[] bins;
		String strOut, strTmp;
		strOut = "";
		for (i = 0; i < setting.getActionSpace(playerID); ++i) {
			bins = Chips.getBins(i, setting.binMax[playerID]);
			strTmp = ""+(setting.binMax[playerID][0] - bins[0]);
			strOut += i+"\t"+bins[0];
			for (j = 1; j < bins.length; ++j) {
				strOut += ","+bins[j];
				strTmp += ","+(setting.binMax[playerID][j] - bins[j]);
			}
			strOut += "\t"+strTmp+"\t"+setting.getAllocatorUtility(playerID, i)+ "\t"+setting.getResponderUtility(playerID, i)+ "\t";
			if (order > 0) {
				strOut += getAcceptanceRate(i)+"\t"+getValuesString(i)+"\n";
			} else {
				strOut += getBelief(i)+"\t"+getExpectedValue(i)+"\n";
			}
		}
		return strOut;
	}*/
	
	public String printConfidence() {
		if (order > 0) {
			return confidence+"\t"+selfModel.printConfidence();
		}
		return "";
	}
	
	public void setSetting(Setting1Shot setting) {
		this.setting = setting;
		if (order > 0) {
			selfModel.setSetting(setting);
			allocatorModel.setSetting(setting);
		}
	}

}
