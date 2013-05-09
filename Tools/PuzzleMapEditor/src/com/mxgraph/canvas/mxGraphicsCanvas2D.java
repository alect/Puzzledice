package com.mxgraph.canvas;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;

/**
 * Used for exporting images. To render to an image from a given XML string,
 * graph size and background color, the following code is used:
 * 
 * <code>
 * BufferedImage image = mxUtils.createBufferedImage(width, height, background);
 * Graphics2D g2 = image.createGraphics();
 * mxUtils.setAntiAlias(g2, true, true);
 * XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
 * reader.setContentHandler(new mxSaxOutputHandler(new mxGraphicsCanvas2D(g2)));
 * reader.parse(new InputSource(new StringReader(xml)));
 * </code>
 */
public class mxGraphicsCanvas2D implements mxICanvas2D
{

	/**
	 * Specifies the image scaling quality. Default is Image.SCALE_SMOOTH.
	 * See {@link #scaleImage(Image, int, int)}
	 */
	public static int IMAGE_SCALING = Image.SCALE_SMOOTH;

	/**
	 * Reference to the graphics instance for painting.
	 */
	protected Graphics2D graphics;

	/**
	 * Specifies if anti aliasing should be disabled for rectangles
	 * and orthogonal paths. Default is true.
	 */
	protected boolean autoAntiAlias = true;

	/**
	 * Represents the current state of the canvas.
	 */
	protected transient CanvasState state = new CanvasState();

	/**
	 * Stack of states for save/restore.
	 */
	protected transient Stack<CanvasState> stack = new Stack<CanvasState>();

	/**
	 * Holds the current path.
	 */
	protected transient GeneralPath currentPath;

	/**
	 * Holds the current state for crisp rendering. This should be true while
	 * a subsequent stroke operation should be rendering without anti aliasing.
	 */
	protected transient boolean currentPathIsOrthogonal = true;

	/**
	 * Holds the last point of a moveTo or lineTo operation to determine if the
	 * current path is orthogonal.
	 */
	protected transient Point2D lastPoint;

	/**
	 * Holds the current stroke.
	 */
	protected transient Stroke currentStroke;

	/**
	 * Holds the current font.
	 */
	protected transient Font currentFont;

	/**
	 * Holds the current value for the shadow color. This is used to hold the
	 * input value of a shadow operation. The parsing result of this value is
	 * cached in the global scope as it should be repeating.
	 */
	protected transient String currentShadowValue;

	/**
	 * Holds the current parsed shadow color. This holds the result of parsing
	 * the currentShadowValue, which is an expensive operation.
	 */
	protected transient Color currentShadowColor;

	/**
	 * Constructs a new graphics export canvas.
	 */
	public mxGraphicsCanvas2D(Graphics2D g)
	{
		setGraphics(g);
		state.g = g;
	}

	/**
	 * Sets the graphics instance.
	 */
	public void setGraphics(Graphics2D value)
	{
		graphics = value;
	}

	/**
	 * Returns the graphics instance.
	 */
	public Graphics2D getGraphics()
	{
		return graphics;
	}

	/**
	 * Returns true if automatic anti aliasing is enabled.
	 */
	public boolean isAutoAntiAlias()
	{
		return autoAntiAlias;
	}

	/**
	 * Disabled or enabled automatic anti aliasing.
	 */
	public void setAutoAntiAlias(boolean value)
	{
		autoAntiAlias = value;
	}

	/**
	 * Saves the current canvas state.
	 */
	public void save()
	{
		stack.push(state);
		state = cloneState(state);
		state.g = (Graphics2D) state.g.create();
	}

	/**
	 * Restores the last canvas state.
	 */
	public void restore()
	{
		state = stack.pop();

		// TODO: Check if stroke is part of graphics state
		currentStroke = state.g.getStroke();
		currentFont = null;
	}

	/**
	 * Returns a clone of thec given state.
	 */
	protected CanvasState cloneState(CanvasState state)
	{
		try
		{
			return (CanvasState) state.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 */
	public void scale(double value)
	{
		// This implementation uses custom scale/translate and built-in rotation
		state.scale = state.scale * value;
		currentFont = null;
	}

	/**
	 * 
	 */
	public void translate(double dx, double dy)
	{
		// This implementation uses custom scale/translate and built-in rotation
		state.dx += dx;
		state.dy += dy;
	}

	/**
	 * 
	 */
	public void rotate(double theta, boolean flipH, boolean flipV, double cx,
			double cy)
	{
		cx += state.dx;
		cy += state.dy;

		cx *= state.scale;
		cy *= state.scale;

		// This implementation uses custom scale/translate and built-in rotation
		// Rotation state is part of the AffineTransform in state.transform
		if (flipH ^ flipV)
		{
			double tx = (flipH) ? cx : 0;
			int sx = (flipH) ? -1 : 1;

			double ty = (flipV) ? cy : 0;
			int sy = (flipV) ? -1 : 1;

			state.g.translate(tx, ty);
			state.g.scale(sx, sy);
			state.g.translate(-tx, -ty);
		}

		state.g.rotate(Math.toRadians(theta), cx, cy);
	}

	/**
	 * 
	 */
	public void setStrokeWidth(double value)
	{
		// Lazy and cached instantiation strategy for all stroke properties
		if (value * state.scale != state.strokeWidth)
		{
			state.strokeWidth = value * state.scale;

			// Invalidates cached stroke
			currentStroke = null;
		}
	}

	/**
	 * Caches color conversion as it is expensive.
	 */
	public void setStrokeColor(String value)
	{
		// Lazy and cached instantiation strategy for all stroke properties
		if (!state.strokeColorValue.equals(value))
		{
			state.strokeColorValue = value;
			state.strokeColor = null;
		}
	}

	/**
	 * 
	 */
	public void setDashed(boolean value)
	{
		// Lazy and cached instantiation strategy for all stroke properties
		if (value != state.dashed)
		{
			state.dashed = value;

			// Invalidates cached stroke
			currentStroke = null;
		}
	}

	/**
	 * 
	 */
	public void setDashPattern(String value)
	{
		// FIXME: Initial dash pattern (3, 3) isn't scaled
		if (!state.dashPattern.equals(value))
		{
			float[] dashpattern = null;

			if (state.dashed && state.dashPattern != null)
			{
				String[] tokens = value.split(" ");
				dashpattern = new float[tokens.length];

				for (int i = 0; i < tokens.length; i++)
				{
					dashpattern[i] = (float) (Float.parseFloat(tokens[i]) * state.scale);
				}
			}

			state.dashPattern = dashpattern;
			currentStroke = null;
		}
	}

	/**
	 * 
	 */
	public void setLineCap(String value)
	{
		if (!state.lineCap.equals(value))
		{
			state.lineCap = value;
			currentStroke = null;
		}
	}

	/**
	 * 
	 */
	public void setLineJoin(String value)
	{
		if (!state.lineJoin.equals(value))
		{
			state.lineJoin = value;
			currentStroke = null;
		}
	}

	/**
	 * 
	 */
	public void setMiterLimit(double value)
	{
		if (value != state.miterLimit)
		{
			state.miterLimit = value;
			currentStroke = null;
		}
	}

	/**
	 * 
	 */
	public void setFontSize(double value)
	{
		if (value != state.fontSize)
		{
			state.fontSize = value * state.scale;
			currentFont = null;
		}
	}

	/**
	 * 
	 */
	public void setFontColor(String value)
	{
		if (!state.fontColorValue.equals(value))
		{
			state.fontColorValue = value;
			state.fontColor = null;
		}
	}

	/**
	 * 
	 */
	public void setFontFamily(String value)
	{
		if (!state.fontFamily.equals(value))
		{
			state.fontFamily = value;
			currentFont = null;
		}
	}

	/**
	 * 
	 */
	public void setFontStyle(int value)
	{
		if (value != state.fontStyle)
		{
			state.fontStyle = value;
			currentFont = null;
		}
	}

	/**
	 * 
	 */
	public void setAlpha(double value)
	{
		if (state.alpha != value)
		{
			state.g.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, (float) (value)));
			state.alpha = value;
		}
	}

	/**
	 * 
	 */
	public void setFillColor(String value)
	{
		if (!state.fillColorValue.equals(value))
		{
			state.fillColorValue = value;
			state.fillColor = null;

			// Setting fill color resets paint color
			state.paint = null;
		}
	}

	/**
	 * 
	 */
	public void setGradient(String color1, String color2, double x, double y,
			double w, double h, String direction)
	{
		// LATER: Add lazy instantiation and check if paint already created
		float x1 = (float) (state.dx + x * state.scale);
		float y1 = (float) (state.dy + y * state.scale);
		float x2 = (float) x1;
		float y2 = (float) y1;
		h *= state.scale;
		w *= state.scale;

		if (direction == null || direction.length() == 0
				|| direction.equals(mxConstants.DIRECTION_SOUTH))
		{
			y2 = (float) (y1 + h);
		}
		else if (direction.equals(mxConstants.DIRECTION_EAST))
		{
			x2 = (float) (x1 + w);
		}
		else if (direction.equals(mxConstants.DIRECTION_NORTH))
		{
			y1 = (float) (y1 + h);
		}
		else if (direction.equals(mxConstants.DIRECTION_WEST))
		{
			x1 = (float) (x1 + w);
		}

		state.paint = new GradientPaint(x1, y1, parseColor(color1), x2, y2,
				parseColor(color2), true);
	}

	/**
	 * Helper method that uses {@link mxUtils#parseColor(String)}. Subclassers
	 * can override this to implement caching for frequently used colors.
	 */
	protected Color parseColor(String hex)
	{
		return mxUtils.parseColor(hex);
	}

	/**
	 * 
	 */
	public void setGlassGradient(double x, double y, double w, double h)
	{
		double size = 0.4;
		x = state.dx + x * state.scale;
		y = state.dy + y * state.scale;
		h *= state.scale;
		w *= state.scale;

		state.paint = new GradientPaint((float) x, (float) y, new Color(1, 1,
				1, 0.9f), (float) (x), (float) (y + h * size), new Color(1, 1,
				1, 0.3f));
	}

	/**
	 *
	 */
	public void rect(double x, double y, double w, double h)
	{
		currentPath = new GeneralPath();
		currentPath.append(new Rectangle2D.Double(state.dx + x * state.scale,
				state.dy + y * state.scale, w * state.scale, h * state.scale),
				false);
	}

	/**
	 * Implements a rounded rectangle using a path.
	 */
	public void roundrect(double x, double y, double w, double h, double dx,
			double dy)
	{
		begin();
		moveTo(x + dx, y);
		lineTo(x + w - dx, y);
		quadTo(x + w, y, x + w, y + dy);
		lineTo(x + w, y + h - dy);
		quadTo(x + w, y + h, x + w - dx, y + h);
		lineTo(x + dx, y + h);
		quadTo(x, y + h, x, y + h - dy);
		lineTo(x, y + dy);
		quadTo(x, y, x + dx, y);
	}

	/**
	 * 
	 */
	public void ellipse(double x, double y, double w, double h)
	{
		currentPath = new GeneralPath();
		currentPath.append(new Ellipse2D.Double(state.dx + x * state.scale,
				state.dy + y * state.scale, w * state.scale, h * state.scale),
				false);
		currentPathIsOrthogonal = false;
	}

	/**
	 * 
	 */
	public void image(double x, double y, double w, double h, String src,
			boolean aspect, boolean flipH, boolean flipV)
	{
		if (src != null && w > 0 && h > 0)
		{
			Image img = loadImage(src);

			if (img != null)
			{
				Rectangle bounds = getImageBounds(img, x, y, w, h, aspect);
				img = scaleImage(img, bounds.width, bounds.height);

				if (img != null)
				{
					drawImage(
							createImageGraphics(bounds.x, bounds.y,
									bounds.width, bounds.height, flipH, flipV),
							img, bounds.x, bounds.y);
				}
			}
		}
	}

	/**
	 * 
	 */
	protected void drawImage(Graphics2D graphics, Image image, int x, int y)
	{
		graphics.drawImage(image, x, y, null);
	}

	/**
	 * Hook for image caching.
	 */
	protected Image loadImage(String src)
	{
		return mxUtils.loadImage(src);
	}

	/**
	 * 
	 */
	protected final Rectangle getImageBounds(Image img, double x, double y,
			double w, double h, boolean aspect)
	{
		x = state.dx + x * state.scale;
		y = state.dy + y * state.scale;
		w *= state.scale;
		h *= state.scale;

		if (aspect)
		{
			Dimension size = getImageSize(img);
			double s = Math.min(w / size.width, h / size.height);
			int sw = (int) Math.round(size.width * s);
			int sh = (int) Math.round(size.height * s);
			x += (w - sw) / 2;
			y += (h - sh) / 2;
			w = sw;
			h = sh;
		}
		else
		{
			w = Math.round(w);
			h = Math.round(h);
		}

		return new Rectangle((int) x, (int) y, (int) w, (int) h);
	}

	/**
	 * Returns the size for the given image.
	 */
	protected Dimension getImageSize(Image image)
	{
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}

	/**
	 * Uses {@link #IMAGE_SCALING} to scale the given image.
	 */
	protected Image scaleImage(Image img, int w, int h)
	{
		return img.getScaledInstance(w, h, IMAGE_SCALING);
	}

	/**
	 * Creates a graphic instance for rendering an image.
	 */
	protected final Graphics2D createImageGraphics(double x, double y,
			double w, double h, boolean flipH, boolean flipV)
	{
		Graphics2D g2 = state.g;

		if (flipH || flipV)
		{
			g2 = (Graphics2D) g2.create();
			int sx = 1;
			int sy = 1;
			int dx = 0;
			int dy = 0;

			if (flipH)
			{
				sx = -1;
				dx = (int) (-w - 2 * x);
			}

			if (flipV)
			{
				sy = -1;
				dy = (int) (-h - 2 * y);
			}

			g2.scale(sx, sy);
			g2.translate(dx, dy);
		}

		return g2;
	}

	/**
	 * Draws the given text.
	 */
	public void text(double x, double y, double w, double h, String str,
			String align, String valign, boolean vertical)
	{
		if (!state.fontColorValue.equals(mxConstants.NONE))
		{
			x = state.dx + x * state.scale;
			y = state.dy + y * state.scale;
			w *= state.scale;
			h *= state.scale;

			// Font-metrics needed below this line
			Graphics2D g2 = createTextGraphics(x, y, w, h, vertical);
			FontMetrics fm = g2.getFontMetrics();
			String[] lines = str.split("\n");

			y = getVerticalTextPosition(x, y, w, h, align, valign, vertical,
					fm, lines);
			x = getHorizontalTextPosition(x, y, w, h, align, valign, vertical,
					fm, lines);

			for (int i = 0; i < lines.length; i++)
			{
				double dx = 0;

				if (align != null)
				{
					if (align.equals(mxConstants.ALIGN_CENTER))
					{
						int sw = fm.stringWidth(lines[i]);
						dx = (w - sw) / 2;
					}
					else if (align.equals(mxConstants.ALIGN_RIGHT))
					{
						int sw = fm.stringWidth(lines[i]);
						dx = w - sw;
					}
				}

				g2.drawString(lines[i], (int) Math.round(x + dx),
						(int) Math.round(y));
				y += fm.getHeight() + mxConstants.LINESPACING;
			}
		}
	}

	/**
	 * Returns a new graphics instance with the correct color and font for
	 * text rendering.
	 */
	protected final Graphics2D createTextGraphics(double x, double y, double w,
			double h, boolean vertical)
	{
		Graphics2D g2 = state.g;
		updateFont();

		if (vertical)
		{
			g2 = (Graphics2D) state.g.create();
			g2.rotate(-Math.PI / 2, x + w / 2, y + h / 2);
		}

		if (state.fontColor == null)
		{
			state.fontColor = parseColor(state.fontColorValue);
		}

		g2.setColor(state.fontColor);

		return g2;
	}

	/**
	 * 
	 */
	protected double getVerticalTextPosition(double x, double y, double w,
			double h, String align, String valign, boolean vertical,
			FontMetrics fm, String[] lines)
	{
		double lineHeight = fm.getHeight() + mxConstants.LINESPACING;
		double textHeight = lines.length * lineHeight;
		double dy = h - textHeight;

		// Top is default
		if (valign == null || valign.equals(mxConstants.ALIGN_TOP))
		{
			y = Math.max(y - 2 * state.scale, y + dy / 2);
		}
		else if (valign.equals(mxConstants.ALIGN_MIDDLE))
		{
			y = y + dy / 2;
		}
		else if (valign.equals(mxConstants.ALIGN_BOTTOM))
		{
			y = Math.min(y, y + dy);
		}

		return y + fm.getHeight() * 0.75;
	}

	/**
	 * This implementation returns x.
	 */
	protected double getHorizontalTextPosition(double x, double y, double w,
			double h, String align, String valign, boolean vertical,
			FontMetrics fm, String[] lines)
	{
		if (align == null || align.equals(mxConstants.ALIGN_LEFT))
		{
			x += 2 * state.scale;
		}

		return x;
	}

	/**
	 * 
	 */
	public void begin()
	{
		currentPath = new GeneralPath();
		currentPathIsOrthogonal = true;
		lastPoint = null;
	}

	/**
	 * 
	 */
	public void moveTo(double x, double y)
	{
		if (currentPath != null)
		{
			currentPath.moveTo((float) (state.dx + x * state.scale),
					(float) (state.dy + y * state.scale));

			if (isAutoAntiAlias())
			{
				lastPoint = new Point2D.Double(x, y);
			}
		}
	}

	/**
	 * 
	 */
	public void lineTo(double x, double y)
	{
		if (currentPath != null)
		{
			currentPath.lineTo((float) (state.dx + x * state.scale),
					(float) (state.dy + y * state.scale));

			if (isAutoAntiAlias())
			{
				if (lastPoint != null && currentPathIsOrthogonal
						&& x != lastPoint.getX() && y != lastPoint.getY())
				{
					currentPathIsOrthogonal = false;
				}

				lastPoint = new Point2D.Double(x, y);
			}
		}
	}

	/**
	 * 
	 */
	public void quadTo(double x1, double y1, double x2, double y2)
	{
		if (currentPath != null)
		{
			currentPath.quadTo((float) (state.dx + x1 * state.scale),
					(float) (state.dy + y1 * state.scale),
					(float) (state.dx + x2 * state.scale),
					(float) (state.dy + y2 * state.scale));
			currentPathIsOrthogonal = false;
		}
	}

	/**
	 * 
	 */
	public void curveTo(double x1, double y1, double x2, double y2, double x3,
			double y3)
	{
		if (currentPath != null)
		{
			currentPath.curveTo((float) (state.dx + x1 * state.scale),
					(float) (state.dy + y1 * state.scale),
					(float) (state.dx + x2 * state.scale),
					(float) (state.dy + y2 * state.scale),
					(float) (state.dx + x3 * state.scale),
					(float) (state.dy + y3 * state.scale));
			currentPathIsOrthogonal = false;
		}
	}

	/**
	 * 
	 */
	public void arcTo(double rx, double ry, double xAxisRotation,
			boolean largeArc, boolean sweep, double x, double y)
	{
		if (currentPath != null)
		{
			rx *= state.scale;
			ry *= state.scale;

			x = x * state.scale + state.dx;
			y = y * state.scale + state.dy;

			Point2D currentPoint = currentPath.getCurrentPoint();
			double x0 = currentPoint.getX();
			double y0 = currentPoint.getY();

			// Compute the half distance between the current and the final point
			double dx2 = (x0 - x) / 2.0;
			double dy2 = (y0 - y) / 2.0;
			// Convert angle from degrees to radians
			xAxisRotation = Math.toRadians(xAxisRotation % 360.0);
			double cosAngle = Math.cos(xAxisRotation);
			double sinAngle = Math.sin(xAxisRotation);

			//
			// Step 1 : Compute (x1, y1)
			//
			double x1 = (cosAngle * dx2 + sinAngle * dy2);
			double y1 = (-sinAngle * dx2 + cosAngle * dy2);
			// Ensure radii are large enough
			rx = Math.abs(rx);
			ry = Math.abs(ry);
			double Prx = rx * rx;
			double Pry = ry * ry;
			double Px1 = x1 * x1;
			double Py1 = y1 * y1;
			// check that radii are large enough
			double radiiCheck = Px1 / Prx + Py1 / Pry;

			if (radiiCheck > 1)
			{
				rx = Math.sqrt(radiiCheck) * rx;
				ry = Math.sqrt(radiiCheck) * ry;
				Prx = rx * rx;
				Pry = ry * ry;
			}

			//
			// Step 2 : Compute (cx1, cy1)
			//
			double sign = (largeArc == sweep) ? -1 : 1;
			double sq = ((Prx * Pry) - (Prx * Py1) - (Pry * Px1))
					/ ((Prx * Py1) + (Pry * Px1));
			sq = (sq < 0) ? 0 : sq;
			double coef = (sign * Math.sqrt(sq));
			double cx1 = coef * ((rx * y1) / ry);
			double cy1 = coef * -((ry * x1) / rx);

			//
			// Step 3 : Compute (cx, cy) from (cx1, cy1)
			//
			double sx2 = (x0 + x) / 2.0;
			double sy2 = (y0 + y) / 2.0;
			double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
			double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

			//
			// Step 4 : Compute the angleStart (angle1) and the angleExtent (dangle)
			//
			double ux = (x1 - cx1) / rx;
			double uy = (y1 - cy1) / ry;
			double vx = (-x1 - cx1) / rx;
			double vy = (-y1 - cy1) / ry;
			double p, n;
			// Compute the angle start
			n = Math.sqrt((ux * ux) + (uy * uy));
			p = ux; // (1 * ux) + (0 * uy)
			sign = (uy < 0) ? -1.0 : 1.0;
			double angleStart = Math.toDegrees(sign * Math.acos(p / n));

			// Compute the angle extent
			n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
			p = ux * vx + uy * vy;
			sign = (ux * vy - uy * vx < 0) ? -1.0 : 1.0;
			double angleExtent = Math.toDegrees(sign * Math.acos(p / n));

			if (!sweep && angleExtent > 0)
			{
				angleExtent -= 360f;
			}
			else if (sweep && angleExtent < 0)
			{
				angleExtent += 360f;
			}

			angleExtent %= 360f;
			angleStart %= 360f;

			Arc2D.Double arc = new Arc2D.Double();
			arc.x = cx - rx;
			arc.y = cy - ry;
			arc.width = rx * 2.0;
			arc.height = ry * 2.0;
			arc.start = -angleStart;
			arc.extent = -angleExtent;

			currentPath.append(arc, true);
			currentPathIsOrthogonal = false;
		}
	}

	/**
	 * Closes the current path.
	 */
	public void close()
	{
		if (currentPath != null)
		{
			currentPath.closePath();
		}
	}

	/**
	 * 
	 */
	public void stroke()
	{
		if (currentPath != null
				&& !state.strokeColorValue.equals(mxConstants.NONE))
		{
			if (state.strokeColor == null)
			{
				state.strokeColor = parseColor(state.strokeColorValue);
			}

			updateStroke();
			state.g.setColor(state.strokeColor);

			Object previousHint = null;

			if (isAutoAntiAlias() && currentPathIsOrthogonal)
			{
				previousHint = state.g
						.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
				state.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
			}

			state.g.draw(currentPath);

			if (previousHint != null)
			{
				state.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						previousHint);
			}
		}
	}

	/**
	 * 
	 */
	public void fill()
	{
		if (currentPath != null
				&& (!state.fillColorValue.equals(mxConstants.NONE) || state.paint != null))
		{
			if (state.paint != null)
			{
				state.g.setPaint(state.paint);
			}
			else
			{
				if (state.fillColor == null)
				{
					state.fillColor = parseColor(state.fillColorValue);
				}

				state.g.setColor(state.fillColor);
				state.g.setPaint(null);
			}

			state.g.fill(currentPath);
		}
	}

	/**
	 * 
	 */
	public void fillAndStroke()
	{
		fill();
		stroke();
	}

	/**
	 * 
	 */
	public void shadow(String value)
	{
		if (value != null && currentPath != null)
		{
			if (currentShadowColor == null || currentShadowValue == null
					|| !currentShadowValue.equals(value))
			{
				currentShadowColor = parseColor(value);
				currentShadowValue = value;
			}

			updateStroke();
			state.g.setColor(currentShadowColor);
			state.g.fill(currentPath);
			state.g.draw(currentPath);
		}
	}

	/**
	 * 
	 */
	public void clip()
	{
		if (currentPath != null)
		{
			state.g.clip(currentPath);
		}
	}

	/**
	 * 
	 */
	protected void updateFont()
	{
		// LATER: Make currentFont part of state
		if (currentFont == null)
		{
			int size = (int) Math.round(state.fontSize * state.scale);

			int style = ((state.fontStyle & mxConstants.FONT_BOLD) == mxConstants.FONT_BOLD) ? Font.BOLD
					: Font.PLAIN;
			style += ((state.fontStyle & mxConstants.FONT_ITALIC) == mxConstants.FONT_ITALIC) ? Font.ITALIC
					: Font.PLAIN;

			currentFont = createFont(state.fontFamily, style, size);
			state.g.setFont(currentFont);
		}
	}

	/**
	 * Hook for subclassers to implement font caching.
	 */
	protected Font createFont(String family, int style, int size)
	{
		return new Font(family, style, size);
	}

	/**
	 * 
	 */
	protected void updateStroke()
	{
		if (currentStroke == null)
		{
			int cap = BasicStroke.CAP_BUTT;

			if (state.lineCap.equals("round"))
			{
				cap = BasicStroke.CAP_ROUND;
			}
			else if (state.lineCap.equals("square"))
			{
				cap = BasicStroke.CAP_SQUARE;
			}

			int join = BasicStroke.JOIN_MITER;

			if (state.lineJoin.equals("round"))
			{
				join = BasicStroke.JOIN_ROUND;
			}
			else if (state.lineJoin.equals("bevel"))
			{
				join = BasicStroke.JOIN_BEVEL;
			}

			float miterlimit = (float) state.miterLimit;

			currentStroke = new BasicStroke((float) state.strokeWidth, cap,
					join, miterlimit,
					(state.dashed) ? state.dashPattern : null, 0);
			state.g.setStroke(currentStroke);
		}
	}

	/**
	 * 
	 */
	protected class CanvasState implements Cloneable
	{
		/**
		 * 
		 */
		protected double alpha = 1;

		/**
		 * 
		 */
		protected double scale = 1;

		/**
		 * 
		 */
		protected double dx = 0;

		/**
		 * 
		 */
		protected double dy = 0;

		/**
		 * 
		 */
		protected double miterLimit = 10;

		/**
		 * 
		 */
		protected int fontStyle = 0;

		/**
		 * 
		 */
		protected double fontSize = mxConstants.DEFAULT_FONTSIZE;

		/**
		 * 
		 */
		protected String fontFamily = mxConstants.DEFAULT_FONTFAMILY;

		/**
		 * 
		 */
		protected String fontColorValue = "#000000";

		/**
		 * 
		 */
		protected Color fontColor;

		/**
		 * 
		 */
		protected String lineCap = "flat";

		/**
		 * 
		 */
		protected String lineJoin = "miter";

		/**
		 * 
		 */
		protected double strokeWidth = 1;

		/**
		 * 
		 */
		protected String strokeColorValue = mxConstants.NONE;

		/**
		 * 
		 */
		protected Color strokeColor;

		/**
		 * 
		 */
		protected String fillColorValue = mxConstants.NONE;

		/**
		 * 
		 */
		protected Color fillColor;

		/**
		 * 
		 */
		protected Paint paint;

		/**
		 * 
		 */
		protected boolean dashed = false;

		/**
		 * 
		 */
		protected float[] dashPattern = { 3, 3 };

		/**
		 * Stores the actual state.
		 */
		protected transient Graphics2D g;

		/**
		 * 
		 */
		public Object clone() throws CloneNotSupportedException
		{
			return super.clone();
		}

	}

}
