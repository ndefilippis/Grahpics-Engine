
public class Mat4f {
	private double[][] m = new double[4][4];
	
	public Mat4f(){
		m = new double[4][4];
		for(int i = 0; i < 4; i++){
			m[i][i] = 1;
		}
	}
	
	public Mat4f(double[][] mat) {
		this.m = mat;
	}
	
	public Mat4f(double a, double b, double c, double d, double e, double f,
		 double g, double h, double i, double j, double k, double l, double m1, double n, double o, double p){
		 	m[0][0] = a;
		 	m[0][1] = b;
		 	m[0][2] = c;
		 	m[0][3] = d;
		 	
		 	m[1][0] = e;
		 	m[1][1] = f;
		 	m[1][2] = g;
		 	m[1][3] = h;
		 	
		 	m[2][0] = i;
		 	m[2][1] = j;
		 	m[2][2] = k;
		 	m[2][3] = l;
		 	
		 	m[3][0] = m1;
		 	m[3][1] = n;
		 	m[3][2] = o;
		 	m[3][3] = p;
		 }
	
	
	public static Mat4f copy(Mat4f m){
		Mat4f _ret = new Mat4f();
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				_ret.m[i][j] = m.m[i][j];
			}
		}
		return _ret;
	}
	
	public Mat4f mult(Mat4f m){
		Mat4f _C = new Mat4f();
		
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				_C.m[i][j] = this.m[i][0] * m.m[0][j] + 
							 this.m[i][1] * m.m[1][j] + 
							 this.m[i][2] * m.m[2][j] + 
							 this.m[i][3] * m.m[3][j];
			}
		}
		return _C;
	}
	
	public static Mat4f inverse(Mat4f m) {
	    int[] indxc = new int[4], indxr = new int[4];
	    int[] ipiv = { 0, 0, 0, 0 };
	    double[][] minv = new double[4][4];
	    minv = copy(m).m;
	    for (int i = 0; i < 4; i++) {
	        int irow = -1, icol = -1;
	        float big = 0;
	        // Choose pivot
	        for (int j = 0; j < 4; j++) {
	            if (ipiv[j] != 1) {
	                for (int k = 0; k < 4; k++) {
	                    if (ipiv[k] == 0) {
	                        if (Math.abs(minv[j][k]) >= big) {
	                            big = (float)(Math.abs(minv[j][k]));
	                            irow = j;
	                            icol = k;
	                        }
	                    }
	                    else if (ipiv[k] > 1)
	                        throw new Error("Singular matrix in MatrixInvert");
	                }
	            }
	        }
	        ++ipiv[icol];
	        // Swap rows _irow_ and _icol_ for pivot
	        if (irow != icol) {
	            for (int k = 0; k < 4; ++k){
	            	double _c = minv[irow][k];
	            	minv[irow][k] = minv[icol][k];
	            	minv[icol][k] = _c;
	            }
	        }
	        indxr[i] = irow;
	        indxc[i] = icol;
	        if (minv[icol][icol] == 0.)
	        	throw new Error("Singular matrix in MatrixInvert");

	        // Set $m[icol][icol]$ to one by scaling row _icol_ appropriately
	        double pivinv = 1.0 / minv[icol][icol];
	        minv[icol][icol] = 1.f;
	        for (int j = 0; j < 4; j++)
	            minv[icol][j] *= pivinv;

	        // Subtract this row from others to zero out their columns
	        for (int j = 0; j < 4; j++) {
	            if (j != icol) {
	                double save = minv[j][icol];
	                minv[j][icol] = 0;
	                for (int k = 0; k < 4; k++)
	                    minv[j][k] -= minv[icol][k]*save;
	            }
	        }
	    }
	    // Swap columns to reflect permutation
	    for (int j = 3; j >= 0; j--) {
	        if (indxr[j] != indxc[j]) {
	            for (int k = 0; k < 4; k++){
	            	double _c = minv[k][indxr[j]];
	                minv[k][indxr[j]] = minv[k][indxc[j]];
	                minv[k][indxc[j]] = _c;
	            }
	        }
	    }
	    return new Mat4f(minv);
	}
	
	public Vec3f mult(Vec3f v){
		double _x = v.x*m[0][0] + v.y*m[0][1] + v.z*m[0][2] + m[0][3];
		double _y = v.x*m[1][0] + v.y*m[1][1] + v.z*m[1][2] + m[1][3];
		double _z = v.x*m[2][0] + v.y*m[2][1] + v.z*m[2][2] + m[2][3];
		return new Vec3f(_x, _y, _z);
	}
	
	public Mat4f transpose(){
		return new Mat4f(m[0][0], m[1][0], m[2][0], m[3][0],
						 m[0][1], m[1][1], m[2][1], m[3][1],
						 m[0][2], m[1][2], m[2][2], m[3][2],
						 m[0][3], m[1][3], m[2][3], m[3][3]);
	}
	
	public String toString(){
		String r = "[";
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				r += m[i][j]+",\t";
			}
			r = r.substring(0, r.length()-2)+"\n";
		}
		return r.substring(0, r.length()-1)+"]";
	}
}
