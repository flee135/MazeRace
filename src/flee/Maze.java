package flee;

import org.lwjgl.opengl.GL11;

import java.awt.Font;
import java.time.Instant;
import java.util.LinkedList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;

public class Maze {

	// CONSTANTS
	private final int WINDOW_WIDTH = 800;
	private final int WINDOW_HEIGHT = 600;
	private final int FRAMES_PER_SECOND = 60;
	private final int MIN_MAZE_SIZE = 5;
	private final int MAX_MAZE_SIZE = 50;
	private final int MAX_BLIND_RADIUS = 3;

	// GRAPHICS VARIABLES
	private UnicodeFont menuFont;
	private UnicodeFont settingsFont;
	private UnicodeFont gameFont;
	private Graphics graphics = new Graphics();

	// SETTINGS VARIABLES
	private SettingsOption settingsOption = SettingsOption.MAZE_SIZE;
	private int mazeSize = 25;
	private GameMode gameMode = GameMode.STANDARD;
	private int blindRadius = 2;
	private boolean changingMaze = false;
	private int changingMazeMoves = 5;

	// MENU VARIABLES
	private State state = State.MENU;
	private MenuOption menuOption = MenuOption.SINGLE;

	// MAZE VARIABLES
	private MazeGenerator mazeGenerator = new MazeGenerator(mazeSize);
	private MazeCell[][] maze;
	private float mazeLength = 400;
	private float cellLength = mazeLength / mazeSize;
	private Player player1 = new Player(0,0);
	private boolean[][] discoveredCells = new boolean[mazeSize][mazeSize];
	private Instant startTime;
	private boolean hintRequested = false;


	private boolean running = true;	

	private void run() {
		try {
			init();
			loop();
		} catch (LWJGLException e){
			e.printStackTrace();
			System.exit(1);
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Display.destroy();
	}

	@SuppressWarnings("unchecked")
	private void init() throws LWJGLException, SlickException {
		Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
		Display.create();

		ColorEffect c = new ColorEffect(java.awt.Color.white);
		menuFont = new UnicodeFont(new java.awt.Font("Verdana", Font.BOLD, 24));
		menuFont.getEffects().add(c);
		menuFont.addAsciiGlyphs();
		menuFont.loadGlyphs();

		settingsFont = new UnicodeFont(new java.awt.Font("Verdana", Font.PLAIN, 16));
		settingsFont.getEffects().add(c);
		settingsFont.addAsciiGlyphs();
		settingsFont.loadGlyphs();

		gameFont = new UnicodeFont(new java.awt.Font("Verdana", Font.PLAIN, 12));
		gameFont.getEffects().add(c);
		gameFont.addAsciiGlyphs();
		gameFont.loadGlyphs();

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private void loop() {
		while (!Display.isCloseRequested() && running) {
			render();
			processInput();

			Display.update();
			Display.sync(FRAMES_PER_SECOND);
		}
	}


	private void render() {
		switch (state) {
		case MENU:
			renderMenu();			
			break;

		case SINGLE:
			renderSingle();
			break;

		case MULTI:
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			Line l = new Line(100, 100, 200, 200);
			graphics.draw(l);

			break;

		case SETTINGS:
			renderSettings();
			break;
		}
	}

	private void renderMenu() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		Color primary = Color.red;
		Color secondary = Color.gray;

		GL11.glPushMatrix();
		menuFont.drawString(100, 350, "SINGLE PLAYER", secondary);
		menuFont.drawString(100, 400, "MULTIPLAYER", secondary);
		menuFont.drawString(100, 450, "SETTINGS", secondary);
		menuFont.drawString(100, 500, "EXIT", secondary);

		switch (menuOption) {
		case SINGLE:
			menuFont.drawString(100, 350, "SINGLE PLAYER", primary);
			break;
		case MULTI:
			menuFont.drawString(100, 400, "MULTIPLAYER", primary);
			break;
		case SETTINGS:
			menuFont.drawString(100, 450, "SETTINGS", primary);
			break;
		case EXIT:
			menuFont.drawString(100, 500, "EXIT", primary);
		}
		GL11.glPopMatrix();
	}

	private void renderSettings() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		Color primary = Color.magenta;
		Color secondary = Color.white;

		GL11.glPushMatrix();
		settingsFont.drawString(100, 100, "Maze Size:", secondary);
		settingsFont.drawString(400, 100, String.valueOf(mazeSize), secondary);
		settingsFont.drawString(100, 150, "Game Mode:", secondary);
		if (gameMode == GameMode.STANDARD) settingsFont.drawString(400, 150, "Standard", secondary);
		else if (gameMode == GameMode.DISCOVERY) settingsFont.drawString(400, 150, "Discovery", secondary);
		else if (gameMode == GameMode.BLIND) settingsFont.drawString(400, 150, "Blind", secondary);
		settingsFont.drawString(100, 200, "Blind radius:", secondary);
		settingsFont.drawString(400, 200, String.valueOf(blindRadius), secondary);
		settingsFont.drawString(100, 250, "Changing Maze:", secondary);
		settingsFont.drawString(400, 250, changingMaze ? "ON" : "OFF", secondary);
		settingsFont.drawString(100, 300, "Moves Before Maze Change:", secondary);
		settingsFont.drawString(400, 300, String.valueOf(changingMazeMoves), secondary);
		settingsFont.drawString(100, 350, "EXIT", secondary);

		switch (settingsOption) {
		case MAZE_SIZE:
			settingsFont.drawString(100, 100, "Maze Size:", primary);
			break;
		case GAME_MODE:
			settingsFont.drawString(100, 150, "Game Mode:", primary);
			break;
		case BLIND_RADIUS:
			settingsFont.drawString(100, 200, "Blind radius:", primary);
			break;
		case CHANGING_MAZE:
			settingsFont.drawString(100, 250, "Changing Maze:", primary);
			break;
		case CHANGING_MAZE_MOVES:
			settingsFont.drawString(100, 300, "Moves Before Maze Change:", primary);
			break;
		case EXIT:
			settingsFont.drawString(100, 350, "EXIT", primary);
			break;
		}
		GL11.glPopMatrix();
	}

	private void renderSingle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		// Draw container (either square or circle)
		Rectangle r = new Rectangle(200, 100, mazeLength, mazeLength);
		if (gameMode != GameMode.BLIND) {
			graphics.setColor(Color.white);
			graphics.fill(r);
			if (hintRequested) {
				drawPath(player1.row, player1.col, mazeSize-1, mazeSize-1);
			}
		} else {
			Circle c = new Circle(200+cellLength/2 + player1.col*cellLength, 100+cellLength/2 + player1.row*cellLength, blindRadius*cellLength);
			graphics.setColor(Color.white);
			graphics.fill(c);
			if (hintRequested) {
				drawPath(player1.row, player1.col, mazeSize-1, mazeSize-1);
			}
			graphics.setColor(Color.black);
			graphics.draw(r);
		}

		// Draw statistics
		long timeDiff = Instant.now().toEpochMilli() - startTime.toEpochMilli();
		gameFont.drawString(20, 100, String.format("Elapsed Time: %02d:%02d.%03d", timeDiff/60000, (timeDiff/1000)%60, timeDiff%1000) );
		gameFont.drawString(20, 150, "P1 Total Moves: " + player1.totalMoves);

		// Draw maze walls.
		for (int row=0; row<mazeSize; row++) {
			for (int col=0; col<mazeSize; col++) {
				MazeCell cell = maze[row][col];
				if (!cell.topOpen) {
					Line l = new Line(200+col*cellLength, 100+row*cellLength, 200+(col+1)*cellLength, 100+row*cellLength);
					graphics.setColor(Color.black);
					graphics.draw(l);
				}
				if (!cell.leftOpen) {
					Line l = new Line(200+col*cellLength, 100+row*cellLength, 200+col*cellLength, 100+(row+1)*cellLength);
					graphics.setColor(Color.black);
					graphics.draw(l);
				}
				if (gameMode == GameMode.DISCOVERY && !discoveredCells[row][col]) {
					r = new Rectangle(200+col*cellLength, 100+row*cellLength, cellLength+1, cellLength+1);
					graphics.setColor(Color.black);
					graphics.fill(r);
				}
			}
		}

		// Draw player1
		Circle c = new Circle(200+cellLength/2 + player1.col*cellLength, 100+cellLength/2 + player1.row*cellLength, cellLength/2 - cellLength/4);
		graphics.setColor(Color.blue);
		graphics.fill(c);
	}
	
	private void drawPath(int startRow, int startCol, int endRow, int endCol) {
		int currRow = startRow;
		int currCol = startCol;
		graphics.setColor(Color.green);
		LinkedList<Direction> soln = MazeSolver.solve(maze, startRow, startCol, endRow, endCol);
		for (Direction d : soln) {
			Line l;
			switch (d) {
			case UP:
				l = new Line(200+cellLength/2+currCol*cellLength, 100+cellLength/2+currRow*cellLength, 200+cellLength/2+currCol*cellLength, 100+cellLength/2+(currRow-1)*cellLength);
				currRow--;
				break;
			case LEFT:
				l = new Line(200+cellLength/2+currCol*cellLength, 100+cellLength/2+currRow*cellLength, 200+cellLength/2+(currCol-1)*cellLength, 100+cellLength/2+currRow*cellLength);
				currCol--;
				break;
			case DOWN:
				l = new Line(200+cellLength/2+currCol*cellLength, 100+cellLength/2+currRow*cellLength, 200+cellLength/2+currCol*cellLength, 100+cellLength/2+(currRow+1)*cellLength);
				currRow++;
				break;
			case RIGHT:
				l = new Line(200+cellLength/2+currCol*cellLength, 100+cellLength/2+currRow*cellLength, 200+cellLength/2+(currCol+1)*cellLength, 100+cellLength/2+currRow*cellLength);
				currCol++;
				break;
			default:
				l = null;	
			}
			graphics.draw(l);
		}
	}

	private void processInput() {
		while (Keyboard.next()) {
			switch (state) {
			case MENU:
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
						menuOption = menuOption.prev();
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
						menuOption = menuOption.next();
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
						switch (menuOption) {
						case SINGLE:
							state = State.SINGLE;
							maze = mazeGenerator.generate();
							player1.reset(0, 0);
							startTime = Instant.now();
							discoveredCells = new boolean[mazeSize][mazeSize];
							discoveredCells[0][0] = true;
							checkDiscovery(player1);
							hintRequested = false;
							break;
						case MULTI:
							state = State.MULTI;
							break;
						case SETTINGS:
							state = State.SETTINGS;
							break;
						case EXIT:
							running = false;
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						running = false;
					}
				}
				break;

			case SINGLE:
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						state = State.MENU;
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_UP || Keyboard.getEventKey() == Keyboard.KEY_W) {
						if (maze[player1.row][player1.col].topOpen) {
							player1.moveUp();
							checkDiscovery(player1);
							checkNewMaze(player1);
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN || Keyboard.getEventKey() == Keyboard.KEY_S) {
						if (player1.row+1 < mazeSize && maze[player1.row+1][player1.col].topOpen) {
							player1.moveDown();
							checkDiscovery(player1);
							checkNewMaze(player1);
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_LEFT || Keyboard.getEventKey() == Keyboard.KEY_A) {
						if (maze[player1.row][player1.col].leftOpen) {
							player1.moveLeft();
							checkDiscovery(player1);
							checkNewMaze(player1);
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT || Keyboard.getEventKey() == Keyboard.KEY_D) {
						if (player1.col+1 < mazeSize && maze[player1.row][player1.col+1].leftOpen) {
							player1.moveRight();
							checkDiscovery(player1);
							checkNewMaze(player1);
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_R) {
						maze = mazeGenerator.generate();
						player1.reset(0, 0);
						startTime = Instant.now();
						if (gameMode == GameMode.DISCOVERY) {
							discoveredCells = new boolean[mazeSize][mazeSize];
							discoveredCells[0][0] = true;
							if (maze[1][0].topOpen) discoveredCells[1][0] = true;
							if (maze[0][1].leftOpen) discoveredCells[0][1] = true;
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_H) {
						hintRequested = !hintRequested;
					}
				}
				break;

			case MULTI:
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						state = State.MENU;
					}
				}
				break;

			case SETTINGS:
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
						settingsOption = settingsOption.prev();
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
						settingsOption = settingsOption.next();
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
						switch (settingsOption) {
						case MAZE_SIZE: // Maze Size
							if (mazeSize-5 >= MIN_MAZE_SIZE) {
								mazeSize-=5;
								mazeGenerator = new MazeGenerator(mazeSize);
								cellLength = mazeLength / mazeSize;
							}
							break;
						case GAME_MODE:
							gameMode = gameMode.prev();
							break;
						case BLIND_RADIUS:
							if (blindRadius > 1) blindRadius--;
							break;
						case CHANGING_MAZE:
							changingMaze = !changingMaze;
							break;
						case CHANGING_MAZE_MOVES:
							if (changingMazeMoves > 1) changingMazeMoves -= 1;
							break;
						default:
							break;
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
						switch (settingsOption) {
						case MAZE_SIZE: // Maze Size
							if (mazeSize+5 <= MAX_MAZE_SIZE) {
								mazeSize+=5;
								mazeGenerator = new MazeGenerator(mazeSize);
								cellLength = mazeLength / mazeSize;
							}
							break;
						case GAME_MODE:
							gameMode = gameMode.next();
							break;
						case BLIND_RADIUS:
							if (blindRadius < MAX_BLIND_RADIUS) blindRadius++;
							break;
						case CHANGING_MAZE:
							changingMaze = !changingMaze;
							break;
						case CHANGING_MAZE_MOVES:
							changingMazeMoves += 1;
						default:
							break;
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE || Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
						state = State.MENU;
					}
				}
				break;
			}
		}		
	}

	private void checkDiscovery(Player p) {
		// Check if player1 can see on any of the adjacent sides
		int row = p.row;
		int col = p.col;

		if (maze[row][col].topOpen && row-1 >= 0) {
			discoveredCells[row-1][col] = true;
		}

		if (maze[row][col].leftOpen && col-1 >= 0) {
			discoveredCells[row][col-1] = true;
		}

		if (row+1 < mazeSize && maze[row+1][col].topOpen) {
			discoveredCells[row+1][col] = true;
		}

		if (col+1 < mazeSize && maze[row][col+1].leftOpen) {
			discoveredCells[row][col+1] = true;
		}
	}

	private void checkNewMaze(Player p) {
		if (changingMaze && p.totalMoves % changingMazeMoves == 0) {
			maze = mazeGenerator.generate();
			if (gameMode == GameMode.DISCOVERY) {
				discoveredCells = new boolean[mazeSize][mazeSize];
				discoveredCells[0][0] = true;
				if (maze[1][0].topOpen) discoveredCells[1][0] = true;
				if (maze[0][1].leftOpen) discoveredCells[0][1] = true;
			}
		}
	}

	public static void main(String[] args) {
		new Maze().run();
	}

}