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
@Table(name = "rental_record")
public class RentalRecord extends BaseRowModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty(value = "租赁记录id",index = 0)
    private Long rentalId;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT ''  COMMENT '用户id'")
    @ExcelProperty(value = "用户id",index = 1)
    private String userId;

    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0  COMMENT '车辆id'")
    @ExcelProperty(value = "车辆id",index = 2)
    private String vehicleId;

    @Column(columnDefinition = "int(2)  DEFAULT 0 COMMENT '记录状态: 0:未完成,1:已完成'")
    @ExcelProperty(value = "记录状态", index = 3)
    private Integer status;

    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '开始的租赁点id'")
    @ExcelProperty(value = "开始的租赁点id",index = 4)
    private Long beginPointId;

    @Column(columnDefinition = "bigint(20) NOT NULL DEFAULT 0 COMMENT '结束的租赁点id'")
    @ExcelProperty(value = "结束的租赁点id",index = 5)
    private Long endPointId;


    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间'")
    @ExcelProperty(value = "开始时间", index = 6)
    private Date beginTime;

    @Column(columnDefinition = "timestamp COMMENT '结束时间'")
    @ExcelProperty(value = "结束时间", index = 7)
    private Date endTime;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP")
    @ExcelProperty(value = "创建时间", index = 8)
    private Date creteTime;

    @LastModifiedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @ExcelProperty(value = "修改时间", index = 9)
    private Date updateTime;


}
