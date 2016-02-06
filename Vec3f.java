
public class Vec3f {
	public double x, y, z;
	
	public Vec3f(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3f() {
		// TODO Auto-generated constructor stub
	}

	public Vec3f add(Vec3f r){
		return new Vec3f(x + r.x, y + r.y, z + r.z);
	}
	
	public static Vec3f add(Vec3f v1, Vec3f v2){
		return new Vec3f(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public Vec3f sub(Vec3f r){
		return new Vec3f(x - r.x, y - r.y, z - r.z);
	}
	
	public static Vec3f sub(Vec3f v1, Vec3f v2){
		return new Vec3f(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	public Vec3f mult(double f){
		return new Vec3f(x*f, y*f, z*f);
	}
	
	public static Vec3f mult(Vec3f v, double f){
		return new Vec3f(v.x*f, v.y*f, v.z*f);
	}
	
	public Vec3f div(double f){
		double r_ = 1.0/f;
		return mult(r_);
	}
	
	public static Vec3f div(Vec3f v1, double f){
		double r_ = 1.0/f;
		return mult(v1, r_);
	}
	
	public double dot(Vec3f r){
		return x*r.x+y*r.y+z*r.z;
	}
	
	public double lengthSquared(){
		return this.dot(this);
	}
	
	public double length(){
		return Math.sqrt(lengthSquared());
	}
	
	public double distanceSquaredTo(Vec3f v){
		return (x-v.x)*(x-v.x) + (y-v.y)*(y-v.y) + (z-v.z)*(z-v.z);
	}
	
	public double distanceTo(Vec3f v){
		return Math.sqrt(distanceSquaredTo(v));
	}
	
	public Vec3f cross(Vec3f r){
		double x_ = y*r.z - z*r.y;
		double y_ = z*r.x - x*r.z;
		double z_ = x*r.y - y*r.x;
		return new Vec3f(x_, y_, z_);
	}
	public Vec3f normalize(){
		return this.div(this.length());
	}

	public double get(int index){
		switch(index){
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			throw new IllegalArgumentException("Index out of range [0-2]: " + index);
		}
	}

	public String toString(){
		return "<"+x+", "+y+", "+z+">";
	}
}
