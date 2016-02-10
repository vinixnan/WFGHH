package experiments;


import hyperheuristics.core.AlgorithmHH;
import hyperheuristics.core.HeuristicBuilder;
import hyperheuristics.core.HyperHeuristicSelector;
import hyperheuristics.core.PopulationWorks;
import hyperheuristics.selectors.ChoiceFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import jmetal.core.*;
import jmetal.problems.WFG.*;
import jmetal.util.JMException;

public class Experiment_HH_WFG {

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
        
        double[][] referencePoint = new double[M][M];
        referencePoint[0][0] = 2;
        referencePoint[1][1] = 4;
        
        int runsNumber = 30;
        int populationSize = 100;
        int archiveSize = 100;
        int totalEvaluations = 625000; //ou seja o que for
        int runHH = 25;
        totalEvaluations=totalEvaluations-(2*(totalEvaluations / runHH));
        int maxEvaluations = totalEvaluations / runHH;//slideSize como no artigo
        double scalingFactor = 100;

        //runHH-=2;//remover a inicializacao da conta
        double crossoverProbability = 0.9;
        double mutationProbability = 1.0/24.0; //0.2;
        int numObj = 2;

        for (WFG problem : softwares) {
            long initTime = System.currentTimeMillis();
            SolutionSet todasRuns = new SolutionSet();

            SolutionSet partial = new SolutionSet();
            
            System.out.println("==========================Executando o problema " + problem.getName() + "==========================");
            System.out.println("HH: Choice Function");
            System.out.println("Params:");
            System.out.println("\tPop -> " + populationSize);
            System.out.println("\tArchiveSize -> " + archiveSize);
            System.out.println("\tMaxEva -> " + totalEvaluations);
            System.out.println("\tSlide Window -> " + maxEvaluations);
            System.out.println("\tCross -> " + crossoverProbability);
            System.out.println("\tMuta -> " + mutationProbability);

            HeuristicBuilder builder = new HeuristicBuilder(problem, "SBXCrossover", "PolynomialMutation", "BinaryTournament", crossoverProbability, mutationProbability, populationSize, totalEvaluations/3, archiveSize);
            //escolhe algoritimo
            for (int runs = 0; runs < runsNumber; runs++) {
                long initRunTime = System.currentTimeMillis();
                SolutionSet resultFront = new SolutionSet();
                builder.initAlgs();
                HyperHeuristicSelector selector = new ChoiceFunction(scalingFactor, builder.getAlgs(), numObj, maxEvaluations, populationSize, referencePoint);
                AlgorithmHH algorithm;
                for (AlgorithmHH alg : builder.getAlgs()) {
                    System.out.println("Inicializando " + alg.getMethodName());
                    int i = 0;
                    long initialtime=System.currentTimeMillis();
                    while (i < maxEvaluations*2) {
                        alg.executeMethod();
                        i = alg.getEvaluations();
                    }
                    selector.updateAEImprovement(alg, initialtime, System.currentTimeMillis());
                }
                selector.calcRanking();
                for (int eval = 0; eval < runHH; eval++) {
                    algorithm = selector.chooseAlg();
                    int i = 0;
                    selector.startTime();
                    while (i < maxEvaluations) {
                        algorithm.executeMethod();
                        i = algorithm.getEvaluations();
                    }
                    selector.finishTime();
                    selector.incrementTimebutNotThis(algorithm);
                    partial = algorithm.getNonDominatedPopulation();
                    //escolhe algoritimo
                }
                resultFront=partial;
                resultFront.printObjectivesToFile("resultado/all/" + problem.getName() + "/FUN_all" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                resultFront.printVariablesToFile("resultado/all/" + problem.getName() + "/VAR_all" + "-" + problem.getName() + "-" + runs + ".NaoDominadas");
                //armazena as solucoes de todas runs
                todasRuns = todasRuns.union(resultFront);
                long estimatedTime = System.currentTimeMillis() - initRunTime;
                System.out.println("Iruns: " + runs + "\tTotal time: " + estimatedTime);
            }
            todasRuns = PopulationWorks.removeDominadas(todasRuns);
            todasRuns = PopulationWorks.removeRepetidas(todasRuns);
            todasRuns.printObjectivesToFile("resultado/all/" + problem.getName() + "/All_FUN_all" + "-" + problem.getName());
            todasRuns.printVariablesToFile("resultado/all/" + problem.getName() + "/All_VAR_all" + "-" + problem.getName());
            
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
