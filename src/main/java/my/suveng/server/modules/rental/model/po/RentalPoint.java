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
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "rental_point")
public class RentalPoint extends BaseRowModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty(value = "租赁点id",index = 0)
    private Long pointId;

    @Column(columnDefinition = "double(255,6) NOT NULL DEFAULT '0.000000' COMMENT '经度'")
    @ExcelProperty(value = "经度", index = 1)
    private Double longitude;


    @Column(columnDefinition = "double(255,6) NOT NULL DEFAULT '0.000000' COMMENT '纬度'")
    @ExcelProperty(value = "纬度", index = 2)
    private Double latitude;


    @Column(columnDefinition = "int(255) NOT NULL DEFAULT '0' COMMENT '剩余车辆数'")
    @ExcelProperty(value = "剩余车辆数", index = 3)
    private Integer leftBike;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP")
    @ExcelProperty(value = "创建时间", index = 4)
    private Date creteTime;

    @LastModifiedDate
    @Column(nullable = false,columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @ExcelProperty(value = "修改时间", index = 5)
    private Date updateTime;


}
