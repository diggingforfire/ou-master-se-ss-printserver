:: executes the compiled and packaged printserver application
java -classpath PrintServer.jar;Action.jar;LoginModule.jar;sqlite-jdbc-3.36.0.jar -Djava.security.manager -Djava.security.policy==printserver_role_based.policy -Djava.security.auth.login.config==printserver_jaas.config printserver.Main
