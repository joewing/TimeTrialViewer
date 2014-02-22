package timetrial

import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing.{JFrame, Timer}
import scala.swing._

object TimeTrialViewer extends ActionListener {

    private var traceReader: TraceReader = null

    private def usage {
        println("scala TimeTrialViewer [<datafile>]")
    }

    private def selectFile: File  = {
        val chooser = new FileChooser
        chooser.title = "TimeTrial Viewer"
        val result = chooser.showOpenDialog(null)
        result match {
            case FileChooser.Result.Approve => chooser.selectedFile
            case _ => sys.exit(0)
        }
    }

    def actionPerformed(e: ActionEvent) {
        traceReader.process
    }

    private def run(f: File) {
        traceReader = new TraceReader(f)
        val frame = new Frame
        val panel = new BorderPanel
        val selectionPanel = new FlowPanel
        val graph = new GraphPanel(traceReader)
        val tapList = new TapSelector(traceReader, graph)
        frame.contents = panel
        panel.layout(selectionPanel) = BorderPanel.Position.North
        panel.layout(graph) = BorderPanel.Position.Center
        selectionPanel.contents += new Label("Tap:")
        selectionPanel.contents += tapList
        traceReader.process
        frame.peer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.title = "TimeTrial Viewer"
        frame.pack
        val timer = new Timer(1000, this)
        timer.start
        frame.visible = true
    }

    def main(args: Array[String]) {
        args.length match {
            case 0  => run(selectFile)
            case 1  => run(new File(args(0)))
            case _  => usage
        }
    }

}
