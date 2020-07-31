package com.example.demo.model

import javax.persistence.*

@Entity
@Table(uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("code", "sec_id"))))
class GeologicalClass(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var name: String,
        var code: String,
        var sec_id: Long? = null)
