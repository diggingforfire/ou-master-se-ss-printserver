:: creates jar files
jar -cvf PrintServer.jar printserver/Main.class printserver/PrintServerPermission.class printserver/LoginCallbackHandler.class printserver/data/DataManager.class printserver/security/Hasher.class
jar -cvf LoginModule.jar printserver/module/PrintServerLoginModule.class printserver/principal/PrintServerPrincipal.class
jar -cvf Action.jar printserver/action/*.class