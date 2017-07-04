/*
uncomment this Tab for training the Neural Net
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
