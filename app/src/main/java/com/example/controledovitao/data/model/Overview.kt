package com.example.controledovitao.data.model

import java.math.BigDecimal

data class Overview (
    var totalBalance: BigDecimal,
    var totalLimit: BigDecimal,
    var totalInvest: BigDecimal,
    var paymentsSaved: List<Payment>,
    var investMade: List<Invest>
)