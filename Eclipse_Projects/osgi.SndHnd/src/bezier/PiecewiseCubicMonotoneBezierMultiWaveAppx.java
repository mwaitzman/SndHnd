




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


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import core.InterpolationPoint;
import core.WaveForm;


import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * Piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * See references section of  https://en.wikipedia.org/wiki/Monotone_cubic_interpolation
 * 
 * @author tgreen
 *
 */
public class PiecewiseCubicMonotoneBezierMultiWaveAppx implements Externalizable {
	
	/**
	 * The interpolation points of the curve.
	 */
	protected ArrayList<InterpolationPoint> interpolationPoints = null;
	
	/**
	 * The number of samples to grab from the waveform to build the interpolation points for the curve.
	 */
	protected int numSamplesToGrab = 0;
	
	/**
	 * Number of unit periods over which to make the curve periodic.
	 */
	protected int numWaves = 0;
	
	/**
	 * The original waveform from which to build the cubic Bezier approximation.
	 */
	protected WaveForm orig = null;
	
	/**
	 * The piecewise list of cubic Bezier curve segments.
	 */
	protected ArrayList<CubicBezierCurve> bezierCurves = null;
	
	/**
	 * The parameter of the second interpolation point, meaning anything below this parameter can be associated with the first curve segment.
	 */
	protected double firstMarkParam = 0.0;
	
	/**
	 * The current curve segment index.
	 */
	protected int currentIndex = 0;

	/**
	 * Constructs the curve.  Used for persistence purposes only.
	 */
	public PiecewiseCubicMonotoneBezierMultiWaveAppx() {
		super();
	}
	
	/**
	 * Constructs the curve.
	 * @param _orig The original waveform from which to build the cubic Bezier approximation.
	 * @param _numWaves Number of unit periods over which to make the curve periodic.
	 * @param roughDraftBezSamplesPerWave The number of interpolation points to collect for each unit period.
	 */
	public PiecewiseCubicMonotoneBezierMultiWaveAppx( WaveForm _orig , int _numWaves , double roughDraftBezSamplesPerWave )
	{
		orig = _orig;
		numWaves = _numWaves;
		numSamplesToGrab = (int)( numWaves * roughDraftBezSamplesPerWave ) + 1;
	}
	
	/**
	 * Gets the number of cubic Bezier curve segments.
	 * @return The number of cubic Bezier curve segments.
	 */
	public int getNumCurves()
	{
		return( bezierCurves.size() );
	}
	
	/**
	 * Clones the wave.
	 * @return The cloned wave.
	 */
	public PiecewiseCubicMonotoneBezierMultiWaveAppx genCloneWave() throws Throwable
	{
		if( interpolationPoints == null )
		{
			System.out.println( "Sampling To Clone Appx..." );
			updateAll();
		}
		PiecewiseCubicMonotoneBezierMultiWaveAppx ret = new PiecewiseCubicMonotoneBezierMultiWaveAppx();
		ret.interpolationPoints = new ArrayList<InterpolationPoint>( interpolationPoints );
		ret.numSamplesToGrab = numSamplesToGrab;
		ret.numWaves = numWaves;
		ret.orig = (WaveForm)( orig.genClone() );
		ret.bezierCurves = new ArrayList<CubicBezierCurve>( bezierCurves );
		ret.firstMarkParam = firstMarkParam;
		ret.currentIndex = currentIndex;
		return( ret );
	}
	
	
	/**
	 * Apply Fritsch-Carlson Monotonicity Constraints.
	 * @param index The interpolation point index at which to check slope constraints.
	 * @param absRawSlope The absolute value of the originally estimated slope.
	 * @return The Fritsch-Carlson adjusted slope.
	 */
	protected double chkRawSlope( int index , double absRawSlope )
	{
		InterpolationPoint p1 = gPointOwrp( index );
		InterpolationPoint p2 = gPointOwrp( index + 1 );
		
		double ptMaxRise = 3.0 * ( p2.getValue() - p1.getValue() );
		double ptRun = p2.getParam() - p1.getParam();
		
		double maxAbsSlope = Math.abs( ptMaxRise / ptRun );
		
	//	System.out.println( "***" );
	//	System.out.println( index );
	//	System.out.println( absRawSlope );
	//	System.out.println( maxAbsSlope );
		
		return( Math.min( absRawSlope , maxAbsSlope ) );
	}
	
	
	/**
	 * Gets the Fritsch-Carlson adjusted estimated slope estimate for a particular interpolation point.
	 * @param index The interpolation point for which to estimate the slope.
	 * @return The estimated slope.
	 */
	protected double getSlopeComp( int index )
	{
		InterpolationPoint p0 = gPointOwrp( index - 1 );
		InterpolationPoint p1 = gPointOwrp( index );
		InterpolationPoint p2 = gPointOwrp( index + 1 );
		
		double slope0 = ( p1.getValue() - p0.getValue() ) / ( p1.getParam() - p0.getParam() );
		double slope1 = ( p2.getValue() - p1.getValue() ) / ( p2.getParam() - p1.getParam() );
		
		// System.out.println( "&&&&&&&&&&&&&" );
		// System.out.println( slope0 );
		// System.out.println( slope1 );
		
		if( ( slope0 * slope1 ) <= 0.0 )
		{
			// System.out.println( "Returned Zero." );
			return( 0.0 );
		}
		
		double rawSlope = ( slope0 + slope1 ) / 2.0;
		double absRawSlope = Math.abs( rawSlope );
		
		double absRawSlope1 = chkRawSlope( index - 1 , absRawSlope );
		double absRawSlope2 = chkRawSlope( index , absRawSlope1 );
		
		double slope = absRawSlope2;
		if( rawSlope < 0.0 )
		{
			slope = -slope;
		}
		
		// System.out.println( "###" );
		// System.out.println( slope );
		
		return( slope );
	}
	
	
	/**
	 * Gets the Fritsch-Carlson adjusted estimated slope estimate for a particular interpolation point.
	 * @param index The interpolation point for which to estimate the slope.
	 * @return The estimated slope.
	 */
	public double getSlope( int index )
	{
		return( getSlopeComp( index ) );
	}
	
	/**
	 * Updates one curve segment to match a change in an interpolation point.
	 * @param index The index of the curve segment to change.
	 */
	public void updateCurve( int index )
	{
		InterpolationPoint strt = gPointBkwrp( index - 1 );
		InterpolationPoint end = gPointFtwrp( index );
		double slope0 = getSlope( index - 1 );
		double slope1 = getSlope( index );
		CubicBezierCurve curve = gCurve( index );
		double[] bezPts = curve.getBezPts();
		
		bezPts[ 0 ] = strt.getValue();
		bezPts[ 3 ] = end.getValue();
		
		double deltaParam = end.getParam() - strt.getParam();
		double ptRun = ( 1.0 / 3.0 ) * deltaParam;
		
		double slopeRise0 = slope0 * ptRun;
		double slopeRise1 = slope1 * ptRun;
		
		bezPts[ 1 ] = bezPts[ 0 ] + slopeRise0;
		bezPts[ 2 ] = bezPts[ 3 ] - slopeRise1;
		
		firstMarkParam = interpolationPoints.get( 1 ).getParam();
	}
	
	/**
	 * Updates the interpolation points of the curve by sampling the waveform.
	 */
	protected void updateInterpolationPoints()
	{
		interpolationPoints = new ArrayList<InterpolationPoint>();
		
		System.out.println( "Caching Approximation..." );
		int cnt;
		for( cnt = 0 ; cnt < numSamplesToGrab ; cnt++ )
		{
			double param = ( (double) cnt ) / ( numSamplesToGrab ) * numWaves;
			double value = orig.eval( param );
			InterpolationPoint pt = new InterpolationPoint( param , value );
			interpolationPoints.add( pt );
		}
		
	}
	
	/**
	 * Updates all curve segments to match the interpolation points.
	 */
	public void updateAll()
	{
		if( interpolationPoints == null )
		{
			updateInterpolationPoints();
		}
		
		currentIndex = 0;
		bezierCurves = new ArrayList<CubicBezierCurve>();
		int max = interpolationPoints.size() - 1;
		int count;
		
		for( count = -1 ; count < ( max + 1 ) ; count++ )
		{
			CubicBezierCurve curve = new CubicBezierCurve();
			InterpolationPoint strt = gPointBkwrp( count );
			InterpolationPoint end = gPointFtwrp( count + 1 );
			curve.setStartParam( strt.getParam() );
			curve.setEndParam( end.getParam() );
			bezierCurves.add( curve );
		}
		
		for( count = 0 ; count < ( max + 2 ) ; count++ )
		{
			updateCurve( count );
		}
		
		firstMarkParam = interpolationPoints.get( 1 ).getParam();
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
	 * Gets interpolation points remapped so that negative indices contain the original points re-parameterized to negative values.
	 * @param index The index of the interpolation point.
	 * @return The interpolation point or remapped interpolation point.
	 */
	public InterpolationPoint gPointBkwrp( int index )
	{
		InterpolationPoint pt = null;
		if( index < 0 )
		{
			InterpolationPoint apt = interpolationPoints.get( interpolationPoints.size() + index );
			pt = new InterpolationPoint( apt );
			while( pt.getParam() > 0.0 )
			{
				pt.setParam( pt.getParam() - 1.0 );
			}
		}
		else
		{
			 pt = interpolationPoints.get( index );
		}
		return( pt );
	}
	
	/**
	 * Gets interpolation points remapped so that indices past the end of the interpolation point list contain the original points re-parameterized past the end of the periodic interval.
	 * @param index The index of the interpolation point.
	 * @return The interpolation point or remapped interpolation point.
	 */
	public InterpolationPoint gPointFtwrp( int index )
	{
		InterpolationPoint pt = null;
		if( index >= interpolationPoints.size() )
		{
			InterpolationPoint apt = interpolationPoints.get( index - interpolationPoints.size() );
			pt = new InterpolationPoint( apt );
			while( pt.getParam() < 1.0 )
			{
				pt.setParam( pt.getParam() + 1.0 );
			}
		}
		else
		{
			 pt = interpolationPoints.get( index );
		}
		return( pt );
	}
	
	/**
	 * Gets interpolation points remapped so that indices outside of the interpolation point list contain the original points re-parameterized to indices outside of the interpolation point list.
	 * @param index The index of the interpolation point.
	 * @return The interpolation point or remapped interpolation point.
	 */
	public InterpolationPoint gPointOwrp( int index )
	{
		InterpolationPoint pt = null;
		if( index < 0 )
		{
			InterpolationPoint apt = interpolationPoints.get( interpolationPoints.size() + index );
			pt = new InterpolationPoint( apt );
			while( pt.getParam() > 0.0 )
			{
				pt.setParam( pt.getParam() - 1.0 );
			}
		}
		else if( index >= interpolationPoints.size() )
		{
			InterpolationPoint apt = interpolationPoints.get( index - interpolationPoints.size() );
			pt = new InterpolationPoint( apt );
			while( pt.getParam() < 1.0 )
			{
				pt.setParam( pt.getParam() + 1.0 );
			}
		}
		else
		{
			 pt = interpolationPoints.get( index );
		}
		return( pt );
	}
	
	/**
	 * Gets the cubic Bezier curve segment at a particular index.
	 * @param index The input index.
	 * @return The cubic Bezier curve segment at the index.
	 */
	public CubicBezierCurve gCurve( int index )
	{
		CubicBezierCurve nd = bezierCurves.get( index );
		return( nd );
	}
	
	/**
	 * Gets the current cubic Bezier curve segment for a particular parameter.
	 * @param param The input parameter value.
	 * @return The current curve segment.
	 */
	public CubicBezierCurve getCurrentCurve( double param )
	{	
		if( ( param < gCurve( currentIndex ).getStartParam() ) && ( currentIndex > 0 ) )
		{
			if( param < firstMarkParam )
			{
				currentIndex = 0;
			}
			
			while( ( param < gCurve( currentIndex ).getStartParam() ) && ( currentIndex > 0 ) )
			{
				currentIndex--;
			}
		}
		
		
		
		while( ( param > gCurve( currentIndex ).getEndParam() ) && ( currentIndex < ( bezierCurves.size() - 1 ) ) )
		{
			currentIndex++;
		}
		
		return( gCurve( currentIndex ) );
	}
	
	/**
	 * Evaluates the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The evaluated curve at the parameter.
	 */
	public final double eval( final double iparam )
	{
		if( interpolationPoints == null )
		{
			updateAll();
		}
		
		final double dparam = iparam / numWaves;
		final double qparam = dparam - (int)( dparam );
		
		final double param = qparam * numWaves;
		
		return( getCurrentCurve( param ).eval( param ) );
	}

	/**
	 * Gets the piecewise list of cubic Bezier curve segments.
	 * @return The piecewise list of cubic Bezier curve segments.
	 */
	public ArrayList<CubicBezierCurve> getBezierCurves() {
		return bezierCurves;
	}

	/**
	 * Sets the piecewise list of cubic Bezier curve segments.
	 * @param bezierCurves The piecewise list of cubic Bezier curve segemnts.
	 */
	protected void setBezierCurves(ArrayList<CubicBezierCurve> bezierCurves) {
		this.bezierCurves = bezierCurves;
		currentIndex = 0;
	}

	/**
	 * Gets the interpolation points of the curve.
	 * @return The interpolation points of the curve.
	 */
	public ArrayList<InterpolationPoint> getInterpolationPoints() {
		return interpolationPoints;
	}
	

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			int plen = myv.getInt("InterpSize");
			interpolationPoints = new ArrayList<InterpolationPoint>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				interpolationPoints.add((InterpolationPoint) (myv.getPropertyEx("Interp_" + count)));
				
			currentIndex = 0;
			updateAll();
			}
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("InterpSize", interpolationPoints.size());
		int plen = interpolationPoints.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Interp_" + count, interpolationPoints.get(count));
		}

		out.writeObject(myv);
	}


}
