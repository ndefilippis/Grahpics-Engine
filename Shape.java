import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Shape {
	Vec3f[] verts = new Vec3f[10000];
	int[] faces = new int[10000*3];
	Vec3f[] norms = new Vec3f[10000];
	public Vec3f[] textures = new Vec3f[10000];
	int nVerticies;
	int nFaces;
	public State previous, current;
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
	
	public Shape(){
		current = new State();
		current.o2w = new Transform();
		current.position = new Vec3f(2, 0, 0);
		current.linearMomentum = new Vec3f(0, 0, 0);
		current.orientation = new Quaternion();
		current.rotationalMomentum = new Vec3f(0, 0, 0);
		current.inertia = current.mass*current.size*1.0/6;
		current.recalculate();
		previous = current;
	}
	
	public void computeNormals(){
		for(int i = 0; i < nFaces/3; i++){
			Vec3f v0 = verts[faces[3*i]];
			Vec3f v1 = verts[faces[3*i+1]];
			Vec3f v2 = verts[faces[3*i+2]];
			Vec3f normal = v1.sub(v0).cross(v2.sub(v0));
			norms[faces[3*i]] = norms[faces[3*i]].add(normal);
			norms[faces[3*i+1]] = norms[faces[3*i+1]].add(normal);
			norms[faces[3*i+2]] = norms[faces[3*i+2]].add(normal);
		}
		for(int i = 0; i < nVerticies; i++){
			norms[i] = norms[i].normalize();
		}
	}
	
	public Shape(String path) throws IOException{
		this();
		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		nVerticies = 0;
		nFaces = 0;
		while(in.ready()){
			String s = in.readLine();
			if(s.length() == 0)
				continue;
			String header = s.split(" ")[0];
			if(header.equals("v")){
				String[] temp = s.split(" ");
				double _x = Double.parseDouble(temp[1]);
				double _y = Double.parseDouble(temp[2]);
				double _z = Double.parseDouble(temp[3]); 
				verts[nVerticies] = new Vec3f(_x, _y, _z);
				norms[nVerticies] = new Vec3f();
				textures[nVerticies] = new Vec3f(1.0, 1.0, 1.0);
				nVerticies++;
			}
			else if(s.charAt(0) == 'f'){
				String[] temp = s.split(" ");
				int[] verticies = new int[temp.length-1];
				int[] normals  = new int[temp.length-1];;
				int[] textures = new int[temp.length-1];
				for(int i = 1; i < temp.length; i++){
					String[] temp2 = temp[i].split("/");
					int index = 0;
					verticies[i-1] = Integer.parseInt(temp2[0])-1;
					if(temp2.length > 1){
						normals[i-1] = Integer.parseInt(temp2[1])-1;
					}
					if(temp2.length > 2){
						textures[i-1] = Integer.parseInt(temp2[2])-1;
					}
				}
				int origin = verticies[0];
				for(int i = 1; i < verticies.length-1; i++){
					faces[nFaces++] = origin;
					faces[nFaces++] = verticies[i];
					faces[nFaces++] = verticies[i+1];
				}
			}
		}
		computeNormals();
	}

	public void transform(Transform t){
		for(int i = 0; i < nVerticies; i++){
			verts[i] = t.transformPoint(verts[i]);
			norms[i] = t.transformNormal(norms[i]);
		}
	}
	
	public Vec3f transformVecToWorld(Vec3f v){
		return current.o2w.transformPoint(v);
	}
	public Vec3f transformNormalToWorld(Vec3f v){
		return current.o2w.transformNormal(v);
	}
	
	public void update(double t, double dt){
		previous = current;
		integrate(current, t, dt);
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
		forces(state, t, output);
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
		forces(state, t+dt, output);
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
	public void forces(State state, double t, Derivative output){
		output.force = state.position.mult(-10);
		
		output.force.x += 10* Math.sin(t*0.9+0.5);
		output.force.y += 11* Math.sin(t*0.5+0.4);
		output.force.z += 12* Math.sin(t*0.7+0.9);
		
		output.torque.x = 1.0*Math.sin(t*0.9+0.5);
		output.torque.y = 1.1*Math.sin(t*0.5+0.4);
		output.torque.z = 1.2*Math.sin(t*0.7+0.9);
		output.torque = output.torque.sub(state.rotationalVelocity.mult(0.2));
	}
}
