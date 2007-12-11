java -classpath ".;jogl.jar;js.jar;gluegen-rt.jar" workcraft/Console 
=======
set PATH_OLD=%PATH% 
set PATH=%PATH%;./dll
java -classpath ".;jogl.jar;js.jar;gluegen-rt.jar" workcraft/Console 
set PATH=%PATH_OLD% 
set PATH_OLD=
>>>>>>> 1.1
