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
  Result Recognize(Point[] points) {
    points = Resample(points, NumPoints);
    points = Scale(points);
    points = TranslateTo(points, Origin);
    float[] input = PointsToFloat(points);
    float[] r = n.forward(input);
    return new Result(getFromVocab(highestIndex(r)), max(r));
  }
}
//------------------------------------------------
float[] PointsToFloat(Point[] points) {
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
Point[] Resample(Point[] points, int n) {

  float I = PathLength(points) / (n-1); // interval length
  float D = 0.0;                          
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
        D = 0.0;
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
Point[] Scale(Point[] points) {
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
Point[] TranslateTo(Point[] points, Point pt) { // translates points' centroid 
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
Point Centroid(Point[] points) {

  float x = 0.0, y = 0.0;
  for (int i = 0; i < points.length; i++) {
    x += points[i].X;
    y += points[i].Y;
  }
  x /= points.length;
  y /= points.length;
  return new Point(x, y, 0);
}
//------------------------------------------------
float PathDistance(Point[] pts1, Point[] pts2) { // average distance between corresponding points in two paths

  float d = 0.0;
  for (int i = 0; i < pts1.length; i++) // assumes pts1.length == pts2.length
    d += Distance(pts1[i], pts2[i]);
  return d / pts1.length;
}
//------------------------------------------------
float PathLength(Point[] points) { // length traversed by a point path
  float d = 0.0;
  for (int i = 1; i < points.length; i++)
  {
    if (points[i].ID == points[i-1].ID)
      d += Distance(points[i - 1], points[i]);
  }
  return d;
}

//------------------------------------------------
float Distance(Point p1, Point p2) {// Euclidean distance between two points
  float dx = p2.X - p1.X;
  float dy = p2.Y - p1.Y;
  return sqrt(dx * dx + dy * dy);
}

