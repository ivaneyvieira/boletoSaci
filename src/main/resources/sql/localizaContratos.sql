SELECT I.storeno,
       I.contrno,
       I.status,
       C.no      AS codigo,
       C.cpf_cgc AS documento,
       C.name    AS nome,
       CAST(I.date AS date) as data,
       I.cashamt / 100 AS valor
FROM sqldados.inst AS I
  inner join sqldados.custp AS C
  ON C.no = I.custno
WHERE I.storeno IN (1, 3, 5, 6, 8, 9, 10, 11, 12)
  AND I.status = 0
  AND (C.no = :codigo OR :codigo = 0)
  AND (C.cpf_cgc = :documento OR :documento = '')
  AND (:codigo <> 0 OR :documento <> '')
