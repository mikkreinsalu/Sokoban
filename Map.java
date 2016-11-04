import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class Map {

	private final int TILE_PATH = 0;
	private final int TILE_WALL = 1;
	private final int TILE_BOX  = 2;
	private final int TILE_TARGET = 3;
	private final int TILE_CHARACTER = 4;

	private final int TILE_WIDTH = 25;
	private final int TILE_HEIGHT = 25;
	private final int TILE_SEPARATION = 1;

	public int[][] scores;
	private int rows;
	private int columns;
	private int[][] map;
	private int numberOfScores = 0;
	private int scoreArraySize;
	private int currentColumn;
	private int currentRow;
	private int lastRow;
	private int lastCol;
	private int counter = 0;
	private int fontSize = 16;
	private int highscore = 999;

	private MainWindow listener;

	private int nrTargets = 0;
	private int[][] targets = new int[nrTargets][2];

	public Map(int rows, int columns){
		this.rows = rows;
		this.columns = columns;

		map = new int[rows][columns];

		for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					map[row][column] = TILE_PATH;					
				}
		}
	}

	public void loadMap(String fileName, MainWindow listener) {
		this.listener = listener;
		try {
				FileInputStream inputStream = new FileInputStream(fileName);

				rows = inputStream.read();
				columns = inputStream.read();
				nrTargets = 0;
				map = new int[rows][columns];


				for (int row = 0; row < rows; row++) {
    				for (int column = 0; column < columns; column++) {
    					int v = inputStream.read();
    					if (v == 0) {
    						map[row][column] = TILE_PATH;
    					} else if (v == 1) {
    						map[row][column] = TILE_WALL;
    					} else if (v == 2) {
    						map[row][column] = TILE_BOX;
    					} else if (v == 3) {
    						map[row][column] = TILE_TARGET;
    						nrTargets++;
    					} else if (v == 4) {
    						map[row][column] = TILE_CHARACTER;
    						currentRow = row;
    						currentColumn = column;

    					}
    				}
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			targets = new int[nrTargets][2];
			int indx = 0;
			if (nrTargets > 0) {
				for (int row = 0; row < rows; row++) {
	    				for (int column = 0; column < columns; column++) {
	    					if (map[row][column] == TILE_TARGET) {
	    						int[] targetArray = new int[2];
	    						targetArray[0] = row;
	    						targetArray[1] = column;
	    						targets[indx] = targetArray;
	    						indx++;
	    					}
	    				}
	    			}
    		}
	}
	
	private Boolean checkTarget(int row, int col) {
		for (int i = 0; i < targets.length; i++) {
			if (row == targets[i][0] && col == targets[i][1]) {
				return true;
			}
		}
		return false;

	}

	private Boolean forTheWin() {
		int row, col;

		for (int j = 0; j < targets.length; j++) {
			row = targets[j][0];
			col = targets[j][1];
			if (map[row][col] != TILE_BOX) {
				return false;
			}
		}
		System.out.println("võit");
		return true;
	}

	public int getMap() {
		return map[rows][columns];
	}
	public void changeTile(int row, int column) {
		int tile = map[row][column];
		tile++;
		if(tile == 5) {tile = 0;}
		map[row][column] = tile;

	}
	public int[] getPosition(int x, int y) {
		int[] position = new int[2];

		int mapStartingX = (MainWindow.WINDOW_WIDTH / 2) - (getWidth() /2 );
		int mapStartingY = (MainWindow.WINDOW_HEIGHT / 2) - (getHeight() /2);
		int col = (x - mapStartingX) / (TILE_WIDTH + TILE_SEPARATION);
		int row = (y - mapStartingY) / (TILE_HEIGHT + TILE_SEPARATION);
		position[0] = row;
		position[1] = col;

		return position;
	}
	public void saveMap(String levelName) {
		try {
			FileOutputStream outStream = new FileOutputStream(levelName);
			outStream.write((byte) rows);
			outStream.write((byte) columns);

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					outStream.write((byte)map[i][j]);
				}
			}
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getWidth() {

		return (columns * TILE_WIDTH) + ((columns - 1) * TILE_SEPARATION);
	}

	public int getHeight() {

		return (rows * TILE_HEIGHT) + ((rows - 1) * TILE_SEPARATION);
	}
	public void drawEditor(Graphics g, int x, int y) {
		int offsetX = 0;
		int offsetY = 0;

		for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					Color color =  getTileColor(map[row][column]);
					g.setColor(color);
					g.fillRect(x + offsetX, y + offsetY, TILE_WIDTH, TILE_HEIGHT);


					offsetX += TILE_WIDTH + TILE_SEPARATION;
				}
			offsetX = 0;
			offsetY += TILE_HEIGHT + TILE_SEPARATION;
		}
	}
	public void saveBlankScore (){
		BufferedWriter outputStream = null;
		try {

            outputStream = new BufferedWriter(new FileWriter(listener.scoreName + "Score.txt"));
            outputStream.write(1 + "\n");

            outputStream.close();
            
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void draw(Graphics g, int x, int y) {

		int offsetX = 0;
		int offsetY = 0;

		g.setFont(new Font("ARIAL", Font.BOLD, fontSize));
    	g.setColor(Color.black);
		g.drawString("Moves " + counter , 20, 20);
		g.drawString("Seconds " + listener.seconds, 680, 20);
		g.drawString("Best time: " + highscore + " seconds", 20, 50);

 		for (int row = 0; row < rows; row++) {
				for (int column = 0; column < columns; column++) {
					Color color =  getTileColor(map[row][column]);
					if (checkTarget(row, column) && map[row][column] == TILE_PATH) {
						color = Color.RED;
					}
					g.setColor(color);
					g.fillRect(x + offsetX, y + offsetY, TILE_WIDTH, TILE_HEIGHT);


					offsetX += TILE_WIDTH + TILE_SEPARATION;
				}
			offsetX = 0;
			offsetY += TILE_HEIGHT + TILE_SEPARATION;
		}
	}
	
	public void readScore(String lvlname){
		String lvlName = lvlname + "Score.txt";
		
		BufferedReader inputStream = null;
		File scoreFile = new File(lvlName);
		if(scoreFile.exists() && scoreFile.isFile()){

			try {
				inputStream = new BufferedReader(new FileReader(lvlName));

				String timeScore;

				numberOfScores = Integer.parseInt(inputStream.readLine().trim());
				scores = new int[numberOfScores][2];
				int index = 0;
	            while ((timeScore = inputStream.readLine()) != null) {
	            	timeScore = timeScore.trim();
	            	int time = Integer.parseInt(timeScore.split(" ")[0]);
	            	int moves = Integer.parseInt(timeScore.split(" ")[1]);
	            	int[] timeMoves = new int[2];
	            	timeMoves[0] = time;
	            	timeMoves[1] = moves;
	            	scores[index] = timeMoves;
	            	index++;

	                //System.out.println(timeScore);
	            }
	            inputStream.close();
	            scores = sortingScores(scores);
	            highscore = scores[0][0];
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			saveBlankScore();

		}

	}

	public void saveScore() {

		BufferedWriter outputStream = null;
		try {

            outputStream = new BufferedWriter(new FileWriter(listener.scoreName + "Score.txt"));
            outputStream.write(numberOfScores + 1 + "\n");
            for( int i = 0; i < numberOfScores; i ++) {
            	outputStream.write(scores[i][0] + " " + scores[i][1] + "\n");
            }
            outputStream.write(listener.seconds + " " + counter);

            outputStream.close();
            
        } catch (IOException e) {
			e.printStackTrace();
		}

	}
	public int[][] sortingScores (int[][] inScores){
		int[][] scores = inScores;
		int scoresLen = scores.length;
		  
		int idx1 = 0;
		while (idx1 < scoresLen){
			int idx2 = idx1 + 1;
			while (idx2 < scoresLen){
				if (scores[idx1][0] > scores[idx2][0]){
		    	int[] buffer = scores[idx2];
		    	scores[idx2] = scores[idx1];
		    	scores[idx1] = buffer;
		    } else if (scores[idx1][0] == scores[idx2][0]){
		    	if (scores[idx1][1] > scores[idx2][1]){
		    	int[] buffer = scores[idx2];
		    	scores[idx2] = scores[idx1];
		    	scores[idx1] = buffer;
		    	}
		    }
		    idx2 += 1;
			}
		idx1 += 1;
		}
		return scores;
	}
	
	private Color getTileColor(int tileValue) {
		switch (tileValue) {
			case TILE_PATH:
				return Color.BLACK;
			case TILE_WALL:
				return Color.GRAY;
			case TILE_BOX:
				return Color.ORANGE;
			case TILE_TARGET:
				return Color.RED;
			case TILE_CHARACTER:
				return Color.GREEN;
		}
		return Color.YELLOW;
	}

	public void upDate(int keyCode) {

        int targetRow = currentRow;
        int targetColumn = currentColumn;
        counter ++;
        if (keyCode == KeyEvent.VK_UP) {
            if (targetRow > 0) {
                targetRow--;
                if (map[targetRow][targetColumn] == TILE_BOX && targetRow > 0){
                	if (map[targetRow - 1][targetColumn] == TILE_WALL || map[targetRow - 1][targetColumn] == TILE_BOX) {
                		//System.out.println("sein");
                	} else {
            			map[targetRow][targetColumn] = TILE_PATH;
            			map[targetRow - 1][targetColumn] = TILE_BOX;
            		}
            	}
            }
        } else if (keyCode == KeyEvent.VK_DOWN) {
			if (targetRow < (rows - 1)) {
                targetRow++;
                if (map[targetRow][targetColumn] == TILE_BOX && targetRow < (rows - 1)){
                	if (map[targetRow + 1][targetColumn] == TILE_WALL || map[targetRow + 1][targetColumn] == TILE_BOX) {
                		//System.out.println("sein");
                	} else {
            			map[targetRow][targetColumn] = TILE_PATH;
            			map[targetRow + 1][targetColumn] = TILE_BOX;
            		} 
            	}
            }
        } else if (keyCode == KeyEvent.VK_LEFT) {
            if (targetColumn > 0) {
                targetColumn--;
                if (map[targetRow][targetColumn] == TILE_BOX && targetColumn > 0){
                	if (map[targetRow][targetColumn - 1] == TILE_WALL || map[targetRow][targetColumn - 1] == TILE_BOX) {
                		//System.out.println("sein");
                	} else {
            			map[targetRow][targetColumn] = TILE_PATH;
            			map[targetRow][targetColumn - 1] = TILE_BOX;
            		}	
            	}
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            if (targetColumn < (columns - 1)) {
                targetColumn++;
                if (map[targetRow][targetColumn] == TILE_BOX && targetColumn < (columns - 1)){
                	if (map[targetRow][targetColumn + 1] == TILE_WALL || map[targetRow][targetColumn + 1] == TILE_BOX) {
                		//System.out.println("sein");
                	} else {
                		map[targetRow][targetColumn] = TILE_PATH;
            			map[targetRow][targetColumn + 1] = TILE_BOX;
            		}	
            	} 
            }
        } 

        if (map[targetRow][targetColumn] == TILE_PATH || map[targetRow][targetColumn] == TILE_TARGET) {
        	map[currentRow][currentColumn] = TILE_PATH;        	
            currentRow = targetRow;
            currentColumn = targetColumn;
            map[currentRow][currentColumn] = TILE_CHARACTER;
        }
        if (forTheWin()) {
        	listener.stopTimer();
        	listener.playerWon();
        }
        
    }
}

