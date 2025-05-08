package core;


public class PuntCarrega {
    private TipusPuntCarrega tipusCarga;
    private boolean ocupat;

    public PuntCarrega(TipusPuntCarrega tipusCarga) {
        this.tipusCarga = tipusCarga;
        this.ocupat = false;
    }


    public TipusPuntCarrega getTipusCarga() {
        return this.tipusCarga;
    }

    public void setTipusCarga(TipusPuntCarrega tipusCarga) {
        this.tipusCarga = tipusCarga;
    }

    public boolean isOcupat() {
        return this.ocupat;
    }

    public boolean getOcupat() {
        return this.ocupat;
    }

    public void setOcupat(boolean ocupat) {
        this.ocupat = ocupat;
    }

}
