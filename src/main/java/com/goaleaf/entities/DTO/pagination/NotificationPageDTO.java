package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.NotificationDTO;

public class NotificationPageDTO {

    private Iterable<NotificationDTO> list;

    private Integer pageNr;

    public NotificationPageDTO() {
    }

    public NotificationPageDTO(Iterable<NotificationDTO> list, Integer pageNr) {
        this.list = list;
        this.pageNr = pageNr;
    }

    public Iterable<NotificationDTO> getList() {
        return list;
    }

    public void setList(Iterable<NotificationDTO> list) {
        this.list = list;
    }

    public Integer getPageNr() {
        return pageNr;
    }

    public void setPageNr(Integer pageNr) {
        this.pageNr = pageNr;
    }
}
