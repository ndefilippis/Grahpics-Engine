import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class Panel extends JPanel{
	public static BufferedImage image;
	
	public Panel(BufferedImage bi){
		image = bi;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
	}
}
