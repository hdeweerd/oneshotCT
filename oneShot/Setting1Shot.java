package oneShot;

import java.io.Serializable;

import utilities.Chips;


public class Setting1Shot implements Serializable {

	private static final long serialVersionUID = 1L;
	int[][] binMax;
	public int[][][] utilityFunctionsAllocators;
	public int[][][] utilityFunctionsResponder;
	public int[] allocatorChipset;
	int[] responderChipset;
	int[][] board;
	public int[] locations;
	
	static final int SCORE_GOAL = 500;
	static final int SCORE_STEP = 100;
	static final int SCORE_SURPLUS = 50;
	
	public int[] generateRandomChipSet(int nrChips, int nrBins) {
		int i;
		int[] bins = new int[nrBins];
        for (i = 0; i < nrChips; ++i) {
			bins[(int)(Math.random()*nrBins)]++;
		}
        return bins;
	}
	
	public void generateSetting(int boardSize, int tokenDiversity, int nrPlayers, int tokensPerPlayer) {
		int[] responderChips;
		int[][] allocatorChips = new int[nrPlayers][];
		int p;

        board = new int[boardSize][boardSize];
		initBoard(tokenDiversity);
		responderChips = generateRandomChipSet(tokensPerPlayer, tokenDiversity);
        for (p = 0; p < nrPlayers; ++p) {
    		allocatorChips[p] = generateRandomChipSet(tokensPerPlayer, tokenDiversity);
        }
        generateSetting(responderChips, allocatorChips);
	}
	
	
	public void generateSetting(int[] responderChips, int[][] allocatorChips) {
		int i,j,k,p,pos,boardSize, nrPlayers;
		boardSize = board.length;
		nrPlayers = allocatorChips.length;
		binMax = new int[nrPlayers][responderChips.length];
		utilityFunctionsAllocators = new int[nrPlayers][][];
		utilityFunctionsResponder = new int[nrPlayers][][];
		allocatorChipset = new int[nrPlayers];
		responderChipset = new int[nrPlayers];
        locations = new int[nrPlayers+1];

        for (p = 0; p < nrPlayers; ++p) {
    		k = 1;
    		for (i = 0; i < allocatorChips[p].length; ++i) {
    			k *= (responderChips[i]+allocatorChips[p][i]+1);
    			binMax[p][i] = responderChips[i]+allocatorChips[p][i];
    		}
    		allocatorChipset[p] = Chips.convert(allocatorChips[p], binMax[p]); 
    		responderChipset[p] = Chips.convert(responderChips, binMax[p]);
    		pos = 0;
    		utilityFunctionsAllocators[p] = new int[(boardSize-1)*(boardSize+1)/2][k];
    		utilityFunctionsResponder[p] = new int[(boardSize-1)*(boardSize+1)/2][k];
    		for (i = 0; i < boardSize; ++i) {
    			for (j = 0; j < boardSize; ++j) {
    				if (Math.abs(2*i-boardSize+1)+Math.abs(2*j-boardSize+1) >= boardSize) {
    					getUtilityFunction(utilityFunctionsAllocators[p][pos], binMax[p], i, j, false);
    					getUtilityFunction(utilityFunctionsResponder[p][pos], binMax[p], i, j, true);
    					pos++;
    				}
				}
			}
		}
	}
	
	public void loadBoard(String strBoard) {
		int boardSize, i, j;
		boardSize = (int)Math.sqrt(strBoard.length());
        board = new int[boardSize][boardSize];
		for (i = 0; i < board.length; ++i) {
			for (j = 0; j < board[i].length; ++j) {
				board[i][j] = strBoard.charAt(i*board.length+j) - 48;
			}
		}
	}

	private void initBoard(int tokenDiversity) {
		int i,j;
		for (i = 0; i < board.length; ++i) {
			for (j = 0; j < board[i].length; ++j) {
				board[i][j] = (int)(Math.random()*tokenDiversity);
			}
		}
	}

	public String toString() {
		String outVal = "";
		int i,j;
		for (i = 0; i < board.length; ++i) {
			outVal += board[i][0];
			for (j = 1; j < board.length; ++j) {
				outVal += "  "+board[i][j];
			}
			outVal += "\n";
		}
		int [] bins;
		for (i = 0; i < allocatorChipset.length; ++i) {
			bins = Chips.getBins(allocatorChipset[i], binMax[i]);
			outVal += "P"+i+" chips:";
			for (j = 0; j < bins.length; ++j) {
				for (int k = 0; k < bins[j]; ++k) {
					outVal += "  "+j;
				}
			}
			outVal += "\tgoal location: "+locations[i]+"\n";
		}
		bins = Chips.getBins(responderChipset[0], binMax[0]);
		outVal += "R chips: ";
		for (j = 0; j < bins.length; ++j) {
			for (int k = 0; k < bins[j]; ++k) {
				outVal += "  "+j;
			}
		}
		outVal += "\tgoal location: "+locations[2] + "\n";
/*		for (i = 0; i < utilityFunctionsAllocators[0].length; ++i) {
			outVal += "Location "+i+":";
			for (j = 0; j < utilityFunctionsAllocators.length; ++j) {
				outVal += "\t("+utilityFunctionsAllocators[j][i][allocatorChipset[j]]+","+utilityFunctionsResponder[j][i][allocatorChipset[j]]+")";
			}
			outVal += "\n";
		}*/
//		System.out.println(utilityFunctions.length+" "+utilityFunctions[0].length);
		return outVal;
	}

	public int[] getUtilityFunctionAllocator(int player, int location) {
		return utilityFunctionsAllocators[player][location];
	}

	public int[] getUtilityFunctionResponder(int player, int location) {
		return utilityFunctionsResponder[player][location];
	}

	public int[] getBinMax(int player) {
		return binMax[player];
	}

	private void getUtilityFunction(int[] utilityFunction, int[] binMax, int x, int y, boolean flipped) {
		int i,j,k,n;
		boolean doContinue = true;
		int[][][] scoreMatrix = new int[board.length][board[0].length][utilityFunction.length];
		for (k = 0; k < utilityFunction.length; ++k) {
			n = Chips.getNrTokens(k, binMax);
			for (i = 0; i < scoreMatrix.length; ++i) {
				for (j = 0; j < scoreMatrix[0].length; ++j) {
					scoreMatrix[i][j][k] = n*SCORE_SURPLUS - SCORE_STEP*(Math.abs(x-i) + Math.abs(y-j));
					if (i == x && j == y) {
						scoreMatrix[i][j][k] += SCORE_GOAL; 
					}
				}
			}
		}
		while (doContinue) {
			doContinue = false;
			for (i = 0; i < scoreMatrix.length; ++i) {
				for (j = 0; j < scoreMatrix[0].length; ++j) {
					doContinue = doContinue || calculateLocation(scoreMatrix, binMax, i, j);
				}
			}
		}
		for (k = 0; k < utilityFunction.length; ++k) {
			if (flipped) {
				utilityFunction[k] = scoreMatrix[board.length/2][board[0].length/2][Chips.invert(k, binMax)];
			} else {
				utilityFunction[k] = scoreMatrix[board.length/2][board[0].length/2][k];
			}
		}
	}
	
	private boolean calculateLocation(int[][][] scoreMatrix, int[] binMax, int x, int y) {
		int k,k2;
		int[] bins;
		boolean hasChanged = false;
		for (k = 0; k < scoreMatrix[0][0].length; ++k) {
			// For each offer
			bins = Chips.getBins(k,binMax);
			if (bins[board[x][y]] < binMax[board[x][y]]) {
				// If it is possible to have used a chip to get to this location...
				bins[board[x][y]]++;
				k2 = Chips.convert(bins,binMax);
				if (x > 0 && scoreMatrix[x-1][y][k2] < scoreMatrix[x][y][k]) {
					// If the current score at (x-1,y) can be improved by handing over a chip to reach (x,y)... 
//					System.out.println(x+" "+y+" "+scoreMatrix[x-1][y][k2]+"="+scoreMatrix[x][y][k]);
					scoreMatrix[x-1][y][k2] = scoreMatrix[x][y][k];
					hasChanged = true;
				}
				if (y > 0 && scoreMatrix[x][y-1][k2] < scoreMatrix[x][y][k]) {
					scoreMatrix[x][y-1][k2] = scoreMatrix[x][y][k];
					hasChanged = true;
				}
				if (x < scoreMatrix.length-1 && scoreMatrix[x+1][y][k2] < scoreMatrix[x][y][k]) {
					scoreMatrix[x+1][y][k2] = scoreMatrix[x][y][k];
					hasChanged = true;
				}
				if (y < scoreMatrix[0].length-1 && scoreMatrix[x][y+1][k2] < scoreMatrix[x][y][k]) {
					scoreMatrix[x][y+1][k2] = scoreMatrix[x][y][k];
					hasChanged = true;
				}
			}
		}
		return hasChanged;
	}
	
	public int getAllocatorUtility(int playerID, int offer) {
		return utilityFunctionsAllocators[playerID][locations[playerID]][offer] - utilityFunctionsAllocators[playerID][locations[playerID]][allocatorChipset[playerID]];
	}

	public int getAllocatorTotalUtility(int playerID, int offer) {
		if (offer < 0) {
			return utilityFunctionsAllocators[playerID][locations[playerID]][allocatorChipset[playerID]];
		}
		return utilityFunctionsAllocators[playerID][locations[playerID]][offer];
	}

	public int getActionSpace(int playerID) {
		return utilityFunctionsAllocators[playerID][locations[playerID]].length;
	}

	public int getResponderUtility(int allocatorID, int offer) {
		return utilityFunctionsResponder[allocatorID][locations[locations.length-1]][offer] - utilityFunctionsResponder[allocatorID][locations[locations.length-1]][allocatorChipset[allocatorID]];
	}

	public int getResponderTotalUtility(int allocatorID, int offer) {
		if (offer < 0) {
			return utilityFunctionsResponder[allocatorID][locations[locations.length-1]][allocatorChipset[allocatorID]];
		}
		return utilityFunctionsResponder[allocatorID][locations[locations.length-1]][offer];
	}


}
