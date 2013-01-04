# SBT Playground

I am using this repo to document some of my experiments to understand SBT better.

Some important key concepts are (obviously) ...

* Sub-Projects
* Settings, Tasks, ...
* Scopes
    * Project, Configuration, Task ...

I am (almost exclusively) use `Build.scala`, because it exposes the full power (and flexibility) of SBT. I am only using `*.sbt` to define the version (so that you have something to play around with to see how the `*.sbt-files` fit into it). In general I recommend to use `Build.scala` as much as possible, because sooner or later you will run into configuration issues, that will require you to use `Build.scala` and by then you want to know, how to use it.

## Getting started

* Just run `> sbt` to compile `./project/Build.scala`
* At the prompt run ...
     > inspect show-time
	 > inspect hello-world
	 > show show-time
	 > show hell-world
* Edit `Build.scala` to play around with the settings
* Either restart sbt or run `> reload` to reload/recompile `Build.scala`

## Getting into it

### 0.0.2

* Explore the difference between the configuration `Compile` and the task `compile`
* Explore if and how the build settings will override project settings
    * e.g. if there is a setting for `ThisBuild` in configuration `Compile`, then how will that become visible in sub-projects?
* Run `> inspect hello-world` and try to guess what the following commands will display
	> show sub-project1/*:hello-world
    > show sub-project1/compile:hello-world
    > show sub-project1/compile:compile::hello-world
    > show sub-project2/compile:compile::hello-world
    > show sub-project1/*:compile::hello-world
    > show sub-project2/*:compile::hello-world

### 0.0.3

* Create a dependency between show-timer and show-timer-upper
* And apply `.toUpperCase` to show-timer-upper

### 0.0.4

* Make sbt-assembly work
* This shows a lot of good stuff, e.g. ...
    * How to make one task depend on another (with `<<=`)
	* The difference between compile, package and publish
	* How to use Artifacts (for publishing something)
* To make it work you can run `> sbt clean publish-local` and can then run the jar files from the commandline (they are self-contained): `> java -jar ./sub1/target/scala-2.9.2/sub-project1_2.9.2-0.0.1-assembly.jar` and `> java -jar ./sub2/target/scala-2.9.2/sub-project2_2.9.2-0.0.2-assembly.jar`
