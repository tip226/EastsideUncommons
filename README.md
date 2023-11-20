To populate the database, go to directory PopulateDB and run the following commands in order:
```
java -jar Test.jar
```
To populate the desired data, feel free to edit the .txt files

jdbc hw4:
```
java -cp ojdbc11.jar Test.java
```
Commands for submission:
zip -r ../tip226pham.zip *

## Running as a Jar File

Include all the necessary `.class` files in the jar file
```
jar cvfm Test.jar Manifest.txt -C . .
```

Verify the contents of the JAR file with:
```
jar tf Test.jar
```

Run the jar file with:
```
java -jar Test.jar
```
