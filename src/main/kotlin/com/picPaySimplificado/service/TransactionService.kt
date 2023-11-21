package com.picPaySimplificado.service

import com.picPaySimplificado.model.CustomerModel
import com.picPaySimplificado.model.transactionModel
import com.picPaySimplificado.repository.customerRepository
import com.picPaySimplificado.repository.transactionRepository
import com.picPaySimplificado.validations.TransactionValidation
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class TransactionService(
    private val transactionRepository: transactionRepository,
    private val transactionValidation: TransactionValidation,
    private val customerRepository: customerRepository
) {

    fun transferencia(transaction: transactionModel) {

        val validar1: Boolean = transactionValidation.checarExistencia(transaction)
        val validar2: Boolean = transactionValidation.checarRegistroGoverno(transaction)
        val validar3: Boolean = transactionValidation.checarSaldo(transaction)
        val validar4: Boolean = transactionValidation.checarApiAprovacao()

        val GetsubtrairValor = customerRepository.findById(transaction.envia)
        val subtrairValor = (GetsubtrairValor as CustomerModel)

        val GetsomarValor = customerRepository.findById(transaction.recebe)
        val somarValor = (GetsomarValor as CustomerModel)


        val dataTransacao: LocalDate? = java.time.LocalDate.now()
        val postHistory = transactionModel(
            envia = transaction.envia,
            recebe = transaction.recebe,
            valor = transaction.valor,
            date = dataTransacao,
        )

        if (validar1 && validar2 && validar3 && validar4) {
            subtrairValor.saldo = transaction.valor - subtrairValor.saldo
            customerRepository.save(subtrairValor)

            somarValor.saldo = transaction.valor + somarValor.saldo
            customerRepository.save(somarValor)

            transactionRepository.save(postHistory)
        } else {
            throw Exception()
        }
    }

}