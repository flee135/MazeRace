package flee;

import java.util.Random;
import java.util.PriorityQueue;

public class MazeGenerator {

	static final int RAND_RANGE = 100000;

	int size;
	Random random;

	public MazeGenerator(int size) {
		this.size = size;
		random = new Random();
	}

	public MazeCell[][] generate() {

		MazeCell[][] maze = new MazeCell[size][size];

		maze[0][0] = new MazeCell(0, 0, false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);

		// Randomize weights in first row.
		for (int i=1; i<size; i++) {
			int leftWeight = random.nextInt(RAND_RANGE);
			maze[0][i] = new MazeCell(0, i, false, false, Integer.MAX_VALUE, leftWeight);
		}

		// Randomize weights in first col.
		for (int i=1; i<size; i++) {
			int topWeight = random.nextInt(RAND_RANGE);
			maze[i][0] = new MazeCell(i, 0, false, false, topWeight, Integer.MAX_VALUE);
		}

		// Randomize weights of others.
		for (int i=1; i<size; i++) {
			for (int j=1; j<size; j++) {
				int leftWeight = random.nextInt(RAND_RANGE);
				int topWeight = random.nextInt(RAND_RANGE);
				maze[i][j] = new MazeCell(i, j, false, false, topWeight, leftWeight);
			}
		}

		PriorityQueue<EdgeWeight> q = new PriorityQueue<EdgeWeight>();
		boolean[][] reachedCells = new boolean[size][size];

		MazeCell end = maze[size-1][size-1];
		reachedCells[size-1][size-1] = true;
		q.add(new EdgeWeight(end, false, end.leftWeight));
		q.add(new EdgeWeight(end, true, end.topWeight));

		int resolved = 1;

		// Keep looping until all cells are resolved.
		while (resolved < size*size) {
			EdgeWeight e = q.poll();
			MazeCell cell = e.cell;
			if (e.isTop && cell.row-1 >= 0) {
				if (!reachedCells[cell.row-1][cell.col]) {
					MazeCell added = maze[cell.row-1][cell.col];
					reachedCells[cell.row-1][cell.col] = true;
					cell.setTopOpen(true);
					resolved++;

					q.add(new EdgeWeight(added, false, added.leftWeight));
					q.add(new EdgeWeight(added, true, added.topWeight));
					if (added.col < size-1) {
						MazeCell adj = maze[added.row][added.col+1];
						q.add(new EdgeWeight(adj, false, adj.leftWeight));
					}
				} else if (!reachedCells[cell.row][cell.col]) {
					// This IS the added cell
					reachedCells[cell.row][cell.col] = true;
					cell.setTopOpen(true);
					resolved++;
					
					q.add(new EdgeWeight(cell, false, cell.leftWeight));
				}

			} else if (cell.col-1 >= 0) {
				if (!reachedCells[cell.row][cell.col-1]) {
					MazeCell added = maze[cell.row][cell.col-1];
					reachedCells[cell.row][cell.col-1] = true;
					cell.setLeftOpen(true);
					resolved++;

					q.add(new EdgeWeight(added, false, added.leftWeight));
					q.add(new EdgeWeight(added, true, added.topWeight));
					if (added.row < size-1) {
						MazeCell adj = maze[added.row+1][added.col];
						q.add(new EdgeWeight(adj, true, adj.topWeight));
					}
				} else if (!reachedCells[cell.row][cell.col]) {
					// This IS the added cell
					reachedCells[cell.row][cell.col] = true;
					cell.setLeftOpen(true);
					resolved++;
					
					q.add(new EdgeWeight(cell, true, cell.topWeight));
				}
			}			
		}

		return maze;
	}

}
