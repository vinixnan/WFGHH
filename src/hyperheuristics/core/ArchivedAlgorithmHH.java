/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import hyperheuristics.core.PopulationWorks;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

/**
 *
 * @author vinicius
 */
public abstract class ArchivedAlgorithmHH extends AlgorithmHH {

    protected SolutionSet archive;
    protected int archiveSize;

    public ArchivedAlgorithmHH(Problem problem) {
        super(problem);
    }

    public SolutionSet getArchive() {
        return this.archive;
    }

    public void setArchive(SolutionSet archive) {
        this.archive = this.clonePopulation(archive);
    }

    @Override
    public SolutionSet getNonDominatedPopulation() {
        SolutionSet pop = this.archive.union(population);
        pop=PopulationWorks.removeDominadas(pop);
        pop=PopulationWorks.removeRepetidas(pop);
        pop.setCapacity(this.populationSize);
        return pop;
    }

    @Override
    public void setNonDominatedPopulation(SolutionSet population) {
        super.setNonDominatedPopulation(population);
        this.archive = this.clonePopulation(population);
    }
    
    /**
     * Configure and init population.
     *
     * @throws jmetal.util.JMException
     * @throws java.lang.ClassNotFoundException
     */
    @Override
    public abstract void executeMethod() throws JMException;

    /**
     * Configure and init population.
     */
    @Override
    public abstract void initPopulation();
    
    
    @Override
    public abstract void completePopulation();
}
