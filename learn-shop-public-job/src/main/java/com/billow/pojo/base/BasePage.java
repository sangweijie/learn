package com.billow.pojo.base;

import java.io.Serializable;

/**
 * 分布数据
 *
 * @author LiuYongTao
 * @date 2018/4/27 11:46
 */
public abstract class BasePage implements Serializable {

    private String requestUrl; // 请求url
    private int pageSize = 10; // 每页要显示的记录数
    private int pageNo = 0; // 当前页号
    private int recordCount; // 总记录数
    private String objectOrderBy;

    public String getObjectOrderBy() {
        return objectOrderBy;
    }

    public void setObjectOrderBy(String objectOrderBy) {
        this.objectOrderBy = objectOrderBy;
    }

    /**
     * 请求url
     *
     * @return
     * @author XiaoY
     * @date: 2016年12月3日 下午3:57:19
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * 请求url
     *
     * @param requestUrl
     * @author XiaoY
     * @date: 2016年12月3日 下午3:57:23
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * 每页要显示的记录数
     *
     * @return
     * @author XiaoY
     * @date: 2016年12月3日 下午3:57:08
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 每页要显示的记录数
     *
     * @param pageSize
     * @author XiaoY
     * @date: 2016年12月3日 下午3:57:11
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 当前页号
     *
     * @return
     * @author XiaoY
     * @date: 2016年12月3日 下午3:56:44
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 当前页号
     *
     * @param pageNo
     * @author XiaoY
     * @date: 2016年12月3日 下午3:56:48
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 总记录数
     *
     * @return
     * @author XiaoY
     * @date: 2016年12月3日 下午3:56:56
     */
    public int getRecordCount() {
        return recordCount;
    }

    /**
     * 总记录数
     *
     * @param recordCount
     * @author XiaoY
     * @date: 2016年12月3日 下午3:56:59
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    @Override
    public String toString() {
        return "BasePage{" +
                "requestUrl='" + requestUrl + '\'' +
                ", pageSize=" + pageSize +
                ", pageNo=" + pageNo +
                ", recordCount=" + recordCount +
                ", objectOrderBy='" + objectOrderBy + '\'' +
                '}';
    }
}