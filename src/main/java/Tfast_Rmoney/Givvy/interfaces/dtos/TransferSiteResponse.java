package Tfast_Rmoney.Givvy.interfaces.dtos;

import Tfast_Rmoney.Givvy.entities.TransferSite;

public class TransferSiteResponse {

    private int id;
    private String name;
    private String addressOne;
    private String addressTwo;
    private String city;
    private String state;
    private String zip;
    private String imageUrl;
    private String description;

    public TransferSiteResponse(TransferSite site) {
        this.id = site.getId();
        this.name = site.getName();
        this.addressOne = site.getAddressOne();
        this.addressTwo = site.getAddressTwo();
        this.city = site.getCity();
        this.state = site.getState();
        this.zip = site.getZip();
        this.imageUrl = site.getImageUrl();
        this.description = site.getDescription();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}