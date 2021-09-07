package base.service.frameworks.vo;

import base.service.frameworks.utils.RegexUtil;
import base.service.frameworks.utils.StringUtil;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by someone on 2017-03-01.
 *
 * 通用参数，理论上所有接口都应当上报这些数据
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class StatCommon {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    public int    pid; // 产品ID
    public String versionCode; // 产品版本号
    public String versionName; // 产品版本名称
    public int    gameID; // 游戏ID
    public String gameVersionCode; // 游戏版本号
    public String gameVersionName; // 游戏版本名称
    public String channel; // 渠道
    public String os; // 系统，android|ios|H5
    public String osVersionName; // 系统版本名称，如android中的 4.4.0
    public String osVersionCode; // 系统版本号，如Android的API版本
    public String mac; // MAC地址
    public String deviceID; // 设备ID
    public String deviceModel; // 设备型号
    public String ip; // ip

    // ===========================================================
    // Constructors
    // ===========================================================
    public StatCommon(){
        this.pid = 0; // 产品ID
        this.versionCode = ""; // 产品版本号
        this.versionName = ""; // 产品版本名称
        this.gameID = 0; // 游戏ID
        this.gameVersionCode = ""; // 游戏版本号
        this.gameVersionName = ""; // 游戏版本名称
        this.channel = "Default"; // 渠道
        this.os = ""; // 系统，android|ios|H5
        this.osVersionName = ""; // 系统版本名称，如android中的 4.4.0
        this.osVersionCode = ""; // 系统版本号，如Android的API版本
        this.mac = ""; // MAC地址
        this.deviceID = ""; // 设备ID
        this.deviceModel = ""; // 设备型号
        this.ip = "0.0.0.0"; // 设备型号
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public String printAsParameters(){
        List<String> pairs = new ArrayList<>();
        if(this.pid > 0){
            pairs.add(String.format("pid=%d", pid));
        }
        if(!StringUtil.isEmpty(this.versionCode)){ // 产品版本号
            pairs.add(String.format("versionCode=%s", versionCode));
        }
        if(!StringUtil.isEmpty(this.versionName)){ // 产品版本名称
            pairs.add(String.format("versionName=%s", versionName));
        }
        if(this.gameID > 0){
            pairs.add(String.format("gameID=%d", gameID));
        }
        if(!StringUtil.isEmpty(this.gameVersionCode)){ // 游戏版本号
            pairs.add(String.format("gameVersionCode=%s", gameVersionCode));
        }
        if(!StringUtil.isEmpty(this.gameVersionName)){ // 游戏版本名称
            pairs.add(String.format("gameVersionName=%s", gameVersionName));
        }
        if(!StringUtil.isEmpty(this.channel)){ // 渠道
            pairs.add(String.format("channel=%s", channel));
        }
        if(!StringUtil.isEmpty(this.os)){ // 系统，android|ios|H5
            pairs.add(String.format("os=%s", os));
        }
        if(!StringUtil.isEmpty(this.osVersionName)){ // 系统版本名称，如android中的 4.4.0
            pairs.add(String.format("osVersionName=%s", osVersionName));
        }
        if(!StringUtil.isEmpty(this.osVersionCode)){ // 系统版本号，如Android的API版本
            pairs.add(String.format("osVersionCode=%s", osVersionCode));
        }
        if(!StringUtil.isEmpty(this.mac)){ // MAC地址
            pairs.add(String.format("mac=%s", mac));
        }
        if(!StringUtil.isEmpty(this.deviceID)){ // 设备ID
            pairs.add(String.format("deviceID=%s", deviceID));
        }
        if(!StringUtil.isEmpty(this.deviceModel)){ // 设备型号
            pairs.add(String.format("deviceModel=%s", deviceModel));
        }
        if(!"0.0.0.0".equals(ip) && RegexUtil.isIPAddress(ip)){
            pairs.add(String.format("ip=%s", ip));
        }
        return Joiner.on("&").join(pairs);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
