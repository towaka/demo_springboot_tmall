package com.springboot.tmall.util;

import org.springframework.data.domain.Page;

import java.util.List;

public class Page4Navigator<T> {
    //Page类功能很丰富，基本满足各种需求，
    //但是如果需要指定页码导航栏需要显示多少个页码，Page类就爱莫能助
    //所以就需要在Page类的基础上提供这方面的功能需求
    Page<T> pageFromJPA;
    //比如分页出来的超链是这样的：[7,8,9,10,11],那么navigatePages就是5
    int navigatePages;
    //总页面数
    int totalPages;
    //第几页（基0）
    int number;
    //总共有多少条数据
    long totalElements;
    //一页最多有多少条数据
    int sizeOfEachPages;
    //当前页有多少条数据 (与 size，不同的是，最后一页可能不满 size 个)
    int numberofElements;
    //数据集合
    List<T> content;
    //是否有数据
    boolean hasContent;
    //是否是首页
    boolean first;
    //是否是最后一页
    boolean last;
    //是否有下一页
    boolean hasNext;
    //是否有上一页
    boolean hasPrevious;
    //例如页码导航栏上是[8,9,10,11,12],那么 navigatepageNums 就是这个数组
    int[] navigatepageNums;

    public Page4Navigator() {
        //这个空的分页是为了 Redis 从 json格式转换为 Page4Navigator 对象而专门提供的
    }

    public Page4Navigator(Page<T> pageFromJPA, int navigatePages) {
        this.pageFromJPA = pageFromJPA;
        this.navigatePages = navigatePages;

        totalPages = pageFromJPA.getTotalPages();

        number  = pageFromJPA.getNumber() ;

        totalElements = pageFromJPA.getTotalElements();

        sizeOfEachPages = pageFromJPA.getSize();


        numberofElements = pageFromJPA.getNumberOfElements();

        content = pageFromJPA.getContent();

        hasContent = pageFromJPA.hasContent();

        first = pageFromJPA.isFirst();

        last = pageFromJPA.isLast();

        hasNext = pageFromJPA.hasNext();

        hasPrevious  = pageFromJPA.hasPrevious();

        calcNavigatepageNums();
    }

    private void calcNavigatepageNums() {
        int navigatepageNums[];
        int totalPages = getTotalPages();
        int num = getNumber();
        //当总页数小于或等于允许显示的导航页码数量时
        if (totalPages <= navigatePages) {
            navigatepageNums = new int[totalPages];
            for (int i = 0; i < totalPages; i++) {
                navigatepageNums[i] = i + 1;//记录页码的是数组，基0，所以要加1
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = num - navigatePages / 2;
            int endNum = num + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > totalPages) {
                endNum = totalPages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
        this.navigatepageNums = navigatepageNums;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getSizeOfEachPages() {
        return sizeOfEachPages;
    }

    public void setSizeOfEachPages(int sizeOfEachPages) {
        this.sizeOfEachPages = sizeOfEachPages;
    }


    public int getNumberofElements() {
        return numberofElements;
    }

    public void setNumberofElements(int numberofElements) {
        this.numberofElements = numberofElements;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public boolean isHasContent() {
        return hasContent;
    }

    public void setHasContent(boolean hasContent) {
        this.hasContent = hasContent;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }
}
