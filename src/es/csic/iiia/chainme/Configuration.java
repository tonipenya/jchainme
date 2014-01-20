package es.csic.iiia.chainme;

import es.csic.iiia.chainme.communication.AbstractCommunicationAdapter;
import es.csic.iiia.chainme.communication.ParallelCommunicationAdapter;
import es.csic.iiia.maxsum.MaxOperator;
import es.csic.iiia.maxsum.Maximize;

public class Configuration {
    final static int DEFAULT_MAX_ITER = 100;
    final static double DEFAULT_CONVERGENCE_TOLERANCE = 1e-9;
    final static double DEFAULT_DAMPING_FACTOR = 0d;
    final static boolean DEFAULT_SHUFFLE_FACTORS = false;

    public int maxIters;
    public double convergenceTolerance;
    public double dampingFactor;
    public boolean shuffleFactors;
    public MaxOperator op;
    public AbstractCommunicationAdapter com;

    public Configuration() {
        maxIters = DEFAULT_MAX_ITER;
        convergenceTolerance = DEFAULT_CONVERGENCE_TOLERANCE;
        dampingFactor = DEFAULT_DAMPING_FACTOR;
        shuffleFactors = DEFAULT_SHUFFLE_FACTORS;
        op = new Maximize();
        com = new ParallelCommunicationAdapter();
    }
}
