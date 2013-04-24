/*Abstract class which contains additional functionality for powerups. 
 * Extends the GameObj class*/

public abstract class PowerUp extends GameObj {

	private static final int VEL = 0;
	private Boolean isUsed;

	public PowerUp(int p_x, int p_y, int size, int courtWidth, int courtHeight) {
		super(VEL, VEL, p_x, p_y, size, size, courtWidth, courtHeight);
		isUsed = false;
	}

	public Boolean isUsed() {
		return isUsed;
	}

	public void used() {
		isUsed = true;
	}

	abstract void gain(Player p);
}
