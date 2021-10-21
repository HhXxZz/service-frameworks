package base.service.frameworks.processor;

import base.service.frameworks.misc.Parameters;
import base.service.frameworks.processor.annotation.API;
import base.service.frameworks.base.ApiFactory;

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
        Map<String,String>map = new HashMap<>();
        map.put("aaa","aa");
        return successResponse(map);
    }
}
