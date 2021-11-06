# ExShell

ExShell is an expert system shell with GUI editor written in Java.

This project is intended to be used for educational purposes only.

## Features

- Inference engine with backward chaining
- Knowledge base editor
- Explanation subsystem with inference tree and representation of variables in memory
- FIFO rules order
- Full graphical user interface
	- interdependent editing for rules, variables and domains
	- drag&drop support for rules
	- save and restore current working state

## Build

- Maven

	```
	mvn compile
	mvn package
	java -jar target/exshell-1.0.jar
	```

## Dependencies

- XStream v.1.4.17
