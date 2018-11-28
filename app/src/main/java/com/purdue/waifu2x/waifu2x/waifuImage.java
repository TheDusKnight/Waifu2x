package com.purdue.waifu2x.waifu2x;

public final class waifuImage {
//the table for the SQLI+ite Database
    private int _id;
    private String _imagePath;

    public waifuImage() {
    }

    public waifuImage(int id, String imagePath) {
        this._id = id;
        this._imagePath = imagePath;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }

    public void set_imagePath(String _imagePath) {
        this._imagePath = _imagePath;
    }

    public String get_imagePath() {
        return _imagePath;
    }

}


