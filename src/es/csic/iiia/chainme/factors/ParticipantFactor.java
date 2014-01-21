package es.csic.iiia.chainme.factors;

import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.VariableFactor;

public class ParticipantFactor extends VariableFactor<Factor> {
    public double calcBelief() {
        double belief = 0d;
        for (Factor<Factor> neighbor : getNeighbors()) {
            belief += getMessage(neighbor);
        }

        return belief;
    }
}
