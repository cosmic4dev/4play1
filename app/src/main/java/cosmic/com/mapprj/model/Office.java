package cosmic.com.mapprj.model;

import java.util.ArrayList;

public class Office extends ArrayList<Office> {
    public String name;
    public String address;
    public String call;
    public String geopoint;
    public String image;
    public String url;
    public double distance;

    public Office(){

    }

    public Office(String name, String address, String call, String geopoint, String image, String url) {
        this.name = name;
        this.address = address;
        this.call = call;
        this.geopoint = geopoint;
        this.image = image;
        this.url = url;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
