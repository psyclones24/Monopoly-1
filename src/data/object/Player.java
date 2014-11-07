package data.object;

import data.global.Game;
import data.module.Helper;
import data.module.Vocab;
import data.module.Window;

public class Player {
	// attr_accessor
	public String name = Vocab.AvailableCell, icon = "\u3000\u3000";
	public int location = 0, direction = 1;
	public int cash = 5000, deposit = 0, coupon = 0, property = 0;
	public int totalAssets = 0; // totalAssets is counted every time when show
	// define states
	public int slowRound = 0, fineFreeRound = 0, cellRobRound = 0;
	// define cells[] items[]
	public boolean[] cells = new boolean[Map.length];
	public int[] items = new int[Item.ItemNum];
	
	// def initialize
	public Player() {
	}
	
	// def method
	/** Control player to move */
	public void move(int step) {
		// whether player can move?
		for (int i = 0; i < step; i++) {
			step();
			// whether player is blocked by barrier
			if (Game.mapWithInfo.route[location].isBarrier) {
				break;// show info 
			}
			// whether bank
			if (Game.mapWithInfo.route[location].type == 2) {
				//bank
			}
		}
	}
	
	/** Use items */
	void useItem(int index) {
		
	}
	
	public void buy() {   // only can buy cell at your current location
		// by default location always equals to players[cplayer].location
		if (cash >= Game.mapWithInfo.route[location].price * Game.mapWithInfo.route[location].level) {
			cash -= Game.mapWithInfo.route[location].price * Game.mapWithInfo.route[location].level;
			cells[location] = true;
			Game.mapWithInfo.route[location].owner = Game.currentPlayer;
			Game.mapWithInfo.route[location].icon = Vocab.CellIcon[6 + Game.currentPlayer];
			property += Game.mapWithInfo.route[location].price;
		} else {
			Window.showErrorInfo(Vocab.LackOfCashError);
		}
	}
	
	public void levelUp() {
		if (cash >= Game.mapWithInfo.route[location].price / 2) {
			cash -= Game.mapWithInfo.route[location].price / 2;
			Game.mapWithInfo.route[location].level += 1;
			property += Game.mapWithInfo.route[location].price;
		} else {
			Window.showErrorInfo(Vocab.LackOfCashError);
		}
	}
	
	public void fined() {
		int sum = 0, current = 0, fine = 0, fineRemaining = 0;
		boolean isFailed = true;  // suppose true
		// Calculate value in all street
		for (int i = 0; i < Map.length; i++) {			
			if ((Game.mapWithInfo.route[i].street == Game.mapWithInfo.route[location].street) && (Game.mapWithInfo.route[i].owner == Game.mapWithInfo.route[location].owner)) {
				sum += Game.mapWithInfo.route[i].price * Game.mapWithInfo.route[i].level;
			}
		}
		// Calculate value in current cell
		current = Game.mapWithInfo.route[location].price * Game.mapWithInfo.route[location].level;
		fine = sum / 10 + current / 5;
		// fine
		if (cash >= fine) {
			cash -= fine;
			Window.showLossInfo(2, fine);
		} else {  // cash not enough
			fineRemaining = fine - cash;
			Window.showLossInfo(2, cash);
			cash = 0;
			// fine deposit
			if (deposit >= fineRemaining) {
				deposit -= fineRemaining;
				Window.showLossInfo(3, fineRemaining);
			} else {  // deposit not enough
				fineRemaining -= deposit;
				Window.showLossInfo(3, deposit);
				deposit = 0;
				// fine property
				int value = 0;
				for (int i = 0; i < Map.length; i++) {
					value = 0;
					if (cells[i]) {
						value = Game.mapWithInfo.route[i].price * Game.mapWithInfo.route[i].level;
						// sell cell[i]
						Game.mapWithInfo.route[i].owner = 0;
						cells[i] = false;
						Game.mapWithInfo.route[i].icon = Vocab.CellIcon[0];
						property -= value;
						if (value >= fineRemaining) {
							cash += value - fineRemaining;
							Window.showLossInfo(4, Game.mapWithInfo.route[i].street); // need +
							isFailed = false;
							break;
						} else {
							fineRemaining -= value;
						}
					}
				}
				if (isFailed) {  // need +  // support 2 players
					Window.showEndGameWithWinOf(Game.players[3 - Game.currentPlayer]);
					Helper.getEnter();
					System.exit(0);
				}
			}
		}
		Game.players[3 - Game.currentPlayer].cash += fine;
		Window.showGetInfo(fine + Vocab.PlayersInfoListHead[2]);
	}
	
	public void getCash(int cashGet) {
		cash += cashGet;
		Window.showGetInfo(cashGet + Vocab.PlayersInfoListHead[2]);
	}
	
	public void getCoupon(int couponGet) {
		coupon += couponGet;
		Window.showGetInfo(couponGet + Vocab.PlayersInfoListHead[1]);
	}
	
	public void calTotalAssets() {
		totalAssets = cash + deposit + coupon + property;
	}
	
	// def private method
	/** One step and deal with sth */
	private void step() {
		location += direction;
		location = Helper.ensure(location);
	}

}
