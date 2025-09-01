## Running the exercises in Gradle

1. Clone the course repository `$ git clone https://github.itu.dk/jst/PCPP2025-public.git`
2. Navigate to the exercises folder for first week, e.g., `$ cd week01/code-exercises/week01exercises/`.
3. Execute the desired program by running `$ gradle -PmainClass=<package>.<java_class> run`, e.g., `$ gradle -PmainClass=exercises01.TestLongCounterExperiments run`.
   - Note that `<java_class>` should include a `main()` method.
   - This step implicitly compiles the code. You can compile only (without executing) by running `$ gradle compileJava`. No need to specify a class; this command compiles all files in `app/src/main/java`.

### Windows PowerShell

If you use Windows PowerShell to run Grade, make sure that it interprets the `Pmainclass` flag correctly by using quotation marks (`""`) around it.
For instance, to run the code for the first week above, you should modify the command above as follows:
Remember to be in the folder for that week so week01exercises for the below

`$ gradle "-PmainClass=exercises01.TestLongCounterExperiments" run`.

In general, for an arbitrary package and class you should run `$ gradle "-PmainClass=<package>.<java_class>" run` in Windows PowerShell.