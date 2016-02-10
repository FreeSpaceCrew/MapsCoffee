package classes;

public class BoundingBox {
    private double n;
    private double s;
    private double w;
    private double e;

    public BoundingBox() {

    }

    public BoundingBox(double n, double s, double w, double e) {
        this.n = n;
        this.s = s;
        this.w = w;
        this.e = e;
    }

    public BoundingBox(String n, String s, String w, String e) {
        this.n = Double.valueOf(n);
        this.s = Double.valueOf(s);
        this.w = Double.valueOf(w);
        this.e = Double.valueOf(e);
    }

    public Double getE() {
        return e;
    }

    public Double getN() {
        return n;
    }

    public Double getS() {
        return s;
    }

    public Double getW() {
        return w;
    }

    public void setE(double e) {
        this.e = e;
    }

    public void setN(double n) {
        this.n = n;
    }

    public void setS(double s) {
        this.s = s;
    }

    public void setW(double w) {
        this.w = w;
    }

    public void setE(String e) {
        this.e = Double.valueOf(e);
    }

    public void setN(String n) {
        this.n = Double.valueOf(n);
    }

    public void setS(String s) {
        this.s = Double.valueOf(s);
    }

    public void setW(String w) {
        this.w = Double.valueOf(w);
    }


}
