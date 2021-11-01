package org.hxz.service.frameworks.dao.builder;

import org.hxz.service.frameworks.utils.StringUtil;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Created by someone on 2018-08-08.
 *
 * </pre>
 */
@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class Page<T> {
    // ===========================================================
    // Constants
    // ===========================================================


    // ===========================================================
    // Fields
    // ===========================================================
    @Expose
    private           int     page;      // 页码
    @Expose
    private           int     size;      // 每页数量
    @Expose
    private           int     total;     // 总记录数
    @Expose
    private           int     totalPage; // 总页数
    @Expose
    private           List<T> records;   // 当前记录
    private transient String  limit;


    // ===========================================================
    // Constructors
    // ===========================================================
    public Page(){
        this.page = 1;
        this.size = 5;
    }

    public Page(int pPage, int pSize){
        this.page = pPage <= 0 ? 1 : pPage;
        this.size = pSize <= 0 ? 5 : pSize;
        this.limit = " LIMIT " + (page - 1) * size + ", " + size;
    }

    // ===========================================================
    // Getter &amp; Setter
    // ===========================================================

    public int getTotal() {
        return total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public String getLimit() {
        return StringUtil.isEmpty(limit) ? " LIMIT " + (page - 1) * size + ", " + size : limit;
    }

    public List<T> getRecords() {
        return records;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================


    // ===========================================================
    // Methods
    // ===========================================================
    public void setTotalCount(int pTotal){
        this.total = pTotal;
        this.totalPage = (pTotal + size - 1) / size;
    }

    public void setRecords(List<T> pRecords){
        this.records = new ArrayList<>();
        if(pRecords != null && pRecords.size() > 0){
            this.records.addAll(pRecords);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
