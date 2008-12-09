
	BUILDING

Edit the paths in profiles.xml to point to your installations of IDEA and 
VisualVM. Build with the Maven command "mvn clean package".


	RELEASING

To release a new version, use the following steps:

	svn up
	mvn release:prepare -DautoVersionSubmodules=true
	mvn release:clean

Then export from SVN the tag which was just created, and build it.


	HOW THE PLUGIN HOOKS INTO VISUALVM

To be able to send commands to VisualVM, we must load some of our own code 
inside the same JVM as VisualVM, so that we would have direct access VisualVM's 
internal classes at runtime. Then we can just send commands from the IDEA plugin 
to the VisualVM over a socket.

This is complicated by the fact that VisualVM uses NetBeans platform, which 
separates each module into its own class loader. So it's not enough that we load 
some code using the system class loader, because it would not see the classes in 
VisualVM's modules. Also, VisualVM uses a custom executable for launching 
itself, but fortunately it allows us to specify JVM command line parameters.

This plugin launches VisualVM with command line parameters (see 
VisualVmLauncher), which cause VisualVM's JVM to load a Java agent provided by 
this plugin (visualvm4idea-visualvm-agent.jar). VisualVM also recieves as 
command line options the path of this plugin's core libraries (visualvm4idea-
core.jar) and the number of the port which this plugin listens to.

The Java agent modifies the bytecode of the Installer class in VisualVM's 
profiler module (see HookLoadingClassAdapter) by inserting there a method call 
to our own HookLoader class. The HookLoader in turn takes from system properties 
the path of our plugin's core libraries, and creates a custom class loader which 
is a child class loader of VisualVM's profiler module and which adds our 
plugin's core libraries to the class path. Then it loads VisualVmHook (from 
visualvm4idea-core.jar) inside that class loader. VisualVmHook in turn connects 
to the IDEA plugin through a socket and begins to wait for commands.

This way the plugin can execute any commands inside VisualVM's JVM and it has 
access to all the same classes as VisualVM's profiler module has.
