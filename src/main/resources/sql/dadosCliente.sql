SELECT no      AS codigo,
       cpf_cgc AS documento,
       name    AS nome
FROM sqldados.custp AS C
WHERE (no = :codigo OR :codigo = 0)
  AND (cpf_cgc = :documento OR :documento = '')
  AND (:codigo <> 0 OR :documento <> '')