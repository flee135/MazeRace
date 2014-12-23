package flee;

import org.lwjgl.opengl.GL11;

import java.awt.Font;

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
import org.newdawn.slick.geom.Shape;

public class Maze {
	
	private final int FRAMES_PER_SECOND = 60;
	private final int MENU_SIZE = 4;
	private final int SETTINGS_SIZE = 2;
	
	private static State state = State.MENU;	
	private int menuIndex = 0;	
	private int settingsIndex = 0;
	private UnicodeFont menuFont;
	private UnicodeFont settingsFont;
	private UnicodeFont gameFont;
	private boolean running = true;
	Graphics graphics = new Graphics();

	int mazeSize = 25;
	MazeGenerator mazeGenerator = new MazeGenerator(mazeSize);
	MazeCell[][] maze;
	float mazeLength = 400;
	float cellLength = mazeLength / mazeSize;
	
	Player player1 = new Player(0,0);

	public void run() {
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
	
	private void init() throws LWJGLException, SlickException {
		Display.setDisplayMode(new DisplayMode(800,600));
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
		
		switch (menuIndex) {
		case 0:
			menuFont.drawString(100, 350, "SINGLE PLAYER", primary);
			break;
		case 1:
			menuFont.drawString(100, 400, "MULTIPLAYER", primary);
			break;
		case 2:
			menuFont.drawString(100, 450, "SETTINGS", primary);
			break;
		case 3:
			menuFont.drawString(100, 500, "EXIT", primary);
		}
		GL11.glPopMatrix();
	}
	
	private void renderSettings() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		Color primary = Color.magenta;
		Color secondary = Color.gray;
		
		GL11.glPushMatrix();
		settingsFont.drawString(100, 100, "Maze Size:", secondary);
		settingsFont.drawString(400, 100, String.valueOf(mazeSize), secondary);
		settingsFont.drawString(100, 150, "EXIT", secondary);
		
		switch (settingsIndex) {
		case 0:
			settingsFont.drawString(100, 100, "Maze Size:", primary);
			break;
		case 1:
			settingsFont.drawString(100, 150, "EXIT", primary);
			break;
		}
		GL11.glPopMatrix();
	}
	
	private void renderSingle() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		gameFont.drawString(20, 100, "P1 Total Moves: " + player1.totalMoves);
	    
	    Rectangle r = new Rectangle(200, 100, 400, 400);
	    graphics.setColor(Color.white);
	    graphics.fill(r);
	    
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
	    	}
	    }
	    
	    Circle c = new Circle(200+cellLength/2 + player1.col*cellLength, 100+cellLength/2 + player1.row*cellLength, cellLength/2 - cellLength/4);
	    graphics.setColor(Color.blue);
	    graphics.fill(c);
	}
	
	private void processInput() {
		while (Keyboard.next()) {
			switch (state) {
			case MENU:
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
						menuIndex = (menuIndex + MENU_SIZE - 1) % MENU_SIZE;
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
						menuIndex = (menuIndex + 1) % MENU_SIZE;
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
						switch (menuIndex) {
						case 0:
							state = State.SINGLE;
							maze = mazeGenerator.generate();
							player1.reset(0, 0);
							break;
						case 1:
							state = State.MULTI;
							break;
						case 2:
							state = State.SETTINGS;
							break;
						case 3:
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
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN || Keyboard.getEventKey() == Keyboard.KEY_S) {
						if (player1.row+1 < mazeSize && maze[player1.row+1][player1.col].topOpen) {
							player1.moveDown();
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_LEFT || Keyboard.getEventKey() == Keyboard.KEY_A) {
						if (maze[player1.row][player1.col].leftOpen) {
							player1.moveLeft();
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT || Keyboard.getEventKey() == Keyboard.KEY_D) {
						if (player1.col+1 < mazeSize && maze[player1.row][player1.col+1].leftOpen) {
							player1.moveRight();
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_R) {
						maze = mazeGenerator.generate();
						player1.reset(0, 0);
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
						settingsIndex = (settingsIndex + SETTINGS_SIZE - 1) % SETTINGS_SIZE;
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
						settingsIndex = (settingsIndex + 1) % SETTINGS_SIZE;
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
						switch (settingsIndex) {
						case 0: // Maze Size
							if (mazeSize > 5) {
								mazeSize-=5;
								mazeGenerator = new MazeGenerator(mazeSize);
								cellLength = mazeLength / mazeSize;
							}
							break;
						case 1:
							state = State.MENU;
							break;
						}
					}
					if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
						switch (settingsIndex) {
						case 0: // Maze Size
							if (mazeSize < 50) {
								mazeSize+=5;
								mazeGenerator = new MazeGenerator(mazeSize);
								cellLength = mazeLength / mazeSize;
							}
							break;
						case 1:
							state = State.MENU;
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

	public static void main(String[] args) {
		new Maze().run();
	}

}