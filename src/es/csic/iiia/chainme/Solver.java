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

import es.csic.iiia.chainme.communication.ParallelCommunicationAdapter;
import es.csic.iiia.chainme.communication.SequentialCommunicationAdapter;
import es.csic.iiia.chainme.parser.ExampleProblem;
import es.csic.iiia.chainme.parser.LibDaiParser;
import es.csic.iiia.chainme.parser.ProblemParser;
import es.csic.iiia.maxsum.Factor;
import gnu.getopt.Getopt;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Toni Penya-Alba <tonipenya@iiia.csic.es>
 */
public class Solver {
    final static String PROBLEM_FILE = "examples/5000participants_2500goods.fg";
    final static String USAGE =
    "Usage: jchainme [options] <problem-file>\n" +
    "Options\n" +
    "   -s[r]       Sequential message update. Optional parameter 'r' for random order.\n" +
    "   -p          Parallel message update.\n" +
    "   -d value    Damp messages by a value.\n" +
    "   -i value    Set the maximum number of iterations.\n" +
    "   -h          Display this message.";

    /**
     * @param args
     */
    public static void main(String[] args) {
        Getopt g = new Getopt("jchainme", args, "ps::d:i:h");
        Configuration conf = new Configuration();

        // Parse command line options.
        int c;
        String arg;
        while ((c = g.getopt()) != -1) {
            switch (c) {
            case 'd':
                conf.dampingFactor = Double.valueOf(g.getOptarg());
                break;

            case 's':
                arg = g.getOptarg();
                conf.com = new SequentialCommunicationAdapter();
                conf.shuffleFactors = arg != null;
                break;

            case 'p':
                conf.com = new ParallelCommunicationAdapter();
                break;

            case 'i':
                arg = g.getOptarg();
                conf.maxIters = Integer.valueOf(arg);
                break;

            case '?':
                System.err.println("The option '" + (char) g.getOptopt()
                        + "' is not valid");
            case 'h':
                System.out.println(USAGE);
                System.exit(-1);
            default:
                printUsageAndExit(null);
                break;
            }
        }

        // Parse input file.
        if (g.getOptind() >= args.length) {
            printUsageAndExit("No file specified.");
        }
        System.out.println("Parsing problem");
        String problemFile = args[g.getOptind()];
        ProblemParser parser = new LibDaiParser();
//         ProblemParser parser = new ExampleProblem();
        List<Factor> factors = null;

        try {
            factors = parser.parseProblemFile(problemFile);
        } catch (FileNotFoundException ex) {
            printUsageAndExit("The file " + problemFile + " does not exist.");
        }

        // Initialize the factors and beliefs.
        System.out.println("Initializing...");
        Algorithm algorithm = new Chainme(conf, factors);

        // Run binary max-sum.
        System.out.println("Running...");
        long tic = System.currentTimeMillis();
        algorithm.solve();
        long toc = System.currentTimeMillis();
        double time = (toc - tic)/1000d;

        // Output results.
        boolean[] allocation = algorithm.getAllocation();
        double objective = algorithm.getObjective();
        System.out.println("nParticipants: " + algorithm.getNParticipants());
        System.out.println("Iters: " + algorithm.getIters());
        System.out.println("OBJECTIVE: " + objective);
//        printAllocation(allocation);

        tic = System.currentTimeMillis();
        algorithm.pruneAllocation();
        toc = System.currentTimeMillis();
        time += (toc - tic)/1000d;
        System.out.println("Time: " + time);

        objective = algorithm.getObjective();
        System.out.println("OBJECTIVE-D: " + objective);
        allocation = algorithm.getAllocation();
        printAllocation(allocation);
    }

    private static void printAllocation(boolean[] allocation) {
        String allocationString = Arrays.toString(allocation).replace(",", "")
                .replace("true", "1").replace("false", "0");

        System.out.println(allocationString);
    }

    private static void printUsageAndExit(String message) {
        System.err.println(message);
        System.err.println(USAGE);
        System.exit(-1);
    }
}
