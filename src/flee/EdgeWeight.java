package flee;

public class EdgeWeight implements Comparable<EdgeWeight> {
	MazeCell cell;
	boolean isTop;
	int weight;
	
	public EdgeWeight(MazeCell cell, boolean isTop, int weight) {
		this.cell = cell;
		this.isTop = isTop;
		this.weight = weight;
	}

	@Override
	public int compareTo(EdgeWeight o) {
		return this.weight - o.weight;
	}
	
	
}
