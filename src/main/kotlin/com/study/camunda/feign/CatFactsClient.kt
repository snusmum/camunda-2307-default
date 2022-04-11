package com.study.camunda.feign

import com.study.camunda.model.CatFact
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "cat-facts-service", url = "https://catfact.ninja/")
interface CatFactsClient {

    @GetMapping("fact")
    fun getFact(): CatFact?

}