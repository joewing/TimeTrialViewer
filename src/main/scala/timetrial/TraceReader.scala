package timetrial

import java.io.{File, FileReader, LineNumberReader}

class TraceReader(val file: File) {

    private var traceListeners = Seq[TraceListener]()

    private def lines: Stream[String] = {
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

    private def records: Stream[Record] = {
        lines.map { line =>
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
    }

    def taps: Stream[StartRecord] = {
        records.takeWhile(!_.isInstanceOf[DataRecord]).collect {
            case s: StartRecord => s
        }
    }

    def values(id: Int): Stream[DataRecord] = {
        records.collect { case d: DataRecord if d.id == id => d }
    }

    def register(l: TraceListener) {
        traceListeners = traceListeners :+ l
    }

    def process {
        for (l <- traceListeners) {
            l.updateTrace
        }
    }

}
