#!/bin/bash

travis_wait

# run the integration tests
mvn verify -pl molgenis-platform-integration-tests --batch-mode --quiet \
  -Dmaven.test.redirectTestOutputToFile=true \
  -Dit_db_user=postgres \
  -Dit_db_password=molgenis \
 t_d