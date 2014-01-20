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

import java.util.List;
import java.util.Map;

import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.TwoSidedEqualityFactor;
import es.csic.iiia.maxsum.util.NeighborValue;

/**
 *
 * @author Toni Penya-Alba <tonipenya@iiia.csic.es>
 */
public class MediatorFactor extends TwoSidedEqualityFactor<Factor> {

    public boolean isFeasible(Map<Factor, Boolean> allocation) {
        return evaluate(allocation) != getMaxOperator().getWorstValue();
    }

    public void mend(Map<Factor, Boolean> allocation) {
        int nActiveSellers = 0;
        int nActiveBuyers = 0;

        final List<Factor> neighbors = getNeighbors();
        final int nNeighbors = neighbors.size();
        for (int i = 0; i < nNeighbors; i++) {
            Factor neighbor = neighbors.get(i);

            if (allocation.get(neighbor)) {
                if (i < nElementsA) {
                    nActiveSellers++;
                } else {
                    nActiveBuyers++;
                }
            }
        }

        if (nActiveSellers > nActiveBuyers) {
            keepNHighest(nActiveBuyers, getSortedSetAPairs(), allocation);
        } else {
            keepNHighest(nActiveSellers, getSortedSetBPairs(), allocation);
        }
    }

    private void keepNHighest(int nToKeep,
            List<NeighborValue<Factor>> neighbors,
            Map<Factor, Boolean> allocation) {
        final int nNeighbors = neighbors.size();
        int nProcessed = 0;
        for (; nProcessed < nNeighbors && nToKeep > 0; nProcessed++) {
            Factor neighbor = neighbors.get(nProcessed).neighbor;
            if (allocation.get(neighbor)) {
                nToKeep--;
            }
        }

        for (int i = nProcessed; i < nNeighbors; i++) {
            allocation.put(neighbors.get(i).neighbor, false);
        }
    }

    @Override
    public void send(double message, Factor recipient) {
        super.send(message, recipient);
    }


}
