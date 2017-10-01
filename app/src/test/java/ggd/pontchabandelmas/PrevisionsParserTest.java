package ggd.pontchabandelmas;

import org.junit.Test;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class PrevisionsParserTest {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z", Locale.FRANCE);

    @Test
    public void parse() throws Exception {
        InputStream csv = PrevisionsParserTest.class.getClassLoader().getResourceAsStream("previsions.csv");
        Date startDate = parse("01/10/2017 14:00:00 +0200");

        List<Passage> passages = new PrevisionsParser().parse(startDate, csv);

        assertEquals(13, passages.size());
        assertEquals("Totale", passages.get(0).type);
        assertEquals(parse("01/10/2017 14:34:00 +0200"), passages.get(0).closing);
        assertEquals(parse("01/10/2017 18:07:00 +0200"), passages.get(0).reopening);
        assertEquals("Totale", passages.get(0).type);
    }


    @Test
    public void parseNonAscii() throws Exception {
        InputStream csv = PrevisionsParserTest.class.getClassLoader().getResourceAsStream("previsions.csv");
        Date startDate = parse("07/06/2017 09:00:00 +0200");

        List<Passage> passages = new PrevisionsParser().parse(startDate, csv);

        assertEquals("Partielle : Réduction à une file sens rue Lucien Faure vers quai de Brazza + voie piétonne correspondante fermée", passages.get(0).type);
    }

    private static Date parse(String date) throws ParseException {
        return DATE_FORMAT.parse(date);
    }
}
