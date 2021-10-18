:: executes the compiled and packaged printserver application
java -classpath PrintServer.jar;SampleAction.jar;LoginModule.jar -Djava.security.manager -Djava.security.policy==printserver.policy -Djava.security.auth.login.config==printserver_jaas.config printserver.Main
