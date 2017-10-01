package ggd.pontchabandelmas;

import java.util.Date;
import java.util.Objects;

class Passage {

    public final String boat;
    public final Date closing;
    public final Date reopening;
    public final String type;

    public Passage(String boat, Date closing, Date reopening, String type) {
        this.boat = boat;
        this.closing = closing;
        this.reopening = reopening;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passage passage = (Passage) o;
        return Objects.equals(boat, passage.boat) &&
                Objects.equals(closing, passage.closing) &&
                Objects.equals(reopening, passage.reopening) &&
                Objects.equals(type, passage.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boat, closing, reopening, type);
    }

}
