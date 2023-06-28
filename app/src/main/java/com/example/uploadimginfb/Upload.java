package com.example.uploadimginfb;

public class Upload {

    private String name;
    private String imageUrl;

    public Upload(){
        //EMPTY CONSTRUCTOR IS NEEDED
    }

    public Upload(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUri) {
        this.imageUrl = imageUrl;
    }


}
