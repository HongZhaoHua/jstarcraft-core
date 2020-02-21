package com.jstarcraft.core.storage;

/**
 * ORM分页
 * 
 * @author Birdy
 *
 */
public class StoragePagination {

    /** 页码(至少为1) */
    private final int page;
    /** 大小(至少为1) */
    private final int size;

    public StoragePagination(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("分页的页码与大小至少为1");
        }
        this.page = page;
        this.size = size;
    }

    /**
     * 获取第一条记录的位置
     * 
     * @return
     */
    public int getFirst() {
        return size * page - size;
    }

    /**
     * 获取最后一条记录的位置
     * 
     * @return
     */
    public int getLast() {
        return size * page;
    }

    /**
     * 获取分页页码
     * 
     * @return
     */
    public int getPage() {
        return page;
    }

    /**
     * 获取分页大小
     * 
     * @return
     */
    public int getSize() {
        return size;
    }

}
