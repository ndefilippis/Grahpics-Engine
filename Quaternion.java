
public class Quaternion {
	Vec3f xyz;
	double w;
	
	public Quaternion(){
		xyz = new Vec3f();
		w = 1;
	}
	
	public Quaternion(Vec3f xyz, double w){
		this.xyz = xyz;
		this.w = w;
	}
	
	public Quaternion(double x, double y, double z, double w){
		xyz = new Vec3f(x, y, z);
		this.w = w;
	}
	
	public Quaternion(double angle, Vec3f axis){
		double s = Math.sin(angle/2);
		double c = Math.cos(angle/2);
		w = c;
		xyz = axis.mult(s);
	}
	
	public Transform toTransform(){
		Vec3f T = xyz.mult(2);
		Vec3f Tw = T.mult(w);
		Vec3f Tx = xyz.mult(T.x);
		Vec3f Ty = xyz.mult(T.y);
		Vec3f Tz = xyz.mult(T.z);
		
		Mat4f m = new Mat4f(1-(Ty.y+Tz.z), Tx.y-Tw.z, Tx.z+Tw.y, 0,
						 Tx.y+Tw.z, 1- (Tx.x+Tz.z), Ty.z- Tw.x,0,
						 Tx.z-Tw.y, Ty.z+Tw.x, 1-(Tx.x+Ty.y), 0, 
						 0, 0, 0, 1);
		return new Transform(m, m.transpose());
	}
	
	public Quaternion add(Quaternion q){
		return new Quaternion(xyz.add(q.xyz), w+q.w);
	}
	public Quaternion sub(Quaternion q){
		return new Quaternion(xyz.sub(q.xyz), w-q.w);
	}
	public Quaternion mult(double s){
		return new Quaternion(xyz.mult(s), w*s);
	}
	
	public Quaternion mult(Quaternion q){
		return q;
		
	}
	
	public double lengthSquared(){
		return xyz.lengthSquared()+w*w;
	}
	
	public Quaternion div(double s){
		return this.mult(1/s);
	}
	
	public double length(){
		return Math.sqrt(this.lengthSquared());
	}
	
	public Quaternion normalize(){
		double length = this.length();
		return this.div(length);
	}
	
	public Quaternion slerp(Quaternion q){
		return null;
	}
	
	public static Quaternion slerp(Quaternion a, Quaternion b, double t){
		return null;
	}
	public String toString(){
		return xyz.toString()+":" + w;
	}
}
