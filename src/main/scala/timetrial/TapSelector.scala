package timetrial

import scala.swing._

class TapSelector(val trace: TraceReader)
    extends ComboBox(Seq[String]()) with TraceListener {

    def updateTrace {
        val names = trace.taps.map(_.toString).toSeq
//        peer.setModel(ComboBox.newConstantModel(names))
    }

}
