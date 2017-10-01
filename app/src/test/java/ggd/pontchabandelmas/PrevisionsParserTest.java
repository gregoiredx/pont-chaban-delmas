package ggd.pontchabandelmas;

import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PrevisionsParserTest {

    @Test
    public void parse() throws Exception {
        InputStream csv = PrevisionsParserTest.class.getClassLoader().getResourceAsStream("previsions.csv");

        List<Passage> passages = new PrevisionsParser().parse(csv);

        assertEquals(passages.get(0).boat, "BELEM");
        assertEquals(passages.get(0).date, "07/01/2017");
        assertEquals(passages.get(0).closing, "13:19");
        assertEquals(passages.get(0).reopening, "14:57");
        assertEquals(passages.get(0).type, "Totale");

        assertEquals(passages.get(130).boat, "MAINTENANCE");
        assertEquals(passages.get(130).date, "10/12/2017");
        assertEquals(passages.get(130).closing, "23:00");
        assertEquals(passages.get(130).reopening, "05:00");
        assertEquals(passages.get(130).type, "Totale");

        assertEquals(passages.get(2).type, "Partielle : Réduction à une file sens rue Lucien Faure vers quai de Brazza + voie piétonne correspondante fermée");
    }
}
