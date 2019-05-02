package my.suveng.server.modules.rental.model.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author suwenguang
 *         suveng@163.com
 * since 2019/5/1
 * description:
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Vehicle extends BaseRowModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty(value = "车辆id", index = 0)
    private Long vehicleId;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '车辆二维码'")
    @ExcelProperty(value = "车辆二维码", index = 1)
    private String qrCode;

    @Column(columnDefinition = "double(255,6) NOT NULL DEFAULT '0.000000' COMMENT '经度'")
    @ExcelProperty(value = "经度", index = 2)
    private Double longitude;


    @Column(columnDefinition = "double(255,6) NOT NULL DEFAULT '0.000000' COMMENT '纬度'")
    @ExcelProperty(value = "纬度", index = 3)
    private Double latitude;

    @Column(columnDefinition = "int(2) NOT NULL DEFAULT 0 COMMENT '车辆状态'")
    @ExcelProperty(value = "车辆状态", index = 4)
    private Integer status;

    @Column(columnDefinition = "int(2) NOT NULL DEFAULT 0 COMMENT '车辆类型'")
    @ExcelProperty(value = "车辆类型", index = 5)
    private Integer type;

    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '归属租赁点'")
    @ExcelProperty(value = "归属租赁点", index = 6)
    private Long pointId;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP")
    @ExcelProperty(value = "创建时间", index = 7)
    private Date creteTime;

    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @ExcelProperty(value = "修改时间", index = 8)
    private Date updateTime;


}
