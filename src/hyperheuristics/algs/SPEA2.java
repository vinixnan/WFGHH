//  SPEA2.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package hyperheuristics.algs;

import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.*;
import hyperheuristics.core.ArchivedAlgorithmHH;

import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.Spea2Fitness;

/**
 * This class representing the SPEA2 algorithm
 */
public class SPEA2 extends ArchivedAlgorithmHH {

    /**
     * Defines the number of tournaments for creating the mating pool
     */
    public static final int TOURNAMENTS_ROUNDS = 1;

    /**
     * Constructor. Create a new SPEA2 instance
     *
     * @param problem Problem to solve
     */
    public SPEA2(Problem problem) {
        super(problem);
        this.methodName = "SPEA2";
    } // Spea2

    /**
     * Runs of the Spea2 algorithm.
     *
     * @return a <code>SolutionSet</code> that is a set of non dominated
     * solutions as a result of the algorithm execution
     * @throws JMException
     */
    @Override
    public SolutionSet execute() throws JMException {
        this.initPopulation();
        while (evaluations < maxEvaluations) {
            this.executeMethod();
        } // while
        Ranking ranking = new Ranking(archive);
        return ranking.getSubfront(0);
    } // execute    

    @Override
    public void executeMethod() throws JMException {
        SolutionSet union = ((SolutionSet) population).union(archive);
        Spea2Fitness spea = new Spea2Fitness(union);
        spea.fitnessAssign();
        archive = spea.environmentalSelection(archiveSize);
        // Create a new offspringPopulation
        SolutionSet offSpringSolutionSet = new SolutionSet(populationSize);
        Solution[] parents = new Solution[2];
        while (offSpringSolutionSet.size() < populationSize) {
            int j = 0;
            do {
                j++;
                parents[0] = (Solution) selectionOperator.execute(archive);
            } while (j < SPEA2.TOURNAMENTS_ROUNDS); // do-while                    
            int k = 0;
            do {
                k++;
                parents[1] = (Solution) selectionOperator.execute(archive);
            } while (k < SPEA2.TOURNAMENTS_ROUNDS); // do-while

            //make the crossover 
            Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
            mutationOperator.execute(offSpring[0]);
            problem_.evaluate(offSpring[0]);
            problem_.evaluateConstraints(offSpring[0]);
            offSpringSolutionSet.add(offSpring[0]);
            evaluations++;
        } // while
        // End Create a offSpring population
        population = offSpringSolutionSet;
    }

    @Override
    public void initPopulation() {
        //Read the params
        populationSize = ((Integer) getInputParameter("populationSize")).intValue();
        archiveSize = ((Integer) getInputParameter("archiveSize")).intValue();
        maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();

        //Read the operators
        crossoverOperator = operators_.get("crossover");
        mutationOperator = operators_.get("mutation");
        selectionOperator = operators_.get("selection");

        //Initialize the variables
        population = new SolutionSet(populationSize);
        archive = new SolutionSet(archiveSize);
        evaluations = 0;

        //-> Create the initial population
        Solution newSolution;
        for (int i = 0; i < populationSize; i++) {
            try {
                newSolution = new Solution(problem_);
                problem_.evaluate(newSolution);
                problem_.evaluateConstraints(newSolution);
                evaluations++;
                population.add(newSolution);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void completePopulation() {
        //-> Create the initial population
        Solution newSolution;
         while (this.population.size() < populationSize) {
            try {
                newSolution = new Solution(problem_);
                problem_.evaluate(newSolution);
                problem_.evaluateConstraints(newSolution);
                evaluations++;
                population.add(newSolution);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JMException ex) {
                Logger.getLogger(SPEA2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
} // SPEA2
