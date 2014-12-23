package flee;

public class Player {
	int row;
	int col;
	int totalMoves;
	
	public Player(int row, int col) {
		this.row = row;
		this.col = col;
		this.totalMoves = 0;
	}
	
	public void moveLeft() {
		col--;
		totalMoves++;
	}
	
	public void moveRight() {
		col++;
		totalMoves++;
	}
	
	public void moveUp() {
		row--;
		totalMoves++;
	}
	
	public void moveDown() {
		row++;
		totalMoves++;
	}
	
	public void reset(int row, int col) {
		this.row = row;
		this.col = col;
		this.totalMoves = 0;
	}
}
