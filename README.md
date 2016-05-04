# fact-optimize-segmentation
Small test environment for optimizing image segmentation procedures using evelutionary methods

## How to use.

This is a maven project. Download maven version 3 at https://maven.apache.org/ 

Clone the project and build it by executing 

    $> mvn package
    
Unit tests will be performed and an executable .jar will be placed into the target directory.
The program can be executed by calling

    $> java -jar /path/to/jar.jar /path/to/process.xml
    
The .xml defines the process you want to execute. An example file can be found in this repo. 
For more information see https://sfb876.de/fact-tools/ and https://sfb876.de/streams/quickstart.html
