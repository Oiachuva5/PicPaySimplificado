package com.picPaySimplificado.service

import com.picPaySimplificado.enums.Errors
import com.picPaySimplificado.exception.NotFoundException
import com.picPaySimplificado.model.TransactionModel
import com.picPaySimplificado.repository.CustomerRepository
import com.picPaySimplificado.repository.TransactionRepository
import com.picPaySimplificado.service.functions.CheckoutTransference
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val checkoutTransference: CheckoutTransference,
    private val customerRepository: CustomerRepository
) {

    fun transference(transaction: TransactionModel) {

        val validar1: Boolean = checkoutTransference.checkExists(transaction)
        val validar2: Boolean = checkoutTransference.checkRegistroGoverno(transaction)
        val validar3: Boolean = checkoutTransference.checkBalance(transaction)
        val validar4: Boolean = checkoutTransference.checkApprovalApi()

        val GetsubtrairValor = customerRepository.findById(transaction.envia).get()
        val subtrairValor = GetsubtrairValor

        val GetsomarValor = customerRepository.findById(transaction.recebe).get()
        val somarValor = GetsomarValor

//        val teste= transactionValidation.postForEmailApi(transaction)

        val dataTransacao: LocalDate? = java.time.LocalDate.now()
        val postHistory = TransactionModel(
            envia = transaction.envia,
            recebe = transaction.recebe,
            valor = transaction.valor,
            date = dataTransacao,
        )

//        if (!validar1) {
//            return
//        }


        if (validar1 && validar2 && validar3 && validar4) {
            subtrairValor.saldo = subtrairValor.saldo - transaction.valor

            customerRepository.save(subtrairValor)

            somarValor.saldo = transaction.valor + somarValor.saldo
            customerRepository.save(somarValor)

            transactionRepository.save(postHistory)
        } else {
            return
        }
    }
    fun getAll(): List<TransactionModel> {
        return transactionRepository.findAll().toList()
    }

    fun getTransaction(id: Int): TransactionModel {
        return transactionRepository.findById(id).orElseThrow{
            NotFoundException(Errors.TO001.message.format(id), Errors.TO001.code)
        }

    }
}