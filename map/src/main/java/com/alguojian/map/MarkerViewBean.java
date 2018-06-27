package com.alguojian.map;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/6/26
 */
public class MarkerViewBean {

    private double latitude;
    private double longitude;
    private int num;
    private int status;

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public MarkerViewBean(double latitude, double longitude, int num, int status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.num = num;
        this.status = status;
    }

    public int getNum() {

        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public double getLatitude() {

        return latitude;

    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
