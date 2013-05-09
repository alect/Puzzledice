package com.mxgraph.io.vdx;

import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxPoint;

/**
 * This class determines the form of a shape to be applied in the
 * property style-shape.
 */
public class mxVdxShapeForm
{
	/**
	 * Shape wrapped, to which the shape will be determinate.
	 */
	mxVdxShape shape;

	/**
	 * Master shape of the shape.
	 */
	mxMasterShape masterShape;

	/**
	 * Master element of the shape.
	 */
	mxMasterElement masterElement;

	/**
	 * Height of the parent cell of the shape.
	 */
	double parentHeight;

	/**
	 * Create a new instance of mxVdxShapeForm.
	 * @param shape Shape wrapped, to which the shape will be determinate.
	 * @param masterShape Master shape of the shape.
	 * @param masterElement Master element of the shape.
	 * @param parentHeight Height of the parent cell of the shape.
	 */
	public mxVdxShapeForm(mxVdxShape shape, mxMasterShape masterShape,
			mxMasterElement masterElement, double parentHeight)
	{
		this.shape = shape;
		this.masterShape = masterShape;
		this.masterElement = masterElement;
		this.parentHeight = parentHeight;

	}

	/**
	 * Returns the constant that represents the Shape.
	 * @return String that represent the form.
	 */
	public String getForm()
	{

		if (isRectangle())
		{
			return mxConstants.SHAPE_RECTANGLE;
		}
		else if (isEllipse())
		{
			return mxConstants.SHAPE_ELLIPSE;
		}
		else if (isRounded())
		{
			return mxConstants.SHAPE_RECTANGLE + ";"
					+ mxConstants.STYLE_ROUNDED + "=1";
		}
		else if (isTriangle())
		{
			return mxConstants.SHAPE_TRIANGLE;
		}
		else if (isHexagon())
		{
			return mxConstants.SHAPE_HEXAGON;
		}
		else if (isRhombus())
		{
			return mxConstants.SHAPE_RHOMBUS;
		}
		else if (isCloud())
		{
			return mxConstants.SHAPE_CLOUD;
		}
		else if (this.isSwimlane())
		{
			return mxConstants.SHAPE_SWIMLANE;
		}
		else if (isDoubleEllipse())
		{
			return mxConstants.SHAPE_DOUBLE_ELLIPSE;
		}
		else if (isCylinder())
		{
			return mxConstants.SHAPE_CYLINDER;
		}
		else if (isAND())
		{
			return "and_h";
		}
		else if (isOR())
		{
			return "or_h";
		}
		else if (isXOR())
		{
			return "xor_h";
		}
		else if (isMUX())
		{
			return "mux_h";
		}
		else if (isInverter())
		{
			return "inv_h";
		}
		else if (isBuff())
		{
			return "buff_h";
		}
		else if (isPapertape())
		{
			return "Paper_tape";
		}
		else if (isInternalstorage())
		{
			return "Internal_storage";
		}
		else if (isStoreddata())
		{
			return "Stored_data";
		}
		else if (isMagnetictape())
		{
			return "Magnetic_tape";
		}
		else if (isData())
		{
			return "Data";
		}

		else if (isDocument())
		{
			return "Document";
		}

		else if (isManualinput())
		{
			return "Manual_input";
		}
		else if (isCard())
		{
			return "Card";
		}
		else if (isPredefinedprocess())
		{
			return "Predefined_process";
		}
		else if (isOff_line_storage())
		{
			return "Off_line_storage";
		}
		else if (isSort())
		{
			return "Sort_2";
		}
		else if (isReturn())
		{
			return "Return";
		}
		else if (isOr())
		{
			return "Or";
		}
		else if (isSystem_database())
		{
			return "System_database";
		}
		else if (isOperation_inspection())
		{
			return "Operation_inspection";
		}
		else if (isSystem_function())
		{
			return "System_function";
		}
		else if (isExternal_organization())
		{
			return "External_organization";
		}
		else if (isExternal_process())
		{
			return "External_process";
		}
		else if (isExternal_process())
		{
			return "External_process";
		}
		else if (isCollate())
		{
			return "Collate";
		}
		else if (isDivided_process())
		{
			return "Divided_process";

		}
		else if (isLined_document())
		{
			return "Lined_document";
		}
		else if (isTransportation())
		{
			return "Transportation";
		}
		else if (isParallel_mode())
		{
			return "Parallel_mode";
		}
		else if (isOff_page_reference())
		{
			return "Off_page_reference";
		}
		else if (isNode())
		{
			return "Node";
		}
		else if (isDocument_file())
		{
			return "Document_file";
		}
		else if (isNote())
		{
			return "Note";
		}
		else if (isActor())
		{
			return "Actor";
		}
		else if (isResistance_seam())
		{
			return "Resistance_seam";
		}
		else if (isSemicircle())
		{
			return "Semicircle";
		}
		else if (isSecurity_booth())
		{
			return "Security_booth";
		}
		else if (isConcentricity())
		{
			return "Concentricity";
		}
		else if (isMain_control())
		{
			return "Main_control";
		}
		else if (isScreening_device())
		{
			return "Screening_device";
		}
		else if (isComponent())
		{
			return "Component";
		}
		else if (isPump())
		{
			return "Winding_connection";
		}
		else if (isDouble_Delta())
		{
			return "double_delta";
		}
		else if (isPLC())
		{
			return "PLC";
		}
		else if (isWeight_device())
		{
			return "Weight_device";
		}
		else if (isCeiling_fan())
		{
			return "Ceiling_fan";
		}

		return "";
	}

	/**
	 * Checks if a shape may to be imported like a rectangle.
	 * @return Returns <code>true</code> if a shape may to be imported like a rectangle.
	 */
	public boolean isRectangle()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Square")
					|| masterElement.getNameU().equals("Rectangle")
					|| masterElement.getNameU().equals("Process")
					|| masterElement.getNameU().equals("External interactor")
					|| masterElement.getNameU().equals("External entity 1")
					|| masterElement.getNameU().equals("Check 2")
					|| masterElement.getNameU().equals("Information/ Material")
					|| masterElement.getNameU().equals(
							"Inspection/ measurement")
					|| masterElement.getNameU().equals("Metric")
					|| masterElement.getNameU().equals("Inspection")
					|| masterElement.getNameU().equals("Check 2 (audit)")
					|| masterElement.getNameU().equals("Compare 2")
					|| masterElement.getNameU().equals("Single line frame")
					|| masterElement.getNameU().equals("Reference rectangle")
					|| masterElement.getNameU().equals("Box")
					|| masterElement.getNameU().equals("Square stone")
					|| masterElement.getNameU().equals("Driveway")
					|| masterElement.getNameU().equals("Rectangular pool")
					|| masterElement.getNameU().equals("Lap pool")
					|| masterElement.getNameU().equals("Competition pool")
					|| masterElement.getNameU().equals("Equipment")
					|| masterElement.getNameU().equals("PBX")
					|| masterElement.getNameU().equals("Radiator")
					|| masterElement.getNameU().equals("Rect. table")
					|| masterElement.getNameU().equals("Night stand")
					|| masterElement.getNameU().equals("Table")
					|| masterElement.getNameU().equals("Desk")
					|| masterElement.getNameU().equals("Slab")
					|| masterElement.getNameU().equals("Square label")
					|| masterElement.getNameU().equals("Page element")
					|| masterElement.getNameU().equals("Small site map node")
					|| masterElement.getNameU().equals("Object In State")
					|| masterElement.getNameU().equals("Process")
					|| masterElement.getNameU().equals("Function / subroutine")
					|| masterElement.getNameU().equals("Interface")
					|| masterElement.getNameU().equals("Byte or variable")
					|| masterElement.getNameU().equals("Actor reference")
					|| masterElement.getNameU().equals("Open/closed bar")
					|| masterElement.getNameU().equals("Command button")
					|| masterElement.getNameU().equals("Generating station")
					|| masterElement.getNameU().equals("Classifier Role")
					|| masterElement.getNameU().equals(
							"Function w / invocation")
					|| masterElement.getNameU().equals("Bookshelf")
					|| masterElement.getNameU().equals("Boundary")
					|| masterElement.getNameU().equals("PBX")
					|| masterElement.getNameU().equals("Rectangular pool")
					|| masterElement.getNameU().equals("Masonry postpillar")
					|| masterElement.getNameU()
							.equals("Text box (single-line)")
					|| masterElement.getNameU().equals("Callout 3")
					|| masterElement.getNameU().equals("Inser")
					|| masterElement.getNameU().equals("Backing/ spacer")
					|| masterElement.getNameU().equals("Thermostat")
					|| masterElement.getNameU().equals("Water meter")
					|| masterElement.getNameU().equals("Controller")
					|| masterElement.getNameU().equals("Mezzanine floor"))
			{
				ret = true;

			}
		}
		if (shape.getNameU().equals("Square")
				|| shape.getNameU().equals("Rectangle")
				|| shape.getNameU().equals("Process")
				|| shape.getNameU().equals("External interactor")
				|| shape.getNameU().equals("External entity 1")
				|| shape.getNameU().equals("Check 2")
				|| shape.getNameU().equals("Information/ Material")
				|| shape.getNameU().equals("Inspection/ measurement")
				|| shape.getNameU().equals("Metric")
				|| shape.getNameU().equals("Inspection")
				|| shape.getNameU().equals("Check 2 (audit)")
				|| shape.getNameU().equals("Compare 2")
				|| shape.getNameU().equals("Single line frame")
				|| shape.getNameU().equals("Reference rectangle")
				|| shape.getNameU().equals("Box")
				|| shape.getNameU().equals("Square stone")
				|| shape.getNameU().equals("Driveway")
				|| shape.getNameU().equals("Rectangular pool")
				|| shape.getNameU().equals("Lap pool")
				|| shape.getNameU().equals("Competition pool")
				|| shape.getNameU().equals("Equipment")
				|| shape.getNameU().equals("PBX")
				|| shape.getNameU().equals("Radiator")
				|| shape.getNameU().equals("Rect. table")
				|| shape.getNameU().equals("Night stand")
				|| shape.getNameU().equals("Table")
				|| shape.getNameU().equals("Desk")
				|| shape.getNameU().equals("Slab")
				|| shape.getNameU().equals("Square label")
				|| shape.getNameU().equals("Page element")
				|| shape.getNameU().equals("Small site map node")
				|| shape.getNameU().equals("Object In State")
				|| shape.getNameU().equals("Process")
				|| shape.getNameU().equals("Function / subroutine")
				|| shape.getNameU().equals("Interface")
				|| shape.getNameU().equals("Interface")
				|| shape.getNameU().equals("Actor reference")
				|| shape.getNameU().equals("Open/closed bar")
				|| shape.getNameU().equals("Command button")
				|| shape.getNameU().equals("Generating station")
				|| shape.getNameU().equals("Classifier Role")
				|| shape.getNameU().equals("Function w / invocation")
				|| shape.getNameU().equals("Bookshelf")
				|| shape.getNameU().equals("Boundary")
				|| shape.getNameU().equals("PBX")
				|| shape.getNameU().equals("Rectangular pool")
				|| shape.getNameU().equals("Masonry postpillar")
				|| shape.getNameU().equals("Text box (single-line)")
				|| shape.getNameU().equals("Callout 3")
				|| shape.getNameU().equals("Insert")
				|| shape.getNameU().equals("Backing/ spacer")
				|| shape.getNameU().equals("Thermostat")
				|| shape.getNameU().equals("Water meter")
				|| shape.getNameU().equals("Controller")
				|| shape.getNameU().equals("Mezzanine floor"))
		{
			ret = true;
		}

		return ret;
	}

	/**
	 * Checks if a shape has east direction by default.
	 * @return Returns <code>true</code> if a shape has east direction by default.
	 */
	public boolean isEastDirection()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Direct data"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Direct data"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a double ellipse.
	 * @return Returns <code>true</code> if a shape may to be imported like a 
	 * double ellipse.
	 */
	public boolean isDoubleEllipse()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Multiple process")
					|| masterElement.getNameU().equals("Final State"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Multiple process")
				|| shape.getNameU().equals("Final State"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a ellipse.
	 * @return Returns <code>true</code> if a shape may to be imported like a ellipse.
	 */
	public boolean isEllipse()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Circle")
					|| masterElement.getNameU().equals("Ellipse")
					|| masterElement.getNameU().equals("On-page reference")
					|| masterElement.getNameU().equals("Process (circle)")
					|| masterElement.getNameU().equals("XOR")
					|| masterElement.getNameU().equals("OR")
					|| masterElement.getNameU().equals("AND")
					|| masterElement.getNameU().equals("Enterprise area")
					|| masterElement.getNameU().equals("Operation")
					|| masterElement.getNameU().equals("System support")
					|| masterElement.getNameU().equals("Connector (TQM)")
					|| masterElement.getNameU().equals("Fabrication")
					|| masterElement.getNameU().equals("On-page reference")
					|| masterElement.getNameU().equals("Reference oval")
					|| masterElement.getNameU().equals("Colored shapes")
					|| masterElement.getNameU().equals("Circle, ellipse")
					|| masterElement.getNameU().equals("Label")
					|| masterElement.getNameU().equals("Circular table")
					|| masterElement.getNameU().equals("Round table")
					|| masterElement.getNameU().equals("Circle label")
					|| masterElement.getNameU().equals("Initial State")
					|| masterElement.getNameU().equals("Use Case")
					|| masterElement.getNameU().equals("Fabrication")
					|| masterElement.getNameU().equals("Water heater")
					|| masterElement.getNameU().equals("Racetrack table")
					|| masterElement.getNameU().equals("Substation")
					|| masterElement.getNameU().equals("Callout 2")
					|| masterElement.getNameU().equals("Spot"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Circle")
				|| shape.getNameU().equals("Ellipse")
				|| shape.getNameU().equals("On-page reference")
				|| shape.getNameU().equals("Process (circle)")
				|| shape.getNameU().equals("XOR")
				|| shape.getNameU().equals("OR")
				|| shape.getNameU().equals("AND")
				|| shape.getNameU().equals("Enterprise area")
				|| shape.getNameU().equals("Operation")
				|| shape.getNameU().equals("System support")
				|| shape.getNameU().equals("Connector (TQM)")
				|| shape.getNameU().equals("Fabrication")
				|| shape.getNameU().equals("On-page reference")
				|| shape.getNameU().equals("Reference oval")
				|| shape.getNameU().equals("Colored shapes")
				|| shape.getNameU().equals("Circle, ellipse")
				|| shape.getNameU().equals("Label")
				|| shape.getNameU().equals("Circular table")
				|| shape.getNameU().equals("Round table")
				|| shape.getNameU().equals("Circle label")
				|| shape.getNameU().equals("Initial State")
				|| shape.getNameU().equals("Use Case")
				|| shape.getNameU().equals("Fabrication")
				|| shape.getNameU().equals("Water heater")
				|| shape.getNameU().equals("Racetrack table")
				|| shape.getNameU().equals("Substation")
				|| shape.getNameU().equals("Callout 2")
				|| shape.getNameU().equals("Spot"))
		{
			ret = true;
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a cloud.
	 * @return Returns <code>true</code> if a shape may to be imported like a cloud.
	 */
	public boolean isCloud()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Object")
					|| masterElement.getNameU().equals("Circle callout")
					|| masterElement.getNameU().equals("Cloud"))
			{
				ret = true;
			}
		}

		if (shape.getNameU().equals("Object")
				|| shape.getNameU().equals("Circle callout")
				|| shape.getNameU().equals("Cloud"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a cylinder.
	 * @return Returns <code>true</code> if a shape may to be imported like a cylinder.
	 */
	public boolean isCylinder()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Drum type")
					|| masterElement.getNameU().equals("Datastore")
					|| masterElement.getNameU().equals("Direct data"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Drum type")
					|| shape.getNameU().equals("Datastore")
					|| shape.getNameU().equals("Direct data"))
			{
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an AND.
	 * @return Returns <code>true</code> if a shape may to be imported like an AND.
	 */
	public boolean isAND()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("And gate")
					|| masterElement.getNameU().equals("AND gate")
					|| masterElement.getNameU().equals("Logic gate 2"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("And gate")
					|| shape.getNameU().equals("AND gate")
					|| shape.getNameU().equals("Logic gate 2"))
			{
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a OR.
	 * @return Returns <code>true</code> if a shape may to be imported like a OR.
	 */
	public boolean isOR()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Or gate")
					|| masterElement.getNameU().equals("OR gate"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Or gate")
					|| shape.getNameU().equals("OR gate"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a MUX.
	 * @return Returns <code>true</code> if a shape may to be imported like a MUX.
	 */
	public boolean isMUX()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Concentrating")
					|| masterElement.getNameU().equals("Signal generator")
					|| masterElement.getNameU().equals("Manual operation"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Concentrating")
					|| shape.getNameU().equals("Signal generator")
					|| shape.getNameU().equals("Manual operation"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an Inverter.
	 * @return Returns <code>true</code> if a shape may to be imported like an Inverter.
	 */
	public boolean isInverter()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Inverter"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Inverter"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isCeiling_fan()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Ceiling fan"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Ceiling fan"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isWeight_device()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Weight device"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Weight device"))
			{
				ret = true;
			}
		}

		return ret;
	}

	//not yet
	public boolean isPLC()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("PLC"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("PLC"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isDouble_Delta()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("6-phase double delta"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("6-phase double delta"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isPump()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Pump")
					|| masterElement.getNameU().equals("Winding connection"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Pump")
					|| shape.getNameU().equals("Winding connection"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isComponent()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Component"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Component"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isScreening_device()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Screening device"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Screening device"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isMain_control()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Main control")
					|| masterElement.getNameU().equals("Radiant panel(face)"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Main control")
					|| shape.getNameU().equals("Radiant panel(face)"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isConcentricity()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Bollard"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Bollard"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isSecurity_booth()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Security booth")
					|| masterElement.getNameU().equals("Encl ceiling lum"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Security booth")
					|| shape.getNameU().equals("Encl ceiling lum"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isSemicircle()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Backing"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Backing"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isResistance_seam()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Resistance seam"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Resistance seam"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isNote()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Note")
					|| masterElement.getNameU().equals("Constraint"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Note")
					|| shape.getNameU().equals("Constraint"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isActor()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Actor")
					|| masterElement.getNameU().equals("User"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Actor")
					|| shape.getNameU().equals("User"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isNode()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Node"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Node"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an Off page reference.
	 * @return Returns <code>true</code> if a shape may to be imported like an Off page reference.
	 */
	public boolean isOff_page_reference()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Off-page reference")
					|| masterElement.getNameU().equals("Lined/Shaded process"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Off-page reference")
					|| shape.getNameU().equals("Lined/Shaded process"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Divided process.
	 * @return Returns <code>true</code> if a shape may to be imported like a Divided process.
	 */
	public boolean isDivided_process()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Divided process"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Divided process"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an External organization.
	 * @return Returns <code>true</code> if a shape may to be imported like an External organization .
	 */
	public boolean isExternal_organization()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("External organization"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("External organization"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an External process.
	 * @return Returns <code>true</code> if a shape may to be imported like an External process.
	 */
	public boolean isExternal_process()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("External process"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("External process"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Collate.
	 * @return Returns <code>true</code> if a shape may to be imported like a Collate.
	 */
	public boolean isCollate()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Collate"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Collate"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a System function.
	 * @return Returns <code>true</code> if a shape may to be imported like a System function.
	 */
	public boolean isSystem_function()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("System function"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("System function"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an OR.
	 * @return Returns <code>true</code> if a shape may to be imported like an OR.
	 */
	public boolean isOr()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Or")
					|| masterElement.getNameU().equals("Stud"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Or")
					|| shape.getNameU().equals("Stud"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an Operation inspection.
	 * @return Returns <code>true</code> if a shape may to be imported like an
	 * Operation inspection.
	 */
	public boolean isOperation_inspection()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Operation/ inspection"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Operation/ inspection"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Return.
	 * @return Returns <code>true</code> if a shape may to be imported like a Return.
	 */
	public boolean isReturn()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Return"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Return"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Predefined process.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Predefined process.
	 */
	public boolean isPredefinedprocess()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Predefined process")
					|| masterElement.getNameU().equals("Procedure"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Predefined process")
					|| shape.getNameU().equals("Procedure"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Document.
	 * @return Returns <code>true</code> if a shape may to be imported like a Document.
	 */

	public boolean isDocument()
	{
		boolean ret = false;
		if (masterElement != null && !isDocument_file())
		{
			if (masterElement.getNameU().startsWith("Document"))
			{
				ret = true;
			}
			if (shape.getNameU().startsWith("Document"))
			{
				ret = true;
			}
		}

		return ret;
	}

	public boolean isDocument_file()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Document/ file"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Document/ file"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a System database.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * System database.
	 */
	public boolean isSystem_database()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("System database"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("System database"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an Off line storage .
	 * @return Returns <code>true</code> if a shape may to be imported like an Off line storage.
	 */
	public boolean isOff_line_storage()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Off-line storage"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Off-line storage"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Manualinput.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Manualinput.
	 */
	public boolean isManualinput()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Manual input"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Manual input"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Card.
	 * @return Returns <code>true</code> if a shape may to be imported like a Card.
	 */
	public boolean isCard()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Card"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Card"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Microform.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Microform.
	 */
	public boolean isMicroform()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Microform"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Microform"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Magnetictape.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Magnetictape.
	 */
	public boolean isMagnetictape()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Magnetic tape")
					|| masterElement.getNameU().equals("Sequential data"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Magnetic tape")
					|| shape.getNameU().equals("Sequential data"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Data.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Data.
	 */
	public boolean isData()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Data")
					|| masterElement.getNameU().equals("I/O"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Data")
					|| shape.getNameU().equals("I/O"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Lined document.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Lined document.
	 */
	public boolean isLined_document()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Lined document"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Lined document"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Transportation.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Transportation.
	 */
	public boolean isTransportation()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Transportation"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Transportation"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Parallel mode.
	 * @return Returns <code>true</code> if a shape may to be imported like a Parallel mode.
	 */
	public boolean isParallel_mode()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Parallel mode")
					|| masterElement.getNameU().equals("Data store"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Parallel mode")
					|| shape.getNameU().equals("Data store"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Lined shaded process.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Lined shaded process.
	 */
	public boolean isLined_Shaded_process()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Lined/Shaded process"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Lined/Shaded process"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a XOR.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * XOR.
	 */
	public boolean isXOR()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("XOR (Exclusive Or)"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("XOR (Exclusive Or)"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Buff.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Buff.
	 */
	public boolean isBuff()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Amplifier"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Amplifier"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Sort.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Sort.
	 */
	public boolean isSort()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Sort 2")
					|| masterElement.getNameU().equals("Sort"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Sort 2")
					|| shape.getNameU().equals("Sort"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Papertape.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Papertape.
	 */
	public boolean isPapertape()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Paper tape")
					|| masterElement.getNameU().equals("Microform"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Paper tape")
					|| shape.getNameU().equals("Microform"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Internalstorage.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Internalstorage.
	 */
	public boolean isInternalstorage()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Internal storage"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Internal storage"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Storeddata.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Storeddata.
	 */
	public boolean isStoreddata()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Stored data"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Stored data"))
			{
				ret = true;
			}
		}

		return ret;
	}

	/**
	 * Checks if a shape has south direction by default.
	 * @return Returns <code>true</code> if a shape has south direction by default.
	 */
	public boolean isSouthDirection()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Move")
					|| masterElement.getNameU().equals("Correcting element")
					|| masterElement.getNameU().equals("Inbound Goods")
					|| masterElement.getNameU().equals("Kickoff")
					|| masterElement.getNameU().equals("Manual file")
					|| masterElement.getNameU().equals("Stop state 2"))
			{
				ret = true;
			}
			if (shape.getNameU().equals("Move")
					|| shape.getNameU().equals("Correcting element")
					|| shape.getNameU().equals("Inbound Goods")
					|| shape.getNameU().equals("Kickoff")
					|| shape.getNameU().equals("Manual file")
					|| shape.getNameU().equals("Stop state 2"))
			{
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Rounded.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Rounded.
	 */
	public boolean isRounded()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Rounded square")
					|| masterElement.getNameU().equals("Rounded rectangle")
					|| masterElement.getNameU().equals("Rounded process")
					|| masterElement.getNameU().equals("Function")
					|| masterElement.getNameU().equals("Main process")
					|| masterElement.getNameU().equals("Component")
					|| masterElement.getNameU().equals("Issue")
					|| masterElement.getNameU().equals("Corner table")
					|| masterElement.getNameU().equals("Main object")
					|| masterElement.getNameU().equals("Web page")
					|| masterElement.getNameU().equals("Pop-up")
					|| masterElement.getNameU().equals("State context"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Rounded square")
				|| shape.getNameU().equals("Rounded rectangle")
				|| shape.getNameU().equals("Rounded process")
				|| shape.getNameU().equals("Function")
				|| shape.getNameU().equals("Main process")
				|| shape.getNameU().equals("Component")
				|| shape.getNameU().equals("Issue")
				|| shape.getNameU().equals("Corner table")
				|| shape.getNameU().equals("Main object")
				|| shape.getNameU().equals("Web page")
				|| shape.getNameU().equals("Pop-up")
				|| shape.getNameU().equals("State context"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Triangle.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Triangle.
	 */
	public boolean isTriangle()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Triangle")
					|| masterElement.getNameU().equals("Alternative")
					|| masterElement.getNameU().equals("Extract")
					|| masterElement.getNameU().equals("Merge")
					|| masterElement.getNameU().equals("Inbound goods")
					|| masterElement.getNameU().equals("Storage")
					|| masterElement.getNameU().equals("Move")
					|| masterElement.getNameU().equals("Store")
					|| masterElement.getNameU().equals("Manual file")
					|| masterElement.getNameU().equals("Reference triangle")
					|| masterElement.getNameU().equals("Correcting element")
					|| masterElement.getNameU().equals("Kickoff"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Triangle")
				|| shape.getNameU().equals("Alternative")
				|| shape.getNameU().equals("Extract")
				|| shape.getNameU().equals("Merge")
				|| shape.getNameU().equals("Inbound goods")
				|| shape.getNameU().equals("Storage")
				|| shape.getNameU().equals("Move")
				|| shape.getNameU().equals("Store")
				|| shape.getNameU().equals("Manual file")
				|| shape.getNameU().equals("Reference triangle")
				|| shape.getNameU().equals("Correcting element")
				|| shape.getNameU().equals("Kickoff"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like an Hexagon.
	 * @return Returns <code>true</code> if a shape may to be imported like an
	 * Hexagon.
	 */
	public boolean isHexagon()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Hexagon")
					|| masterElement.getNameU().equals("Decision 1")
					|| masterElement.getNameU().equals("Event")
					|| masterElement.getNameU().equals("Data transmission")
					|| masterElement.getNameU().equals("Reference hexagon")
					|| masterElement.getNameU().equals("Hex stone")
					|| masterElement.getNameU().equals("6-phase hexagonal"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Hexagon")
				|| shape.getNameU().equals("Decision 1")
				|| shape.getNameU().equals("Event")
				|| shape.getNameU().equals("Data transmission")
				|| shape.getNameU().equals("Reference hexagon")
				|| shape.getNameU().equals("Hex stone")
				|| shape.getNameU().equals("6-phase hexagonal"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape may to be imported like a Rhombus.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Rhombus.
	 */
	public boolean isRhombus()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Decision")
					|| masterElement.getNameU().equals("Entity relationship")
					|| masterElement.getNameU().equals("Decision 2")
					|| masterElement.getNameU().equals("Check")
					|| masterElement.getNameU().equals("Decision 1 (TQM)")
					|| masterElement.getNameU().equals("Decision 2 (TQM)")
					|| masterElement.getNameU().equals("Check 1 (audit)")
					|| masterElement.getNameU().equals("Diamond")
					|| masterElement.getNameU().equals("Decision"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Decision")
				|| shape.getNameU().equals("Entity relationship")
				|| shape.getNameU().equals("Decision 2")
				|| shape.getNameU().equals("Check")
				|| shape.getNameU().equals("Decision 1 (TQM)")
				|| shape.getNameU().equals("Decision 2 (TQM)")
				|| shape.getNameU().equals("Check 1 (audit)")
				|| shape.getNameU().equals("Diamond")
				|| shape.getNameU().equals("Decision"))
		{
			ret = true;
		}
		return ret;
	}

	/**
	 * Checks if a shape is Complex but may to be imported like a simple shape.
	 * @return Returns <code>true</code> if a shape is Complex but may to be
	 * imported like a simple shape.
	 */
	public boolean isSimpleComplex()
	{
		boolean ret = false;
		if (masterElement != null)
		{
			if (masterElement.getNameU().equals("Open/closed bar")
					|| masterElement.getNameU().equals("Command button")
					|| masterElement.getNameU().equals("Water heater")
					|| masterElement.getNameU().equals("Generating station")
					|| masterElement.getNameU().equals("Classifier Role")
					|| masterElement.getNameU().equals(
							"Function w / invocation")
					|| masterElement.getNameU().equals("6-phase hexagonal")
					|| masterElement.getNameU().equals("Boundary")
					|| masterElement.getNameU().equals("Rotating machine")
					|| masterElement.getNameU().equals("Substation")
					|| masterElement.getNameU().equals("Inverter")
					|| masterElement.getNameU().equals("Logic gate 2")
					|| masterElement.getNameU().equals("Amplifier")
					|| masterElement.getNameU().equals("Signal generator")
					|| masterElement.getNameU()
							.equals("Text box (single-line)")
					|| masterElement.getNameU().equals("Callout 2")
					|| masterElement.getNameU().equals("Callout 2")
					|| masterElement.getNameU().equals("Off-line storage")
					|| masterElement.getNameU().equals("Or")
					|| masterElement.getNameU().equals("Divided process")
					|| masterElement.getNameU().equals("Lined/Shaded process")
					|| masterElement.getNameU().startsWith("Document")
					|| masterElement.getNameU().startsWith("Node")
					|| masterElement.getNameU().startsWith("Note")
					|| masterElement.getNameU().startsWith("Constraint")
					|| masterElement.getNameU().startsWith("Main control")
					|| masterElement.getNameU().startsWith("Screening device")
					|| masterElement.getNameU().startsWith("Recorder")
					|| masterElement.getNameU().startsWith("Ceiling fan")
					|| masterElement.getNameU().startsWith("Thermostat")
					|| masterElement.getNameU().startsWith("System Boundary"))
			{
				ret = true;
			}
		}
		if (shape.getNameU().equals("Open/closed bar")
				|| shape.getNameU().equals("Command button")
				|| shape.getNameU().equals("Water heater")
				|| shape.getNameU().equals("Generating station")
				|| shape.getNameU().equals("Classifier Role")
				|| shape.getNameU().equals("Function w / invocation")
				|| shape.getNameU().equals("6-phase hexagonal")
				|| shape.getNameU().equals("Boundary")
				|| shape.getNameU().equals("Rotating machine")
				|| shape.getNameU().equals("Substation")
				|| shape.getNameU().equals("Inverter")
				|| shape.getNameU().equals("Logic gate 2")
				|| shape.getNameU().equals("Amplifier")
				|| shape.getNameU().equals("Signal generator")
				|| shape.getNameU().equals("Text box (single-line)")
				|| shape.getNameU().equals("Callout 3")
				|| shape.getNameU().equals("Callout 3")
				|| shape.getNameU().equals("Off-line storage")
				|| shape.getNameU().equals("Or")
				|| shape.getNameU().equals("Divided process")
				|| shape.getNameU().equals("Lined/Shaded process")
				|| shape.getNameU().startsWith("Document")
				|| shape.getNameU().equals("Data store")
				|| shape.getNameU().equals("Node")
				|| shape.getNameU().equals("Note")
				|| shape.getNameU().equals("Constraint")
				|| shape.getNameU().equals("Main control")
				|| shape.getNameU().equals("Screening device")
				|| shape.getNameU().equals("Recorder")
				|| shape.getNameU().equals("Ceiling fan")
				|| shape.getNameU().equals("Thermostat")
				|| shape.getNameU().equals("System Boundary"))
		{

			ret = true;
		}
		return ret;
	}

	/**
	 * Returns the constant that represents the Shape using the lines of the Shape.
	 * @return String representation of the shape.
	 */
	public String getAproxForm()
	{
		if (isEllipseAprox())
		{
			//The shape is a Ellipse
			return mxConstants.SHAPE_ELLIPSE;
		}
		else if (isRoundedAprox())
		{
			//The Shape is Rounded
			return mxConstants.SHAPE_RECTANGLE + ";"
					+ mxConstants.STYLE_ROUNDED + "=1";
		}
		else if (isTriangleAprox())
		{
			//The shape is a Triangle
			return mxConstants.SHAPE_TRIANGLE;
		}
		else if (isHexagonAprox())
		{
			//The Shape is a Hexagon
			return mxConstants.SHAPE_HEXAGON;
		}
		else if (isRhombusAprox())
		{
			//The Shape is a Rhombus
			return mxConstants.SHAPE_RHOMBUS;
		}
		else
		{
			return mxConstants.SHAPE_RECTANGLE;
		}

	}

	/**
	 * Returns the name of the function for calculate the perimeter.
	 * @param form Form of the shape.
	 * @return Perimeter function.
	 */
	public String getPerimeter(String form)
	{
		if (form.equals(mxConstants.SHAPE_ELLIPSE))
		{
			return mxConstants.PERIMETER_ELLIPSE;
		}
		else if (form.equals(mxConstants.SHAPE_RECTANGLE))
		{
			return mxConstants.PERIMETER_RECTANGLE;
		}
		else if (form.equals(mxConstants.SHAPE_TRIANGLE))
		{
			return mxConstants.PERIMETER_TRIANGLE;
		}
		else if (form.equals(mxConstants.SHAPE_HEXAGON))
		{
			return mxConstants.PERIMETER_HEXAGON;
		}
		else if (form.equals(mxConstants.SHAPE_RHOMBUS))
		{
			return mxConstants.PERIMETER_RHOMBUS;
		}
		return mxConstants.PERIMETER_RECTANGLE;
	}

	/**
	 * Returns the direction of the shape.
	 * @param form Form of the shape.
	 * @return Direction(south, north, east and south)
	 */
	public String getDirection(String form)
	{
		if (!isSouthDirection())
		{
			if (form.equals(mxConstants.SHAPE_TRIANGLE))
			{
				return mxConstants.DIRECTION_NORTH;
			}
			else
			{
				return mxConstants.DIRECTION_EAST;
			}

		}
		else
		{
			return mxConstants.DIRECTION_SOUTH;
		}

	}

	/**
	 * Checks if a shape may to be imported like a Rhombus.<br/>
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Rhombus.
	 */
	public boolean isRhombusAprox()
	{
		boolean isRhombus = false;

		if (shape.getAmountEllipticalArcTo() == 0)
		{
			isRhombus = shape.getAmountLineTo() == 4;

			if (isRhombus)
			{
				mxPoint[] points = shape.getVertexPoints(parentHeight);
				isRhombus &= (points[0].getX() == points[2].getX())
						&& (points[1].getY() == points[3].getY());
			}
		}
		if (masterShape != null && !isRhombus)
		{

			if (masterShape.getAmountEllipticalArcTo() == 0)
			{
				isRhombus = masterShape.getAmountLineTo() == 4;

				if (isRhombus)
				{
					mxPoint[] points = masterShape
							.getVertexPoints(parentHeight);
					isRhombus &= (points[0].getX() == points[2].getX())
							&& (points[1].getY() == points[3].getY());
				}
			}
		}
		return isRhombus;
	}

	/**
	 * Checks if a shape may to be imported like a Ellipse.<br/>
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Ellipse.
	 */
	private boolean isEllipseAprox()
	{
		boolean isEllipse = false;

		isEllipse = shape.hasEllipse();

		if (!isEllipse)
		{
			isEllipse = shape.getAmountEllipticalArcTo() > 0;
			isEllipse &= shape.getAmountLineTo() < 2;
		}

		if (masterShape != null && !isEllipse)
		{
			isEllipse = masterShape.hasEllipse();

			if (!isEllipse)
			{
				isEllipse = masterShape.getAmountEllipticalArcTo() > 0;
				isEllipse &= masterShape.getAmountLineTo() < 2;
			}
		}

		return isEllipse;
	}

	/**
	 * Checks if a shape may to be imported like a Rounded.<br/>
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Rounded.
	 */
	private boolean isRoundedAprox()
	{
		boolean isRounded = false;

		isRounded = (shape.getAmountLineTo() == 2)
				&& (shape.getAmountEllipticalArcTo() == 2);
		isRounded |= (shape.getAmountLineTo() == 4)
				&& (shape.getAmountArcTo() == 4);
		if (masterShape != null && !isRounded)
		{
			isRounded = (masterShape.getAmountLineTo() == 2)
					&& (masterShape.getAmountEllipticalArcTo() == 2);
			isRounded |= (masterShape.getAmountLineTo() == 4)
					&& (masterShape.getAmountArcTo() == 4);
		}

		return isRounded;
	}

	/**
	 * Checks if a shape may to be imported like a Triangle.<br/>
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Triangle.
	 */
	private boolean isTriangleAprox()
	{
		boolean isTriangle = false;

		if (shape.getAmountEllipticalArcTo() == 0)
		{
			isTriangle = shape.getAmountLineTo() == 3;
		}
		if (masterShape != null && !isTriangle)
		{

			if (masterShape.getAmountEllipticalArcTo() == 0)
			{
				isTriangle = masterShape.getAmountLineTo() == 3;
			}
		}

		return isTriangle;
	}

	/**
	 * Checks if a shape may to be imported like a Hexagon.
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Hexagon.
	 */
	private boolean isHexagonAprox()
	{
		boolean isHexagon = false;

		if (shape.getAmountEllipticalArcTo() == 0)
		{
			isHexagon = shape.getAmountLineTo() == 6;
		}
		if (masterShape != null && !isHexagon)
		{

			if (masterShape.getAmountEllipticalArcTo() == 0)
			{
				isHexagon = masterShape.getAmountLineTo() == 6;
			}
		}
		return isHexagon;
	}

	/**
	 * Checks if a shape may to be imported like a Swimlane.
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Swimlane.
	 */
	public boolean isSwimlane()
	{
		boolean isSwimlane = false;
		isSwimlane |= shape.getNameU().equals("Vertical holder");
		isSwimlane |= shape.getNameU().equals("Functional band");

		if ((masterElement != null) && !isSwimlane)
		{
			isSwimlane |= masterElement.getNameU().equals("Vertical holder");
			isSwimlane |= masterElement.getNameU().equals("Functional band");

		}
		return isSwimlane;
	}

	/**
	 * Checks if a shape may to be imported like a Subproces.
	 * This method is approximated.
	 * @return Returns <code>true</code> if a shape may to be imported like a
	 * Subproces.
	 */
	public boolean isSubproces()
	{
		boolean isSwimlane = false;
		isSwimlane |= shape.getNameU().equals("Subproces");

		if ((masterElement != null) && !isSwimlane)
		{
			isSwimlane |= masterElement.getNameU().equals("Subproces");

		}
		return isSwimlane;
	}
}
