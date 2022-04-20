package entity;

public class Bank {
    private long id;
    private double euro_amount;
    private double usd_amount;
    private double pln_amount;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getEuro_amount() {
        return euro_amount;
    }

    public void setEuro_amount(double euro_amount) {
        this.euro_amount = euro_amount;
    }

    public double getUsd_amount() {
        return usd_amount;
    }

    public void setUsd_amount(double usd_amount) {
        this.usd_amount = usd_amount;
    }

    public double getPln_amount() {
        return pln_amount;
    }

    public void setPln_amount(double pln_amount) {
        this.pln_amount = pln_amount;
    }
}
