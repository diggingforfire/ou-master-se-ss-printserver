:: creates jar files
jar -cvf PrintServer.jar printserver/Main.class printserver/PrintServer.class printserver/PrintServerPermission.class printserver/LoginCallbackHandler.class
jar -cvf LoginModule.jar printserver/module/PrintServerLoginModule.class printserver/principal/PrintServerUserPrincipal.class
jar -cvf Action.jar printserver/action/*.class

::if not exist out mkdir out
::move *.jar out