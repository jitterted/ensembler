## Backing up the Railway Postgres Database

### Pre-requisites

* Railway CLI is installed, so we can get the environment variables pointing to the database. See https://docs.railway.app/guides/cli
* Postgres binaries 15 or later (or at least as current as Railway's version) are installed (for pg_dump). See https://blog.railway.app/p/postgre-backup

### Railway Shell

1. railway link
    * Link to the Ensembler project
2. railway service
    * Use the Ensembler service
3. railway shell
4. echo $PGPASSWORD
5. pg_dump -U $PGUSER -h $PGHOST -p $PGPORT -W -F t $PGDATABASE > database-railway-<today's date>.dump
6. exit
