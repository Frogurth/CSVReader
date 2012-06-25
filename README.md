CSVReader
=========
Scala library to parse a csv-formatted file.

Usage
-----
```scala
val paser = CSVParser("src/test/resources/valid.csv", ",")
```
Initial a parser. The first argument takes a path to the file you want to read, the second takes a string wich seperates the values.

```scala
val result: Option[NonEmptyList[Map[String,String]]] = parser.records
```
The records-method returns an Option that contains a scalaz.NonEmptyList of maps or None if the parsing produced failures.
Each map represents a line from the file parsed in key-value pairs.  

You can check for success or failure with .isSuccess or .isFailur methods

```scala
if(parser.isSuccess) {
 val result = parser.records
} else {
 val errors: Option[NonEmptyList[String]] = parser.errors
}
```

In case of failure you can extract all errormessages using the .error method.
