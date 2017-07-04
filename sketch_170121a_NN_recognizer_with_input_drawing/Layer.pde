//---------------------------------------------------------
class Layer {
  float[][] Wx, dWx;
  float[] Bx, dBx;
  float[] Sy;
  float[] Sx, dSx;
  float[] Syl,dSyl;
  float[][] Wxl,dWxl;
  float lr;
  int in, out;
  //---------------------------------------------------------
  Layer(int in, int out, float lr) {
    this.lr  = lr;
    this.in  = in;
    this.out = out;
    Wx = new float[in][out];
    Bx = new float[out];
    Sx = new float[in];
    Sy = new float[out];
    dBx = new float[out];
    dWx = new float[in][out];
    dSx = new float[in];
    

    for (int i=0; i<in; i++)
      for (int j=0; j<out; j++)
        Wx[i][j]=random(-0.1, 0.1);
  }
  //---------------------------------------------------------
  float[] forward(float[] input) {
    arrayCopy(input, Sx); 
    Sy = new float[out];
    for (int i=0; i<out; i++) {
      for (int j=0; j<in; j++) {
        Sy[i] += Wx[j][i]*input[j];
      }
      Sy[i]+= Bx[i];
      Sy[i] = softSign(Sy[i]);
    }
    Syl = Sy;
    return Sy;
  }
  //---------------------------------------------------------
  float[] backward(float[] dSy) {

    dSx = new float[in];
    for (int i=0; i<out; i++) {
      dSy[i] = (3.0 / sq(1.0 + abs(Sy[i])))*dSy[i]; // softsign
      dBx[i]+= dSy[i];  
      for (int j=0; j<in; j++) {
        dSx[j] += Wx[j][i]*dSy[i];
        dWx[j][i] += Sx[j]*dSy[i];

      }
    }
    return dSx;
  }
  //---------------------------------------------------------
  void update() {
    for (int i=0; i<out; i++) {
      Bx[i] += dBx[i]*lr;
      for (int j=0; j<in; j++) {
        Wx[j][i]+=dWx[j][i]*lr;
 
      }
    }
    dWx = new float[in][out];
    dBx = new float[out];
  }
  //---------------------------------------------------------
  private float softSign(float x) {
    return 3*x / (1 + abs(x));
  }
}

