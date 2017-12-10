package game.model;

public enum Alliance {
	RED() {

		public boolean isBlue() {
			return false;
		}

		public boolean isRed() {
			return true;
		}

		@Override
		public Player choosePlayer(final RedPlayer redPlayer, final BluePlayer bluePlayer) {
			return redPlayer;
		}
	},
	BLUE() {
		
		public boolean isBlue() {
			return true;
		}

		public boolean isRed() {
			return false;
		}

		@Override
		public Player choosePlayer(final RedPlayer redPlayer, final BluePlayer bluePlayer) {
			return bluePlayer;
		}
	};

	public abstract boolean isBlue();
	public abstract boolean isRed();
	public abstract Player choosePlayer(RedPlayer redPlayer, BluePlayer bluePlayer);
	
}
