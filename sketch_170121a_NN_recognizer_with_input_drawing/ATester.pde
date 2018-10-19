//===============================================
// takes mouse input, gets result from pre-trained Neural Network!
//===============================================
Recognizer p;
Timer timer;
boolean testInput = false;
int id = 0;
PVector lastMouse = new PVector();
Point[] currentPoints;
String lastResult;

void setup() {
  size(600, 300);
  frameRate(100000);
  strokeCap(RECT);

  importGestures();
  p = new Recognizer();
  timer = new Timer();
  background(0);
}


void draw() {
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

   // drawAllGestures();
  }
}
//---------------------------------------------------------
void mousePressed() {
  if (!testInput) {
    currentPoints = new Point[0];
  }
  lastMouse.x = mouseX;
  lastMouse.y = mouseY;
  testInput=false;
}
//---------------------------------------------------------
void mouseReleased() {
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
  void reset() {
    mTime=millis();
  }
  int getTime() {
    return millis()-mTime;
  }
}
//---------------------------------------------------------
