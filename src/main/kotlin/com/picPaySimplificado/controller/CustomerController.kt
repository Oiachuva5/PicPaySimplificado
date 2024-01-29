package com.picPaySimplificado.controller

import com.picPaySimplificado.extensions.toCustomerModel
import com.picPaySimplificado.model.CustomerModel
import org.springframework.web.bind.annotation.*
import com.picPaySimplificado.controller.request.PostCustomerRequest
import com.picPaySimplificado.controller.request.PutCustomerRequest
import com.picPaySimplificado.enums.CustomerStatus
import com.picPaySimplificado.service.CustomerService
import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.springframework.http.HttpStatus

@RestController
@Transactional
@RequestMapping("customer")
class CustomerController(
    val service: CustomerService
) {
    @GetMapping("health")
    fun healthController(): String {
        return "olá"
    }

    @GetMapping
    fun getAll() : List<CustomerModel> {
        return service.getAll()
    }

    @GetMapping("/{id}")
    fun filterCustomer(@PathVariable id: Int): CustomerModel {
        return service.findCustomer(id)
    }

    @PostMapping("/cadastrar")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid customer: PostCustomerRequest) {
        service.create(customer.toCustomerModel())
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable id: Int, @RequestBody putCustomerRequest: PutCustomerRequest){
        val customer = CustomerModel(
            id = id,
            nomeCompleto = putCustomerRequest.nomeCompleto,
            registroGoverno = putCustomerRequest.registroGoverno,
            email = putCustomerRequest.email,
            senha = putCustomerRequest.senha,
            saldo = putCustomerRequest.saldo,
            status = CustomerStatus.ATIVO

        )
        service.update(customer)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Int) {
        service.delete(id)
    }
}