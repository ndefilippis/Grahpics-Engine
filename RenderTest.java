import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class RenderTest {
	static Vec3f[] verts = new Vec3f[10000];
	static int[] tris = new int[10000*3];
	static Vec3f[] norms = new Vec3f[10000*3];
	static int vs;
	static int fs;
	private static double[] depthBuffer;
	private static int[] colors;
	static double t, b, l, r;
	static double near = 1; 
	static double farClippingPLane = 1000; 
	static double focalLength = 20;
	static double filmApertureWidth = 0.980; 
	static double filmApertureHeight = 0.735;
	static int imageWidth = 640;
	static int imageHeight = 480;
	static Mat4f worldToCamera = new Mat4f(1, 0, 0, 0,
			  0, 1, 0, -1.5,
			  0, 0, 1, -8,
			  0, 0, 0, 1);
	static Transform transform = new Transform();

	public static void computeScreenCoordinates(double width, double height, int imageW, int imageH, double near, double focal){
		double filmAspectRatio = width*1.0/height;
		double deviceAspectRatio = imageW*1.0/imageH;
		double top = (height*25.4/2)/focal * near;
		double right = (width*25.4/2)/focal * near;
		
		double fov = 2*180*Math.PI*Math.atan(width/2)/focal;
		double xScale = 1;
		double yScale = 1;
		if(filmAspectRatio > deviceAspectRatio){
			xScale = deviceAspectRatio / filmAspectRatio;
		}
		else{
			yScale = deviceAspectRatio /filmAspectRatio;
		}
		right *= xScale;
		top *= yScale;
		t = top;
		r = right;
		b = -top;
		l = -right;
	}
	
	public static Vec3f toRaster(Vec3f vWorld, Mat4f worldToCamera, double l, double r, double t,double b, double near, int width, int height){
		Vec3f vertexCamera = worldToCamera.mult(vWorld);
		double VSx  = near * vertexCamera.x / -vertexCamera.z;
		double VSy  = near * vertexCamera.y / -vertexCamera.z;
		double NDCx = 2 * VSx / (r-l) - (r+l)/(r-l);
		double NDCy = 2 * VSy / (t-b) - (t+b)/(t-b);
		Vec3f raster = new Vec3f();
		raster.x = (NDCx + 1)/2*width;
		raster.y = (1 - NDCy) /2*height;
		raster.z = -vertexCamera.z;
		return raster;
	}
	
	public static double min3(double a, double b, double c){
		return Math.min(a, Math.min(c, b));
	}
	
	public static double max3(double a, double b, double c){
		return Math.max(a, Math.max(c, b));
	}
	
	public static double edge(Vec3f a, Vec3f b, Vec3f c){
		return (c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x);
	}
	
	public static void loadObj() throws NumberFormatException, IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File("B:/Users/Nick/Downloads/teapot.obj")));
		vs = 0;
		fs = 0;
		while(in.ready()){
			String s = in.readLine();
			if(s.length() == 0)
				continue;
			if(s.substring(0, 2).equals("v ")){
				String[] temp = s.split(" ");
				double _x = Double.parseDouble(temp[1]);
				double _y = Double.parseDouble(temp[2]);
				double _z = Double.parseDouble(temp[3]);
				verts[vs] = new Vec3f(_x, _y, _z);
				norms[vs] = new Vec3f();
				vs++;
			}
			else if(s.charAt(0) == 'f'){
				String[] temp = s.split(" ");
				tris[fs] = Integer.parseInt(temp[1])-1;
				tris[fs+1] = Integer.parseInt(temp[2])-1;
				tris[fs+2] = Integer.parseInt(temp[3])-1;
				fs+=3;
			}
		}
		int[] num_faces = new int[vs];
		for(int i = 0; i < fs/3; i++){
			num_faces[tris[3*i]]++;
			num_faces[tris[3*i+1]]++;
			num_faces[tris[3*i+2]]++;
			Vec3f v0 = verts[tris[3*i]];
			Vec3f v1 = verts[tris[3*i+1]];
			Vec3f v2 = verts[tris[3*i+2]];
			Vec3f normal = v1.sub(v0).cross(v2.sub(v0));
			norms[tris[3*i]] = norms[tris[3*i]].add(normal);
			norms[tris[3*i+1]] = norms[tris[3*i+1]].add(normal);
			norms[tris[3*i+2]] = norms[tris[3*i+2]].add(normal);
		}
		for(int i = 0; i < vs; i++){
			norms[i] = norms[i].normalize();
		}

		depthBuffer = new double[imageWidth * imageHeight];
	}
	
	public static BufferedImage render() throws NumberFormatException, IOException{
		
		BufferedImage bf = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		
		for(int i = 0; i < depthBuffer.length; i++){
			depthBuffer[i] = farClippingPLane;
		}
		computeScreenCoordinates(filmApertureWidth, filmApertureHeight, imageWidth, imageHeight, near, focalLength);
		colors = new int[imageHeight*imageWidth];
		for(int i = 0; i < fs/3; i++){
			
			Vec3f v0 = transform.m.mult(verts[tris[3*i]]);
			Vec3f v1 = transform.m.mult(verts[tris[3*i+1]]);
			Vec3f v2 = transform.m.mult(verts[tris[3*i+2]]);
			Vec3f v0N = transform.inv.transpose().mult(norms[tris[3*i]]);
			Vec3f v1N = transform.inv.transpose().mult(norms[tris[3*i+1]]);
			Vec3f v2N = transform.inv.transpose().mult(norms[tris[3*i+2]]);
			
			Vec3f v0R, v1R, v2R;
			v0R = toRaster(v0, worldToCamera, l, r, t, b, near, imageWidth, imageHeight);
			v1R = toRaster(v1, worldToCamera, l, r, t, b, near, imageWidth, imageHeight);
			v2R = toRaster(v2, worldToCamera, l, r, t, b, near, imageWidth, imageHeight);
		
			v0R.z = 1/v0R.z;
			v1R.z = 1/v1R.z;
			v2R.z = 1/v2R.z;
			
			Vec3f st0 = new Vec3f(1.0, 1.0, 1.0);
			Vec3f st1 = new Vec3f(1.0, 1.0, 1.0);
			Vec3f st2 = new Vec3f(1.0, 1.0, 1.0);
			st0 = st0.mult(v0R.z);
			st1 = st1.mult(v1R.z);
			st2 = st2.mult(v2R.z);
			
			double xmin = min3(v0R.x, v1R.x, v2R.x); 
	        double ymin = min3(v0R.y, v1R.y, v2R.y); 
	        double xmax = max3(v0R.x, v1R.x, v2R.x); 
	        double ymax = max3(v0R.y, v1R.y, v2R.y); 
			
			if(xmin > imageWidth-1 || xmax < 0 || ymin > imageHeight || ymax < 0 ) continue;
			int x0 = Math.max(0, (int)xmin);
			int x1 = Math.min(imageWidth, (int)xmax);
			int y0 = Math.max(0, (int)ymin);
			int y1 = Math.min(imageHeight, (int)ymax);
			
			double area = edge(v0R, v1R, v2R);
			
			for(int y = y0; y <= y1; y++){
				for(int x = x0; x <= x1; x++){
					Vec3f samp = new Vec3f(x+0.5, y+0.5, 0);
					double w0 = edge(v1R, v2R, samp);
					double w1 = edge(v2R, v0R, samp);
					double w2 = edge(v0R, v1R, samp);
					if(w0 >= 0 && w1 >= 0 && w2 >= 0){
						w0 /= area;
						w1 /= area;
						w2 /= area;
						double Rz = v0R.z*w0 + v1R.z*w1 + v2R.z*w2;
						double z = 1/Rz;
						if(y*imageWidth+x >= depthBuffer.length) continue;
						if(z < depthBuffer[y*imageWidth+x]){
							depthBuffer[y*imageWidth + x] = z;
							
							Vec3f st = st0.mult(w0).add(st1.mult(w1).add(st2.mult(w2)));
							st = st.mult(z);
							
							Vec3f v0C, v1C, v2C;
							v0C = worldToCamera.mult(v0);
							v1C = worldToCamera.mult(v1);
							v2C = worldToCamera.mult(v2);
							
							double px = (v0C.x/-v0C.z) * w0 + (v1C.x/-v1C.z)*w1+ (v2C.x/-v2C.z)*w2;
							double py = (v0C.y/-v0C.z) * w0 + (v1C.y/-v1C.z)*w1+ (v2C.y/-v2C.z)*w2;
						
							Vec3f pt = new Vec3f(px*z, py*z, -z);
							
							Vec3f n = v0N.mult(w0).add(v1N.mult(w1).add(v2N.mult(w2)));
							n = n.normalize();
							Vec3f view = pt.mult(-1);
							view = view.normalize();

							double nDot = Math.max(0.07, n.dot(view));												
							
							colors[y*imageWidth + x] = 0;
							colors[y*imageWidth + x] ^= (int)(st.x*nDot*255);
							colors[y*imageWidth + x] ^= (int)(st.y*nDot*255) << 8;
							colors[y*imageWidth + x] ^= (int)(st.z*nDot*255) << 16;
							if(x >= imageWidth || y >= imageHeight) continue;
							bf.setRGB(x, y, colors[y*imageWidth + x]);
						}
					}
				}
			}
		}
		return bf;
	}


	public static void main(String[] args) throws IOException{
		loadObj();
		BufferedImage bf = render();
		JFrame f = new JFrame("hey");
		Panel p = new Panel(bf);
		KeyInput ki = new KeyInput();
		f.addKeyListener(ki);
		f.add(p);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(imageWidth, imageHeight);
		double c = Math.cos(1.0/180);
		double s = Math.sin(1.0/180);
		Mat4f rot = new Mat4f(c, 	0, 	s, 	0,
							  0, 	1, 	0, 0,
							  -s, 	0, 	c, 	0,
							  0, 	0, 	0, 	1);
		long time = System.nanoTime();
		while(true){
			long currTime = System.nanoTime();
			if(currTime - time > 1000000000/60){
				transform = transform.mult(ki.t.inverse());
				bf = render();
				p.image = bf;
				p.repaint();
			}
		}
	}
}
