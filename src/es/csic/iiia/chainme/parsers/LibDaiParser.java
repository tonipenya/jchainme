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
package es.csic.iiia.chainme.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.csic.iiia.chainme.factors.MediatorFactor;
import es.csic.iiia.chainme.factors.ParticipantFactor;
import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.IndependentFactor;
import java.util.Locale;

/**
*
* @author Toni Penya-Alba <tonipenya@iiia.csic.es>
*/
public class LibDaiParser implements ProblemParser {
    private List<Factor> factors = new ArrayList<Factor>();

    @Override
    public List<Factor> parseProblemFile(String problemFile) {
        List<ParticipantFactor> vars = new ArrayList<ParticipantFactor>();
        Scanner scanner;
        try {
            scanner = new Scanner(new File(problemFile));
            scanner.useLocale(Locale.ENGLISH);
        } catch (FileNotFoundException e) {
            throw new ParserException(e);
        }

        final int nFactors = scanner.nextInt();

        for (int factorIdx = 0; factorIdx < nFactors; factorIdx++) {
            final int nVars = scanner.nextInt();

            if (nVars == 1) {
                final int varIdx = scanner.nextInt();
                ParticipantFactor var = new ParticipantFactor();
                initialize(var);
                vars.add(varIdx, var);

                // Skip the rest of the line
                scanner.nextLine();
                // Skip cardinality
                scanner.nextLine();
                // Skip number of defined states
                scanner.nextLine();
                // Skip value for inactive state
                scanner.nextLine();
                // Skip active state index
                scanner.next();

                IndependentFactor<Factor> utilityFactor = new IndependentFactor<Factor>();
                initialize(utilityFactor);

                double utility = scanner.nextDouble();
                makeNeighbors(var, utilityFactor);
                utilityFactor.setPotential(var, utility);
            } else {
                // The last variable is not an actual variable. It is used to
                // denote how many sellers has the good.
                MediatorFactor mediator = new MediatorFactor();
                initialize(mediator);

                for (int i = 0; i < nVars - 1; i++) {
                    final int varIdx = scanner.nextInt();
                    makeNeighbors(mediator, vars.get(varIdx));
                }

                // Skip last variable
                scanner.nextLine();

                // Skip cardinalities. Stop at the last one to get the number of sellers.
                for (int i = 0; i < nVars - 1; i++) {
                    scanner.nextInt();
                }

                final int nSellers = scanner.nextInt();
                mediator.setNElementsA(nSellers);

                // Skip the rest of the line
                scanner.nextLine();
                // Skip cardinalities
                scanner.nextLine();
                // Skip number of defined states
                scanner.nextLine();
            }
        }

        return factors;
    }

    private void makeNeighbors(Factor<Factor> f1, Factor<Factor> f2) {
        f1.addNeighbor(f2);
        f2.addNeighbor(f1);
    }

    private void initialize(Factor<Factor> f) {
        f.setIdentity(f);
        factors.add(f);
    }

}
