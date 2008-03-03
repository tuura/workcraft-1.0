set PATH_OLD=%PATH% 
set PATH=%PATH%;./dll
java -splash:pika.gif -classpath ".;jogl.jar;jython.jar;gluegen-rt.jar" workcraft/JavaFrontend 
set PATH=%PATH_OLD% 
set PATH_OLD=
