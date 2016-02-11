import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


public class MouseInput implements MouseMotionListener{
	int mouseX;
	int mouseY;
	Robot robot;
	Transform t = new Transform();
	
	public MouseInput() throws AWTException{
		mouseX = MouseInfo.getPointerInfo().getLocation().x;
		mouseY = MouseInfo.getPointerInfo().getLocation().y;
		robot = new Robot();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void update(Point p){
		int currMouseX = p.x;
		int currMouseY = p.y;
		t = Transform.rotateY((currMouseX - mouseX)/300.0);
		t = t.mult(Transform.rotateX((currMouseY - mouseY)/300.0));
		robot.mouseMove(mouseX, mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
