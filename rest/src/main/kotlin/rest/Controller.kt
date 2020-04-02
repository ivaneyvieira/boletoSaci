package rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
  @GetMapping("/contratos")
  fun contratos() : List<Contrato> {
    return emptyList()
  }
}