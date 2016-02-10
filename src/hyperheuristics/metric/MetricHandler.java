/**
 *
 * @author vinicius
 */
package hyperheuristics.metric;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.util.MetricsUtil;

public abstract class MetricHandler {

    protected SolutionSet population;
    protected final MetricsUtil metricUtil;
    protected final int numObj;

    public MetricHandler(int numObj) {
        this.numObj = numObj;
        this.population = new SolutionSet();
        this.metricUtil = new MetricsUtil();
    }

    public void addParetoFront(SolutionSet front) {
        if(front!=null)
            population = population.union(front);
    }

    public void addParetoFront(String path) {
        addParetoFront(metricUtil.readNonDominatedSolutionSet(path));
    }

    public void clear() {
        this.population = new SolutionSet();
    }

    protected double[][] getReferencePoint(int numberOfObjectives) {
        double[][] referencePoint = new double[numberOfObjectives][numberOfObjectives];
        for (int i = 0; i < referencePoint.length; i++) {
            double[] objective = referencePoint[i];
            objective[i] = 1.01;
            for (int j = 0; j < objective.length; j++) {
                if (i != j) {
                    objective[j] = 0;
                }
            }
        }
        return referencePoint;
    }

    protected void normalizeObjecties(double[][] solutionSet, double[] minimumValues, double[] maximumValues) {
        for (int solutionIndex = 0; solutionIndex < solutionSet.length; solutionIndex++) {
            double[] solution = solutionSet[solutionIndex];
            for (int objectiveIndex = 0; objectiveIndex < solution.length; objectiveIndex++) {
                double max = maximumValues[objectiveIndex];
                double min = minimumValues[objectiveIndex];

                if (min != max) {
                    solution[objectiveIndex] = (solution[objectiveIndex] - min) / (max - min);
                } else {
                    solution[objectiveIndex] = 1.0;
                }
            }
        }
    }
    
    protected void normalizeObjecties(double[][] solutionSet, int numberOfObjectives) {
        double[] maximumValues = metricUtil.getMaximumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
        double[] minimumValues = metricUtil.getMinimumValues(population.writeObjectivesToMatrix(), numberOfObjectives);
        for (int solutionIndex = 0; solutionIndex < solutionSet.length; solutionIndex++) {
            double[] solution = solutionSet[solutionIndex];
            for (int objectiveIndex = 0; objectiveIndex < solution.length; objectiveIndex++) {
                double max = maximumValues[objectiveIndex];
                double min = minimumValues[objectiveIndex];

                if (min != max) {
                    solution[objectiveIndex] = (solution[objectiveIndex] - min) / (max - min);
                } else {
                    solution[objectiveIndex] = 1.0;
                }
            }
        }
    }

    public double calculate(String frontPath) {
        return calculate(this.metricUtil.readNonDominatedSolutionSet(frontPath));
    }

    public double calculate() {
        return this.calculate(population);
    }

    public abstract double calculate(SolutionSet front);
}
