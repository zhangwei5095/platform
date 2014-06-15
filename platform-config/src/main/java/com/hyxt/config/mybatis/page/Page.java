package com.hyxt.config.mybatis.page;

public class Page {
	
	private int count ;
	
	/**
	 * 一页显示的数据量
	 */
	private int pageSize = 0 ;
	
	/**
	 * 当前处于第几页
	 */
	private int pageCount = 0 ;

    /**
     * 排序字段
     */
	private String orderBy ;

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getOffset(){
		return (pageCount-1) * pageSize ;
	}
	
	public int getLimit(){
		return getOffset() + pageSize ;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	
}
