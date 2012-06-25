package de.frogurth.csvreader

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import scalaz._
import Scalaz._
import java.io.File

class CSVReaderSpec extends FlatSpec with ShouldMatchers {
  val emptyTestFile = "src/test/resources/empty.csv"
  val validTestFile = "src/test/resources/valid.csv"
  val invalidTestFile = "src/test/resources/invalid.csv"
  val headerOnlyTestFile = "src/test/resources/headerOnly.csv"
    
  "A FileReder" should "read a file" in {
    val f = CSVFileReader.readFile(emptyTestFile)
    f.isSuccess should be (true)
  }
  
  it should "fail on a directory" in {
    CSVFileReader.readFile("/").isFailure should be (true)
  }
  
  it should "fail on empty path" in {
    CSVFileReader.readFile("").isFailure should be (true)
  }
  
  it should "fail on null pointer" in {
    (CSVFileReader readFile null).isFailure should be (true)
  }
  
  "Parser" should "fail on empty file" in {
    val parser = CSVParser(emptyTestFile, ";")
    parser.isFailur should be (true)
    parser.errors.get.head should be (emptyTestFile + " is empty")
  }
  
  it should "parse valid testfile" in {
    val parser = CSVParser(validTestFile, ",")
    parser.isSuccess should be (true)
    parser.records.get.head should be 
      (Map("name" -> "hans", "family" -> "peter", "age" -> "24").some)
  }
  
  it should "fail on invalid file" in {
    val parser = CSVParser(invalidTestFile, ",")
    parser.isFailur should be (true)
  }
  
  it should "fail with header only" in {
    val parser = CSVParser(headerOnlyTestFile, ",")
    parser.isFailur should be (true)
    parser.errors.get.head should be ("No content after header information!")
  }
}