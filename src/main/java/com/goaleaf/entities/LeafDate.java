package com.goaleaf.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "done_dates")
public class LeafDate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer memberID;

    @Column
    private Date dateValue;

//    @Column
//    @ManyToMany
//    private Set<Member> members;

    public LeafDate(Integer memberID, Date dateValue/*, Set<Member> members*/) {
        this.memberID = memberID;
        this.dateValue = dateValue;
//        this.members = members;
    }

    public LeafDate() {
    }
}
