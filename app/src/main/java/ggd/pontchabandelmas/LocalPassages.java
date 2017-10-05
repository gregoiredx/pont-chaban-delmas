package ggd.pontchabandelmas;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class LocalPassages {

    private static final String LOCAL_FILE = "previsions.csv";

    private Context context;

    public LocalPassages(Context context) {
        this.context = context;
    }

    public List<Passage> read() throws IOException, ParseException {
        Date startDate = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        FileInputStream fileInputStream = context.openFileInput(LOCAL_FILE);
        return new PrevisionsParser().parse(startDate, fileInputStream);
    }

    public void write(byte[] data) throws IOException {
        FileOutputStream fileOutputStream = context.openFileOutput(LOCAL_FILE, Context.MODE_PRIVATE);
        fileOutputStream.write(data);
        fileOutputStream.close();
    }

}
