import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Shape {
	Vec3f[] verts = new Vec3f[10000];
	int[] faces = new int[10000*3];
	Vec3f[] norms = new Vec3f[10000];
	public Vec3f[] textures = new Vec3f[10000];
	int nVerticies;
	int nFaces;
	
	public Shape(){
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
		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		nVerticies = 0;
		nFaces = 0;
		while(in.ready()){
			String s = in.readLine();
			if(s.length() == 0)
				continue;
			if(s.substring(0, 2).equals("v ")){
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
				faces[nFaces++] = Integer.parseInt(temp[1])-1;
				faces[nFaces++] = Integer.parseInt(temp[2])-1;
				faces[nFaces++] = Integer.parseInt(temp[3])-1;
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
}
