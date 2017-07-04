import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_170121a_NN_recognizer_with_input_drawing extends PApplet {

/*
uncomment this for training
and comment the ATester tab!

*/

/*

Network n;
//---------------------------------------------------------
void setup() {

  size(600, 300);
  frameRate(100000);
  strokeCap(RECT);

  //  buildData();
  importGestures();

  n = new Network(1e-5);
  n.batchlearn=true;
  n.memory = 1;
  n.errorListUpdateFrequency = 10;
  n.addLayer(trainingInput[0].length, 50);
  n.addLayer(50, 20);
  n.addLayer(20, 20);
//  n.addLayer(20, 20);
//  n.addLayer(20, 20);
  n.addLayer(20, trainingTarget[0].length);

  println("setup: done");

 if(true) n.loadNetwork("");
//  n.saveNetwork("");
}

//---------------------------------------------------------
void keyPressed(){
  n.saveNetwork("");

}
//---------------------------------------------------------
void draw() {
  float error = 0;
  int numTestsPerCycle = 10;

  for (int i=0; i<numTestsPerCycle; i++)
    error+=n.forwardLearn(trainingInput, trainingTarget);


  error /= numTestsPerCycle;
  error = sqrt(error);

  frame.setTitle(" fps: "+(int)frameRate+ " error: "+error);



  background(150);
  float step = 4;
  strokeWeight(1);
  stroke(0);
  line(0, height/4, width, height/4);
  line(0, height/4*1.5, width, height/4*1.5);
  line(0, height/4*2, width, height/4*2);
  line(0, height/4*2.5, width, height/4*2.5);
  line(0, height/4*3, width, height/4*3);
  pushMatrix();
  float testingError = 0;
  float timer = millis();
  for (int i=0; i<evalInput.length; i++) {
    float[]r = n.forward(evalInput[i]);

    for (int j=0; j<r.length; j++) {
      testingError+= abs(evalTarget[i][j]-r[j]);
    }
    translate(15, 0);
    text(getFromVocab(highestIndex(r)), 0, 30);
    text(getFromVocab(highestIndex(evalTarget[i])), 0, 50);
  }
  text((millis()-timer)/evalInput.length+" ms", 0, 70);
  popMatrix();
  text("testing Error: "+testingError/evalInput.length, 10, 10);
  n.displayError();
}

//---------------------------------------------------------
*/

Recognizer p;
Timer timer;
boolean testInput = false;
int id = 0;
PVector lastMouse = new PVector();
Point[] currentPoints;
String lastResult;

public void setup() {
  size(600, 300);
  frameRate(100000);
  strokeCap(RECT);
  
  importGestures();
  p = new Recognizer();
  timer = new Timer();
  background(0);
}


public void draw() {
  if (mousePressed) {
    currentPoints =(Point[]) append(currentPoints, new Point(mouseX, mouseY, id));
    fill(255);
    stroke(255);
    line(mouseX, mouseY, lastMouse.x, lastMouse.y);

    ellipse(mouseX, mouseY, 2, 2);
    lastMouse.x = mouseX;
    lastMouse.y = mouseY;
  }

  if (timer.getTime()>1000 && testInput==true) {
    Result r =  p.Recognize(currentPoints);
    testInput=false;
    lastResult = r.Name;
    background(0);
   // println(r.Name+" / "+r.Score);
    stroke(200);
    fill(200);
    textSize(32);
    text(r.Name+" ("+r.Score+")",50, 50);
    frame.setTitle("result: "+r.Name+" ("+r.Score+")");
    id=0;
    
 //   drawAllGestures();
  }
}
//---------------------------------------------------------
public void mousePressed() {
  if (!testInput) {
    currentPoints = new Point[0];
  }
  lastMouse.x = mouseX;
  lastMouse.y = mouseY;
  testInput=false;
}
//---------------------------------------------------------
public void mouseReleased() {
  timer.reset(); 
  testInput=true;
  id++;
 // println("id increased to "+id);
}

//---------------------------------------------------------
class Timer {
  int mTime;
  Timer() {
    mTime=millis();
  }
  public void reset() {
    mTime=millis();
  }
  public int getTime() {
    return millis()-mTime;
  }
}
//---------------------------------------------------------

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
        Wx[i][j]=random(-0.1f, 0.1f);
  }
  //---------------------------------------------------------
  public float[] forward(float[] input) {
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
  public float[] backward(float[] dSy) {

    dSx = new float[in];
    for (int i=0; i<out; i++) {
      dSy[i] = (3.0f / sq(1.0f + abs(Sy[i])))*dSy[i]; // softsign
      dBx[i]+= dSy[i];  
      for (int j=0; j<in; j++) {
        dSx[j] += Wx[j][i]*dSy[i];
        dWx[j][i] += Sx[j]*dSy[i];

      }
    }
    return dSx;
  }
  //---------------------------------------------------------
  public void update() {
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
          Wx[s][i][j]=random(-0.1f, 0.1f);
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
      dSy[j] = (3.0f / sq(1.0f + abs(Sy[j]))) * dSy[j];         // Softsign
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
  public void update() {
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
    return 3.0f * x / (1.0f + abs(x));
  }//---------------------------------------------------------
  private float[] softSign(float[] x) {
    float[] ret = new float[x.length];
    for (int i=0; i<ret.length; i++)
      ret[i] = 3.0f * x[i] / (1.0f + abs(x[i]));
    return ret;
  }
}

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
  public float forwardLearn(float[][]input, float[][] target) {
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
  public float[] forward(float[] in) {
    for (int i=0; i<layers.length; i++) {
      in = layers[i].forward(in);
    }
    return in;
  }
  //---------------------------------------------------------
  public void backward(float[] dy) {

    for (int i=layers.length-1; i>=0; i--) {
      dy = layers[i].backward(dy);
    }
  }
  //---------------------------------------------------------
  public void update() {
    for (int i=0; i<layers.length; i++)
      layers[i].update();
  }
  //---------------------------------------------------------
  public void addLayer(int in, int out) {
    layers = (MemoryLayer[]) append(layers, new MemoryLayer(in, out, lr, memory));
  }
  //---------------------------------------------------------
  public void displayError() {
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
  
  public void saveNetwork(String _filename) {
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
  public void loadNetwork(String _filename) {
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
public float absSum(float[] ar) {
  float ret = 0;
  for (int i=0; i<ar.length; i++)
    ret+=abs(ar[i]);
  return ret;
}
//---------------------------------------------------------

int NumPoints = 30;
Point Origin = new Point(0, 0, 0);
class Recognizer {
  Network n;

  Recognizer() {
    n = new Network(0);
    n.batchlearn = true;
    n.loadNetwork("");
  }
  //------------------------------------------------
  public Result Recognize(Point[] points) {
    points = Resample(points, NumPoints);
    points = Scale(points);
    points = TranslateTo(points, Origin);
    float[] input = PointsToFloat(points);
    float[] r = n.forward(input);
    return new Result(getFromVocab(highestIndex(r)), max(r));
  }
}
//------------------------------------------------
public float[] PointsToFloat(Point[] points) {
  float[] ret = new float[points.length*3];
  int index=0;
  for (int i=0; i<points.length; i++) {
    ret[index++] = points[i].X;
    ret[index++] = points[i].Y;
    ret[index++] = points[i].ID;
  }
  return ret;
}






//------------------------------------------------
class Point {
  float X, Y; 
  int ID;
  Point(float x, float y, int id) // constructor
  {
    this.X = x;
    this.Y = y;
    this.ID = id; // stroke ID to which this point belongs (1,2,...)
  }
}
//
// PointCloud class: a point-cloud template
//
//------------------------------------------------

class PointCloud {
  String Name;
  Point[] Points;
  PointCloud(String name, Point[] points) // constructor
  {
    this.Name = name;
    this.Points = Resample(points, NumPoints);
    this.Points = Scale(this.Points);
    this.Points = TranslateTo(this.Points, Origin);
  }
}
//
// Result class
//
//------------------------------------------------

class Result {
  String Name;
  float Score;
  Result(String name, float score) // constructor
  {
    this.Name = name;
    this.Score = score;
  }
}













//------------------------------------------------
public Point[] Resample(Point[] points, int n) {

  float I = PathLength(points) / (n-1); // interval length
  float D = 0.0f;                          
  Point[] newpoints = new Point[0];       
  newpoints = (Point[])append(newpoints, points[0]);
  for (int i = 1; i < points.length; i++) {
    if (points[i].ID == points[i-1].ID) {
      float d = Distance(points[i - 1], points[i]);
      if ((D + d) >= I) {
        float qx = points[i - 1].X + ((I - D) / d) * (points[i].X - points[i - 1].X);
        float qy = points[i - 1].Y + ((I - D) / d) * (points[i].Y - points[i - 1].Y);
        Point q = new Point(qx, qy, points[i].ID);
        newpoints = (Point[]) append(newpoints, q); 
        points =  (Point[]) splice(points, q, i);// insert 'q' at position i in points s.t. 'q' will be the next i
        D = 0.0f;
      } else D += d;
    }
  }
  if (newpoints.length == n-1 ) { // sometimes we fall a rounding-error short of adding the last point, so add it if so
    newpoints = (Point[]) append(newpoints, new Point(points[points.length - 1].X, points[points.length - 1].Y, points[points.length - 1].ID));
    //  println("adjusted for rounding error");
    //  println(newpoints[newpoints.length-1].X);
    //  println(newpoints[newpoints.length-2].X);
  }
  //  println("///////");
  //  println(newpoints.length+" points, ");
  //  for (int i=0; i<newpoints.length-1; i++)
  //    println(Distance(newpoints[i], newpoints[i+1]));
  //  println("///////");
  return newpoints;
}
//------------------------------------------------
public Point[] Scale(Point[] points) {
  float  minX =10000000, maxX = -10000000, minY = +10000000, maxY = -10000000;
  //println(points.length);
  for (int i = 0; i < points.length; i++) {
    minX = min(minX, points[i].X);
    minY = min(minY, points[i].Y);
    maxX = max(maxX, points[i].X);
    maxY = max(maxY, points[i].Y);
  }
  float size = max(maxX - minX, maxY - minY);
  Point[] newpoints = new Point[points.length];
  for (int i = 0; i < points.length; i++) {
    float qx = (points[i].X - minX) / size;
    float qy = (points[i].Y - minY) / size;
    newpoints[i] = new Point(qx, qy, points[i].ID); // changed something here!
    // println(qx+" / "+qy);
  }
  return newpoints;
}
//------------------------------------------------
public Point[] TranslateTo(Point[] points, Point pt) { // translates points' centroid 
  Point c = Centroid(points);
  Point[] newpoints = new Point[points.length];
  for (int i = 0; i < points.length; i++) {
    float qx = points[i].X + pt.X - c.X;
    float qy = points[i].Y + pt.Y - c.Y;
    newpoints[i] = new Point(qx, qy, points[i].ID);
  }
  return newpoints;
}
//------------------------------------------------
public Point Centroid(Point[] points) {

  float x = 0.0f, y = 0.0f;
  for (int i = 0; i < points.length; i++) {
    x += points[i].X;
    y += points[i].Y;
  }
  x /= points.length;
  y /= points.length;
  return new Point(x, y, 0);
}
//------------------------------------------------
public float PathDistance(Point[] pts1, Point[] pts2) { // average distance between corresponding points in two paths

  float d = 0.0f;
  for (int i = 0; i < pts1.length; i++) // assumes pts1.length == pts2.length
    d += Distance(pts1[i], pts2[i]);
  return d / pts1.length;
}
//------------------------------------------------
public float PathLength(Point[] points) { // length traversed by a point path
  float d = 0.0f;
  for (int i = 1; i < points.length; i++)
  {
    if (points[i].ID == points[i-1].ID)
      d += Distance(points[i - 1], points[i]);
  }
  return d;
}

//------------------------------------------------
public float Distance(Point p1, Point p2) {// Euclidean distance between two points
  float dx = p2.X - p1.X;
  float dy = p2.Y - p1.Y;
  return sqrt(dx * dx + dy * dy);
}

float[][] trainingInput, trainingTarget, evalInput, evalTarget;

public void buildData() {
  int trainingSamples = 250;
  int evalSamples = 100;
  trainingInput = new float[trainingSamples][2];
  trainingTarget = new float[trainingSamples][1];
  evalInput = new float[evalSamples][2];
  evalTarget = new float[evalSamples][1];
  //build trainingData
  for (int i=0; i<trainingInput.length; i++) {
    for (int j=0; j<trainingInput[i].length; j++) {
      trainingInput[i][j]=random(1);
    }

    for (int j=0; j<trainingTarget[i].length; j++) {
  
      if (trainingInput[i][0] < 0.5f && trainingInput[i][1]<0.5f)
     trainingTarget[i][j] = 1;
     else if (trainingInput[i][0] < 0.5f && trainingInput[i][1]>0.5f)
     trainingTarget[i][j] = 0.5f;
     else if (trainingInput[i][0] > 0.5f && trainingInput[i][1]<0.5f)
     trainingTarget[i][j] = -0.5f;
     else trainingTarget[i][j]=-1;
     }
     
  }
  //build evalData
  for (int i=0; i<evalInput.length; i++) {
    for (int j=0; j<evalInput[i].length; j++) {
      evalInput[i][j]=random(1);
    }


    for (int j=0; j<evalTarget[i].length; j++) {
   

      if (evalInput[i][0] < 0.5f && evalInput[i][1]<0.5f)
     evalTarget[i][j] = 1;
     else if (evalInput[i][0] < 0.5f && evalInput[i][1]>0.5f)
     evalTarget[i][j] = 0.5f;
     else if (evalInput[i][0] > 0.5f && evalInput[i][1]<0.5f)
     evalTarget[i][j] = -0.5f;
     else evalTarget[i][j]=-1;
     }
  }
}

/*float[][] trainingInput, trainingTarget, evalInput, evalTarget;*/

//---------------------------------------------------------
public void importGestures() {
  // trainingInput
  String lines[] = loadStrings("data/pointClouds.txt");
  lines = sort(lines);
  String[][] entries = new String[lines.length][0];
  String[] labels = new String[lines.length];
  for (int i=0; i<lines.length; i++) {
    entries[i] = split(lines[i], ",");
    entries[i][0] = entries[i][0].toUpperCase();
    labels[i] = entries[i][0];
    entries[i] = subset(entries[i], 1);
  }
  buildVocab(labels);

  trainingInput = new float[entries.length][entries[0].length];
  trainingTarget = new float[entries.length][vocab.length];
  for (int i=0; i<entries.length; i++) {
    for (int j=0; j<entries[i].length; j++) {
      trainingTarget[i][getFromVocab(labels[i])] = 1;
      trainingInput[i] = stringToFloat(entries[i]);
    }
  }

  lines = loadStrings("data/pointClouds_eval.txt");
  entries = new String[lines.length][0];
  labels = new String[lines.length];
  for (int i=0; i<lines.length; i++) {
    entries[i] = split(lines[i], ",");
    entries[i][0] = entries[i][0].toUpperCase(); // ignore case! (... for now)
    labels[i] = entries[i][0];
    entries[i] = subset(entries[i], 1);
  }

  evalInput = new float[entries.length][0];
  evalTarget = new float[entries.length][vocab.length];
  for (int i=0; i<entries.length; i++) {
    evalTarget[i][getFromVocab(labels[i])]=1;
    evalInput[i] = stringToFloat(entries[i]);
  }


  println("trainingData: "+trainingInput.length+" samples");
  println("evalData: "+evalInput.length+" samples");  
  println("single Sample Length: "+entries[0].length);
  println("vocab length: "+vocab.length);
  // evaluation input
}
//---------------------------------------------------------
public float[] stringToFloat(String[] in) {
  float[] out = new float[in.length];

  for (int i=0; i<out.length; i++)
    out[i] = Float.parseFloat(in[i]);
  return out;
}
//---------------------------------------------------------
public float[][] mapPointsToGrid(String[] in, int x, int y, boolean randomize) {

  float[][] ret = new float[x][y];
  float[] points = new float[in.length];
  for (int i=0; i<points.length; i++)
    points[i] = randomize ? Float.parseFloat(in[i])+random(-0.05f, 0.05f) : Float.parseFloat(in[i]);

  float min = min(points);  
  float max= max(points);

  for (int i=0; i<points.length; i++)
    points[i] = map(points[i], min, max, 0, 1);


  for (int i=0; i<points.length; i+=2) {
    int px = round(points[i]*(x-1));
    int py = round(points[i+1]*(y-1));
    ret[px][py]=1;
  }

  return ret;
}
//---------------------------------------------------------
public float[][] mapPointsToPoints(String[] in) {
  // maybe apply some kind of sorting algorithm!!
  float ret[][] = new float[in.length/3][3];
  int j = 0;

  for (int i=0; i<ret.length; i++) {
    ret[i][0] = Float.parseFloat(in[j]);
    ret[i][1] = Float.parseFloat(in[j+1]);
    ret[i][2] = Float.parseFloat(in[j+2]);
    j+=3;
  }
  // ret = Xsort(ret);

  return ret;
}
//---------------------------------------------------------
public float[][] Xsort(float[][] ar) {
  float[] x;
  int j;
  for (int i=0; i<ar.length; i++) {
    x = ar[i];
    j = i-1;
    while (j>=0 && ar[j][0]>x[0] && ar[j][2]==x[2]) {
      ar[j+1] = ar[j];
      j--;
    }
    ar[j+1] = x;
  }

  return ar;
}
//---------------------------------------------------------
String[] vocab;
public void buildVocab(String[] labels) {
  vocab = new String[0];
  labels = sort(labels);
  for (int i=0; i<labels.length; i++) {
    if (getFromVocab(labels[i])==-1)
      vocab = append(vocab, labels[i]);
  }
  print("vocabulary: ");
  println(vocab);
}
//---------------------------------------------------------
public int getFromVocab(String input) {
  int ret = -1;
  for (int i=0; i<vocab.length; i++)
    if (vocab[i].equals(input)) ret = i;
  return ret;
}
//---------------------------------------------------------
public String getFromVocab(int input) {
 // println(input);
  return vocab[input];
}
//---------------------------------------------------------
public String getFromVocab(float[][] in) {
  return vocab[highestIndex(in)];
}
//---------------------------------------------------------
public int highestIndex(float[] in) {
  int pos=-1;
  float max=Float.MIN_VALUE;
  for (int i=0; i<in.length; i++) {
    if (in[i]>max) {
      pos = i;
      max=in[i];
    }
  }
  return pos;
}
//---------------------------------------------------------
public int highestIndex(float[][] in) {
  int pos=-1;
  float max=Float.MIN_VALUE; //lowest possible value of an int.
  for (int i=0; i<in.length; i++) {
    for (int j=0; j<in[i].length; j++) {
      if (in[i][j]>max) {
        pos=i;
        max=in[i][j];
      }
    }
  }
  return pos;
}
//---------------------------------------------------------

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_170121a_NN_recognizer_with_input_drawing" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
