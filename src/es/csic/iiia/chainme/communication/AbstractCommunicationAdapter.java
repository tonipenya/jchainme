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
package es.csic.iiia.chainme.communication;

import es.csic.iiia.chainme.MediatorFactor;
import es.csic.iiia.maxsum.CommunicationAdapter;
import es.csic.iiia.maxsum.Factor;

/**
*
* @author Toni Penya-Alba <tonipenya@iiia.csic.es>
*/
public abstract class AbstractCommunicationAdapter<T extends Factor<Factor>> implements CommunicationAdapter<T> {
    private double dampingFactor;;
    private double maxDiff = Double.POSITIVE_INFINITY;


    /**
     * TODO: Document.
     */
    public abstract void doSend(double message, T sender, T recipient);

    public void send(double message, T sender, T recipient) {
        if (sender instanceof MediatorFactor) {
            message = recipient.getMessage(sender) * dampingFactor
                    + message * (1 - dampingFactor);
        }

        double diff = Math.abs(recipient.getMessage(sender) - message);
        maxDiff = Math.max(diff, maxDiff);

        doSend(message, sender, recipient);
    }

    /**
     * TODO: Document.
     */
    public void setDampingFactor(double dampingFactor) {
        this.dampingFactor = dampingFactor;

    }

    /**
     * TODO: Document.
     */
    public void tick() {
        // Do nothing.
    }

    public double getMaxDiff() {
        return maxDiff;
    }

    public void setMaxDiff(double maxDiff) {
        this.maxDiff = maxDiff;
    }
}
