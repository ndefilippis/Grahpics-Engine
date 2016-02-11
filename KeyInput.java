import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyInput implements KeyListener{
	Transform t;
	boolean[] keys = new boolean[256];
	
	public KeyInput(){
		t = new Transform();
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		keys[arg0.getKeyCode()] = true;
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		keys[arg0.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void update(){
		t = new Transform();
		for(int i = 0; i < 256; i++){
			if(keys[i] == true){
				switch(i){				
				case KeyEvent.VK_W:
					t = Transform.translate(new Vec3f(0, 0, .1)).mult(t);
					break;
				case KeyEvent.VK_S:
					t = Transform.translate(new Vec3f(0, 0, -.1)).mult(t);
					break;
				case KeyEvent.VK_A:
					t = Transform.translate(new Vec3f(.1, 0, 0)).mult(t);
					break;
				case KeyEvent.VK_D:
					t = Transform.translate(new Vec3f(-.1, 0, 0)).mult(t);
					break;
				case KeyEvent.VK_R:
					t = Transform.rotateX(0.1).mult(t);
					break;
				case KeyEvent.VK_T:
					t = Transform.rotateX(-0.1).mult(t);
					break;
				case KeyEvent.VK_F:
					t = Transform.rotateY(0.1).mult(t);
					break;
				case KeyEvent.VK_G:
					t = Transform.rotateY(-0.1).mult(t);
					break;
				case KeyEvent.VK_V:
					t = Transform.rotateZ(0.1).mult(t);
					break;
				case KeyEvent.VK_B:
					t = Transform.rotateZ(-0.1).mult(t);
					break;
				}
			}
		}
	}

}
