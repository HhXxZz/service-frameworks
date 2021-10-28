package base.service.frameworks.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Created by hxz on 2021/10/28 10:34.
 */

public enum  CommonDao {
    INSTANCE;

    private static final Logger logger = LogManager.getLogger(BaseDao.class);

    private final String SQL_QUERY_USER_LIST = ""+
            " SELECT uid,name FROM base_user.user WHERE 1=1 ORDER BY uid DESC LIMIT 1 ";
    private final String SQL_INSERT_USER = ""+
            " INSERT INTO `base_user`.`user`(name) VALUES (?) ";

    private final BaseDao mDao = new BaseDao();


    public void addUser(){
        mDao.insert(SQL_INSERT_USER,new Object[]{UUID.randomUUID().toString()});
    }

    public List<User> getUserList(){
        return mDao.query(User.class,SQL_QUERY_USER_LIST,null);
    }

    public static class User{
        private long uid;
        private String name;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
