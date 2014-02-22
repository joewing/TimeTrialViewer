package timetrial

abstract class Record

case class InvalidRecord(val line: String) extends Record

case class StartRecord(val id: Int,
                       val stat: String,
                       val name: String,
                       val label: String) extends Record {

    override def toString = id.toString + ": " + name

}

case class DataRecord(val id: Int,
                      val frame: Int,
                      val index: Long,
                      val value: Long) extends Record
