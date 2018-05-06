package musicaflight.dashboard.games;

public abstract class CasinoGame extends Game {

	public CasinoGame(boolean pausable) {
		super(pausable);
	}

	public abstract void moneyDropped(int droppedMoney);

	public abstract long getMoneyValue();
	
	public abstract void cashIn();
	
}
