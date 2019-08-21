package com.azimpathan.whatsstat;

/**
 * Created by AZIM_2 on 18/12/2017.
 */

public class User {
    private String name;
    private String pic;
    private String thumb;
    private String uploadTime;
    User(String uname,String upic,String uthumb,String uploadTime)
    {
        this.name=uname;
        this.pic=upic;
        this.thumb=uthumb;
        this.uploadTime=uploadTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}