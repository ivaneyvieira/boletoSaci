SELECT name  AS nome,
       cgc   AS documento,
       addr  AS endereco,
       nei   AS bairro,
       zip   AS cep,
       city  AS cidade,
       state AS uf
FROM sqldados.store
WHERE no = 1