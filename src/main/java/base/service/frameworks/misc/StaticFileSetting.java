package base.service.frameworks.misc;

import base.service.frameworks.utils.*;
import com.google.common.collect.ImmutableList;
import base.service.frameworks.vo.UploadedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by someone on 2020/4/16 19:46.
 */
@SuppressWarnings("unused")
public enum StaticFileSetting {
    // ===========================================================
    // Enums
    // ===========================================================
    file(0, "文件", "file"),
    image(1, "图片", "image"),
    avatar(2, "头像", "avatar"),
    credential(3, "凭证", "credential"),
    video(4, "视频", "video");

    // ===========================================================
    // Constants
    // ===========================================================
    private static final Logger LOG = LogManager.getLogger(StaticFileSetting.class);

    // ===========================================================
    // Fields
    // ===========================================================
    private int                   mType;
    private String                mName;
    private String                mRelativeRootPath;
    private ImmutableList<String> mAllowedExtensions;
    private ImmutableList<String> mBlockedExtensions;
    private long                  mMaxSize;
    private int                   mPartition;

    // ===========================================================
    // Constructors
    // ===========================================================
    StaticFileSetting(int pType, String pName, String pRelativeRootPath){
        this.mType = pType;
        this.mName = pName;
        this.mRelativeRootPath = pRelativeRootPath;
        this.mAllowedExtensions = ImmutableList.of();
        this.mBlockedExtensions = ImmutableList.of();
        this.mMaxSize = 0;
        this.mPartition = 20;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    public int getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public String getRelativeRootPath(){
        return mRelativeRootPath;
    }

    public void resetAllowedExtensions(List<String> pExtensions){
        if(pExtensions != null && pExtensions.size() > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} allowed {} > {}", mType, mRelativeRootPath, mName, GsonUtil.toJson(mAllowedExtensions), GsonUtil.toJson(pExtensions));
            this.mAllowedExtensions = ImmutableList.copyOf(pExtensions);
        }
    }

    public void resetAllowedExtensions(String ... pExtensions){
        if(pExtensions != null && pExtensions.length > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} allowed {} > {}", mType, mRelativeRootPath, mName, GsonUtil.toJson(mAllowedExtensions), GsonUtil.toJson(pExtensions));
            this.mAllowedExtensions = ImmutableList.copyOf(pExtensions);
        }
    }

    public void resetBlockedExtensions(List<String> pExtensions){
        if(pExtensions != null && pExtensions.size() > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} allowed {} > {}", mType, mRelativeRootPath, mName, GsonUtil.toJson(mBlockedExtensions), GsonUtil.toJson(pExtensions));
            this.mBlockedExtensions = ImmutableList.copyOf(pExtensions);
        }
    }

    public void resetBlockedExtensions(String ... pExtensions){
        if(pExtensions != null && pExtensions.length > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} blocked {} > {}", mType, mRelativeRootPath, mName, GsonUtil.toJson(mBlockedExtensions), GsonUtil.toJson(pExtensions));
            this.mBlockedExtensions = ImmutableList.copyOf(pExtensions);
        }
    }

    public void setMaxSize(long pSize){
        if(pSize > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} max-size {} > {} ({})", mType, mRelativeRootPath, mName, mMaxSize, pSize, FileUtil.getHumanReadableSize(pSize, false));
            this.mMaxSize = pSize;
        }
    }

    public void setPartition(int pPartition){
        if(pPartition > 0){
            LOG.info("UPLOAD TYPE {}:{}:{} partition {} > {}", mType, mRelativeRootPath, mName, mPartition, pPartition);
            this.mPartition = pPartition;
        }
    }

    public boolean isExtensionAllowed(String pExtension){
        String extension = !StringUtil.isEmpty(pExtension) ? pExtension.replace(".", "").toLowerCase() : "";
        if(this.mAllowedExtensions.size() > 0){
            return this.mAllowedExtensions.contains(extension);
        }else if(this.mBlockedExtensions.size() > 0){
            return !this.mBlockedExtensions.contains(extension);
        }
        return false;
    }

    public boolean isExtensionAllowed(UploadedFile pFile){
        return pFile != null && isExtensionAllowed(pFile.extension);
    }

    public boolean isSizeAllowed(long pSize){
        return pSize <= mMaxSize;
    }

    public boolean isSizeAllowed(UploadedFile pFile){
        if(pFile != null) {
            return pFile.raw.length <= mMaxSize;
        }
        return false;
    }

    /**
     * 根据当前 partition、relativeRootPath 配置生成有限唯一新文件名，有限在于每秒生成数量 <100000
     * @return /relativeRootPath/partition_index/filename
     */
    public String generateFiniteRandomFileName(){
        String id = AtomicUtil.generateFiniteIDInSecond();
        int index = MathUtils.random(1000, 9999);
        int partition_id = index % mPartition;
        return mRelativeRootPath + "/" + (partition_id < 10 ? "0" : "") + partition_id + "/" + id + "_" + index;
    }

    public static StaticFileSetting getStaticFileSettingByType(int pType){
        for(StaticFileSetting s : values()) {
            if(s.mType == pType){
                return s;
            }
        }
        return null;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    @Override
    public String toString() {
        return "{\"type\":" + this.mType + ", \"name\":\"" + this.mName + "\", \"allowed\":" + GsonUtil.toJson(this.mAllowedExtensions) + ", \"max-size\":" + this.mMaxSize + ", \"partition\":" + this.mPartition + "}";
    }

    // ===========================================================
    // Methods
    // ===========================================================
    public static void main(String[] args){
        StaticFileSetting.file.resetAllowedExtensions(Arrays.asList("jpeg", "jpg", "png"));
        StaticFileSetting.image.resetAllowedExtensions(Arrays.asList("bmp", "jpg"));
        StaticFileSetting.image.setMaxSize(1024000);
        System.out.println(file.generateFiniteRandomFileName());
        System.out.println(image.generateFiniteRandomFileName());
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
