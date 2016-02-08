import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class RenderTest {
	static double near = 1;
	static double far = 1000;
	static double focal = 20;
	static double fWidth = 0.980;
	static double fHeight = 0.735;
	static Shape teapot;
	static int imageWidth = 640;
	static int imageHeight = 480;
	static Mat4f w2c = new Mat4f(1, 0, 0, -1.5,
			  					 0, 1, 0, -1.5,
			  					 0, 0, 1, -8,
			  					 0, 0, 0, 1);

	public static void main(String[] args) throws IOException{
		teapot = new Cube();
		Camera cam = new Camera(near, far, focal, fWidth, fHeight, imageWidth, imageHeight, w2c);
		Renderer renderer = new Renderer(imageHeight, imageHeight, cam);
		BufferedImage bf = renderer.render(teapot);
		JFrame f = new JFrame("hey");
		Panel p = new Panel(bf);
		KeyInput ki = new KeyInput();
		f.addKeyListener(ki);
		f.add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(imageWidth, imageHeight);
		double c = Math.cos(1.0/180);
		double s = Math.sin(1.0/180);
		Mat4f rot = new Mat4f(c, 	0, 	s, 	0,
							  0, 	1, 	0, 0,
							  -s, 	0, 	c, 	0,
							  0, 	0, 	0, 	1);
		long time = System.nanoTime();
		while(true){
			long currTime = System.nanoTime();
			if(currTime - time > 1000000000/60){
				teapot.transform(ki.t);
				bf = renderer.render(teapot);
				p.image = bf;
				p.repaint();
				time = currTime;
			}
		}
	}
}
