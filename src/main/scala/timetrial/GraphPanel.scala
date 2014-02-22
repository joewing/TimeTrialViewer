package timetrial

import scala.swing._
import java.awt.font.TextLayout

class GraphPanel(val trace: TraceReader)
    extends Component with TraceListener {

    private val bg = new Color(0, 0, 0)
    private val fg = new Color(255, 0, 0)
    private val hl = new Color(255, 255, 255)
    private lazy val left = font.getSize * 2
    private lazy val bottom = font.getSize * 2

    var tap: Option[StartRecord] = trace.taps.headOption

    preferredSize = new Dimension(640, 480)

    override def paintComponent(g: Graphics2D) {
        super.paintComponent(g)
        g.setBackground(bg)
        g.setColor(fg)
        g.clearRect(0, 0, size.width, size.height)
        tap.map { t =>
            if (t.stat == "hist") {
                showHistogram(g)
            } else {
                showTrace(g)
            }
        }
    }

    private def drawAxes(g: Graphics2D) {
        g.setColor(hl)
        g.drawLine(left, size.height - bottom,
                   size.width, size.height - bottom)
        g.drawLine(left, 0, left, size.height - bottom)

        val xlayout = new TextLayout("Frame", font, g.getFontRenderContext)
        val fx = size.width / 2 - xlayout.getBounds.getWidth / 2
        val fy = size.height - font.getSize
        xlayout.draw(g, fx.toFloat, fy.toFloat)

        val label = tap.head.label
        val ylayout = new TextLayout(label, font, g.getFontRenderContext)
        g.translate(font.getSize,
                    size.height / 2 - ylayout.getBounds.getWidth / 2)
        g.rotate(-math.Pi / 2.0)
        ylayout.draw(g, 0, 0)
    }

    private def showHistogram(g: Graphics2D) {
        val id = tap.head.id
        val data = trace.histogram(id).map(_.value)
        val len = data.length
        if (len == 0) {
            return
        }

        drawAxes(g)
    }

    private def showTrace(g: Graphics2D) {
        val id = tap.head.id
        val data = trace.values(id).map(_.value).toSeq
        val len = data.length
        if (len == 0) {
            return
        }

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

        drawAxes(g)
    }

    def updateTrace = repaint

}
