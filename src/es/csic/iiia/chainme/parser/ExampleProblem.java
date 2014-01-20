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
package es.csic.iiia.chainme.parser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import es.csic.iiia.chainme.MediatorFactor;
import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.IndependentFactor;
import es.csic.iiia.maxsum.factors.VariableFactor;

/**
*
* @author Toni Penya-Alba <tonipenya@iiia.csic.es>
*/
public class ExampleProblem implements ProblemParser {

    @Override
    public List<Factor> parseProblemFile(String problemFile) throws FileNotFoundException {
        List<Factor> factors = new ArrayList<Factor>();

        // Create variables
        VariableFactor<Factor> var1 = new VariableFactor<Factor>();
        var1.setIdentity(var1);
        factors.add(var1);
        VariableFactor<Factor> var2 = new VariableFactor<Factor>();
        var2.setIdentity(var2);
        factors.add(var2);
        VariableFactor<Factor> var3 = new VariableFactor<Factor>();
        var3.setIdentity(var3);
        factors.add(var3);

        // Create utility factors
        IndependentFactor<Factor> u1 = new IndependentFactor<Factor>();
        u1.setPotential(var1, -2);
        u1.setIdentity(u1);
        factors.add(u1);
        IndependentFactor<Factor> u2 = new IndependentFactor<Factor>();
        u2.setPotential(var2, -6);
        u2.setIdentity(u2);
        factors.add(u2);
        IndependentFactor<Factor> u3 = new IndependentFactor<Factor>();
        u3.setPotential(var3, 15);
        u3.setIdentity(u3);
        factors.add(u3);

        // Create factors
        MediatorFactor mediator1 = new MediatorFactor();
        mediator1.setIdentity(mediator1);
        mediator1.setNElementsA(1);
        factors.add(mediator1);
        MediatorFactor mediator2 = new MediatorFactor();
        mediator2.setIdentity(mediator2);
        mediator2.setNElementsA(1);
        factors.add(mediator2);

        // Set neighbors
        var1.addNeighbor(mediator1);
        mediator1.addNeighbor(var1);
        var1.addNeighbor(u1);
        u1.addNeighbor(var1);

        var2.addNeighbor(mediator1);
        mediator1.addNeighbor(var2);
        var2.addNeighbor(mediator2);
        mediator2.addNeighbor(var2);
        var2.addNeighbor(u2);
        u2.addNeighbor(var2);

        var3.addNeighbor(mediator2);
        mediator2.addNeighbor(var3);
        var3.addNeighbor(u3);
        u3.addNeighbor(var3);

        return factors;
    }
//    public List<Factor> parseProblemFile(String problemFile) throws FileNotFoundException {
//        List<Factor> factors = new ArrayList<Factor>();
//
//        // Create variables
//        VariableFactor<Factor> var1 = new VariableFactor<Factor>();
//        var1.setIdentity(var1);
//        factors.add(var1);
//        VariableFactor<Factor> var2 = new VariableFactor<Factor>();
//        var2.setIdentity(var2);
//        factors.add(var2);
//        VariableFactor<Factor> var3 = new VariableFactor<Factor>();
//        var3.setIdentity(var3);
//        factors.add(var3);
//
//        // Create utility factors
//        IndependentFactor<Factor> u1 = new IndependentFactor<Factor>();
//        u1.setPotential(var1, -2);
//        u1.setIdentity(u1);
//        factors.add(u1);
//        IndependentFactor<Factor> u2 = new IndependentFactor<Factor>();
//        u2.setPotential(var2, -6);
//        u2.setIdentity(u2);
//        factors.add(u2);
//        IndependentFactor<Factor> u3 = new IndependentFactor<Factor>();
//        u3.setPotential(var3, 5);
//        u3.setIdentity(u3);
//        factors.add(u3);
//
//        // Create factors
//        MediatorFactor mediator = new MediatorFactor();
//        mediator.setIdentity(mediator);
//        mediator.setNElementsA(1);
//        factors.add(mediator);
//
//        // Set neighbors
//        var1.addNeighbor(mediator);
//        mediator.addNeighbor(var1);
//        var1.addNeighbor(u1);
//        u1.addNeighbor(var1);
//
//        var2.addNeighbor(mediator);
//        mediator.addNeighbor(var2);
//        var2.addNeighbor(u2);
//        u2.addNeighbor(var2);
//
//        var3.addNeighbor(mediator);
//        mediator.addNeighbor(var3);
//        var3.addNeighbor(u3);
//        u3.addNeighbor(var3);
//
//        return factors;
//    }
}
