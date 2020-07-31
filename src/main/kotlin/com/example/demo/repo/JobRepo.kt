package com.example.demo.repo

import com.example.demo.model.Job
import org.springframework.data.repository.CrudRepository

interface JobRepo : CrudRepository<Job, Long> {
    fun findJobById(id: Long): Job?
}