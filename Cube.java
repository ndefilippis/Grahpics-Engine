import java.io.IOException;

public class Cube extends Shape {

	public Cube(String path) throws IOException {
		super(path);
	}
	
	public Cube(){
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
			new Vec3f(1, 0, 1),
			new Vec3f(0, 1, 1),
			new Vec3f(1, 1, 0),
			new Vec3f(1, 0, 1),
			
			new Vec3f(1, 1, 0),
			new Vec3f(1, 0, 1),
			new Vec3f(0, 0, 1),
			new Vec3f(1, 0, 1),
		};
	}

}
