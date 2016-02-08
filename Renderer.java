import java.awt.image.BufferedImage;

public class Renderer {
	private int[] colors;
	private Camera camera;
	private double[] depthBuffer;
	private int imageWidth, imageHeight;
	BufferedImage bf;
	
	public Renderer(int iWidth, int iHeight, Camera c){
		this.imageWidth = iWidth;
		this.imageHeight = iHeight;
		this.camera = c;
		colors = new int[imageWidth*imageHeight];
		depthBuffer = new double[imageWidth*imageHeight];
		bf = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
	}
	
	public BufferedImage render(Shape shape){
		bf = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
		for(int i = 0; i < depthBuffer.length; i++){
			depthBuffer[i] = camera.farClippingPlane;
		}
		colors = new int[imageHeight*imageWidth];
		for(int i = 0; i < shape.nFaces/3; i++){
			
			Vec3f v0 = shape.verts[shape.faces[3*i]];
			Vec3f v1 = shape.verts[shape.faces[3*i+1]];
			Vec3f v2 = shape.verts[shape.faces[3*i+2]];
			Vec3f v0Normal = shape.norms[shape.faces[3*i]];
			Vec3f v1Normal = shape.norms[shape.faces[3*i+1]];
			Vec3f v2Normal = shape.norms[shape.faces[3*i+2]];
			
			Vec3f v0Raster, v1Raster, v2Raster;
			v0Raster = camera.toRaster(v0);
			v1Raster = camera.toRaster(v1);
			v2Raster = camera.toRaster(v2);
		
			v0Raster.z = 1/v0Raster.z;
			v1Raster.z = 1/v1Raster.z;
			v2Raster.z = 1/v2Raster.z;
			Vec3f st0 = shape.textures[shape.faces[3*i]];
			Vec3f st1 = shape.textures[shape.faces[3*i+1]];
			Vec3f st2 = shape.textures[shape.faces[3*i+2]];
			st0 = st0.mult(v0Raster.z);
			st1 = st1.mult(v1Raster.z);
			st2 = st2.mult(v2Raster.z);
			
			double xmin = Utility.min3(v0Raster.x, v1Raster.x, v2Raster.x); 
	        double ymin = Utility.min3(v0Raster.y, v1Raster.y, v2Raster.y); 
	        double xmax = Utility.max3(v0Raster.x, v1Raster.x, v2Raster.x); 
	        double ymax = Utility.max3(v0Raster.y, v1Raster.y, v2Raster.y); 
			
			if(xmin > imageWidth-1 || xmax < 0 || ymin > imageHeight || ymax < 0 ) continue;
			int x0 = Math.max(0, (int)xmin);
			int x1 = Math.min(imageWidth, (int)xmax);
			int y0 = Math.max(0, (int)ymin);
			int y1 = Math.min(imageHeight, (int)ymax);
			
			double area = Utility.edge(v0Raster, v1Raster, v2Raster);
			
			for(int y = y0; y <= y1; y++){
				for(int x = x0; x <= x1; x++){
					Vec3f samp = new Vec3f(x+0.5, y+0.5, 0);
					double w0 = Utility.edge(v1Raster, v2Raster, samp);
					double w1 = Utility.edge(v2Raster, v0Raster, samp);
					double w2 = Utility.edge(v0Raster, v1Raster, samp);
					if(w0 >= 0 && w1 >= 0 && w2 >= 0){
						w0 /= area;
						w1 /= area;
						w2 /= area;
						double Rz = v0Raster.z*w0 + v1Raster.z*w1 + v2Raster.z*w2;
						double z = 1/Rz;
						if(y*imageWidth+x >= depthBuffer.length) continue;
						if(z < depthBuffer[y*imageWidth+x] && z > camera.nearClippingPlane){
							depthBuffer[y*imageWidth + x] = z;
							Vec3f st = st0.mult(w0).add(st1.mult(w1).add(st2.mult(w2)));
							st = st.mult(z);
							
							Vec3f v0Camera, v1Camera, v2Camera;
							v0Camera = camera.getCameraVec(v0);
							v1Camera = camera.getCameraVec(v1);
							v2Camera = camera.getCameraVec(v2);
							
							double px = (v0Camera.x/-v0Camera.z) * w0 + (v1Camera.x/-v1Camera.z)*w1+ (v2Camera.x/-v2Camera.z)*w2;
							double py = (v0Camera.y/-v0Camera.z) * w0 + (v1Camera.y/-v1Camera.z)*w1+ (v2Camera.y/-v2Camera.z)*w2;
						
							Vec3f pt = new Vec3f(px*z, py*z, -z);
							
							Vec3f n = v0Normal.mult(w0).add(v1Normal.mult(w1).add(v2Normal.mult(w2)));
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
}
