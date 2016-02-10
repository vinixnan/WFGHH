/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.metric;

import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Epsilon;

/**
 *
 * @author vinicius
 */
public class EpsilonHandler extends MetricHandler {

    private final Epsilon epsilon;

    public EpsilonHandler(int numObj) {
        super(numObj);
        this.epsilon = new Epsilon();
    }

   
    @Override
    public double calculate(SolutionSet front) {
        if (population.size() != 0) {
            double[][] referencePoint = getReferencePoint(this.numObj);
            double[][] objectives = front.writeObjectivesToMatrix();
            double[] maximumValues = metricUtil.getMaximumValues(population.writeObjectivesToMatrix(), this.numObj);
            double[] minimumValues = metricUtil.getMinimumValues(population.writeObjectivesToMatrix(), this.numObj);
            normalizeObjecties(objectives, minimumValues, maximumValues);
            return epsilon.epsilon(objectives, referencePoint, this.numObj);
        }
        return 0D;
    }
}
