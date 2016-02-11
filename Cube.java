import java.io.IOException;

public class Cube extends Shape {
	private State previous, current;
	
	public class State{
		
		public Vec3f position;
		public Quaternion orientation;
		public Vec3f linearMomentum, rotationalMomentum;
		
		public Vec3f linearVelocity, rotationalVelocity;
		public Quaternion spin;
		public Transform o2w;
	
		public Vec3f force;
		public double mass = 1;
		public double inertia;
		double size = 1;
		
		public void recalculate(){
			linearVelocity = linearMomentum.div(mass);
			rotationalVelocity = rotationalMomentum.div(inertia);
			orientation = orientation.normalize();
			spin = (new Quaternion(rotationalVelocity.x, rotationalVelocity.y, rotationalVelocity.z, 0).mult(0.5)).mult(orientation);
			Transform translation = Transform.translate(position);
			o2w = translation.mult(orientation.toTransform());
		}
	}
	
	public class Derivative{
		public Vec3f velocity = new Vec3f();
		public Vec3f force = new Vec3f();
		public Quaternion spin = new Quaternion();
		public Vec3f torque = new Vec3f();
		
	}
	

	public Cube(String path) throws IOException {
		super(path);
	}
	
	public Cube(){
		current = new State();
		current.o2w = new Transform();
		current.position = new Vec3f(2, 0, 0);
		current.linearMomentum = new Vec3f(0.1, 0, 0);
		current.orientation = new Quaternion();
		current.rotationalMomentum = new Vec3f(0, 0, 0);
		current.inertia = current.mass*current.size*1.0/6;
		current.recalculate();
		previous = current;
		
		nVerticies = 8;
		nFaces = 36;
		verts = new Vec3f[]{
			new Vec3f(-0.5, -0.5, 0.5),
			new Vec3f(-0.5, 0.5, 0.5),
			new Vec3f(0.5, -0.5, 0.5),
			new Vec3f(0.5, 0.5, 0.5),
			
			new Vec3f(-0.5, -0.5, -0.5),
			new Vec3f(-0.5, 0.5, -0.5),
			new Vec3f(0.5, -0.5, -0.5),
			new Vec3f(0.5, 0.5, -0.5),
		};
		faces = new int[]{
			1, 0, 2,
			1, 2, 3,
			
			2, 6, 3,
			3, 6, 7,
			
			5, 6, 4,
			7, 6, 5,
			
			1, 4, 0,
			1, 5, 4,
			
			5, 3, 7,
			1, 3, 5,
			
			4, 2, 0,
			4, 6, 2
		};
		norms = new Vec3f[]{
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0),
			new Vec3f(0, 0, 0)
		};
		computeNormals();
		textures = new Vec3f[]{
			new Vec3f(1, 1, 1),
			new Vec3f(1, 1, 1),
			new Vec3f(1, 1, 1),
			new Vec3f(1, 0, 1),
			
			new Vec3f(1, 1, 1),
			new Vec3f(1, 1, 1),
			new Vec3f(1, 1, 1),
			new Vec3f(1, 1, 1),
		};
	}
	
	public void update(double t, double dt){
		previous = current;
		integrate(current, t, dt);
		Transform tr = Transform.translate(current.linearVelocity);
		transform(tr);
	}
	
	private static State interpolate(State a, State b, double t){
		State state = b;
		state.position = a.position.mult(1-t).add(b.position.mult(t));
		state.linearMomentum = a.linearMomentum.mult(1-t).add(b.linearMomentum.mult(t));
		state.orientation = Quaternion.slerp(a.orientation, b.orientation, t);
		state.rotationalMomentum = a.rotationalMomentum.mult(1-t).add(b.rotationalMomentum.mult(t));
		state.recalculate();
		return state;
	}
	
	private Derivative evaluate(State state, double t){
		Derivative output = new Derivative();
		output.velocity = state.linearVelocity;
		output.spin = state.spin;
		forces(state, t, output.force, output.torque);
		return output;
	}
	
	private Derivative evaluate(State state, double t, double dt, Derivative derivative){
		state.position = state.position.add(derivative.velocity.mult(dt));
		state.linearMomentum = state.linearMomentum.add(derivative.force.mult(dt));
		state.orientation = state.orientation.add(derivative.spin.mult(dt));
		state.rotationalMomentum = state.rotationalMomentum.add(derivative.torque.mult(dt));
		state.recalculate();
		
		Derivative output = new Derivative();
		output.velocity = state.linearVelocity;
		output.spin = state.spin;
		forces(state, t+dt, output.force, output.torque);
		return output;
	}
	
	private void integrate(State state, double t, double dt){
		Derivative a = evaluate(state, t);
		Derivative b = evaluate(state, t, dt*0.5, a);
		Derivative c = evaluate(state, t, dt*0.5, b);
		Derivative d = evaluate(state, t, dt, c);

		Vec3f nPosition = (a.velocity.add((b.velocity.add(c.velocity).mult(2.0))).add(d.velocity)).mult(1.0/6.0*dt);
		Vec3f nMomentum = (a.force.add((b.force.add(c.force).mult(2.0))).add(d.force)).mult(1.0/6.0*dt);
		Quaternion nOrientation = (a.spin.add((b.spin.add(c.spin).mult(2.0))).add(d.spin)).mult(1.0/6.0*dt);
		Vec3f nRotationalMomentum = (a.torque.add((b.torque.add(c.torque).mult(2.0))).add(d.torque)).mult(1.0/6.0*dt);
		
		state.position = state.position.add(nPosition);
		state.linearMomentum = state.linearMomentum.add(nMomentum);
		state.orientation = state.orientation.add(nOrientation);
		state.rotationalMomentum = state.rotationalMomentum.add(nRotationalMomentum);
		
		state.recalculate();

	}
	
	public void transform(Transform t){
		super.transform(t);
	}
	
	public void forces(State state, double t, Vec3f force, Vec3f torque){
		force = state.position.mult(-10);
		
		force.x += 10* Math.sin(t*0.9+0.5);
		force.y += 11* Math.sin(t*0.5+0.4);
		force.z += 12* Math.sin(t*0.7+0.9);
		
		torque.x = 1.0*Math.sin(t*0.9+0.5);
		torque.y = 1.1*Math.sin(t*0.5+0.4);
		torque.z = 1.2*Math.sin(t*0.7+0.9);
		
		torque = torque.sub(state.rotationalVelocity.mult(0.2));
	}
}
