float[][] trainingInput, trainingTarget, evalInput, evalTarget;

void buildData() {
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
  
      if (trainingInput[i][0] < 0.5 && trainingInput[i][1]<0.5)
     trainingTarget[i][j] = 1;
     else if (trainingInput[i][0] < 0.5 && trainingInput[i][1]>0.5)
     trainingTarget[i][j] = 0.5;
     else if (trainingInput[i][0] > 0.5 && trainingInput[i][1]<0.5)
     trainingTarget[i][j] = -0.5;
     else trainingTarget[i][j]=-1;
     }
     
  }
  //build evalData
  for (int i=0; i<evalInput.length; i++) {
    for (int j=0; j<evalInput[i].length; j++) {
      evalInput[i][j]=random(1);
    }


    for (int j=0; j<evalTarget[i].length; j++) {
   

      if (evalInput[i][0] < 0.5 && evalInput[i][1]<0.5)
     evalTarget[i][j] = 1;
     else if (evalInput[i][0] < 0.5 && evalInput[i][1]>0.5)
     evalTarget[i][j] = 0.5;
     else if (evalInput[i][0] > 0.5 && evalInput[i][1]<0.5)
     evalTarget[i][j] = -0.5;
     else evalTarget[i][j]=-1;
     }
  }
}

