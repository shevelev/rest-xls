package com.example.demo.repo

import com.example.demo.model.GeologicalClass
import org.springframework.data.repository.CrudRepository

interface GeoRepo : CrudRepository<GeologicalClass, Long> {
    fun findAllByCode(code: String) : GeologicalClass
}