package ggd.pontchabandelmas;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

class PrevisionsParser {

    private final SimpleDateFormat simpleDateFormat;

    public PrevisionsParser() {
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyyhh:mm", Locale.FRANCE);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
    }

    public List<Passage> parse(Date startDate, InputStream inputStream) throws IOException, ParseException {
        CSVParser parser = new CSVParser(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1),
                CSVFormat.DEFAULT.withHeader().withDelimiter(';').withRecordSeparator('\n'));
        List<Passage> passages = new ArrayList<>();
        for (CSVRecord record : parser.getRecords()) {
            String date = record.get("Date passage");
            Date closing = simpleDateFormat.parse(date + record.get("Fermeture a la circulation"));
            Date reopening = simpleDateFormat.parse(date + record.get("Re-ouverture a la circulation"));
            if(closing.equals(startDate) || closing.after(startDate)) {
                passages.add(new Passage(
                        record.get("Bateau"),
                        closing,
                        reopening,
                        record.get("Type de fermeture")
                ));
            }
        }
        return passages;
    }

}
