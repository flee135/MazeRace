package flee;

public enum MenuOption {
	SINGLE,
	MULTI,
	SETTINGS,
	EXIT;
	
	public MenuOption next() {
		return values()[(ordinal() + 1) % values().length];
	}
	
	public MenuOption prev() {
		return values()[(ordinal()+values().length-1) % values().length];
	}
}
