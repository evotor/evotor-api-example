package ru.qualitylab.evotor.evotortest6;

/**
 * Created by power on 10.10.2017.
 */

public class CustomEditObject {
    private String uuid;
    private String name;
    private String price;
    private String qty;

    public CustomEditObject() {
    }

    public CustomEditObject(String uuid, String name, String price, String qty) {
        this.uuid = uuid;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
