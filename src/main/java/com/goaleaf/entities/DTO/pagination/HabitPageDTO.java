package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.HabitDTO;

public class HabitPageDTO {

    private Iterable<HabitDTO> list;

    private Integer pageNr;

    private Boolean hasPrevious;

    private Boolean hasNext;

    private Integer allPages;

    public HabitPageDTO() {
    }

    public HabitPageDTO(Iterable<HabitDTO> list, Integer pageNr) {
        this.list = list;
        this.pageNr = pageNr;
    }

    public HabitPageDTO(Iterable<HabitDTO> list, Integer pageNr, Boolean hasPrevious, Boolean hasNext, Integer allPages) {
        this.list = list;
        this.pageNr = pageNr;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.allPages = allPages;
    }

    public Iterable<HabitDTO> getList() {
        return list;
    }

    public void setList(Iterable<HabitDTO> list) {
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
