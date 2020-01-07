package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.MemberDTO;

import java.util.Map;

public class RankPageDTO {

    private Integer number;

    private Map<Integer, MemberDTO> list;

    private Boolean hasPrevious;

    private Boolean hasNext;

    private Integer allPages;

    public RankPageDTO() {
    }

    public RankPageDTO(Integer number, Map<Integer, MemberDTO> list) {
        this.number = number;
        this.list = list;
    }

    public RankPageDTO(Integer number, Map<Integer, MemberDTO> list, Boolean hasPrevious, Boolean hasNext, Integer allPages) {
        this.number = number;
        this.list = list;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.allPages = allPages;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Map<Integer, MemberDTO> getList() {
        return list;
    }

    public void setList(Map<Integer, MemberDTO> list) {
        this.list = list;
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
