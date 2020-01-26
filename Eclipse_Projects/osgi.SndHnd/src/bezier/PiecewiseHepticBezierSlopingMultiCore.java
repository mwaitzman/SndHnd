




//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package bezier;


import java.util.ArrayList;
import java.util.Iterator;

import core.CalcCompositeCurve;
import core.CpuInfo;
import core.InterpolationPoint;

/**
 * Piecewise quartic Bezier curve with a linear slope extending off from each end of the piecewise domain.  The curve is C^1 at each end of the piecewise domain.
 * 
 * Written to support multi-core evaluation.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * @author tgreen
 *
 */
public class PiecewiseHepticBezierSlopingMultiCore {
	
	/**
	 * The interpolation points of the curve.
	 */
	protected ArrayList<InterpolationPoint> interpolationPoints = new ArrayList<InterpolationPoint>();
	
	/**
	 * The piecewise list of heptic Bezier curve segments.
	 */
	protected ArrayList<HepticBezierCurve> bezierCurves = null;
	
	/**
	 * The current curve segment index for each core thread.
	 */
	protected final int[] currentIndex = new int[ CpuInfo.getNumCores() ];
	
	/**
	 * The slope at the start of the curve.
	 */
	protected double strtSlope;
	
	/**
	 * The slope at the end of the curve.
	 */
	protected double endSlope;
	
	/**
	 * The start parameter of the piecewise domain.
	 */
	protected double strtParam;
	
	/**
	 * The end parameter of the piecewise domain.
	 */
	protected double endParam;
	
	/**
	 * The curve value at the start of the curve.
	 */
	protected double strtValue;
	
	/**
	 * The curve value at the end of the curve.
	 */
	protected double endValue;

	/**
	 * Constructs the curve.
	 */
	public PiecewiseHepticBezierSlopingMultiCore() {
		super();
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
	}
	
	/**
	 * Gets the slope at the start of the curve.
	 * @return The slope at the start of the curve.
	 */
	public double getStrtSlope()
	{
		return( strtSlope );
	}
	
	/**
	 * Gets The slope at the end of the curve.
	 * @return The slope at the end of the curve.
	 */
	public double getEndSlope()
	{
		return( endSlope );
	}
	
	/**
	 * Gets the number of heptic Bezier curve segments.
	 * @return The number of heptic Bezier curve segments.
	 */
	public int getNumCurves()
	{
		return( bezierCurves.size() );
	}
	
	/**
	 * Gets the heptic Bezier curve segment at a particular index.
	 * @param index The input index.
	 * @return The heptic Bezier curve segment at the index.
	 */
	public HepticBezierCurve gCurve( int index )
	{
		HepticBezierCurve nd = bezierCurves.get( index );
		return( nd );
	}
	
	/**
	 * Gets the current heptic Bezier curve segment for a particular parameter.
	 * @param param The input parameter value.
	 * @param core The core thread performing the evaluation.
	 * @return The current curve segment.
	 */
	public HepticBezierCurve getCurrentCurve( final double param , final int core )
	{	
		while( ( param < gCurve( currentIndex[ core ] ).getStartParam() ) && ( currentIndex[ core ] > 0 ) )
		{
			( currentIndex[ core ] )--;
		}
		
		while( ( param > gCurve( currentIndex[ core ] ).getEndParam() ) && ( currentIndex[ core ] < ( bezierCurves.size() - 1 ) ) )
		{
			( currentIndex[ core ] )++;
		}
		
		return( gCurve( currentIndex[ core ] ) );
	}
	
	/**
	 * Evaluates the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @param core The core thread performing the evaluation.
	 * @return The evaluated curve at the parameter.
	 */
	public double eval( double param , final int core )
	{	
		if( param > endParam )
		{
			return( endValue + ( param - endParam ) * endSlope );
		}
		
		if( param < strtParam )
		{
			return( strtValue + ( param - strtParam ) * strtSlope );
		}
		
		return( getCurrentCurve( param , core ).eval( param ) );
	}
	
	/**
	 * Evaluates the slope of the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @param core The core thread performing the evaluation.
	 * @return The evaluated slope at the parameter.
	 */
	public double evalSlope( final double param , final int core )
	{
		if( param > endParam )
		{
			return( endSlope );
		}
		
		if( param < strtParam )
		{
			return( strtSlope );
		}
		
		return( getCurrentCurve( param , core ).evalSlope( param ) );
	}

	/**
	 * Gets the piecewise list of heptic Bezier curve segments.
	 * @return The piecewise list of heptic Bezier curve segments.
	 */
	public ArrayList<HepticBezierCurve> getBezierCurves() {
		return bezierCurves;
	}

	/**
	 * Sets the piecewise list of heptic Bezier curve segments.
	 * @param bezierCurves The piecewise list of heptic Bezier curve segemnts.
	 */
	protected void setBezierCurves(ArrayList<HepticBezierCurve> bezierCurves) {
		this.bezierCurves = bezierCurves;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
	}
	
	/**
	 * Gets the interpolation point at a particular index.  Indices start at zero.
	 * @param index The index.
	 * @return The interpolation point at the index.
	 */
	public InterpolationPoint gPoint( int index )
	{
		InterpolationPoint pt = interpolationPoints.get( index );
		return( pt );
	}
	
	/**
	 * Gets the first interpolation point.
	 * @return The first interpolation point.
	 */
	public InterpolationPoint getFirstPoint( )
	{
		return( gPoint( 0 ) );
	}
	
	/**
	 * Gets the last interpolation point.
	 * @return The last interpolation point.
	 */
	public InterpolationPoint getLastPoint()
	{
		return( gPoint( interpolationPoints.size() - 1 ) );
	}
	
	/**
	 * Sets this curve to the integral of an input PiecewiseHexticMonotoneBezierFlatMultiCore.
	 * @param dCrv The input curve to be integrated.
	 * @param crvMultiplier Constant by which to multiply the integral to e.g. perform units conversion.
	 */
	public void integrateCurve( PiecewiseHexticBezierNaturalExtentMultiCore dCrv , double crvMultiplier )
	{
		double curSt = 0.0;
		
		interpolationPoints = new ArrayList<InterpolationPoint>();
		bezierCurves = new ArrayList<HepticBezierCurve>();
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
		// dCrv.updateAll();
		ArrayList<HexticBezierCurve> iVect = dCrv.getBezierCurves();
		Iterator<HexticBezierCurve> it = iVect.iterator();
		HexticBezierCurve cbez = null;
		double[] heptBez = null;
		while( it.hasNext() )
		{
			cbez = it.next();
			HepticBezierCurve qbez = new HepticBezierCurve();
			bezierCurves.add( qbez );
			qbez.setStartParam( cbez.getStartParam() );
			qbez.setEndParam( cbez.getEndParam() );
			double[] cubBez = cbez.getBezPts();
			heptBez = CalcCompositeCurve.integrateCurve( cubBez );
	
			double segMultiplier = crvMultiplier * ( cbez.getEndParam() - cbez.getStartParam() );
			CalcCompositeCurve.scaleCurve( heptBez , segMultiplier );
			CalcCompositeCurve.translateCurve( heptBez , curSt );
			curSt = heptBez[ 7 ];
			qbez.setBezPts( heptBez );
			InterpolationPoint intp = new InterpolationPoint( cbez.getStartParam() , heptBez[ 0 ] );
			interpolationPoints.add( intp );
		}
		
		InterpolationPoint intp = new InterpolationPoint( cbez.getEndParam() , heptBez[ 7 ] );
		interpolationPoints.add( intp );
		
		strtSlope = ( dCrv.getFirstPoint().getValue() ) * crvMultiplier;
		endSlope = ( dCrv.getLastPoint().getValue() ) * crvMultiplier;
		endParam = cbez.getEndParam();
		endValue = heptBez[ 7 ];
		
		InterpolationPoint st = interpolationPoints.get( 0 ) ;
		strtParam = st.getParam();
		strtValue = st.getValue();
	}
	
	/**
	 * Gets the interpolation points of the curve.
	 * @return The interpolation points of the curve.
	 */
	public ArrayList<InterpolationPoint> getInterpolationPoints() {
		return interpolationPoints;
	}


}

