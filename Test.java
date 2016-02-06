
public class Test {
	public static void main(String[] args){
		Mat4f m = new Mat4f(1, 4, 1, 1,
							1, 4, 0, 1,
							2, 3, 1, 2,
							3, 2, 6, 4);
		System.out.println(m+"\n");
		System.out.println(Mat4f.inverse(m));
		Vec3f vec = new Vec3f(1, 0, 0);
		Vec3f vec1 = new Vec3f(0, 0, 1);
		System.out.println(Math.acos(vec.dot(vec1)/(vec.length()*vec1.length()))*180/Math.PI);
	}
	
}
