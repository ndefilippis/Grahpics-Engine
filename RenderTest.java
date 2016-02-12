import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

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

	public static void main(String[] args) throws IOException, AWTException{
		teapot = new Shape("B:/Users/Nick/Downloads/teapot.obj");
		Camera cam = new Camera(near, far, focal, fWidth, fHeight, imageWidth, imageHeight, w2c);
		Renderer renderer = new Renderer(imageHeight, imageHeight, cam);
		BufferedImage bf = renderer.renderScene(teapot);
		JFrame f = new JFrame("hey");
		Panel p = new Panel(bf);
		KeyInput ki = new KeyInput();
		MouseInput mi = new MouseInput();
		f.addKeyListener(ki);
		f.addMouseMotionListener(mi);
		f.add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(imageWidth, imageHeight);
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blank = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		f.getContentPane().setCursor(blank);
		double c = Math.cos(1.0/180);
		double s = Math.sin(1.0/180);
		Mat4f rot = new Mat4f(c, 	0, 	s, 	0,
							  0, 	1, 	0, 0,
							  -s, 	0, 	c, 	0,
							  0, 	0, 	0, 	1);
		long time = System.nanoTime();
		while(true){
			long currTime = System.nanoTime();
			if(currTime - time > 1000000000.0/60){
				ki.update();
				mi.update(MouseInfo.getPointerInfo().getLocation());
				cam.worldToCamera = ki.t.mult(cam.worldToCamera);
				cam.worldToCamera = mi.t.mult(cam.worldToCamera);
				cam.position = cam.getCameraVec(cam.worldToCamera.inverse().getTranslate());
				teapot.update(currTime/1000000000.0, (currTime - time)/1000000000.0);
				bf = renderer.renderScene(teapot);
				p.image = bf;
				p.repaint();
				time = currTime;
			}
		}
	}
}
