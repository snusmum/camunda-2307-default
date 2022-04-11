package com.study.camunda.service

import com.study.camunda.feign.CatFactsClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CatFactsServiceImpl(val catFactClient: CatFactsClient, @Value("\${defaults.cat.fact}") val defaultFact: String) :
    CatFactsService {

    override fun getFact(): String {
        return try {
            catFactClient.getFact()?.fact ?: defaultFact
        } catch (ex: Exception) {
            ex.printStackTrace();
            defaultFact
        }
    }

}