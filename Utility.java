
public class Utility {
	public static double min3(double a, double b, double c){
		return Math.min(a, Math.min(c, b));
	}
	
	public static double max3(double a, double b, double c){
		return Math.max(a, Math.max(c, b));
	}
	
	public static double edge(Vec3f a, Vec3f b, Vec3f c){
		return (c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x);
	}
}
