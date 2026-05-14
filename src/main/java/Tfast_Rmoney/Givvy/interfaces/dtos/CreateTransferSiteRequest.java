package Tfast_Rmoney.Givvy.interfaces.dtos;

public class CreateTransferSiteRequest {

    private String name;
    private String addressOne;
    private String addressTwo;
    private String city;
    private String state;
    private String zip;
    private String imageUrl;
    private String description;

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