package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.PostDTO;

public class PostPageDTO {

    private Iterable<PostDTO> list;

    private Integer pageNr;

    public PostPageDTO() {
    }

    public PostPageDTO(Iterable<PostDTO> list, Integer pageNr) {
        this.list = list;
        this.pageNr = pageNr;
    }

    public Iterable<PostDTO> getList() {
        return list;
    }

    public void setList(Iterable<PostDTO> list) {
        this.list = list;
    }

    public Integer getPageNr() {
        return pageNr;
    }

    public void setPageNr(Integer pageNr) {
        this.pageNr = pageNr;
    }
}
