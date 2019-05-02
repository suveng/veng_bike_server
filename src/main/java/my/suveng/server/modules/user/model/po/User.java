package my.suveng.server.modules.user.model.po;


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
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseRowModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ExcelProperty(value = "用户id",index = 0)
    private Long userId;



    @Column(columnDefinition = "int(2) NOT NULL DEFAULT 0 COMMENT '用户状态: 0:未绑定 ,1:未认证 ,2:未充值 ,3:通过'")
    @ExcelProperty(value = "用户状态", index = 1)
    private Integer status;


    @Column(columnDefinition = "varchar(15) NOT NULL DEFAULT '0' COMMENT '手机号码'")
    @ExcelProperty(value = "手机号码", index = 2)
    private String phone;

    @Column(columnDefinition = "varchar(12) NOT NULL DEFAULT '' COMMENT '姓名'")
    @ExcelProperty(value = "姓名", index = 3)
    private String name;

    @Column(columnDefinition = "varchar(25) NOT NULL DEFAULT '' COMMENT '身份证号码'")
    @ExcelProperty(value = "身份证号码", index = 4)
    private String idNum;

    @Column(columnDefinition = "double(255,0) NOT NULL DEFAULT '0' COMMENT '押金'")
    @ExcelProperty(value = "押金", index = 5)
    private Double deposit;

    @Column(columnDefinition = "double(255,0) NOT NULL DEFAULT '0' COMMENT '余额'")
    @ExcelProperty(value = "余额", index = 6)
    private Double balance;

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP")
    @ExcelProperty(value = "创建时间", index = 7)
    private Date creteTime;

    @LastModifiedDate
    @Column(nullable = false,columnDefinition = "TIMESTAMP not null DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @ExcelProperty(value = "修改时间", index = 8)
    private Date updateTime;


}
