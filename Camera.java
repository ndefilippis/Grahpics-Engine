
public class Camera {
	public double t, b, l, r;
	public double fov;
	public double nearClippingPlane; 
	public double farClippingPlane;
	private double focalLength;
	private double filmApertureWidth;
	private double filmApertureHeight;
	private int imageWidth, imageHeight;
	public Transform worldToCamera;
	private Mat4f perspective;
	public Vec3f position;
	
	
	public Camera(double near, double far, double focal, double filmWidth, double filmHeight, int iWidth, int iHeight, Mat4f camera){
		nearClippingPlane = near;
		farClippingPlane = far;
		focalLength = focal;
		filmApertureWidth = filmWidth;
		filmApertureHeight = filmHeight;
		imageWidth = iWidth;
		imageHeight = iHeight;
		computeScreenCoordinates();
		double S = 1/(Math.tan(fov/2*Math.PI/180));
		perspective = new Mat4f(S, 0, 0, 0,
									  0, S, 0, 0,
									  0, 0, -far/(far-near), -1,
									  0, 0, -far*near/(far-near), 0);
		worldToCamera = new Transform(camera);
		position = worldToCamera.getTranslate();
	}
	
	public void computeScreenCoordinates(){
		double filmAspectRatio = filmApertureWidth/filmApertureHeight;
		double deviceAspectRatio = imageWidth*1.0/imageHeight;
		double top = (filmApertureHeight*25.4/2)/focalLength * nearClippingPlane;
		double right = (filmApertureWidth*25.4/2)/focalLength * nearClippingPlane;
		
		fov = 2*180*Math.PI*Math.atan(imageWidth/2)/focalLength;
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

	public Vec3f toRaster(Vec3f vWorld){
		Vec3f vertexCamera = getCameraVec(vWorld);
		if(vertexCamera.z > 0) vertexCamera.z = 0;
		double VSx  = nearClippingPlane * vertexCamera.x / -vertexCamera.z;
		double VSy  = nearClippingPlane * vertexCamera.y / -vertexCamera.z;
		double NDCx = 2 * VSx / (r-l) - (r+l)/(r-l);
		double NDCy = 2 * VSy / (t-b) - (t+b)/(t-b);
		Vec3f raster = new Vec3f();
		raster.x = (NDCx + 1)/2*imageWidth;
		raster.y = (1 - NDCy) /2*imageHeight;
		raster.z = -vertexCamera.z;
		return raster;
	}
	
	public Vec3f getCameraVec(Vec3f vec){
		return worldToCamera.transformPoint(vec);
	}

	public Vec3f getCameraNorm(Vec3f n) {
		return worldToCamera.transformNormal(n);
	}
}
