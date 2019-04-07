package my.suveng.veng_bike_server.common.page;

import lombok.Data;

/**
 * @author suwenguang
 * suveng@163.com
 * since 2019/3/20
 * description: 分页类
 **/
@Data
public class Pagination {
    private Integer pageSize;
    private Integer pageNum;
}
