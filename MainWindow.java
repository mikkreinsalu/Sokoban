import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame implements ActionListener, KeyListener, MouseListener {

	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;
	private final static String COMMAND_EDITOR = "-editor";
	private final static String COMMAND_LEVEL = "-level";

	private JButton updateButton, loadButton, saveButton;
	private JTextField gameName, rowsFld, colsFld;
	private JLabel rowsLbl, colsLbl, gameNameLbl;
	private DrawingPanel drawingPanel;
	private Map map;

	private Thread timerThread;
	private Timer timer;
	public int seconds = -2;
	public String scoreName = "";


	public static void main(String[] args) {
		boolean isEditor = false;
		int index = 0;
		while (!isEditor && index < args.length){
			if(args[index].equalsIgnoreCase(COMMAND_EDITOR)) {
				isEditor = true;
			}
			index++;
		}
		if (isEditor) {
			MainWindow editorWindow = new MainWindow();
			editorWindow.setVisible(true);
			System.out.println("Editor mode");
		}
		boolean isLevel = false;
		int indexB = 0;
		String levelName = "";
		while (!isLevel && indexB < args.length){
			if (args[indexB].equalsIgnoreCase(COMMAND_LEVEL)) {
				isLevel = true;
				levelName = args[indexB + 1];
				
			}
			indexB++;
		}
		if (isLevel) {
			MainWindow mainWindow = new MainWindow(levelName);
			mainWindow.setVisible(true);
			System.out.println("Game Running");
		}
	}
	public MainWindow() {
		initiaLizeGUI();
		initiaLizeEditor();
		initiaLizeMap();
		addMouseListener(this);
	}
	public MainWindow(String lvlName) {
		initiaLizeGUI();
		initiaLizeEvents();
		initiaLizeMap();		
		// old map
		scoreName = lvlName.split(".skb")[0];
		System.out.println(scoreName);
		
		map.loadMap(lvlName, this);
		drawingPanel.setMap(map, false);
		timer = new Timer(this);
		timerThread =  new Thread(timer);
		timerThread.start();
		map.readScore(scoreName);
		// new map
	}
	private void initiaLizeEditor() {

		JPanel buttonsPanel = new JPanel();

		gameNameLbl = new JLabel("Level name");
		buttonsPanel.add(gameNameLbl);
		gameName = new JTextField(10);
		gameName.setFocusable(true);
		buttonsPanel.add(gameName);

		rowsLbl = new JLabel("Rows");
		buttonsPanel.add(rowsLbl);
		rowsFld = new JTextField(3);
		rowsFld.setFocusable(true);
		buttonsPanel.add(rowsFld);

		colsLbl = new JLabel("Columns");
		buttonsPanel.add(colsLbl);
		colsFld = new JTextField(3);
		colsFld.setFocusable(true);
		buttonsPanel.add(colsFld);
		
		updateButton = new JButton();
		updateButton.setText("Update");
		updateButton.addActionListener(this);
		updateButton.setFocusable(true);
		buttonsPanel.add(updateButton);


		loadButton = new JButton();
		loadButton.setText("Load");
		loadButton.addActionListener(this);
		loadButton.setFocusable(true);
		buttonsPanel.add(loadButton);


		saveButton = new JButton();
		saveButton.setText("Save");
		saveButton.addActionListener(this);
		saveButton.setFocusable(true);
		buttonsPanel.add(saveButton);

		add(buttonsPanel, BorderLayout.SOUTH);
	}
	private void initiaLizeGUI() {
		setTitle("Graphics Test");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setLayout(new BorderLayout());

		drawingPanel = new DrawingPanel();
		add(drawingPanel, BorderLayout.CENTER);	
	}

	private void initiaLizeEvents() {
		addKeyListener(this);
		setFocusable(true);
	}
	private void initiaLizeMap() {
		map = new Map(0, 0);
		drawingPanel.setMap(map, true);

	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {
			if (rowsFld.getText().length() > 0 && 
				colsFld.getText().length() > 0 &&
				isNum(rowsFld.getText()) &&
				isNum(colsFld.getText())
				){
				int rows = Integer.parseInt(rowsFld.getText());
				int columns = Integer.parseInt(colsFld.getText());

				map = new Map(rows, columns);
				drawingPanel.setMap(map, true);
			}
		} else if (e.getSource() == loadButton) {
			String loadLvlName = gameName.getText();
			if (loadLvlName.length() > 0) {
				if (loadLvlName.indexOf(".skb") < 0) {
					loadLvlName += ".skb";
				}
				map.loadMap(loadLvlName, this);
				drawingPanel.setMap(map, true);
			} else {
				JOptionPane.showMessageDialog(this, "No level name", "Error", JOptionPane.WARNING_MESSAGE);
			}
		} else if (e.getSource() == saveButton) {
			String loadLvlName = gameName.getText();
			if (loadLvlName.length() > 0) {
				if (loadLvlName.indexOf(".skb") < 0) {
					loadLvlName += ".skb";
				}
			map.saveMap(loadLvlName);

			} else {
				JOptionPane.showMessageDialog(this, "No level name", "Error", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (map != null) {
			map.upDate(e.getKeyCode());
			drawingPanel.repaint();

		}
	}

	public void keyPressed(KeyEvent e) {	
	}

	public void keyTyped(KeyEvent e) {
	}
	public void drawSeconds() {
		drawingPanel.repaint();
	}
	public void stopTimer() {
		timerThread.interrupt();
	}
	public void playerWon() {
		/* I changed exit and save so exit = yes and save = no on the YES_NO OPTION */
		Object[] options = {"Exit",
                    "Save"};                                  
        int selectedOption = JOptionPane.showOptionDialog(this, 
        		  "Nice, You won,\n" 
				+ "press Save to save score and exit game\n"
				+ "press Exit to close the game", 
				"WINNER!", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.PLAIN_MESSAGE, 
                null, options, options[1]);

		if (selectedOption == JOptionPane.YES_OPTION) {
    		this.dispose();
		} else if (selectedOption == JOptionPane.NO_OPTION) {
			map.saveScore();
			this.dispose();
		}

	}
	public Boolean isNum(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x > ((WINDOW_WIDTH / 2) - (map.getWidth() / 2)) &&
			x < ((WINDOW_WIDTH / 2) + (map.getWidth() / 2)) &&
			y > ((WINDOW_HEIGHT / 2) - (map.getHeight() / 2)) &&
			y < ((WINDOW_HEIGHT / 2) + (map.getHeight() / 2)) ){

				int[] position = map.getPosition(x, y);
				map.changeTile(position[0], position[1]);
				drawingPanel.repaint();
		}
		
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
}