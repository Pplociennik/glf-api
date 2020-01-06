package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.HabitDTO;

public class HabitPageDTO {

    private Iterable<HabitDTO> list;

    private Integer pageNr;

    public HabitPageDTO() {
    }

    public HabitPageDTO(Iterable<HabitDTO> list, Integer pageNr) {
        this.list = list;
        this.pageNr = pageNr;
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
}
