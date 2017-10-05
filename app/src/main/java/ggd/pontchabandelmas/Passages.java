package ggd.pontchabandelmas;

import android.content.Context;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class Passages {

    public static List<Passage> read(Context context) throws IOException, ParseException {
        Date startDate = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        FileInputStream fileInputStream = context.openFileInput(MainActivity.LOCAL_FILE);
        return new PrevisionsParser().parse(startDate, fileInputStream);
    }

}
