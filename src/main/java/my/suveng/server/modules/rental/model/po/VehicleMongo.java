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
    private Long pointId;

    public VehicleMongo() {
    }

    public VehicleMongo(String id, String qrCode, double[] location, Integer status, String pointId) {
        this.id = id;
        this.qrCode = qrCode;
        this.location = location;
        this.status = status;
        this.pointId = Long.valueOf(pointId);
    }

    public Vehicle toMySQL() {
        Vehicle vehicle = new Vehicle();
        vehicle.setPointId(this.getPointId());
        vehicle.setLongitude(this.getLocation()[0]);
        vehicle.setLatitude(this.getLocation()[1]);
        vehicle.setQrCode(this.getQrCode());
        vehicle.setVehicleId(this.getId());
        vehicle.setType(0);
        return vehicle;
    }
}
