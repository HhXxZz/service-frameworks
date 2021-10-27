package base.service.frameworks.processor;

import base.service.frameworks.misc.Parameters;
import base.service.frameworks.processor.annotation.API;
import base.service.frameworks.base.ApiFactory;
import base.service.frameworks.redis.RedisClient;

import java.util.HashMap;
import java.util.Map;


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

        Map<String,String>map = new HashMap<>();
        map.put("id", RedisClient.INSTANCE.get(id));
        //map.put("data", RedisClient.INSTANCE.get("data"));

        return successResponse(map);
    }
}
