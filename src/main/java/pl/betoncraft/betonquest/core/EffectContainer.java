package pl.betoncraft.betonquest.core;

public class EffectContainer {

	private final String type;
	private final int power;
	private final int duration;
	
	/**
	 * Represents potion effect
	 * @param type
	 * @param power
	 * @param duration
	 */
	public EffectContainer(String type, int power, int duration) {
		this.type = type;
		this.power = power;
		this.duration = duration;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * @return the duration
	 */
	public int getDuration() {
		return duration;
	}
}
