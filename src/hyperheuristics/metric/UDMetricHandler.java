/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.metric;

import jmetal.core.SolutionSet;
import jmetal.util.Distance;

/**
 *
 * @author vinicius
 */
public class UDMetricHandler extends MetricHandler {

    public UDMetricHandler(int numObj) {
        super(numObj);
    }

    @Override
    public double calculate(SolutionSet front) {
        /*
        Distance distance = new Distance();
        double[][] distanceMatrix = distance.distanceMatrix(front);
        double snc = this.snc(distanceMatrix);
        return 1 / (1 + snc);
                */
        SpreadHandler sp=new SpreadHandler(numObj);
        sp.population=this.population;
        return sp.calculate(front);
    }

    protected double snc(double[][] distanceMatrix) {
        double sum = 0;
        double ncLine = this.ncLine(distanceMatrix);
        for (int i = 0; i < distanceMatrix.length; i++) {
            sum += Math.pow(((double)this.nc(i, distanceMatrix)) - ncLine, 2);
        }
        double frac = (sum / (distanceMatrix.length - 1));
        return Math.sqrt(frac);
    }

    private double ncLine(double[][] distanceMatrix) {
        double sum = 0;
        for (int i = 0; i < distanceMatrix.length; i++) {
            sum += this.nc(i, distanceMatrix);
        }
        return (sum / distanceMatrix.length);
    }

    private double nc(int i, double[][] distanceMatrix) {
        double sum = 0;
        for (int j = 0; j < distanceMatrix[i].length; j++) {
            if (j != i) {
                sum += this.f(i, j, distanceMatrix);
            }
        }
        return sum;
    }

    private double f(int i, int j, double[][] distanceMatrix) {
        double[] concat = this.concatArray(distanceMatrix[i], distanceMatrix[j]);//É assim?
        double standardD = this.standardDeviation(concat);
        if (distanceMatrix[i][j] > standardD) {
            return 1D;
        } else {
            return 0D;
        }
    }

    protected double[] concatArray(double[] array1, double[] array2) {
        double[] array1and2 = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, array1and2, 0, array1.length);
        System.arraycopy(array2, 0, array1and2, array1.length, array2.length);
        return array1and2;
    }

    protected double standardDeviation(double[] data) {
        double average = 0;
        for (int i = 0; i < data.length; i++) {
            average += data[i];
        }
        average = average / ((double)data.length);
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += Math.pow(data[i] - average, 2);
        }
        return Math.sqrt((1 / ((double)data.length)) * sum);
    }
}
