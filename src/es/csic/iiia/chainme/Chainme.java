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

import java.util.Iterator;
import java.util.List;

import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.VariableFactor;

/**
*
* @author Toni Penya-Alba <tonipenya@iiia.csic.es>
*/
public class Chainme extends Algorithm {
    public Chainme(Configuration conf, List<Factor> factors) {
        super(conf, factors);
    }

    @Override
    public int getNParticipants() {
        return vars.size();
    }

    @Override
    public boolean[] getAllocation() {
        final int nVars = vars.size();
        boolean[] allocation = new boolean[nVars];

        for (int i = 0; i < nVars; i++) {
            VariableFactor var = vars.get(i);
            allocation[i] = solution.get(var);
        }

        return allocation;
    }

    @Override
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

}
