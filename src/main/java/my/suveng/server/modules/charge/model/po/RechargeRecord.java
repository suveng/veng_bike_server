package my.suveng.server.modules.charge.model.po;


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
 * suveng@163.com
 * since 2019/3/19
 * description:
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "recharge_record")
public class RechargeRecord extends BaseRowModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty(value = "充值id",index = 0)
    private Long rechareId;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT ''  COMMENT '用户id'")
    @ExcelProperty(value = "用户id",index = 1)
    private String userId;

    @Column(columnDefinition = "double(255,2) NOT NULL DEFAULT '0.000000' COMMENT '充值金额'")
    @ExcelProperty(value = "充值金额", index = 2)
    private Double charge;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '省'")
    @ExcelProperty(value = "省", index = 3)
    private String province;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '市'")
    @ExcelProperty(value = "市", index = 4)
    private String city;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '街道'")
    @ExcelProperty(value = "街道", index = 5)
    private String district;

    @Column(columnDefinition = "varchar(255) NOT NULL DEFAULT '' COMMENT '地址'")
    @ExcelProperty(value = "地址", index = 6)
    private String address;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP")
    @ExcelProperty(value = "创建时间", index = 7)
    private Date creteTime;

    @LastModifiedDate
    @Column(nullable = false,columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @ExcelProperty(value = "修改时间", index = 8)
    private Date updateTime;


}
