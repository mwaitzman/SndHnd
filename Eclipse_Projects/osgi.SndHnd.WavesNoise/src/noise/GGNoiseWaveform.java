





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

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;

/**
 * Node representing a waveform for Gradient Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GGNoiseWaveform extends GWaveForm implements Externalizable {
	
	/**
	 * Input noise waveform from which to calculate the gradient noise.
	 */
	GWaveForm chld;
	
	/**
	 * Sizing multiplier from which to evaluate the input noise.
	 */
	protected double sz = 0.5;
	
	/**
	 * Constructs the node.
	 */
	public GGNoiseWaveform()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = chld.genWave(s);
		
		WaveForm wv = new GnoiseWaveform(w, sz);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param _chld  Input noise waveform from which to calculate the gradient noise.
	 * @param _sz  Sizing multiplier from which to evaluate the input noise.
	 */
	public void load( GWaveForm _chld , double _sz )
	{
		chld = _chld;
		sz = _sz;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- G Noise WaveForm" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		chld = (GWaveForm) in;

	}

	@Override
	public void removeChld() {
		chld = null;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		GNoiseWaveformEditor editor = new GNoiseWaveformEditor( this ,track);
		ProgramDirector.showPropertyEditor(editor, null,
			"G Noise Wave Form Properties");
	}

	/**
	 * Gets the sizing multiplier from which to evaluate the input noise.
	 * @return The sizing multiplier from which to evaluate the input noise.
	 */
	public double getSz() {
		return sz;
	}

	/**
	 * Sets the sizing multiplier from which to evaluate the input noise.
	 * @param sz The sizing multiplier from which to evaluate the input noise.
	 */
	public void setSz(double sz) {
		this.sz = sz;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("Sz", sz);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GWaveForm)( myv.getProperty("Chld") );
			sz = myv.getDouble("Sz");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

