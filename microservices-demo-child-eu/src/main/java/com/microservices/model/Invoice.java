package com.microservices.model;

/**
 * Created by stephen on 27/02/2016.
 */
public class Invoice extends DynamicFields {
    private Double amount;
    private String id;
    private Double tva;
    private String tvaIntraCom;
    private Header header;

    public Invoice() {
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTva() {
        return tva;
    }

    public void setTva(Double tva) {
        this.tva = tva;
    }

    public String getTvaIntraCom() {
        return tvaIntraCom;
    }

    public void setTvaIntraCom(String tvaIntraCom) {
        this.tvaIntraCom = tvaIntraCom;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "amount=" + amount +
                ", id='" + id + '\'' +
                ", tva=" + tva +
                ", tvaIntraCom='" + tvaIntraCom + '\'' +
                ", header=" + header +
                "} " + super.toString();
    }
}
