package my.suveng.server.config;

import org.hibernate.dialect.MySQL57Dialect;

/**
 * @author suwenguang
 * email suveng@163.com
 * since 2019/3/24
 * description:
 **/
public class MySQL5DialectUTF8 extends MySQL57Dialect {

    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}
