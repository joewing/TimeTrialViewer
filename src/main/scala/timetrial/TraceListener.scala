package timetrial

trait TraceListener {

    val trace: TraceReader

    def updateTrace: Unit

    trace.register(this)

}
