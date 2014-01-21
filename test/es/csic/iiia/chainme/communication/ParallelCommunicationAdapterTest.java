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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.csic.iiia.chainme.communication.ParallelCommunicationAdapter;
import es.csic.iiia.maxsum.Factor;

/**
 *
 * @author Toni Penya-Alba <tonipenya@iiia.csic.es>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class ParallelCommunicationAdapterTest {

    public ParallelCommunicationAdapterTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of tick method, of class TickCommunicationAdapter.
     */
    @Test
    public void testTick() {
        final double message1 = 1d;
        final double message2 = 2d;

        Factor sender = mock(Factor.class);
        Factor recipient = mock(Factor.class);
        ParallelCommunicationAdapter instance = new ParallelCommunicationAdapter();

        instance.initBuffer(3);
        instance.send(message1, sender, recipient);
        // This should deliver the first message
        instance.tick();
        verify(recipient, times(1)).receive(message1, sender);

        // This should do nothing (no messages sent inbetween ticks)
        reset(recipient);
        instance.tick();
        verify(recipient, times(0)).receive(message1, sender);

        // This should deliver the second message
        reset(recipient);
        instance.send(message2, sender, recipient);
        instance.tick();
        verify(recipient, times(1)).receive(message2, sender);
    }

    /**
     * Test of tick method, of class TickCommunicationAdapter.
     */
    @Test
    public void testOverflow() {
        final double message1 = 1d;

        Factor sender = mock(Factor.class);
        Factor recipient = mock(Factor.class);
        ParallelCommunicationAdapter instance = new ParallelCommunicationAdapter();

        instance.initBuffer(1);
        instance.send(message1, sender, recipient);
        try {
            instance.send(message1, sender, recipient);
            fail("Overflown buffer doesn't throw an exception");
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }
}