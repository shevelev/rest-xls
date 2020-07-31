package com.example.demo.model

import javax.persistence.*

@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("name"))))
data class Section(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "section_id")
        val id: Long? = null,
        val name: String,
        @OneToMany(fetch = FetchType.EAGER, cascade= arrayOf(CascadeType.ALL))
        @JoinColumn(name = "sec_id")
        val geologicalClasses: MutableList<GeologicalClass>
)