Debezzium Connector Curl For Asset DB:

curl --location 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1cG4iOiJjNjNmZjFlNy00MTUxLTQzZjUtODRiMi01M2I4ZDkyMzBjN2IiLCJwdXJwb3NlIjoib3RwX3Rva2VuIiwidXNlcl9pZCI6ImM2M2ZmMWU3LTQxNTEtNDNmNS04NGIyLTUzYjhkOTIzMGM3YiIsInVzZXJfcHJvZHVjdHMiOlt7InByb2R1Y3QiOiJTWVNURU0iLCJwcm9kdWN0Um9sZSI6IkNPTEVORElfQURNSU4iLCJ1c2VyUm9sZSI6IkNPTEVORElfQURNSU4ifV0sImV4cCI6MTc1OTM5OTg3MiwiaWF0IjoxNzU5Mzk5NjkyLCJqdGkiOiJlMTEyNTMyYy03ZDkxLTRmMzUtYmJkNS1mNDRlM2ExM2E3YWEifQ.EjWhj4bL_7w_KWKYUpY2TsHw-xT3YNmI6HfddL6TTOA' \
--data '{
"name": "asset-outbox-connector",
"config": {
"connector.class": "io.debezium.connector.postgresql.PostgresConnector",
"database.hostname": "assetdb",
"database.port": "5432",
"database.user": "admin",
"database.password": "admin",
"database.dbname": "asset",
"database.server.name": "asset_cdc_server",
"table.include.list": "public.outbox_asset",
"topic.prefix": "asset",
"publication.name": "dbz_publication_asset",
"slot.name": "dbz_slot_asset",
"key.converter": "org.apache.kafka.connect.storage.StringConverter",
"value.converter": "org.apache.kafka.connect.json.JsonConverter",
"value.converter.schemas.enable": "false"
}
}'

Debezzium Connector Curl For Order DB:

curl --location 'http://localhost:8083/connectors' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1cG4iOiJjNjNmZjFlNy00MTUxLTQzZjUtODRiMi01M2I4ZDkyMzBjN2IiLCJwdXJwb3NlIjoib3RwX3Rva2VuIiwidXNlcl9pZCI6ImM2M2ZmMWU3LTQxNTEtNDNmNS04NGIyLTUzYjhkOTIzMGM3YiIsInVzZXJfcHJvZHVjdHMiOlt7InByb2R1Y3QiOiJTWVNURU0iLCJwcm9kdWN0Um9sZSI6IkNPTEVORElfQURNSU4iLCJ1c2VyUm9sZSI6IkNPTEVORElfQURNSU4ifV0sImV4cCI6MTc1OTM5OTg3MiwiaWF0IjoxNzU5Mzk5NjkyLCJqdGkiOiJlMTEyNTMyYy03ZDkxLTRmMzUtYmJkNS1mNDRlM2ExM2E3YWEifQ.EjWhj4bL_7w_KWKYUpY2TsHw-xT3YNmI6HfddL6TTOA' \
--data '{
"name": "order-outbox-connector",
"config": {
"connector.class": "io.debezium.connector.postgresql.PostgresConnector",
"database.hostname": "orderdb",
"database.port": "5432",
"database.user": "admin",
"database.password": "admin",
"database.dbname": "order",
"database.server.name": "order_cdc_server",
"table.include.list": "public.outbox_order",
"topic.prefix": "order",
"publication.name": "dbz_publication_order",
"slot.name": "dbz_slot_order",
"key.converter": "org.apache.kafka.connect.storage.StringConverter",
"value.converter": "org.apache.kafka.connect.json.JsonConverter",
"value.converter.schemas.enable": "false"
}
}'