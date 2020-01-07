package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.CommentDTO;

public class CommentPageDto {

    private Iterable<CommentDTO> list;

    private Integer pageNr;

    private Boolean hasPrevious;

    private Boolean hasNext;

    private Integer allPages;

    public CommentPageDto() {
    }

    public CommentPageDto(Iterable<CommentDTO> list, Integer pageNr, Boolean hasPrevious, Boolean hasNext, Integer allPages) {
        this.list = list;
        this.pageNr = pageNr;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.allPages = allPages;
    }

    public Iterable<CommentDTO> getList() {
        return list;
    }

    public void setList(Iterable<CommentDTO> list) {
        this.list = list;
    }

    public Integer getPageNr() {
        return pageNr;
    }

    public void setPageNr(Integer pageNr) {
        this.pageNr = pageNr;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Integer getAllPages() {
        return allPages;
    }

    public void setAllPages(Integer allPages) {
        this.allPages = allPages;
    }
}
