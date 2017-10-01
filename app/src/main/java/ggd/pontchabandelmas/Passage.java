package ggd.pontchabandelmas;

class Passage {

    public final String boat;
    public final String date;
    public final String closing;
    public final String reopening;
    public final String type;

    public Passage(String boat, String date, String closing, String reopening, String type) {
        this.boat = boat;
        this.date = date;
        this.closing = closing;
        this.reopening = reopening;
        this.type = type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Passage{");
        sb.append("boat='").append(boat).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", closing='").append(closing).append('\'');
        sb.append(", reopening='").append(reopening).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
