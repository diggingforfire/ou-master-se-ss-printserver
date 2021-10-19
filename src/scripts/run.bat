:: executes the compiled and packaged printserver application
java -classpath PrintServer.jar;Action.jar;LoginModule.jar -Djava.security.manager -Djava.security.policy==printserver_user_based.policy -Djava.security.auth.login.config==printserver_jaas.config printserver.Main
