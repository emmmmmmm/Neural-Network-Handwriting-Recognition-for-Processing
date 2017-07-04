class MemoryLayer {
  float[][] x;
  float[][][] Wx, dWx;
  float[] Bx, dBx;
  float[] Sy, dSy;
  float[] Sx, dSx;
  float lr;
  int in, out;
  int steps;
  //---------------------------------------------------------
  MemoryLayer(int in, int out, float lr, int t) {
    this.lr  = lr;
    this.in  = in;
    this.out = out;
    this.steps = t;
    x = new float[steps][in];
    Wx = new float[steps][in][out];
    Bx = new float[out];
    Sx = new float[in];
    Sy = new float[out];
    dBx = new float[out];
    dWx = new float[steps][in][out];
    dSx = new float[in];
    dSy = new float[out];
    for (int s=0; s<steps; s++)
      for (int i=0; i<in; i++)
        for (int j=0; j<out; j++)
          Wx[s][i][j]=random(-0.1, 0.1);
  }
  //---------------------------------------------------------
  public float[] forward(float[] _x) {
    push(x, _x);  
    Sy = new float[out];

    for (int j=0; j<out; j++) {
      for (int i=0; i<in; i++) 
        for (int t = 0; t<steps; t++) 
          Sy[j] += x[t][i] * Wx[t][i][j];
      Sy[j] += Bx[j];
    }
    Sy = softSign(Sy);
    return Sy;
  }
  //---------------------------------------------------------
  public float[] backward(float[] dSy) {
    dSx = new float[dSx.length];  

    for (int j=0; j<out; j++) {
      dSy[j] = (3.0 / sq(1.0 + abs(Sy[j]))) * dSy[j];         // Softsign
      dBx[j] += dSy[j];

      for (int i=0; i<in; i++) {
        for (int t=0; t<steps; t++) {
          dSx[i]     += Wx[t][i][j]*dSy[j];
          dWx[t][i][j] += x[t][i] * dSy[j];
        }
      }
    }
    return dSx;
  }
  //---------------------------------------------------------
  void update() {
    for (int s=0; s<steps; s++) {
      for (int i=0; i<out; i++) {
        for (int j=0; j<in; j++) {
          Wx[s][j][i]+=dWx[s][j][i]*lr;
        }
      }
    }
    for (int i=0; i<out; i++) 
      Bx[i] += dBx[i]*lr;

    dWx = new float[steps][in][out];
    dBx = new float[out];
  }
  //---------------------------------------------------------
  // pushes value into array (from top) (bottom-value drops out)
  private void push(float[][] ar, float f[]) {
    for (int i=1; i<ar.length; i++) 
      for (int j=0; j<ar[i].length; j++) 
        ar[i-1][j] = ar[i][j]; 

    for (int j=0; j<ar[0].length; j++) 
      ar[ar.length-1][j] = f[j];
  }
  //---------------------------------------------------------
  private float softSign(float x) {
    return 3.0 * x / (1.0 + abs(x));
  }//---------------------------------------------------------
  private float[] softSign(float[] x) {
    float[] ret = new float[x.length];
    for (int i=0; i<ret.length; i++)
      ret[i] = 3.0 * x[i] / (1.0 + abs(x[i]));
    return ret;
  }
}

