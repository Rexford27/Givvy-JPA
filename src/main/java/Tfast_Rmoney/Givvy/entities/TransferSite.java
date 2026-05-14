package Tfast_Rmoney.Givvy.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transfer_sites")
public class TransferSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(name = "address_one")
    private String addressOne;

    @Column(name = "address_two")
    private String addressTwo;

    private String city;

    private String state;

    private String zip;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    public TransferSite() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }   

    public String getAddressOne() {
        return addressOne;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }   

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }   

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }   

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }   

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
