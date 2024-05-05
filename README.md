# a-dynamic-log-tool
a log tool that can output the method trace and other information while java program is running

## How does it work
The tool is divided into three modules: *log_agent*, *log_server* and *log_shared*.

*log_agent* is mainly responsible for the target java program Instrumentation, modify the content of the method , in the method execution of a specific point in time when you want to output the information . Based on the Java Agent technology to provide the bytecode file in the load bytecode to modify the entrance; the use of Javassist and Bcel libraries to parse the bytecode file, access to bytecode class and method information, and modify the content of the bytecode file. By adding code snippets to the original methods to output log information, the information is passed to the *log_server* via TCP or UDP connection.

The *log_server* is mainly responsible for receiving the log messages passed by *log_agent*, organizing them and outputting them to the log file.

log_shared contains the data object classes for communication between log_agent and log_server. *log_agent* initializes the log information into the corresponding data object when it obtains the log information, then serializes it and passes it to log_server, which then receives the information and re-initializes the object through deserialization, and outputs it to the log file in an orderly fashion. object and outputs it to the log file in an orderly manner.

## How to use it
### before use it (configuration)
Both *log_agent* and *log_server* will read the corresponding configuration files after running. log_agent is agent-config.xml and log_server is config.xml, which need to be placed in the same directory as the *log_agent* and *log_server* jar files when running.
#### agent-config.xml

**The following is a description of some of the important configuration items in the configuration file.** 

``` xml
<logserver>
    <host>localhost</host>
    <port>9000</port>
    <type>tcp</type>
</logserver>
```

`<host>` configures the IP address of the *log_server* process that is the target of the data transfer

`<port>` Configures the service port occupied by the target *log_server* process.

`<type>` configures the type of connection accepted by the target *log_server* process

```xml
<nodeid>
    <application>Demo Multi-tier</application> 
    <tier>Presentation</tier>        
    <node>Node A</node>
</nodeid>
```

`<application>` Configures the name of the java application that the current *log_agent* process fetches

`<tier>`Configure the tier of the server where the current *log_agent* process resides (available in a distributed environment).

`<node>`Configure the cluster node where the current *log_agent* process resides (available in distributed environments)

``` xml
<method-pointcut>
    <enabled>true</enabled>
    <include>net.sourceforge.jeval.*</include>
    <trace-constructor>true</trace-constructor>
    <!-- please ues the value pure/common/param -->
    <mine_pattern>pure</mine_pattern>
</method-pointcut>
```

`<include>` Configures the filtering rules for methods that need to be instrumented, e.g. `net.sourceforge.jeval.*` is the full class name of the class to which the instrument belongs, prefixed with `net.sourceforge.jeval`.

`<trace-constructor>` configures whether or not to instrument constructor methods.

`<mine_pattern>` has three optional values that indicate the type of logging output. pure outputs only the method execution trace including basic information about the method; common outputs class information about the class to which the method belongs on top of pure; and param outputs parameters and return values for each method execution on top of pure.



#### config.xml

**The following is a description of some of the important configuration items in the configuration file.** 

``` xml
<server>
	<port>9123</port>
	<type>tcp</type>
</server>
```

`<port>` Configures the service port occupied by *log_server*.

`<type>` Configures the type of connection opened by *log_server*.

```xml
<logfile>
    <enabled>true</enabled>
    <file>log.txt</file>
</logfile>
```

`<file>` configures the name of the log file output by the current *log_server* process

### run this tool
First, run `java -jar log_server.jar`. Take test.jar as an example, use `java -javaagent:log_agent.jar -jar test.jar` to run test.jar with log_agent.jar. When test.jar finishes running, type `ctrl+c` to stop log_server.jar from running and unoccupying the logging The generated log files are located in the same directory as the log_server.jar file by default.

## How to customize the tool

### `premain()`

`multitier_log_agent.log_agent.Agent.premain(String, Instrumentation)` is the entry point for *log_agent*, where the Java Agent runs the *premain* method before the main method of the target program is run, thus enabling the modify the bytecode file when loading the bytecode.

### transform()

The `multitier_log_agent.log_agent.transform.rule.MethodPointcutRule.transform(CtBehavior, CtClass)` defines blocks of code dynamically inserted in the method body that are responsible for obtaining the corresponding information and transferring it over a TCP or UDP connection to *log_server*.
