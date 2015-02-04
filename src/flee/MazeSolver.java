package flee;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class MazeSolver {
	public static LinkedList<Direction> solve(MazeCell[][] maze, int startRow, int startCol, int endRow, int endCol) {
		// Check valid start and end
		if (startRow < 0 || startCol < 0 || endRow < 0 || endCol < 0 ||
				startRow >= maze.length || startCol >= maze.length || endRow >= maze.length || endCol >= maze.length) {
			return null;
		}
		
		MazeCell endCell = null;
		
		Queue<MazeCell> q = new LinkedList<MazeCell>();
		q.add(maze[startRow][startCol]);
		
		HashSet<MazeCell> visited = new HashSet<MazeCell>();
		visited.add(maze[startRow][startCol]);
		HashMap<MazeCell, MazeCell> predecessors = new HashMap<MazeCell, MazeCell>();
		
		while (!q.isEmpty()) {
			MazeCell curr = q.remove();
			
			if (curr.row == endRow && curr.col == endCol) {
				// This is the destination. All information for the path is stored in predecessors
				endCell = curr;
				break;
			}
			
			// Check top
			if (curr.row-1 >= 0 && curr.topOpen && !visited.contains(maze[curr.row-1][curr.col])) {
				q.add(maze[curr.row-1][curr.col]);
				visited.add(maze[curr.row-1][curr.col]);
				predecessors.put(maze[curr.row-1][curr.col], curr);
			}
			
			// Check left
			if (curr.col-1 >= 0 && curr.leftOpen && !visited.contains(maze[curr.row][curr.col-1])) {
				q.add(maze[curr.row][curr.col-1]);
				visited.add(maze[curr.row][curr.col-1]);
				predecessors.put(maze[curr.row][curr.col-1], curr);
			}
			
			// Check down
			if (curr.row+1 < maze.length && maze[curr.row+1][curr.col].topOpen && !visited.contains(maze[curr.row+1][curr.col])) {
				q.add(maze[curr.row+1][curr.col]);
				visited.add(maze[curr.row+1][curr.col]);
				predecessors.put(maze[curr.row+1][curr.col], curr);
			}
			
			// Check right
			if (curr.col+1 < maze.length && maze[curr.row][curr.col+1].leftOpen && !visited.contains(maze[curr.row][curr.col+1])) {
				q.add(maze[curr.row][curr.col+1]);
				visited.add(maze[curr.row][curr.col+1]);
				predecessors.put(maze[curr.row][curr.col+1], curr);
			}
		}
		
		// return path
		LinkedList<Direction> path = new LinkedList<Direction>();
		while (endCell.row != startRow || endCell.col != startCol) {
			MazeCell prevCell = predecessors.get(endCell);
			if (prevCell.row < endCell.row) {
				path.addFirst(Direction.DOWN);
			} else if (prevCell.row > endCell.row) {
				path.addFirst(Direction.UP);
			} else if (prevCell.col < endCell.col) {
				path.addFirst(Direction.RIGHT);
			} else if (prevCell.col > endCell.col) {
				path.addFirst(Direction.LEFT);
			}
			endCell = prevCell;
		}
		
		return path;
		
	}
}
