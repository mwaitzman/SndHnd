





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







package noise;

import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * A waveform for Ward Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class WardNoiseWaveform extends WaveForm implements Externalizable {
	
	
	
	/**
	 * Input noise from which to generate the Ward Noise.
	 */
	protected WaveForm inoise;
	
	/**
	 * The size of the interval over which define the evaluation interval of the hermite function.
	 */
	protected double sz;

	
	/**
	 * Constructs the waveform.
	 * @param _inoise Input noise from which to generate the Ward Noise.
	 * @param _sz The size of the interval over which define the evaluation interval of the hermite function.
	 */
	public WardNoiseWaveform( WaveForm _inoise , double _sz ) {
		super();
		inoise = _inoise;
		sz = _sz;
	}
	
	@Override
	public double eval(double p) {
		final double psz = p / sz;
		final int ix = VnoiseWaveform.floor( psz );
		final double u1 = Math.abs( psz - ix );
		final double d0 = inoise.eval( ( ix ) * sz );
		final double d1 = inoise.eval( ( ix + 1 ) * sz );
		final double b0 = inoise.eval( ( ix + 0.5 ) * sz );
		final double b3 = inoise.eval( ( ( ix + 1 ) + 0.5 ) * sz );
		/* System.out.println( "*****" );
		System.out.println( sz );
		System.out.println( u1 );
		System.out.println( d0 );
		System.out.println( d1 ); */
		final double b1 = b0 + (1.0/3.0) * d0;
		final double b2 = b3 - (1.0/3.0) * d1;
		final double u0 = 1.0 - u1;
		final double b10 = u0 * b0 + u1 * b1;
		final double b11 = u0 * b1 + u1 * b2;
		final double b12 = u0 * b2 + u1 * b3;
		
		final double b20 = u0 * b10 + u1 * b11;
		final double b21 = u0 * b11 + u1 * b12;
		
		final double b30 = u0 * b20 + u1 * b21;
		/*System.out.println( b30 );*/
		
		return( b30 );
	}
	
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( inoise.genClone() );
		if( wv == inoise )
		{
			return( this );
		}
		else
		{
			return( new WardNoiseWaveform( wv , sz ) );
		}
	}
	
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GWardNoiseWaveform wv = new GWardNoiseWaveform();
		s.put(this, wv);
		
		GWaveForm w = inoise.genWave(s);
		
		wv.load(w,sz);
		
		return( wv );
	}
	
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			// atk = myv.getDouble( "Atk" ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		// myv.setDouble( "Atk" , atk ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		out.writeObject(myv);
	}

}

