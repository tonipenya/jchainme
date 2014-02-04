package es.csic.iiia.chainme.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.chainme.factors.ParticipantFactor;
import es.csic.iiia.maxsum.factors.AbstractTwoSidedFactor;
import es.csic.iiia.maxsum.factors.IndependentFactor;

public class DotExporter {
    private static final String PARTICIANT_SHAPE = "oval";
    private static final String DEFAULT_SHAPE = "box";
    private static final String DIRECTED_DELIMITER = "->";
    private static final String UNDIRECTED_DELIMITER = "--";

    public static void export(String fileName, List<Factor> factors)
            throws IOException {
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter out = new BufferedWriter(fw);

        out.write("digraph {");
        out.newLine();

        for (Factor<Factor> f : factors) {
            out.write('"' + f.toString() + '"');
            if (f instanceof ParticipantFactor) {
                out.write(" [shape=" + PARTICIANT_SHAPE + "];");
                out.newLine();
            } else {
                out.write(" [shape=" + DEFAULT_SHAPE + "];");
                out.newLine();
            }
        }

        for (Factor<Factor> f : factors) {
            if (f instanceof AbstractTwoSidedFactor) {
                final AbstractTwoSidedFactor aFactor = (AbstractTwoSidedFactor) f;
                final int nElementsA = aFactor.getnElementsA();

                for (int i = 0; i < nElementsA; i++) {
                    Object n = aFactor.getNeighbors().get(i);
                    out.write(getDirectedString(n, f));
                    out.newLine();
                }

                final int nNeighbors = aFactor.getNeighbors().size();
                for (int i = nElementsA; i < nNeighbors; i++) {
                    Object n = aFactor.getNeighbors().get(i);
                    out.write(getDirectedString(f, n));
                    out.newLine();
                }
            } else if (f instanceof IndependentFactor) {
                for (Factor n : f.getNeighbors()) {
                    out.write(getUndirectedString(f, n));
                    out.newLine();
                }
            }
        }

        out.write("}");
        out.close();
    }

    private static String getUndirectedString(Object from, Object to) {
        return getEdge(from, to, UNDIRECTED_DELIMITER);
    }

    private static String getDirectedString(Object from, Object to) {
        return getEdge(from, to, DIRECTED_DELIMITER);
    }

    private static String getEdge(Object from, Object to, String delimiter) {
        StringBuilder sb = new StringBuilder();
        sb.append('"').append(from).append('"').append(delimiter).append('"');
        sb.append(to).append('"').append(";");
        return sb.toString();
    }
}
