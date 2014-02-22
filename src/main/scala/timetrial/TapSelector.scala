package timetrial

import javax.swing.{DefaultComboBoxModel, JComboBox}
import java.awt.event.{ActionListener, ActionEvent}
import scala.swing._

class TapSelector(
        val trace: TraceReader,
        val graph: GraphPanel
    ) extends Component with TraceListener with ActionListener {

    override lazy val peer: JComboBox[StartRecord] =
        new JComboBox[StartRecord]()

    private val model = new DefaultComboBoxModel[StartRecord]()
    peer.setModel(model)
    peer.addActionListener(this)

    def actionPerformed(e: ActionEvent) {
        val selection = model.getSelectedItem.asInstanceOf[StartRecord]
        if (selection != null) {
            graph.tap = selection
            graph.repaint
        }
    }

    def updateTrace {
        val taps = trace.taps
        if (taps.length != model.getSize) {
            model.removeAllElements
            taps.foreach { t => model.addElement(t) }
        }
    }

}
