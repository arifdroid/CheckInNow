package com.example.checkinnow;

public class Employee {

    private String name;
    private String phone;
    private String rating;
    private String imageurl;
    private String ref_score_card;
    private String uid;

    //this for firestore use
    public Employee() {

    }

    public Employee(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Employee(String name, String phone, String rating, String imageurl, String ref_score_card, String uid) {
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.imageurl = imageurl;
        this.ref_score_card = ref_score_card;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getRef_score_card() {
        return ref_score_card;
    }

    public void setRef_score_card(String ref_score_card) {
        this.ref_score_card = ref_score_card;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
