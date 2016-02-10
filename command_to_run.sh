#!/bin/bash  

echo "WFG - WFG1 TO WFG9"

problem=$1

#java -Xms2048m -Xmx2048m -classpath dist/WFGHH.jar jmetal.experiments.Experiment_NSGAII_WFG  > resultado/time_Experiment_NSGAII.txt &
#java -Xms2048m -Xmx2048m -classpath dist/WFGHH.jar jmetal.experiments.Experiment_SPEA2_WFG  > resultado/time_Experiment_SPEA2.txt &
#java -Xms2048m -Xmx2048m -classpath dist/WFGHH.jar jmetal.experiments.Experiment_MOGA_WFG > resultado/time_Experiment_MOGA.txt &
#java -Xms2048m -Xmx5048m -classpath dist/WFGHH.jar jmetal.experiments.Experiment_IBEA_WFG  > resultado/time_Experiment_IBEA.txt &
java -Xms2048m -Xmx2048m -classpath dist/WFGHH.jar jmetal.experiments.Experiment_HH_WFG  > resultado/time_Experiment_HH.txt &



