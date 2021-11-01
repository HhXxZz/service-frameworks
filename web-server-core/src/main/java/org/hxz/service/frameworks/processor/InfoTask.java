package org.hxz.service.frameworks.processor;

import org.hxz.service.frameworks.dao.CommonDao;
import org.hxz.service.frameworks.misc.Parameters;
import org.hxz.service.frameworks.processor.annotation.API;
import org.hxz.service.frameworks.base.ApiFactory;


/**
 * Created by dada on 2021/5/19 16:24.
 */
@SuppressWarnings("unused")
@API(uri = "/info", name = "好友名片信息", enableMember = true,method = ApiFactory.POST)
public class InfoTask extends BaseTask {

    public InfoTask(Parameters pParams) {
        super(pParams);
    }

    @Override
    public String prepare() {


        return null;
    }

    @Override
    public String execute() {
        String id = mParams.getString("id","");
       // String data = mParams.getString("data","");
       // RedisClient.INSTANCE.set(id,data);

        CommonDao.INSTANCE.addUser();

//        Map<String,String>map = new HashMap<>();
//        map.put("id", RedisClient.INSTANCE.get(id));
        //map.put("data", RedisClient.INSTANCE.get("data"));

        return successResponse(CommonDao.INSTANCE.getUserList());
    }
}
