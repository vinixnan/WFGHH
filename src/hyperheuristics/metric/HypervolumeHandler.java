package hyperheuristics.metric;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.qualityIndicator.Hypervolume;

/**
 *
 * @author giovaniguizzo
 */
public class HypervolumeHandler extends MetricHandler {

    private final Hypervolume hypervolume;
    protected double[][] referencePoint;
     private final Solution point;

    public HypervolumeHandler(int numObj, double[][] referencePoint) {
        super(numObj);
        this.hypervolume = new Hypervolume();
        this.referencePoint=referencePoint;
        point = new Solution(numObj);
        for (int i = 0; i < numObj; i++) {
            point.setObjective(i, 1D);
        }
    }

    @Override
    public double calculate(SolutionSet front) {
        if (population.size() != 0) {
            double value=0.0;
            double[][] objectives = front.writeObjectivesToMatrix();
            if(this.referencePoint==null){
                value = hypervolume.hypervolume(objectives, population.writeObjectivesToMatrix(), numObj);
            }
            else{
                value = hypervolume.hypervolume(objectives, referencePoint, this.numObj);
            }
            return value;
        }
        return 0D;
    }

    public double calculate(SolutionSet a, SolutionSet b) {
        double value = 0;
        if (a != null && a.size() != 0) {
            SolutionSet a_b = a.union(b);

            double[][] objectivesA = a.writeObjectivesToMatrix();
            double[][] objectivesB = b.writeObjectivesToMatrix();
            double[][] objectivesAB = a_b.writeObjectivesToMatrix();

            double valueA = hypervolume.hypervolume(objectivesA, referencePoint, this.numObj);
            double valueB = hypervolume.hypervolume(objectivesB, referencePoint, this.numObj);
            double valueAB = hypervolume.hypervolume(objectivesAB, referencePoint, this.numObj);

            double aba = valueAB - valueA;
            double abb = valueAB - valueB;

            if (aba > abb) {
                return aba;
            } else {
                return abb;
            }
        }
        return value;
    }

    public void WFGHypervolume(double value, SolutionSet newfront, SolutionSet oldfront) {
        System.out.println(value);
        newfront.printObjectivesToFile("resultado/front.txt");
        System.exit(0);
    }
}
