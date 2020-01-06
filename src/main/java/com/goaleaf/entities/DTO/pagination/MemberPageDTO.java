package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.MemberDTO;

public class MemberPageDTO {

    private Iterable<MemberDTO> list;

    private Integer pageNr;

    private Boolean hasPrevious;

    private Boolean hasNext;

    private Integer allPages;

    public MemberPageDTO() {
    }

    public MemberPageDTO(Iterable<MemberDTO> list, Integer pageNr) {
        this.list = list;
        this.pageNr = pageNr;
    }

    public MemberPageDTO(Iterable<MemberDTO> list, Integer pageNr, Boolean hasPrevious, Boolean hasNext, Integer allPages) {
        this.list = list;
        this.pageNr = pageNr;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
        this.allPages = allPages;
    }

    public Iterable<MemberDTO> getList() {
        return list;
    }

    public void setList(Iterable<MemberDTO> list) {
        this.list = list;
    }

    public Integer getPageNr() {
        return pageNr;
    }

    public void setPageNr(Integer pageNr) {
        this.pageNr = pageNr;
    }
}
