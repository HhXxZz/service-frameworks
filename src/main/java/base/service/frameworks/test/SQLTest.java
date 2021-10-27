package base.service.frameworks.test;

import base.service.frameworks.dao.BaseDao;
import base.service.frameworks.utils.ConnectionUtil;
import base.service.frameworks.utils.GsonUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by hxz on 2021/10/27 17:05.
 */

public class SQLTest {






    public static void main(String[] args) {

        ConnectionUtil.INSTANCE.init();
        BaseDao mDao = new BaseDao();

        String sql = "SELECT uid,name FROM base_user.user WHERE 1=1";
        List<SQLTest.User> query = mDao.query(SQLTest.User.class, sql, new Object[]{});
        System.out.println(GsonUtil.toJson(query));


    }


    public static class User{
        public long uid;
        public String name;
    }

}
