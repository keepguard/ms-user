# ms-user

Microsserviço de usuários e perfis (Person / Company). Schema Postgres: `ms_user`.

## Schema / deploy

Perfil 1:1 com `user_id` (`user_person_profile`, `user_company_profile`). Em prod o serviço usa `ddl-auto=validate`: o índice unique **não** é criado pelo Hibernate no boot.

Antes de publicar uma versão que declara `uk_user_*_profile_user_id`, rode o script:

[`scripts/sql/2026-09-fix-profile-user-id-unique.sql`](scripts/sql/2026-09-fix-profile-user-id-unique.sql)

Ele remove duplicatas (mantém PersonProfile com CPF quando houver) e cria os unique indexes.

Docker local (exemplo):

```bash
docker exec -e PGPASSWORD=keepguard_api_pass -i pg_core \
  psql -U keepguard_api_user -d keepguard_api_db \
  < scripts/sql/2026-09-fix-profile-user-id-unique.sql
```

Após dedup, invalide cache Redis de user se ainda houver 500 em `GET` por `codeUser`.
