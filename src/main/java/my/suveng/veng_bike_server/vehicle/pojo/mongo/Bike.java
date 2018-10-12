package my.suveng.veng_bike_server.vehicle.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "bikes")
public class Bike {
    @Id
    private String id;

    private String qrCode;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

    private Integer status=0;

    private String pointid;

    public Bike() {
    }

    public Bike(String id, String qrCode, double[] location, Integer status, String pointid) {
        this.id = id;
        this.qrCode = qrCode;
        this.location = location;
        this.status = status;
        this.pointid = pointid;
    }
}