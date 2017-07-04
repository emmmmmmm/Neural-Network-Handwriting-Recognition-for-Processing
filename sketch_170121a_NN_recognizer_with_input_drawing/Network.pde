//---------------------------------------------------------
class Network {
  MemoryLayer[] layers;
  float lr;
  float[] Sy, dSx;
  boolean batchlearn = true;
  int batchsize = 100;
  int trainingCycleCount = 0;
  int abscyclecount=0;
  float[] errorList;
  float lowestError = 10000000;
  int errorListUpdateFrequency = 500;
  int memory = 1;
  //---------------------------------------------------------
  Network(float lr) {
    this.lr=lr;
    this.layers = new MemoryLayer[0];
    this.errorList = new float[0];
  }
  //---------------------------------------------------------
  float forwardLearn(float[][]input, float[][] target) {
    float err = 0;

    trainingCycleCount = 0;
    for (int t=0; t<input.length; t++) {
      Sy = this.forward(input[t]);
      for (int i=0; i<Sy.length; i++) {
        Sy[i] = target[t][i]-Sy[i];
        Sy[i] *= abs(Sy[i]); // error squared!

        trainingCycleCount++;
      }
      if (batchlearn && trainingCycleCount % batchsize==1) { // batchLearn
        this.update();
        trainingCycleCount=0;
      }
      this.backward(Sy);
      err+=absSum(Sy);
    }
    this.update();
    if (abscyclecount%errorListUpdateFrequency==1)
      errorList = append(errorList, err/input.length);
    if (err/input.length < lowestError)
      lowestError=err/input.length;

    abscyclecount++;

    return err/input.length;
  }
  //---------------------------------------------------------
  float[] forward(float[] in) {
    for (int i=0; i<layers.length; i++) {
      in = layers[i].forward(in);
    }
    return in;
  }
  //---------------------------------------------------------
  void backward(float[] dy) {

    for (int i=layers.length-1; i>=0; i--) {
      dy = layers[i].backward(dy);
    }
  }
  //---------------------------------------------------------
  void update() {
    for (int i=0; i<layers.length; i++)
      layers[i].update();
  }
  //---------------------------------------------------------
  void addLayer(int in, int out) {
    layers = (MemoryLayer[]) append(layers, new MemoryLayer(in, out, lr, memory));
  }
  //---------------------------------------------------------
  void displayError() {
    stroke(255, 0, 0);
    strokeWeight(1);
    for (int i=0; i<this.errorList.length-1; i++)
      line(map(i, 0, errorList.length-1, 0, width), 
      map(errorList[i], 0, max(errorList), height, 0), 
      map(i+1, 0, errorList.length-1, 0, width), 
      map(errorList[i+1], 0, max(errorList), height, 0)
        );
  }
  //---------------------------------------------------------
  // those two functions need to check if they are importing a correct file...!
  
  void saveNetwork(String _filename) {
    String line = "";
    String[] exp = new String[0];
    String filename = _filename.equals("") ? "network":_filename;
    print("saving network... ");
    for (int layer=0; layer<this.layers.length; layer++) {    //layer
      line = layers[layer].in+","+layers[layer].out+","+layers[layer].steps+",";
      for (int i=0; i<layers[layer].Bx.length; i++)
        line += layers[layer].Bx[i]+",";
      for (int t=0; t<layers[layer].Wx.length; t++)
        for (int i=0; i<layers[layer].Wx[t].length; i++)
          for (int j=0; j<layers[layer].Wx[t][i].length; j++)
            line += layers[layer].Wx[t][i][j]+",";
      line = line.substring(0, line.length()-1); 
      exp = append(exp, line);
    }
  //  println(exp);
    saveStrings("data/"+filename+".txt", exp);
    println("current network status saved to file!");
  }
  //---------------------------------------------------------
  void loadNetwork(String _filename) {
    String filename = _filename.equals("") ? "network":_filename;
    String lines[] = loadStrings("data/"+filename+".txt");
    String[] entries = new String[0];
    String line="";
    int index;
    int layerIndex;
    print("loading network from file (data/"+filename+".txt) ... ");
    this.layers = new MemoryLayer[0];
    for (int layer = 0; layer<lines.length; layer++) {
      entries = split(lines[layer], ",");
      addLayer(
      (int)Float.parseFloat(entries[0]), 
      (int)Float.parseFloat(entries[1])
        );
      layerIndex = layers.length-1;
      layers[layerIndex].steps = (int) Float.parseFloat(entries[2]);
      index = 3;
      for (int i=0; i<layers[layerIndex].out; i++) {
        layers[layerIndex].Bx[i] = Float.parseFloat(entries[index++]);
      }
      for (int t=0; t<layers[layerIndex].steps; t++) {
        for (int i=0; i<layers[layerIndex].in; i++) {
          for (int j=0; j<layers[layerIndex].out; j++) {
            layers[layerIndex].Wx[t][i][j] = Float.parseFloat(entries[index++]);
          }
        }
      }
    }
    println("network successfully loaded!");
  }
}
//---------------------------------------------------------
float absSum(float[] ar) {
  float ret = 0;
  for (int i=0; i<ar.length; i++)
    ret+=abs(ar[i]);
  return ret;
}
//---------------------------------------------------------

