package flee;

public class MazeCell {
	int row;
	int col;
	boolean topOpen;
	boolean leftOpen;
	int topWeight;
	int leftWeight;
	
	public MazeCell(int row, int col, boolean topOpen, boolean leftOpen, int topWeight, int leftWeight) {
		this.row = row;
		this.col = col;
		this.topOpen = topOpen;
		this.leftOpen = leftOpen;
		this.topWeight = topWeight;
		this.leftWeight = leftWeight;
	}
	
	public void setTopOpen(boolean b) {
		topOpen = b;
	}
	
	public void setLeftOpen(boolean b) {
		leftOpen = b;
	}
	
	public void setTopWeight(int n) {
		topWeight = n;
	}
	
	public void setLeftWeight(int n) {
		leftWeight = n;
	}
}
