package timetrial

import java.io.{File, FileReader, LineNumberReader}
import java.lang.System

class TraceReader(val file: File, val interval: Int = 1000) {

    private var traceListeners = Seq[TraceListener]()
    private var records = Seq[Record]()
    private var lastUpdate: Long = 0

    private def readLines: Stream[String] = {
        val reader = new LineNumberReader(new FileReader(file))
        def nextLine: Stream[String] = {
            val str = reader.readLine
            if (str != null) {
                str #:: nextLine
            } else {
                Stream.Empty
            }
        }
        nextLine
    }

    private def readRecords {
        val recordStream = readLines.map { line =>
            val fields = line.split(",")
            if (fields.length == 5 && fields(0) == "s") {
                val id = fields(1).toInt
                val stat = fields(2)
                val name = fields(3)
                val label = fields(4)
                StartRecord(id, stat, name, label)
            } else if (fields.length == 5 && fields(0) == "d") {
                val id = fields(1).toInt
                val frame = fields(2).toInt
                val index = fields(3).toLong
                val value = fields(4).toLong
                DataRecord(id, frame, index, value)
            } else {
                InvalidRecord(line)
            }
        }
        records = recordStream.toSeq
    }

    def taps: Seq[StartRecord] = {
        records.takeWhile(!_.isInstanceOf[DataRecord]).collect {
            case s: StartRecord => s
        }
    }

    def values(id: Int): Seq[DataRecord] = {
        records.collect { case d: DataRecord if d.id == id => d }
    }

    /** Get values for the last full frame. */
    def histogram(id: Int): Seq[DataRecord] = {
        var last = Seq[DataRecord]()
        var current = Seq[DataRecord]()
        var frame = -1
        for (d <- values(id)) {
            if (d.frame != frame) {
                last = current
                current = Seq[DataRecord]()
                frame = d.frame
            }
            current = current :+ d
        }
        if (last.isEmpty) {
            return current
        } else {
            return last
        }
    }

    def register(l: TraceListener) {
        traceListeners = traceListeners :+ l
    }

    def process {
        val now = System.currentTimeMillis
        if (now - lastUpdate >= interval) {
            readRecords
            for (l <- traceListeners) {
                l.updateTrace
            }
            lastUpdate = System.currentTimeMillis
        }
    }

}
