:: compiles all source files to class files
for /f "delims=" %%f in ('dir /b /s *.java') do javac -Xlint:unchecked "%%f"