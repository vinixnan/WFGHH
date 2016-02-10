package experiments;


import hyperheuristics.algs.NSGAII;
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
import jmetal.problems.WFG.*;
import jmetal.util.JMException;

public class Experiment_NSGAII_WFG {

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

         WFG[] softwares;
        softwares = new WFG[9];
        int k=24, l=20, M=2;
        softwares[0] = new WFG1("Real", k, l, M);
        softwares[1] = new WFG2("Real", k, l, M);
        softwares[2] = new WFG3("Real", k, l, M);
        softwares[3] = new WFG4("Real", k, l, M);
        softwares[4] = new WFG5("Real", k, l, M);
        softwares[5] = new WFG6("Real", k, l, M);
        softwares[6] = new WFG7("Real", k, l, M);
        softwares[7] = new WFG8("Real", k, l, M);
        softwares[8] = new WFG9("Real", k, l, M);
       
        int runsNumber = 30;
        int populationSize = 100;
        int archiveSize = 100;
        int maxEvaluations = 625000; //ou seja o que for
        
        double crossoverProbability = 0.9;
        double mutationProbability = 1.0/24.0; //0.2;
        

        for (WFG problem : softwares) {
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

            AlgorithmHH algorithm = new NSGAII(problem);

            Operator crossover;
            Operator mutation;
            Operator selection;
            HashMap parameters = new HashMap();

            // Crossover
            parameters.put("probability", crossoverProbability);
            parameters.put("distributionIndex", 10.0);
            crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);//Permutation TwoPointsCrossover

            // Mutation
            parameters.put("probability", mutationProbability);
            parameters.put("distributionIndex", 20.0);
            mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);//

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
                resultFront.printObjectivesToFile("resultado/nsgaii/" + problem.getName() + "/FUN_nsgaii" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/nsgaii/" + problem.getName() + "/VAR_nsgaii" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
                long estimatedTime = System.currentTimeMillis() - initRunTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
            }

            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/nsgaii/" + problem.getName() + "/All_FUN_nsgaii" + "-" + problem.getName());
            todasRuns.printVariablesToFile("resultado/nsgaii/" + problem.getName() + "/All_VAR_nsgaii" + "-" + problem.getName());

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
