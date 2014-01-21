/*
 * Software License Agreement (BSD License)
 *
 * Copyright 2013 Toni Pena-Alba <tonipenya@iiia.csic.es>.
 *
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 *
 *   Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 *
 *   Neither the name of IIIA-CSIC, Artificial Intelligence Research Institute
 *   nor the names of its contributors may be used to
 *   endorse or promote products derived from this
 *   software without specific prior written permission of
 *   IIIA-CSIC, Artificial Intelligence Research Institute
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package es.csic.iiia.chainme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.csic.iiia.chainme.communication.AbstractCommunicationAdapter;
import es.csic.iiia.chainme.communication.ParallelCommunicationAdapter;
import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.MaxOperator;
import es.csic.iiia.maxsum.factors.VariableFactor;

/**
*
* @author Toni Penya-Alba <tonipenya@iiia.csic.es>
*/
public class Chainme {
    final private Configuration conf;
    final private AbstractCommunicationAdapter com;
    final private MaxOperator op;
    private List<Factor> factors;
    private List<VariableFactor<Factor>> vars;
    private Map<Factor, Boolean> solution;
    private int iters;

    public Chainme(Configuration conf, List<Factor> factors) {
        this.conf = conf;
        this.factors = factors;
        extractVarsFromFactors();
        com = conf.com;
        op = conf.op;

        initialize();
    }

    public void solve() {
        for (iters = 0; iters < conf.maxIters
                && com.getMaxDiff() > conf.convergenceTolerance; iters++) {
            com.setMaxDiff(Double.NEGATIVE_INFINITY);

            for (Factor factor : factors) {
                factor.run();
            }

            com.tick();
        }

        calcBeliefs();

        calcSolution();
    }

    public int getIters() {
        return iters;
    }

    public int getNParticipants() {
        return vars.size();
    }

    public boolean[] getAllocation() {
        final int nVars = vars.size();
        boolean[] allocation = new boolean[nVars];

        for (int i = 0; i < nVars; i++) {
            VariableFactor var = vars.get(i);
            allocation[i] = solution.get(var);
        }

        return allocation;
    }

    public double getObjective() {
        double objective = 0;

        for (Factor<Factor> factor : factors) {
            if (factor instanceof VariableFactor) {
                continue;
            }
            objective += factor.evaluate(solution);
        }

        return objective;
    }

    public void pruneAllocation() {

        boolean somethingFixed = true;
        while (somethingFixed) {
            somethingFixed = fixAllocationOneStep();
        }
    }

    private boolean fixAllocationOneStep() {
        boolean somethingFixed = false;

        Iterator<Factor> factorIt = factors.iterator();

        while (factorIt.hasNext()) {
            Factor factor = factorIt.next();
            if (!(factor instanceof MediatorFactor)) {
                continue;
            }

            MediatorFactor mediator = (MediatorFactor) factor;
            if (mediator.isFeasible(solution)) {
                continue;
            }

            mediator.mend(solution);
            somethingFixed = true;
        }

        return somethingFixed;
    }

    private void initialize() {
        com.setDampingFactor(conf.dampingFactor);

        if (conf.shuffleFactors) {
            Collections.shuffle(factors);
        }

        if (com instanceof ParallelCommunicationAdapter) {
            int bufferSize = calcMessageBufferSize();
            ((ParallelCommunicationAdapter) com).initBuffer(bufferSize);
        }

        setFactorsCommunicationAdapter();
        setFactorsMaxOp();
    }

    private int calcMessageBufferSize() {
        int count = 0;

        for (Factor factor : factors) {
            count += factor.getNeighbors().size();
        }

        return count;
    }

    @SuppressWarnings("unchecked")
    private void setFactorsCommunicationAdapter() {
        for (Factor<Factor> factor : factors) {
            factor.setCommunicationAdapter(com);
        }
    }

    private void setFactorsMaxOp() {
        for (Factor factor : factors) {
            factor.setMaxOperator(op);
        }
    }

    private void calcSolution() {
        final int nVars = vars.size();
        final double[] beliefs = calcBeliefs();
        solution = new HashMap<Factor, Boolean>(nVars);

        for (int i = 0; i < nVars; i++) {
            solution.put(vars.get(i), beliefs[i] >= 0);
        }
    }

    private void extractVarsFromFactors() {
        vars = new ArrayList<VariableFactor<Factor>>();

        for (Factor<Factor> factor : factors) {
            if (factor instanceof VariableFactor) {
                vars.add((VariableFactor<Factor>) factor);
            }
        }
    }

    private double[] calcBeliefs() {
        final int nVars = vars.size();
        double[] beliefs = new double[nVars];
        for (int j = 0; j < nVars; j++) {
            VariableFactor<Factor> var = vars.get(j);
            beliefs[j] = calcBelief(var);
        }

        return beliefs;
    }

    private double calcBelief(VariableFactor<Factor> var) {
        double belief = 0d;
        for (Factor<Factor> neighbor : var.getNeighbors()) {
            belief += var.getMessage(neighbor);
        }

        return belief;
    }

}
