package de.frogurth.csvreader

import scalaz._
import Scalaz._
import java.io.File

trait CSVFileReader {
  def readFile(path: String): ValidationNEL[String, File] = {
    Option(path) some {
      p =>
      	if(p.isEmpty)
      	  "Path must not be empty".failNel
      	else {
      	  val f = new File(p)
      	  if(f.isDirectory)
      	    "File is a directory".failNel
      	  else if(!f.exists)
      	    ("File " + path + " does not exist").failNel
      	  else if(!f.canRead)
      	    ("No read permission for file " + path).failNel
      	  else
      	    f.success
      	}
      	
    } none ("Path must not be null".failNel)
  }
}

object CSVFileReader extends CSVFileReader