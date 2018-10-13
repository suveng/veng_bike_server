package my.suveng.veng_bike_server.rentalpoint.pojo.mysql;

public class RentalPoint {
    private String pointid;

    private Double longitude;

    private Double latitude;

    private Integer leftbike;

    public String getPointid() {
        return pointid;
    }

    public void setPointid(String pointid) {
        this.pointid = pointid == null ? null : pointid.trim();
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getLeftbike() {
        return leftbike;
    }

    public void setLeftbike(Integer leftbike) {
        this.leftbike = leftbike;
    }
}