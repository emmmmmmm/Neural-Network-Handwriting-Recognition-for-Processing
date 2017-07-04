/*float[][] trainingInput, trainingTarget, evalInput, evalTarget;*/

//---------------------------------------------------------
void importGestures() {
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
float[] stringToFloat(String[] in) {
  float[] out = new float[in.length];

  for (int i=0; i<out.length; i++)
    out[i] = Float.parseFloat(in[i]);
  return out;
}
//---------------------------------------------------------
float[][] mapPointsToGrid(String[] in, int x, int y, boolean randomize) {

  float[][] ret = new float[x][y];
  float[] points = new float[in.length];
  for (int i=0; i<points.length; i++)
    points[i] = randomize ? Float.parseFloat(in[i])+random(-0.05, 0.05) : Float.parseFloat(in[i]);

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
float[][] mapPointsToPoints(String[] in) {
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
float[][] Xsort(float[][] ar) {
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
void buildVocab(String[] labels) {
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
int getFromVocab(String input) {
  int ret = -1;
  for (int i=0; i<vocab.length; i++)
    if (vocab[i].equals(input)) ret = i;
  return ret;
}
//---------------------------------------------------------
String getFromVocab(int input) {
 // println(input);
  return vocab[input];
}
//---------------------------------------------------------
String getFromVocab(float[][] in) {
  return vocab[highestIndex(in)];
}
//---------------------------------------------------------
int highestIndex(float[] in) {
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
int highestIndex(float[][] in) {
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

