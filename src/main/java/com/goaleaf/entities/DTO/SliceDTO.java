package com.goaleaf.entities.DTO;

public class SliceDTO {

    private Iterable<HabitDTO> list;

    private Integer pageNr;

    public SliceDTO() {
    }

    public SliceDTO(Iterable<HabitDTO> list, Integer pageNr) {
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
