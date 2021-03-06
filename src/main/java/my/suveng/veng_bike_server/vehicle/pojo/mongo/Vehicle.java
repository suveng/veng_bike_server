package my.suveng.veng_bike_server.vehicle.pojo.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "vehicles")
public class Vehicle {
    @Id
    private String id;

    /**
     * 对应二维码
     */
    private String qrCode;

    /**
     * 当前车辆的经纬度
     */
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;

    /**
     * 租车的状态
     */
    private Integer status=0;

    /**
     * 归属的租车点
     */
    private String pointid;

    public Vehicle() {
    }

    public Vehicle(String id, String qrCode, double[] location, Integer status, String pointid) {
        this.id = id;
        this.qrCode = qrCode;
        this.location = location;
        this.status = status;
        this.pointid = pointid;
    }
}
