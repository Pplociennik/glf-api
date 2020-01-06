package com.goaleaf.entities.DTO.pagination;

import com.goaleaf.entities.DTO.MemberDTO;

import java.util.Map;

public class RankPageDTO {

    private Integer number;

    private Map<Integer, MemberDTO> list;

    public RankPageDTO() {
    }

    public RankPageDTO(Integer number, Map<Integer, MemberDTO> list) {
        this.number = number;
        this.list = list;
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
}
