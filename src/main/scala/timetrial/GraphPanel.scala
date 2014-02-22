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

    private var tap: Option[StartRecord] = trace.taps.headOption
    private var data: Seq[DataRecord] = Seq()

    preferredSize = new Dimension(640, 480)

    def setTap(t: StartRecord) {
        tap = Some(t)
        t.stat match {
            case "hist" => data = trace.histogram(t.id)
            case _      => data = trace.values(t.id)
        }
    }

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

    private def showFrame(g: Graphics2D) {
        val frame = data.last.frame
        val str = s"Frame: $frame"
        val layout = new TextLayout(str, font, g.getFontRenderContext)
        val x = size.width - layout.getBounds.getWidth - font.getSize
        val y = layout.getBounds.getHeight * 1.5
        layout.draw(g, x.toFloat, y.toFloat)
    }

    private def drawXAxis(g: Graphics2D, label: String) {
        g.drawLine(left, size.height - bottom,
                   size.width, size.height - bottom)
        val layout = new TextLayout(label, font, g.getFontRenderContext)
        val x = size.width / 2 - layout.getBounds.getWidth / 2
        val y = size.height - font.getSize
        layout.draw(g, x.toFloat, y.toFloat)
    }

    private def drawYAxis(g: Graphics2D, label: String) {
        g.drawLine(left, 0, left, size.height - bottom)
        val layout = new TextLayout(label, font, g.getFontRenderContext)
        val x = font.getSize
        val y = size.height / 2
        val tg = g.create.asInstanceOf[Graphics2D]
        tg.translate(x, y)
        tg.rotate(-math.Pi / 2.0)
        layout.draw(tg, (-layout.getBounds.getWidth / 2.0).toFloat, 0)
        tg.dispose
    }

    private def drawAxes(g: Graphics2D, x: String, y: String) {
        g.setColor(hl)
        drawXAxis(g, x)
        drawYAxis(g, y)
        showFrame(g)
    }

    private def showHistogram(g: Graphics2D) {
        val len = data.length
        if (len == 0) {
            return
        }

        val maxIndex = data.map(_.index).max
        val maxValue = data.map(_.value).max

        val indexRange = maxIndex + 1
        val barWidth = (size.width - left).toDouble / indexRange.toDouble
        val valueRange = maxValue + 1
        val scaley = (size.height - bottom).toDouble / valueRange.toDouble

        g.setColor(fg)
        for (d <- data) {
            val x = left + d.index * barWidth
            val height = d.value * scaley
            val y = (size.height - bottom) - height
            g.fillRect(x.ceil.toInt, y.ceil.toInt,
                       barWidth.ceil.toInt, height.ceil.toInt)
        }

        drawAxes(g, tap.head.label, "n")
    }

    private def showTrace(g: Graphics2D) {
        val len = data.length
        if (len == 0) {
            return
        }

        val values = data.map(_.value)
        val maxValue = values.max
        val minValue = values.min
        val diff = math.max(1, maxValue - minValue)
        val yscale = (size.height - bottom).toDouble / diff.toDouble
        val xscale = (size.width - left).toDouble / len.toDouble

        val npoints = len + 2
        val xpoints = new Array[Int](npoints)
        val ypoints = new Array[Int](npoints)
        for (i <- 0 until len) {
            xpoints(i) = (i * xscale).toInt + left
            ypoints(i) = (values(i) * yscale).toInt
        }
        xpoints(len) = size.width
        ypoints(len) = size.height - bottom
        xpoints(len + 1) = left
        ypoints(len + 1) = size.height - bottom
        g.setColor(fg)
        g.fillPolygon(xpoints, ypoints, npoints)

        drawAxes(g, "Frame", tap.head.label)
    }

    def updateTrace = {
        tap.foreach(setTap)
        repaint
    }

}
