package ggd.pontchabandelmas;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class PrevisionsParser {

    public List<Passage> parse(InputStream inputStream) throws IOException {
        CSVParser parser = new CSVParser(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1),
                CSVFormat.DEFAULT.withHeader().withDelimiter(';').withRecordSeparator('\n'));
        List<Passage> passages = new ArrayList<>();
        for (CSVRecord record : parser.getRecords()) {
            passages.add(new Passage(
                    record.get("Bateau"),
                    record.get("Date passage"),
                    record.get("Fermeture a la circulation"),
                    record.get("Re-ouverture a la circulation"),
                    record.get("Type de fermeture")
            ));
        }
        return passages;
    }

}
