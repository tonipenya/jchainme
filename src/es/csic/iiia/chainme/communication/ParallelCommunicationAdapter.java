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

import java.util.logging.Level;
import java.util.logging.Logger;

import es.csic.iiia.maxsum.Factor;

/**
 * Communication adapter that delivers messages by rounds.
 * <p/>
 * This adapter buffers all the messages being sent, and only delivers them
 * when it is ticked. This makes it very easy to implement lock-stepped max-sum,
 * provided that you do *not* need to send the messages through a simulated
 * network and/or tamper with them in any way.
 *
 * @author Toni Penya-Alba <tonipenya@iiia.csic.es>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ParallelCommunicationAdapter extends AbstractCommunicationAdapter<Factor<Factor>> {
    private static final Logger LOG = Logger.getLogger(ParallelCommunicationAdapter.class.getName());

    private Factor[] senders;
    private Factor[] recipients;
    private double[] values;
    private int bufferIdx;

    /**
     * TODO: Document this method.
     * @param nMessages
     */
    public void initBuffer(int nMessages) {
        senders = new Factor[nMessages];
        recipients = new Factor[nMessages];
        values = new double[nMessages];
        bufferIdx = -1;
    }

    @Override
    public void doSend(double message, Factor<Factor> sender, Factor<Factor> recipient) {
        LOG.log(Level.FINEST, "Message from {0} to {1} : {2}", new Object[]{sender, recipient, message});
        bufferIdx++;
        senders[bufferIdx] = sender;
        recipients[bufferIdx] = recipient;
        values[bufferIdx] = message;
    }

    /**
     * Messages are buffered until the channel is ticked, when it delivers all
     * of the messages sent since the last tick.
     */
    @Override
    public void tick() {
        super.tick();
        for (; bufferIdx >= 0; bufferIdx--) {
            recipients[bufferIdx].receive(values[bufferIdx], senders[bufferIdx]);
        }
    }
}
