package es.csic.iiia.chainme;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.MaxOperator;
import es.csic.iiia.maxsum.Maximize;

@SuppressWarnings("unused")
public class MediatorFactorTest {

    @Test
    public void testFeasible1() {
        final double[] sellersMsgs = {0, 0, 0};
        final double[] buyersMsgs = {0, 0, 0};
        final boolean[] states = {true, true, false, true, true, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        Map<Factor, Boolean> allocation = getAllocation(mediator.getNeighbors(), states);

        assertEquals(true, mediator.isFeasible(allocation));
    }

    @Test
    public void testFeasible2() {
        final double[] sellersMsgs = {0, 0};
        final double[] buyersMsgs = {0, 0};
        final boolean[] states = {false, false, false, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        Map<Factor, Boolean> allocation = getAllocation(mediator.getNeighbors(), states);

        assertEquals(true, mediator.isFeasible(allocation));
    }

    @Test
    public void testFeasible3() {
        final double[] sellersMsgs = {};
        final double[] buyersMsgs = {0, 0};
        final boolean[] states = {false, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        Map<Factor, Boolean> allocation = getAllocation(mediator.getNeighbors(), states);

        assertEquals(true, mediator.isFeasible(allocation));
    }

    @Test
    public void testFeasibleUnfeasible1() {
        final double[] sellersMsgs = {0, 0};
        final double[] buyersMsgs = {0, 0};
        final boolean[] states = {true, true, true, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        Map<Factor, Boolean> allocation = getAllocation(mediator.getNeighbors(), states);

        assertEquals(false, mediator.isFeasible(allocation));
    }

    @Test
    public void testFeasibleUnfeasible2() {
        final double[] sellersMsgs = {};
        final double[] buyersMsgs = {0, 0};
        final boolean[] states = {true, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        Map<Factor, Boolean> allocation = getAllocation(mediator.getNeighbors(), states);

        assertEquals(false, mediator.isFeasible(allocation));

    }

    @Test
    public void testMend() {
        final double[] sellersMsgs = {-8, -2, -15};
        final double[] buyersMsgs = {5, 2, 0};
        final boolean[] initialStates = {true, true, false, true, false, false};
        final boolean[] expectedStates = {false, true, false, true, false, false};

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        final List<Factor> neighbors = mediator.getNeighbors();
        Map<Factor, Boolean> allocation = getAllocation(neighbors, initialStates);

        mediator.mend(allocation);
        System.out.println(allocation.values());

        assertEquals(getAllocation(neighbors, expectedStates), allocation);
    }

    @Test
    public void testNothingToMend() {
        final double[] sellersMsgs = {-8, -2, -15};
        final double[] buyersMsgs = {5, 2, 0};
        final boolean[] initialStates = {true, true, false, true, true, false};
        final boolean[] expectedStates = initialStates;

        MediatorFactor mediator = createMediator(sellersMsgs, buyersMsgs);
        final List<Factor> neighbors = mediator.getNeighbors();
        Map<Factor, Boolean> allocation = getAllocation(neighbors, initialStates);

        mediator.mend(allocation);
        System.out.println(allocation.values());

        assertEquals(getAllocation(neighbors, expectedStates), allocation);
    }

    private Map<Factor, Boolean> getAllocation(List<Factor> neighbors,
            boolean[] states) {
        Map<Factor, Boolean> allocation = new HashMap<Factor, Boolean>(neighbors.size());

        final int nStates = states.length;
        for(int i = 0; i < nStates; i++) {
            allocation.put(neighbors.get(i), states[i]);
        }

        return allocation;
    }

    private MediatorFactor createMediator(double[] sellersMsgs, double[] buyersMsgs) {
        final int nSellers = sellersMsgs.length;
        final int nBuyers = buyersMsgs.length;
        MaxOperator op = new Maximize();
        MediatorFactor mediator = new MediatorFactor();
        mediator.setNElementsA(nSellers);
        mediator.setMaxOperator(op);

        for(int i = 0; i < nSellers; i++) {
            Factor neighbor = mock(Factor.class);
            mediator.addNeighbor(neighbor);
            mediator.receive(sellersMsgs[i], neighbor);
        }

        for(int i = 0; i < nBuyers; i++) {
            Factor neighbor = mock(Factor.class);
            mediator.addNeighbor(neighbor);
            mediator.receive(buyersMsgs[i], neighbor);
        }

        return mediator;
    }

}
