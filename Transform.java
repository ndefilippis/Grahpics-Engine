
public class Transform {
	Mat4f m;
	Mat4f inv;
	
	public Transform(){
		m = new Mat4f();
		inv = new Mat4f();
	}
	
	public Transform(Mat4f m, Mat4f minv){
		this.m = m;
		this.inv = minv;
	}
	
	public Transform(Mat4f m) {
		this.m = m;
		this.inv = Mat4f.inverse(m);
	}

	public static Transform rotateZ(double theta){
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		Mat4f m = new Mat4f(c, -s, 0, 0,
							s,  c, 0, 0,
							0,  0, 1, 0,
							0,  0, 0, 1);
		Mat4f minv = m.transpose();
		return new Transform(m, minv);
	}
	public static Transform rotateY(double theta){
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		Mat4f m = new Mat4f(c,  0, s, 0,
							0,  1, 0, 0,
							-s,  0, c, 0,
							0,  0, 0, 1);
		Mat4f minv = m.transpose();
		return new Transform(m, minv);
	}
	public static Transform rotateX(double theta){
		double c = Math.cos(theta);
		double s = Math.sin(theta);
		Mat4f m = new Mat4f(1,  0, 0, 0,
							0,  c, -s, 0,
							0,  s, c, 0,
							0,  0, 0, 1);
		Mat4f minv = m.transpose();
		return new Transform(m, minv);
	}
	
	public Transform scale(double f){
		Mat4f m = new Mat4f(f, 0, 0, 0, 0, f, 0, 0, 0, 0, f, 0, 0, 0, 0, 1);
		Mat4f minv = new Mat4f(1.0/f, 0, 0, 0, 0, 1.0/f, 0, 0, 0, 0, 1.0/f, 0, 0, 0, 0, 1);
		return new Transform(m, minv);
	}
	
	public Transform mult(Transform t2){
		Mat4f mat = m.mult(t2.m);
		Mat4f minv = t2.inv.mult(inv);
		return new Transform(mat, minv);
	}

	public static Transform translate(Vec3f v) {
		Mat4f m = new Mat4f(1,  0, 0, v.x,
							0,  1, 0, v.y,
							0,  0, 1, v.z,
							0,  0, 0, 1);
		Mat4f inv = new Mat4f(1,  0, 0, -v.x,
				0,  1, 0, -v.y,
				0,  0, 1, -v.z,
				0,  0, 0, 1);
		return new Transform(m, inv);
	}
	public Transform inverse(){
		return new Transform(inv, m);
	}

	public Vec3f transformPoint(Vec3f v){
		return m.mult(v);
	}
	
	public Vec3f transformNormal(Vec3f v){
		return inv.transpose().mult(v);
	}

	public Vec3f getTranslate() {
		return new Vec3f(m.get(0, 3), m.get(1, 3), m.get(2, 3));
	}
}
