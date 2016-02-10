package hyperheuristics.metric;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.R2;

/**
 *
 * @author vinicius
 */
public class R2Handler extends MetricHandler {
    
    private final R2 r2;
    
    public R2Handler(int numObj) {
        super(numObj);
        this.r2 = new R2(this.numObj);
    }
    
    @Override
    public double calculate(SolutionSet front) {
        if (population.size() != 0) {
            double[][] objectives = front.writeObjectivesToMatrix();
            double[][] paretoFront = this.population.writeObjectivesToMatrix();
            return 1 - r2.R2(objectives, paretoFront);
        }
        return 0D;
    }
}
