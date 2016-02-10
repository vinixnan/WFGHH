package hyperheuristics.selectors;

import hyperheuristics.core.Operator;
import java.util.LinkedList;

public class CreditAssignment {

	public double getReward(int op, int type, boolean norm,
			LinkedList<Operator> slidingWindow, int K) {
		double reward = 0;
		// last
		if (type == 0) {
			for (int i = slidingWindow.size() - 1; i >= 0; i--) {
				if (op == slidingWindow.get(i).getOperatorId()) {
					reward = slidingWindow.get(i).getFitnessImprovement();
					break;
				}
			}
		}
		// average
		else if (type == 1) {
			double sum = 0;
			int count = 0;
			for (Operator operator : slidingWindow) {
				if (op == operator.getOperatorId()) {
					sum += operator.getFitnessImprovement();
					count++;
				}
			}
			reward = sum / count;
		}
		// extreme
		else if (type == 2) {
			double max = Double.MIN_VALUE;
			for (Operator operator : slidingWindow) {
				if (op == operator.getOperatorId()) {
					if (operator.getFitnessImprovement() > max) {
						max = operator.getFitnessImprovement();
					}
				}
			}
			reward = max;
		}
		// normalized
		if (norm) {
			double max = Double.MIN_VALUE;
			double normfactor = 0;
			for (int i = 0; i < K; i++) {
				double value = this.getReward(i, type, false, slidingWindow, K);
				if (value >= max) {
					max = value;
				}
			}
			normfactor = max;
			reward = this.getReward(op, type, false, slidingWindow, K)
					/ normfactor;

		}
		return Math.max(reward, 0);
	}

}
