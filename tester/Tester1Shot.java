package tester;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import oneShot.PlayerToM1Shot;
import oneShot.Setting1Shot;

public class Tester1Shot {


	/**
	 * Performs a single game between two agents
	 * @param args - if two arguments are provided, they are expected to be ToM level (0-4)
	 */
	public static void main(String[] args) {
		int boardSize = 5;
		int nrColors = 5;
		int nrChipsPerPlayer = 4;
		int levelp1 =  0;
		int levelp2 = 0;
		
		if (args.length == 2) {
			levelp1 = Integer.parseInt(args[0]);
			levelp2 = Integer.parseInt(args[1]);
		}
		
		Setting1Shot setting = new Setting1Shot();
		setting.generateSetting(boardSize, nrColors, 2, nrChipsPerPlayer);
		PlayerToM1Shot p1 = new PlayerToM1Shot(0, levelp1, setting, nrColors, nrChipsPerPlayer);
		PlayerToM1Shot p2 = new PlayerToM1Shot(1, levelp2, setting, nrColors, nrChipsPerPlayer);
		p1.init(setting.allocatorChipset[0], setting.allocatorChipset[1]);
		p2.init(setting.allocatorChipset[1], setting.allocatorChipset[0]);
		int p1Offer = p1.makeOffer();
		System.out.println("P1 offers "+p1Offer+" ("+setting.getAllocatorUtility(0, p1Offer)+","+setting.getResponderUtility(0, p1Offer)+") EV="+p1.getValue(p1Offer));
		int p2Offer = p2.makeOffer();
		System.out.println("P2 offers "+p2Offer+" ("+setting.getAllocatorUtility(1, p2Offer)+","+setting.getResponderUtility(1, p2Offer)+") EV="+p2.getValue(p2Offer));
		p1.observeAction(0, p1Offer);
		p2.observeAction(0, p1Offer);
		p1.observeAction(1, p2Offer);
		p2.observeAction(1, p2Offer);

		if ((setting.getResponderUtility(0, p1Offer) > setting.getResponderUtility(1, p2Offer)) ||
			((setting.getResponderUtility(0, p1Offer) == setting.getResponderUtility(1, p2Offer)) && Math.random() < 0.5)) {
				// Player 1 had a better offer than player 2, or the same offer and got lucky.
			if (setting.getResponderUtility(0, p1Offer) > 0) {
				// Offer of player 1 is acceptable
				p1.observeAcceptance(0, p1Offer);
				p2.observeAcceptance(0, p1Offer);
				System.out.println("R accepts offer P1");
			} else {
				// Both offers are rejected
				System.out.println("R rejects all");
				p1.observeRejection(0, p1Offer);
				p2.observeRejection(0, p1Offer);
			}
			p1.observeRejection(1, p2Offer);
			p2.observeRejection(1, p2Offer);
		} else {
			// Player 2 had a better offer than player 1, or the same offer and got lucky.
			if (setting.getResponderUtility(1, p2Offer) > 0) {
				p1.observeAcceptance(1, p2Offer);
				p2.observeAcceptance(1, p2Offer);
				System.out.println("R accepts offer P2");
			} else {
				System.out.println("R rejects all");
				p1.observeRejection(1, p2Offer);
				p2.observeRejection(1, p2Offer);
			}
			p1.observeRejection(0, p1Offer);
			p2.observeRejection(0, p1Offer);
		}
	}
	
}
