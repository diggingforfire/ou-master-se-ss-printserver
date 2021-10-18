:: creates jar files
jar -cvf PrintServer.jar printserver/Main.class printserver/LoginCallbackHandler.class
jar -cvf SampleAction.jar printserver/SampleAction.class
jar -cvf LoginModule.jar printserver/module/SampleLoginModule.class printserver/principal/SamplePrincipal.class