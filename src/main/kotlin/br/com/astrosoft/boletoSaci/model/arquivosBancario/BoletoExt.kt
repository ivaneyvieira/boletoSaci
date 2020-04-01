package br.com.astrosoft.boletoSaci.model.arquivosBancario

import br.com.caelum.stella.boleto.Boleto

data class BoletoExt(val boleto: Boleto, val chaveERP: String)