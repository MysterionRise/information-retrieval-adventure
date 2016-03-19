package org.apache.lucene.search;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BM25FQueryTest {

    @Test
    public void testBM25FQuery() throws IOException {
        Directory index = new RAMDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
        IndexWriter w = new IndexWriter(index, config);
        w.addDocument(doc(0, "CNT-infused fiber as a self shielding wire for enhanced power transmission line",
                "A wire includes a plurality of carbon nanotube infused fibers in which the infused carbon nanotubes are aligned parallel to the fiber axes. An electromagnetic shield for a wire includes a plurality of carbon nanotube infused fibers, in which the infused carbon nanotubes are aligned radially about the fiber axes. The plurality of carbon nanotube infused fibers are arranged circumferentially about the wire with the fiber axes parallel to the wire. A self-shielded wire includes 1) a wire that includes a plurality of carbon nanotube infused fibers in which the infused carbon nanotubes are aligned parallel to the fiber axes; and 2) an electromagnetic shield that includes a plurality of carbon nanotube infused fibers in which the carbon nanotubes are aligned radially about the fiber axes. The axes of the carbon nanotube infused fibers of the wire and the carbon nanotube infused fibers of the electromagnetic shield share are parallel."));
        w.addDocument(doc(1, "Method for manufacturing electromagnetic interference shielding film ",
                "Provided is a method for manufacturing an electromagnetic interference (EMI) shielding film, including: (a) providing a single insulating layer on a first protective film, the insulating layer being made of an insulating layer composition including at least one resin selected from a thermoplastic resin and a thermosetting resin and at least one filler selected from a flame-retardant filler and an abrasion-resistant filler; (b) providing a metal layer on the insulating layer; (c) providing a conductive adhesive layer on the metal layer, the conductive adhesive layer being made from a conductive adhesive layer composition including at least one resin selected from a thermoplastic resin and a thermosetting resin and a conductive filler; and (d) providing a second protective film on the conductive adhesive layer, and an EMI shielding film manufactured by the method."));
        w.addDocument(doc(2, "Circuit board and connector shielding apparatus ",
                "A shielding apparatus connectable between a circuit board and a connector having pins connectable to the circuit board is provided. The shielding apparatus includes a wall element defining opposite sides and connection locations respectively connectable with the connector and the circuit board and is configured as an electro-magnetic interference skirt to block radiation emanating from and receivable by the pins."));
        w.addDocument(doc(3, "Interconnection structure in electromagnetic shielding",
                "A panel for an electromagnetic shield includes a light-weight, porous, electrically-conductive, fluid-permeable planar core layer defined between generally parallel first and second surfaces and a first face sheet laminated to the first surface of the core layer with rigidity properties superior to the rigidity properties of the core layer. The thickness of the first face sheet is substantially less than the thickness of the core layer. The core layer is made of metallic foam or a metal coating on an electrically-nonconductive, porous, nonmetallic substrate chosen from among nonwoven fibrous matting, paper, and open-cell nonmetallic foam. Also, the core layer may also may be made up of liberated branching metal nanostrands or a plurality of electrically-coupled, electrically-conductive particles, each taking the from of an electrically-nonconductive, nonmetallic substrate with a metal coating. The first face sheet includes a cured layer of resin and, distributed throughout the resin, electrically-conductive elements selected from among liberated branched metal nanostrands, metal wires, and metal meshes, in addition to fibers, woven fabric, nonwoven matting, or paper that are metal-coated."));
        w.addDocument(doc(4, "Vehicle-mounting charging apparatus and vehicle mounted therewith",
                "A vehicle-mounting charging apparatus includes a body case adapted to be mounted to a ceiling part of a vehicle, and a charging coil for contactless charging that is provided in the body case. A shielding part for shielding electromagnetic waves is provided on a lower side of the charging coil."));
        w.addDocument(doc(5, "High voltage shielding device and a system comprising the same",
                "A high voltage shielding device including a main body having an enclosing outer solid insulating wall, an outer electrode arranged on the solid insulating wall providing a first level of insulation to the outer electrode, and a first inner electrode which is uninsulated or has a coating providing a second level of insulation, which second level of insulation is lower than the first level of insulation. The first inner electrode is oriented relative the outer electrode in such a way that the first inner electrode mainly shields a component of an electric field which is perpendicular to a component of an electric field mainly shielded by the outer electrode."));
        w.addDocument(doc(6, "Electronics compartment",
                "An electronics compartment with a component space which is sealed off from a surrounding environment by walls, including an internal panel arranged in the component space along a wall of the electronics compartment for delimiting an internal channel between the wall and the internal panel. Air flowing in the internal channel can improve transfer of heat through the wall, and consequently, cools electric components in the component space."));
        w.addDocument(doc(7, "Inverter, sealing air duct and heat dissipation system",
                "An inverter, a sealing air duct, and a heat dissipation system are disclosed. The inverter includes: an enclosure having a first cavity and a second cavity that are isolated from each other and sealed; a magnetic conversion circuit including magnetic elements that is arranged in the first cavity; a power conversion circuit including power tubes that is arranged in the second cavity; a heatsink arranged at the bottom of the enclosure and located outside the first cavity and the second cavity; a sealing air duct arranged outside the second cavity, where the columnar pipeline is sealed at two ends, the bottom surface is formed by a side wall of the second cavity or a substrate of the heatsink, and the bottom surface includes an air inlet and an air outlet to communicate with the second cavity and the at least one columnar pipeline."));
        w.addDocument(doc(8, "Information technology equipment cooling method",
                "According to one embodiment, a system for removing heat from a rack of information technology equipment may include a sidecar indoor air to liquid heat exchanger that cools air utilized by the rack of information technology equipment to cool the rack of information technology equipment. The system may also include a liquid to liquid heat exchanger and an outdoor heat exchanger. The system may further include configurable pathways to connect and control fluid flow through the sidecar heat exchanger, the liquid to liquid heat exchanger, the rack of information technology equipment, and the outdoor heat exchanger based upon ambient temperature and/or ambient humidity to remove heat generated by the rack of information technology equipment."));
        w.addDocument(doc(9, "Rapidly assembling/disassembling device and electronic equipment ",
                "The disclosure discloses a rapidly assembling/disassembling device including a fixing frame, partitions, pushing brackets, and a fastening member. The fixing frame includes a first side plate and a second side plate. Fan modules are sequentially arranged between the first and second side plates. The second side plate abuts against a second side of the adjacent fan module. Each of the partitions abuts against the second side of the corresponding fan module. Each of the pushing brackets is pivotally connected in the fixing frame. Each of the pushing brackets includes a pushing arm. The fastening member is disposed on the first side plate. When the fastening member pushes the adjacent pushing bracket to abut against a first side of the adjacent fan module, each of the other pushing brackets is pushed by the corresponding pushing arm to abut against the first side of the corresponding fan module."));
        w.addDocument(doc(10, "Transverse cooling system and method",
                "A system and method for cooling a plurality of electronics cabinets having horizontally positioned electronics assemblies. The system includes at least one blower configured to direct air horizontally across the electronics assemblies, and at least one intercooler configured to extract heat from the air flow such that the system is room neutral, meaning that the ambient temperature remains constant during operation of the system. A plurality of chassis backplanes and power supplies may also include an intercooler, wherein the intercoolers are electronically controlled such that the system is room neutral."));
        w.addDocument(doc(11, "Backplane structure and server system utilizing the same",
                "A server system utilizing a backplane structure comprises first and second hard disk modules; a first backplane comprising a first wiring board comprising air vents and passive components and a second wiring board connected to a bottom portion of the first wiring board at a first angle and comprising first active components; and a second backplane comprising a third wiring board comprising second air vents and second passive components and a fourth wiring board connected to a bottom portion of the third wiring board at a second angle and comprising second active components thereon; wherein the first and second backplanes are disposed between the first and second hard disk modules; the first wiring board is directly corresponding to the first hard disk module; the third wiring board is directly corresponding to the second hard disk module; the first backplane is higher than the second backplane."));
        w.addDocument(doc(12, "Alternative data center building designs",
                "A multi-floor data center, comprising in one implementation, a plurality of floors; a first set of server racks disposed about a first vertical center axis on each floor, the first set of server racks formed in a substantially closed shape, with a substantially vertical open center comprising a first airflow plenum at least for air flow; a first opening in each of the floors, with the first opening aligned with the substantially vertical first airflow plenum on it respective floor, wherein the substantially vertical first airflow plenums on the floors are aligned for communication through the first openings in the floors; outer wall; a roof with a roof opening therein."));
        w.addDocument(doc(13, "Silicon-based heat-dissipation device for heat-generating devices",
                "Embodiments of a silicon-based heat-dissipation device and an apparatus including a silicon-based heat-dissipation device are described. In one aspect, an apparatus includes a silicon-based heat-dissipation device which includes a base portion and a protrusion portion. The base portion has a first primary side and a second primary side opposite the first primary side. The protrusion portion is on the first primary side of the base portion and protruding therefrom. The protrusion portion includes multiple fins. Each of at least two immediately adjacent fins of the fins of the protrusion portion has a tapered profile in a cross-sectional view with a first width near a distal end of the respective fin being less than a second width at a base of the respective fin near the base portion of the heat-dissipation device."));
        w.addDocument(doc(14, "Electronic device provided with socket for card-shaped component",
                "The electronic device according to the Present Disclosure is an electronic device provided with a heat-dissipating portion and an electronic component socket that can accommodate, therein, an installable electronic component, wherein the electronic component socket has a thermal connecting portion for connecting thermally to the electronic component, and, when the electronic component is operating, the electronic component and the heat-dissipating portion are connected thermally through the thermal connecting portion."));

        w.commit();
        w.close();

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25FSimilarity());


        final Map<String, Float> weights = new HashMap<>();
        weights.put("abs", 1.5f);
        weights.put("title", 0.5f);

        final Map<String, Float> norms = new HashMap<>();
        norms.put("abs", 1.5f);
        norms.put("title", 0.5f);

        final List<Query> queries = new ArrayList<>();
        queries.add(new TermBM25FQuery(new Term("title", "system")));
        queries.add(new TermBM25FQuery(new Term("abs", "system")));
        final BM25FQuery query = new BM25FQuery(queries, weights, norms);

        final TopScoreDocCollector collector = TopScoreDocCollector.create(5, true);
        searcher.search(query, collector);
        ScoreDoc[] bScoreDocs = collector.topDocs().scoreDocs;
        double[] scores = new double[bScoreDocs.length];
        for (int i = 0; i < bScoreDocs.length; ++i) {
            scores[i] = bScoreDocs[i].score;
            System.out.println(bScoreDocs[i].doc + " " + scores[i]);
        }
        System.out.println();
        assertEquals(5, collector.getTotalHits());

        final List<Query> queries2 = new ArrayList<>();
        queries2.add(new TermBM25FQuery(new Term("title", "device")));
        queries2.add(new TermBM25FQuery(new Term("abs", "device")));
        final BM25FQuery query2 = new BM25FQuery(queries2, weights, norms);

        final TopScoreDocCollector collector2 = TopScoreDocCollector.create(5, true);
        searcher.search(query2, collector2);
        ScoreDoc[] bScoreDocs2 = collector2.topDocs().scoreDocs;
        double[] scores2 = new double[bScoreDocs2.length];
        for (int i = 0; i < bScoreDocs2.length; ++i) {
            scores2[i] = bScoreDocs2[i].score;
            System.out.println(bScoreDocs2[i].doc + " " + scores2[i]);
        }
        assertEquals(5, collector.getTotalHits());
    }

    private static Document doc(int id, String title, String abs) throws IOException {
        Document doc = new Document();
        doc.add(new IntField("id", id, Field.Store.YES));
        doc.add(new TextField("title", title, Field.Store.YES));
        doc.add(new TextField("abs", abs, Field.Store.YES));
        return doc;
    }

}