package com.example.demo.repo

import com.example.demo.model.Section
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface SectionRepo : CrudRepository<Section, Long> {
    @Query(value = "SELECT * FROM section s JOIN geological_class gc ON s.section_id = gc.sec_id WHERE gc.code= :code", nativeQuery = true)
    fun findSectionByID(@Param("code") code: String): List<Section>
}