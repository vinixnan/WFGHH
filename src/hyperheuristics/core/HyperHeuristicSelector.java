/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyperheuristics.core;

import hyperheuristics.metric.AlgorithmEffort;
import hyperheuristics.metric.HypervolumeHandler;
import hyperheuristics.metric.MetricHandler;
import hyperheuristics.metric.RNI;
import hyperheuristics.metric.UDMetricHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jmetal.core.SolutionSet;

/**
 *
 * @author vinicius
 */
public abstract class HyperHeuristicSelector {

    protected int numObj;
    protected ArrayList<AlgorithmHH> algs;
    protected AlgorithmHH current;

    protected HashMap<AlgorithmHH, ArrayList<AlgorithmRanking>> hash;
    protected HashMap<AlgorithmHH, SolutionSet> olderPopulation;

    protected HypervolumeHandler hypervolume;
    protected RNI rni;
    protected UDMetricHandler ud;
    protected AlgorithmEffort ae;

    private long initialTime;
    private long finalTime;

    public HyperHeuristicSelector(ArrayList<AlgorithmHH> algs, int numObj, int evaluationsPerTime, int populationsize, double[][] referencePoint) {
        this.numObj = numObj;
        this.algs = algs;
        this.current = null;
        this.hash = new HashMap<>();
        this.olderPopulation = new HashMap<>();
        this.hypervolume = new HypervolumeHandler(this.numObj, referencePoint);
        this.rni = new RNI(this.numObj, populationsize);
        this.ae = new AlgorithmEffort(this.numObj);
        this.ae.setEvaluations(evaluationsPerTime);
        this.ud = new UDMetricHandler(this.numObj);
        this.initHHSelector();
    }

    public void startTime() {
        this.initialTime = System.currentTimeMillis();
        this.finalTime = 0;
    }

    public void finishTime() {
        this.finalTime = System.currentTimeMillis();
    }

    protected void trocaContexto(SolutionSet newpop, AlgorithmHH destino) {
        this.olderPopulation.put(destino, destino.getNonDominatedPopulation());
        destino.setNonDominatedPopulation(newpop);
        destino.setEvaluations(0);
        //destino.completePopulation();
    }

    protected void trocaContexto(AlgorithmHH origem, AlgorithmHH destino) {
        if (origem != null) {
            this.trocaContexto(origem.getNonDominatedPopulation(), destino);
            origem.restartEstimatedTime();
        } else {
            for (AlgorithmHH alg : this.algs) {
                alg.setNonDominatedPopulation(destino.getNonDominatedPopulation());
                this.olderPopulation.put(alg, destino.getNonDominatedPopulation());
                alg.restartEstimatedTime();
            }
        }
        destino.setEvaluations(0);
        destino.noTimeCount();
    }

    public void incrementTimebutNotThis(AlgorithmHH algNoincrement) {
        for (AlgorithmHH alg : this.algs) {
            if (alg != algNoincrement) {
                alg.endTimeCount();
            } else {
                alg.noTimeCount();
            }
        }
    }

    protected void hashInit() {
        for (AlgorithmHH alg : algs) {
            SolutionSet fullPop = alg.getNonDominatedPopulation();
            this.olderPopulation.put(alg, fullPop);
            ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
            AlgorithmRanking algranking;

            algranking = new AlgorithmRanking(new Improvement(this.hypervolume), alg, this.numObj);//Hypervolume
            ranking.add(algranking);//Hypervolume

            algranking = new AlgorithmRanking(new Improvement(rni), alg, this.numObj);//RNI
            ranking.add(algranking);//RNI

            algranking = new AlgorithmRanking(new Improvement(ud), alg, this.numObj);//UD
            ranking.add(algranking);//UD

            algranking = new AlgorithmRanking(new Improvement(ae), alg, this.numObj);//AE
            ranking.add(algranking);//AE

            this.hash.put(alg, ranking);
        }
    }

    protected SolutionSet joinAllPopulation() {
        SolutionSet allpopulation = new SolutionSet();
        for (SolutionSet population : this.olderPopulation.values()) {
            allpopulation = allpopulation.union(population);
        }
        return allpopulation;
    }

    public SolutionSet joinNewAllPopulation() {
        SolutionSet allpopulation = new SolutionSet();
        for (AlgorithmHH alg : this.algs) {
            SolutionSet population = alg.getNonDominatedPopulation();
            allpopulation = allpopulation.union(population);
        }
        return allpopulation;
    }

    public void initHHSelector() {
        this.hashInit();

    }

    public AlgorithmHH selectAlg(int id) {
        if (id > this.algs.size()) {
            id = 0;
        }
        AlgorithmHH alg = this.algs.get(id);
        this.trocaContexto(this.current, alg);
        this.current = alg;
        return alg;
    }

    public void updateRankingAlg(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        for (AlgorithmRanking algRanking : ranking) {
            algRanking.calcMetric();
        }
    }

    private ArrayList<AlgorithmRanking> getRankingByMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = new ArrayList<>();
        for (AlgorithmHH alg : algs) {
            ArrayList<AlgorithmRanking> allranking = this.hash.get(alg);
            AlgorithmRanking algranking = null;
            for (AlgorithmRanking aux : allranking) {
                if (aux.getImp().getMetric() == metric) {
                    algranking = aux;
                    algranking.calcMetric();
                }
            }
            ranking.add(algranking);
        }
        Collections.sort(ranking);
        return ranking;
    }

    protected void calcRankingMetric(MetricHandler metric) {
        ArrayList<AlgorithmRanking> ranking = this.getRankingByMetric(metric);
        int rank = 0;
        double previus = Double.MIN_VALUE;
        for (AlgorithmRanking algranking : ranking) {
            if (algranking.getValue() != previus) {
                rank++;
                previus = algranking.getValue();
            }
            algranking.setRanking(rank);
        }
    }

    public void updateAEImprovement(AlgorithmHH alg, long initialtime, long finaltime) {
        this.ae.setTimeExpend(finaltime - initialtime);
        double improveAE = this.calcImprovement(alg, ae, null);//AE
        Improvement imp = this.getImprovementObj(alg, ae);//AE
        imp.addImprovement(improveAE);//AE
    }

    public void updateImprovement(AlgorithmHH alg, SolutionSet allpopulation) {
        double improveHyp = this.calcImprovement(alg, hypervolume, allpopulation);//Hypervolume
        Improvement imp = this.getImprovementObj(alg, hypervolume);//Hypervolume
        imp.addImprovement(improveHyp);//Hypervolume

        double improveRNI = this.calcImprovement(alg, rni, allpopulation);//RNI
        imp = this.getImprovementObj(alg, rni);//RNI
        imp.addImprovement(improveRNI);//RNI

        double improveUD = this.calcImprovement(alg, ud, allpopulation);//UD
        imp = this.getImprovementObj(alg, ud);//UD
        imp.addImprovement(improveUD);//UD

        if (this.finalTime - this.initialTime > 0) {
            this.updateAEImprovement(alg, initialTime, finalTime);
        }
    }

    public void calcRanking() {
        SolutionSet allPop = this.joinAllPopulation();
        for (AlgorithmHH alg : this.algs) {
            this.updateImprovement(alg, allPop);
        }
        this.calcRankingMetric(hypervolume);//Hypervolume
        this.calcRankingMetric(rni); //RNI
        this.calcRankingMetric(ud); //UD
        this.calcRankingMetric(ae); //AE
    }

    private int calcFrequencyFirst(AlgorithmHH alg) {
        ArrayList<AlgorithmRanking> ranking = this.hash.get(alg);
        int qtdfirst = 0;
        if (ranking != null) {
            for (AlgorithmRanking algrank : ranking) {
                if (algrank.getRanking() == 1) {
                    qtdfirst++;
                }
            }
        }
        return qtdfirst;
    }

    protected int[] calcAllFrequency() {
        HashMap<AlgorithmHH, Integer> frequencies = new HashMap<AlgorithmHH, Integer>();
        int biggerFrequency = Integer.MIN_VALUE;
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            int aux = this.calcFrequencyFirst(alg);
            frequencies.put(alg, aux);
            if (aux > biggerFrequency) {
                biggerFrequency = aux;
            }
        }
        LinkedHashMap orderedfrequencies = this.sortByComparator(frequencies);
        int[] frequenciesArray = new int[this.algs.size()];
        for (int i = 0; i < this.algs.size(); i++) {
            AlgorithmHH alg = this.algs.get(i);
            frequenciesArray[i] = this.returnrRankPosition(alg, orderedfrequencies);
        }
        return frequenciesArray;
    }

    private int returnrRankPosition(AlgorithmHH alg, LinkedHashMap<AlgorithmHH, Integer> frequencies) {
        Iterator it = frequencies.entrySet().iterator();
        int counter = 0;
        int lastFrequency = Integer.MIN_VALUE;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            counter++;
            if (pairs.getKey() == alg) {
                if (lastFrequency == ((Integer) pairs.getValue())) {
                    return counter - 1;
                } else {
                    return counter;
                }
            }
            lastFrequency = ((Integer) pairs.getValue());
        }
        return counter;
    }

    private LinkedHashMap<AlgorithmHH, Integer> sortByComparator(Map<AlgorithmHH, Integer> unsortMap) {

        // Convert Map to List
        List<Map.Entry<AlgorithmHH, Integer>> list
                = new LinkedList<Map.Entry<AlgorithmHH, Integer>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<AlgorithmHH, Integer>>() {
            public int compare(Map.Entry<AlgorithmHH, Integer> o1,
                    Map.Entry<AlgorithmHH, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Convert sorted map back to a Map
        LinkedHashMap<AlgorithmHH, Integer> sortedMap = new LinkedHashMap<AlgorithmHH, Integer>();
        for (Iterator<Map.Entry<AlgorithmHH, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<AlgorithmHH, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    protected Improvement getImprovementObj(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank.getImp();
            }
        }
        return null;
    }

    protected AlgorithmRanking getMetric(AlgorithmHH alg, MetricHandler metric) {
        ArrayList<AlgorithmRanking> rankings = this.hash.get(alg);
        for (AlgorithmRanking rank : rankings) {
            if (rank.getImp().getMetric() == metric) {
                return rank;
            }
        }
        return null;
    }

    public void updateOldPopulation(AlgorithmHH alg, SolutionSet solution) {
        this.olderPopulation.put(alg, solution);
    }

    protected double calcAtualMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());
        double newH = metric.calculate(alg.getNonDominatedPopulation());
        return newH;/// - oldH;
    }

    protected double calcOldMetricValue(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        metric.clear();
        metric.addParetoFront(allPop);
        metric.addParetoFront(alg.getNonDominatedPopulation());
        double oldH = metric.calculate(this.olderPopulation.get(alg));
        return oldH;/// - oldH;
    }

    protected double calcImprovement(AlgorithmHH alg, MetricHandler metric, SolutionSet allPop) {
        return this.calcAtualMetricValue(alg, metric, allPop);// - this.calcOldMetricValue(alg, metric, allPop);
    }

    public AlgorithmHH chooseAlg() {
        return null;
    }

    public AlgorithmHH chooseAlg(int firstIteration, Double[] qOp, Double[] nOptrial) {
        return null;
    }

    public ArrayList<AlgorithmHH> getAlgs() {
        return algs;
    }
}
