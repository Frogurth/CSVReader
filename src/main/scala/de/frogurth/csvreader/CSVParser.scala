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
  
  def parseHeader(c: List[String]): ValidationNEL[String, RecordNEL] = {
    val header = c.head |> {_ split seperator}
    
    try{
      val res = c.tail map {
        line =>
          val values = line split seperator
          if(values.length > header.length)
            throw new Exception("to many values on line " + c.indexOf(line))
          if(values.length < header.length)
        	  throw new Exception("to few values on line " + c.indexOf(line))
          (header zip values).toMap
      }
      
      nel(res.head, res.tail).success
    } catch {
      case e: Exception => e.getMessage.failNel
    }
  }
  
  def records: Option[RecordNEL] = content match {
    case Success(value) => some(value)
    case Failure(_) => none
  }
  
  def errors = content match {
    case Success(_) => none
    case Failure(messages) => some(messages)
  }
  
  def isFailur = content.isFailure
  
  def isSuccess = content.isSuccess
}