import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class DrawingPanel extends JPanel {

	private Color drawingColor = Color.WHITE;
	private Boolean isEditor;
	private Map map = null;

	public void updateColor(Color newColor) {
		drawingColor = newColor;
		repaint();
	}

	public void setMap(Map map, Boolean isEditor) {
		this.isEditor = isEditor;
		this.map = map;
		repaint();
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);

		g.setColor(drawingColor);
		g.fillRect(0, 0, getSize().width, getSize().height);

		if (map != null) {
			int x = (getSize().width / 2) - (map.getWidth() / 2);
			int y = (getSize().height / 2) - (map.getHeight() / 2);
			if (isEditor){
				map.drawEditor(g, x, y);
			} else {
				map.draw(g, x, y);
			}
		}
		//System.out.println("Paint called");
	}
}