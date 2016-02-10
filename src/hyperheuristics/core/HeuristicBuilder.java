/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Operator;
import jmetal.core.Problem;
import hyperheuristics.algs.IBEA;
import hyperheuristics.algs.NSGAII;
import hyperheuristics.algs.SPEA2;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.MutationFactory;
import jmetal.operators.selection.SelectionFactory;

import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public class HeuristicBuilder {

    protected Problem problem;
    protected double crossoverProbability;
    protected double mutationProbability;
    protected int populationSize;
    protected int maxEvaluations;
    protected int archiveSize;
    
    protected String crossoverName;
    protected String mutationName;
    protected String selectionName;

    protected ArrayList<AlgorithmHH> algs;

    public HeuristicBuilder(Problem problem, String crossoverName, String mutationName, String selectionName, double crossoverProbability, double mutationProbability, int populationSize, int maxEvaluations, int archiveSize) {
        this.problem = problem;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.populationSize = populationSize;
        this.maxEvaluations = maxEvaluations;
        this.archiveSize = archiveSize;
        this.algs = new ArrayList<>();
        this.crossoverName=crossoverName;
        this.mutationName=mutationName;
        this.selectionName=selectionName;
    }

    private void setParametersAlg(AlgorithmHH algorithm) throws JMException {

        Operator crossover;
        Operator mutation;
        Operator selection;
        HashMap parameters = new HashMap();

        // Crossover
        parameters.put("probability", crossoverProbability);
        parameters.put("distributionIndex", 10.0);
        crossover = CrossoverFactory.getCrossoverOperator(this.crossoverName, parameters);//Permutation TwoPointsCrossover

        // Mutation
        parameters.put("probability", mutationProbability);
        parameters.put("distributionIndex", 20.0);
        mutation = MutationFactory.getMutationOperator(this.mutationName, parameters);//

        // Selection
        parameters.clear();
        selection = SelectionFactory.getSelectionOperator(this.selectionName, parameters);

        // Algorithm params
        algorithm.setInputParameter("populationSize", populationSize);
        algorithm.setInputParameter("maxEvaluations", maxEvaluations);
        algorithm.setInputParameter("archiveSize", archiveSize);
        //algorithm.setInputParameter("indicators", new QualityIndicator(problem, "./WFG1.2D.pf"));
        algorithm.addOperator("crossover", crossover);
        algorithm.addOperator("mutation", mutation);
        algorithm.addOperator("selection", selection);
    }

    public AlgorithmHH createIBEA() throws JMException {
        AlgorithmHH algorithm = new IBEA(problem);
        this.setParametersAlg(algorithm);
        algorithm.initPopulation();
        return algorithm;
    }

    public AlgorithmHH createNSGAII() throws JMException {
        AlgorithmHH algorithm = new NSGAII(problem);
        this.setParametersAlg(algorithm);
        algorithm.initPopulation();
        algorithm.executeMethod();
        return algorithm;
    }
    
    public AlgorithmHH createSPEA2() throws JMException {
        AlgorithmHH algorithm = new SPEA2(problem);
        this.setParametersAlg(algorithm);
        algorithm.initPopulation();
        algorithm.executeMethod();
        return algorithm;
    }

    public void initAlgs() {
        this.algs = new ArrayList<>();
        try {
            this.algs.add(this.createNSGAII());
            this.algs.add(this.createSPEA2());
            this.algs.add(this.createIBEA());
            //this.algs.add(this.createMOEAD());
            //this.algs.add(this.createPAES()); //removido devido a solicitacao da Aurora
        } catch (JMException ex) {
            Logger.getLogger(HeuristicBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.setSamePopulation();
    }

    private void setSamePopulation() {
        if (this.algs.size() > 0) {
            Random gerador = new Random();
            int pos = gerador.nextInt(this.algs.size());
            AlgorithmHH chosen = this.algs.get(pos);
            for (AlgorithmHH alg : this.algs) {
                alg.setNonDominatedPopulation(chosen.getNonDominatedPopulation());
            }
        }
    }

    public ArrayList<AlgorithmHH> getAlgs() {
        return algs;
    }

}
