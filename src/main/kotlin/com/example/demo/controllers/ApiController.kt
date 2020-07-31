package com.example.demo.controllers

import com.example.demo.model.GeologicalClass
import com.example.demo.model.Section
import com.example.demo.repo.GeoRepo
import com.example.demo.repo.SectionRepo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class ApiController(val geoRepo: GeoRepo, val secRepo: SectionRepo) {
    @GetMapping("/sections/by-code")
    fun getByCode(@RequestParam(value = "code") code: String): List<Map<String, Any?>> {
        val geologicalClasses: GeologicalClass = geoRepo.findAllByCode(code)
        val section = secRepo.findSectionByID(geologicalClasses.code)
        val sectionsWithOutGeo: MutableList<Map<String, Any?>> = mutableListOf()

        for (s in section) {
            val map: MutableMap<String, Any?> = mutableMapOf()
            map["id"] = s.id
            map["name"] = s.name
            sectionsWithOutGeo.add(map)
        }

        return sectionsWithOutGeo
    }

    @GetMapping("/sections/")
    fun getAllSection(): MutableIterable<Section> {
        return secRepo.findAll()
    }

    @GetMapping("/sections/{id}")
    fun getSecById(@PathVariable("id") id: Long): Optional<Section> {
        return secRepo.findById(id)
    }

    @DeleteMapping("/sections/{id}")
    fun delSecById(@PathVariable("id") id: Long) {
        secRepo.deleteById(id)
    }

    @PutMapping("/sections/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody s: Section): ResponseEntity<Section> {
        return secRepo.findById(id).map { it ->
            val updateSection: Section = it.copy(name = s.name, geologicalClasses = s.geologicalClasses)
            ResponseEntity.ok().body(secRepo.save(updateSection))
        }.orElse(ResponseEntity.notFound().build())
    }


}