package flee;

public enum SettingsOption {
	MAZE_SIZE,
	GAME_MODE,
	BLIND_RADIUS,
	CHANGING_MAZE,
	CHANGING_MAZE_MOVES,
	EXIT;
	
	public SettingsOption next() {
		return values()[(ordinal() + 1) % values().length];
	}
	
	public SettingsOption prev() {
		return values()[(ordinal()+values().length-1) % values().length];
	}
}
