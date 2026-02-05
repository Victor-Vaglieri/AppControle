package com.example.controledovitao.data.repository

import java.math.BigDecimal
import com.example.controledovitao.data.model.SimulationOption
enum class SimulationType { BANCO, CRIPTO }

class SimulationRepository {

    fun getOptions(type: SimulationType): List<SimulationOption> {
        return if (type == SimulationType.BANCO) {
            listOf(
                SimulationOption("1", "Poupança", SimulationType.BANCO, 0.0617), // ~6.17% a.a.
                SimulationOption("2", "CDB (100% CDI)", SimulationType.BANCO, 0.105), // ~10.5% a.a.
                SimulationOption("3", "Tesouro Direto", SimulationType.BANCO, 0.11)
            )
        } else {
            listOf(
                SimulationOption("4", "Bitcoin (BTC)", SimulationType.CRIPTO, 0.60), // Performance ano passado (exemplo)
                SimulationOption("5", "Ethereum (ETH)", SimulationType.CRIPTO, 0.45),
                SimulationOption("6", "Solana (SOL)", SimulationType.CRIPTO, 0.80)
            )
        }
    }
}