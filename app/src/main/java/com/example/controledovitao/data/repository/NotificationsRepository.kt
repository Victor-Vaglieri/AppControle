package com.example.controledovitao.data.repository

import com.example.controledovitao.data.model.Notification
import com.example.controledovitao.data.model.Status

class NotificationsRepository {

    private val fakeNotifications = mutableListOf(
        Notification(
            Status.INFO,
    "Fatura fechada (Master Crédito)",
    "A fatura do Master Crédito fechou no valor de R$ 1.500,00. Vencimento em 10 dias.",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 10000

    ),
        Notification(
            Status.STANDARD,
    "Ação necessária (Master Débito)",
    "Movimentação suspeita detectada no cartão final 9090.",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 10000
    ),
        Notification(
            Status.CONCLUDE,
    "Gasto Salvo (PIX)",
    "Seu gasto de R$ 50,00 foi salvo e categorizado com sucesso.",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 10000

    ),
        Notification(
            Status.STANDARD,
    "Aviso de Limite (Master Crédito)",
    "Você atingiu 90% do seu limite disponível.",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 10000
    ),
        Notification(
            Status.URGENT,
    "Gastos maiores que o saldo",
    "Atenção: Seus gastos previstos ultrapassam seu saldo atual.",
            System.currentTimeMillis(),
            System.currentTimeMillis() + 10000


    )
    )

    fun getNotifications(): MutableList<Notification> {
        return fakeNotifications
    }

    fun savePushPreference(isEnabled: Boolean){
        println("Configuração salva: Push = $isEnabled")
    }

    fun saveEmailPreference(isEnabled: Boolean){
        println("Configuração salva: Email = $isEnabled")
    }
}