package experiments;


import hyperheuristics.algs.IBEA;
import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.PopulationWorks;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jmetal.core.*;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.problems.mqap.mQAP;
import jmetal.util.JMException;

public class Experiment_IBEA_MqAp {

    public static void printData(String algName, String filename, String context, int numberOfElements) {
        System.out.println("\n================" + algName + "================");
        System.out.println("Software: " + filename);
        System.out.println("Context: " + context);
        System.out.println("Number of elements: " + numberOfElements);
        long heapSize = Runtime.getRuntime().totalMemory();
        heapSize = (heapSize / 1024) / 1024;
        System.out.println("Heap Size: " + heapSize + "Mb\n");
    }

    public static void verifyLocation(String algName, String filename, String context) {
        File directory = new File("resultado/" + algName + "/" + filename + context);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                //System.err.println("Impossivel criar diretorio");
                //System.exit(0);
            }
        }
    }

    public static AlgorithmHH chooseMethod(ArrayList<AlgorithmHH> matrixAlgs) {
        return null;
    }

    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
    public static void main(String[] args) throws FileNotFoundException, IOException, JMException, ClassNotFoundException {

        String[] softwares;
        softwares = new String[1];
        softwares[0] = "KCall/KC10-2fl-2rl.dat";
        
        double[][][] fronts=new double[1][4][2];
        fronts[0][0][0]=2913670;
        fronts[0][0][1]=2157762;
        fronts[0][1][0]=3016380;
        fronts[0][1][1]=1655122;
        fronts[0][2][0]=3492962;
        fronts[0][2][1]=1472730;
        fronts[0][3][0]=4656190;
        fronts[0][3][1]=1440264;
        
        int runsNumber = 30;
        int populationSize = 100;
        int archiveSize = 100;
        int maxEvaluations = 625000; //ou seja o que for 625000
        
        double crossoverProbability = 0.9;
        double mutationProbability = 1.0/24.0; //0.2;
        

        for (String problemname : softwares) {
            mQAP problem=new mQAP("Permutation", problemname);
            double[][] referencePoint=fronts[0];
            long initTime = System.currentTimeMillis();
            SolutionSet todasRuns = new SolutionSet();
            SolutionSet partial;

            System.out.println("==========================Executando o problema " + problem.getName() + "==========================");
            System.out.println("HH: Choice Function");
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArchiveSize -> " + archiveSize);
            System.out.println("\tMaxEva -> " + maxEvaluations);
            System.out.println("\tSlide Window -> " + maxEvaluations);
            System.out.println("\tCross -> " + crossoverProbability);
            System.out.println("\tMuta -> " + mutationProbability);

            AlgorithmHH algorithm = new IBEA(problem);

            Operator crossover;
            Operator mutation;
            Operator selection;
            HashMap parameters = new HashMap();

            // Crossover
            parameters.put("probability", crossoverProbability);
            parameters.put("distributionIndex", 10.0);
            crossover = CrossoverFactory.getCrossoverOperator("TwoPointsCrossover", parameters);//Permutation TwoPointsCrossover

            // Mutation
            parameters.put("probability", mutationProbability);
            parameters.put("distributionIndex", 20.0);
            mutation = MutationFactory.getMutationOperator("SwapMutation", parameters);//

            // Selection
            parameters.clear();
            selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

            // Algorithm params
            algorithm.setInputParameter("populationSize", populationSize);
            algorithm.setInputParameter("maxEvaluations", maxEvaluations);
            algorithm.setInputParameter("archiveSize", archiveSize);
            algorithm.addOperator("crossover", crossover);
            algorithm.addOperator("mutation", mutation);
            algorithm.addOperator("selection", selection);

            //escolhe algoritimo
            for (int runs = 0; runs < runsNumber; runs++) {
                long initRunTime = System.currentTimeMillis();
                SolutionSet resultFront = new SolutionSet();
                resultFront = algorithm.execute();
                resultFront = PopulationWorks.removeDominadas(resultFront);
                resultFront = PopulationWorks.removeRepetidas(resultFront);
                resultFront.printObjectivesToFile("resultado/ibea/" + problem.getName() + "/FUN_ibea" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/ibea/" + problem.getName() + "/VAR_ibea" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
                long estimatedTime = System.currentTimeMillis() - initRunTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
            }

            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/ibea/" + problem.getName() + "/All_FUN_ibea" + "-" + problem.getName());
            todasRuns.printVariablesToFile("resultado/ibea/" + problem.getName() + "/All_VAR_ibea" + "-" + problem.getName());

            long estimatedTotalTime = System.currentTimeMillis() - initTime;
            System.out.println("\n===============REPORT===================");
            System.out.println("Software: " + problem.getName());

            System.out.println("Estimated Total Time: " + estimatedTotalTime);
            System.out.println("\n=======================================");
            //grava arquivo juntando funcoes e variaveis
            //gravaCompleto(todasRuns, "TodasRuns-Completo_ibea");
        }
    }
    //  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --  --
}
