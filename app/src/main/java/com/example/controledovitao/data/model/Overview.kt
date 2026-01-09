package com.example.controledovitao.data.model

import java.math.BigDecimal

data class Overview (
    var totalBalance: BigDecimal,
    var totalLimit: BigDecimal,
    var totalInvest: BigDecimal,
    var paymentsSaved: MutableList<Payment>,
    var investMade: MutableList<Invest>
)