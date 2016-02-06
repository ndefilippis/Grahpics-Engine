import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyInput implements KeyListener{
	Transform t;
	
	public KeyInput(){
		t = new Transform();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		switch(arg0.getKeyCode()){
		
		case KeyEvent.VK_W:
		t = Transform.translate(new Vec3f(0, 0, -.1));
		break;
		case KeyEvent.VK_S:
			t = Transform.translate(new Vec3f(0, 0, .1));
			break;
		case KeyEvent.VK_A:
			t = Transform.translate(new Vec3f(.1, 0, 0));
			break;
		case KeyEvent.VK_D:
			t = Transform.translate(new Vec3f(-.1, 0, 0));
			break;
		case KeyEvent.VK_R:
			t = Transform.rotateX(0.1);
			break;
		case KeyEvent.VK_T:
			t = Transform.rotateX(-0.1);
			break;
		case KeyEvent.VK_F:
			t = Transform.rotateY(0.1);
			break;
		case KeyEvent.VK_G:
			t = Transform.rotateY(-0.1);
			break;
		case KeyEvent.VK_V:
			t = Transform.rotateZ(0.1);
			break;
		case KeyEvent.VK_B:
			t = Transform.rotateZ(-0.1);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		t = new Transform();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
