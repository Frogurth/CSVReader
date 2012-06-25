package de.frogurth.csvreader
import scalaz._
import Scalaz._
import scalaz.effects.IO


case class CSVParser(path: String, seperator: String) extends CSVFileReader {
  type Record = Map[String, String]
  type RecordNEL = NonEmptyList[Record]
  private val content: ValidationNEL[String, RecordNEL] = parse
  
  def parse = {
    readFile(path) flatMap {
      f =>
        try {
          io.Source.fromFile(f).getLines.success
        } catch {
          case e: Throwable => e.getMessage.failNel
        }
    } flatMap {
      lines =>
        if(lines.isEmpty)
          (path + " is empty").failNel
        else parseHeader(lines.toList)
    }
  }
  
  def parseHeader(c: List[String]): Validation[NonEmptyList[String], RecordNEL] = {
    if(c.size <= 1)
      return "No content after header information!".failNel
    
    val header = c.head split seperator
    
    val res = (c.tail map {
      line =>
        val values = line split seperator
        if(values.length > header.length)
          ("to many values on line " + c.indexOf(line)).failNel
        else if(values.length < header.length)
      	  ("to few values on line " + c.indexOf(line)).failNel
        else
          (header zip values).toMap.success
    }) map { _.map(nel(_)) } foldl1 {
      (pv, v) =>
        (pv <**> v) {_ |+| _}
    }
    
    res.get
  }
  
  def records: Option[RecordNEL] = content fold (e => none, s => some(s))
  
  def errors = content fold (e => some(e), s => none)
  
  def isFailur = content.isFailure
  
  def isSuccess = content.isSuccess
}