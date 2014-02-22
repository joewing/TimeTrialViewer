package timetrial

import scala.swing._
import java.awt.font.TextLayout

class GraphPanel(val trace: TraceReader)
    extends Component with TraceListener {

    private val bg = new Color(0, 0, 0)
    private val fg = new Color(255, 0, 0)
    private val hl = new Color(255, 255, 255)

    var tap = trace.taps.head

    preferredSize = new Dimension(256, 128)

    override def paintComponent(g: Graphics2D) {
        super.paintComponent(g)
        g.setBackground(bg)
        g.setColor(fg)
        g.clearRect(0, 0, size.width, size.height)
        tap.stat match {
            case "hist" => showHistogram(g)
            case _      => showTrace(g)
        }
    }

    private def showHistogram(g: Graphics2D) {
    }

    private def showTrace(g: Graphics2D) {
        val data = trace.values(tap.id).map(_.value).toSeq
        val len = data.length
        if (len == 0) {
            return
        }

        val left = font.getSize * 2
        val bottom = font.getSize * 2
        val maxValue = data.max
        val minValue = data.min
        val diff = math.max(1, maxValue - minValue)
        val yscale = (size.height - left).toDouble / diff.toDouble
        val xscale = (size.width - bottom).toDouble / len.toDouble

        val npoints = len + 2
        val xpoints = new Array[Int](npoints)
        val ypoints = new Array[Int](npoints)
        for (i <- 0 until len) {
            xpoints(i) = (i * xscale).toInt + left
            ypoints(i) = (data(i) * yscale).toInt
        }
        xpoints(len) = size.width
        ypoints(len) = size.height - bottom
        xpoints(len + 1) = left
        ypoints(len + 1) = size.height - bottom
        g.setColor(fg)
        g.fillPolygon(xpoints, ypoints, npoints)

        g.setColor(hl)
        g.drawLine(left, size.height - bottom,
                   size.width, size.height - bottom)
        g.drawLine(left, 0, left, size.height - bottom)

        val xlayout = new TextLayout("Frame", font, g.getFontRenderContext)
        val fx = size.width / 2 - xlayout.getBounds.getWidth / 2
        val fy = size.height - font.getSize
        xlayout.draw(g, fx.toFloat, fy.toFloat)

        val ylayout = new TextLayout(tap.label, font, g.getFontRenderContext)
        g.translate(font.getSize,
                    size.height / 2 - ylayout.getBounds.getWidth / 2)
        g.rotate(-math.Pi / 2.0)
        ylayout.draw(g, 0, 0)

    }

    def updateTrace {
        repaint
    }

}
