package entity;

public class Rates {
   private double commissionRate = 0.05;
   private double euroToPln;
   private double plnToEuro;
   private double plnToUsd;
   private double usdToPln;
   private double usdToEuro;
   private double euroToUsd;

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }

    public double getEuroToPln() {
        return euroToPln;
    }

    public void setEuroToPln(double euroToPln) {
        this.euroToPln = euroToPln;
    }

    public double getPlnToEuro() {
        return plnToEuro;
    }

    public void setPlnToEuro(double plnToEuro) {
        this.plnToEuro = plnToEuro;
    }

    public double getPlnToUsd() {
        return plnToUsd;
    }

    public void setPlnToUsd(double plnToUsd) {
        this.plnToUsd = plnToUsd;
    }

    public double getUsdToPln() {
        return usdToPln;
    }

    public void setUsdToPln(double usdToPln) {
        this.usdToPln = usdToPln;
    }

    public double getUsdToEuro() {
        return usdToEuro;
    }

    public void setUsdToEuro(double usdToEuro) {
        this.usdToEuro = usdToEuro;
    }

    public double getEuroToUsd() {
        return euroToUsd;
    }

    public void setEuroToUsd(double euroToUsd) {
        this.euroToUsd = euroToUsd;
    }
}
