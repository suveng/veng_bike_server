package my.suveng.server.modules.rental.model.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "vehicles")
public class VehicleMongo {
    @Id
    private Long id;

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

    public VehicleMongo() {
    }

    public VehicleMongo(String id, String qrCode, double[] location, Integer status, String pointid) {
        this.id = id;
        this.qrCode = qrCode;
        this.location = location;
        this.status = status;
        this.pointid = pointid;
    }

    public my.suveng.server.vehicle.pojo.mysql.Vehicle toMySQL() {
        my.suveng.server.vehicle.pojo.mysql.Vehicle vehicle = new my.suveng.server.vehicle.pojo.mysql.Vehicle();
        vehicle.setPointid(this.getPointid());
        vehicle.setLongitude(this.getLocation()[0]);
        vehicle.setLatitude(this.getLocation()[1]);
        vehicle.setQrcode(this.getQrCode());
        vehicle.setVehicleid(this.getId());
        vehicle.setType(0);
        return vehicle;
    }
}
