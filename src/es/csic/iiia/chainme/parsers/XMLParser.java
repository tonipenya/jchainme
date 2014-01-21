package es.csic.iiia.chainme.parsers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import es.csic.iiia.chainme.factors.MediatorFactor;
import es.csic.iiia.chainme.factors.ParticipantFactor;
import es.csic.iiia.maxsum.Factor;
import es.csic.iiia.maxsum.factors.IndependentFactor;

public class XMLParser implements ProblemParser {
    private static final String GOOD = "Good";
    private static final String TRANSFORMATION = "Transformation";
    private static final String INPUT_GOOD = "InputGood";
    private static final String OUTPUT_GOOD = "OutputGood";
    private static final String ATOMIC_BID = "AtomicBid";
    private static final String BID_TRANSFORMATION = "BidTransformation";
    private static final QName ID_REF_QNAME = new QName("idRef");
    private static final QName PRICE_QNAME = new QName("price");

    private List<Factor> factors = new ArrayList<Factor>();

    @Override
    public List<Factor> parseProblemFile(String problemFile) {

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r;
        try {
            r = factory.createXMLEventReader(problemFile,
                    new FileInputStream(problemFile));
        } catch (FileNotFoundException e) {
            throw new ParserException(e);
        } catch (XMLStreamException e) {
            throw new ParserException(e);
        }

        List<ParticipantFactor> participants = new ArrayList<ParticipantFactor>();
        List<MediatorFactor> mediators = new ArrayList<MediatorFactor>();
        List<Integer> nSellers = new ArrayList<Integer>();

        ParticipantFactor participant = new ParticipantFactor();
        double cost = 0d;

        while (r.hasNext()) {
            XMLEvent event;
            try {
                event = r.nextEvent();
            } catch (XMLStreamException e) {
                throw new ParserException(e);
            }

            if (XMLEvent.START_ELEMENT != event.getEventType()) {
                continue;
            }

            final StartElement startElement = event.asStartElement();
            final String name = startElement.getName().getLocalPart();

            if (GOOD.equals(name)) {
                MediatorFactor mediator = new MediatorFactor();
                mediators.add(mediator);
                nSellers.add(0);
                initialize(mediator);
            } else if (TRANSFORMATION.equals(name)) {
                participant = new ParticipantFactor();
                participants.add(participant);
                initialize(participant);
            } else if (INPUT_GOOD.equals(name)) {
                final int mediatorId = getIntValue(startElement, ID_REF_QNAME) - 1;
                makeNeighbors(participant, mediators.get(mediatorId));
            } else if (OUTPUT_GOOD.equals(name)) {
                final int mediatorId = getIntValue(startElement, ID_REF_QNAME) - 1;
                nSellers.set(mediatorId, nSellers.get(mediatorId) + 1);
                makeNeighbors(participant, mediators.get(mediatorId));
            } else if (ATOMIC_BID.equals(name)) {
                cost = getDoubleValue(startElement, PRICE_QNAME);
            } else if (BID_TRANSFORMATION.equals(name)) {
                final int nParticipants = participants.size();
                final int participantId = getIntValue(startElement,
                        ID_REF_QNAME) - nParticipants + 1;
                final ParticipantFactor lParticipant = participants
                        .get(participantId);
                IndependentFactor<Factor> utilityFactor = new IndependentFactor<Factor>();
                utilityFactor.setPotential(lParticipant, cost);
                initialize(utilityFactor);
                makeNeighbors(utilityFactor, lParticipant);
            }
        }

        final int nMediators = mediators.size();
        for (int i = 0; i < nMediators; i++) {
            final MediatorFactor mediator = mediators.get(i);
            mediator.setNElementsA(nSellers.get(i));
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

    private int getIntValue(StartElement element, QName attribute) {
        return Integer
                .valueOf(element.getAttributeByName(attribute).getValue());
    }

    private double getDoubleValue(StartElement element, QName attribute) {
        return Double.valueOf(element.getAttributeByName(attribute).getValue());
    }
}
