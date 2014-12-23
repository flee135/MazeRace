package flee;

public enum GameMode {
	STANDARD,
	DISCOVERY,
	BLIND;
	
	public GameMode next() {
		return values()[(ordinal() + 1) % values().length];
	}
	
	public GameMode prev() {
		return values()[(ordinal()+values().length-1) % values().length];
	}
}
