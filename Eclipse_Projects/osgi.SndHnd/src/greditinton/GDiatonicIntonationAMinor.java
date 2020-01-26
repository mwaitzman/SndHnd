




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







package greditinton;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.DiatonicIntonationAMinor;
import core.Intonation;

/**
 * A node representing a diatonic intonation for a western 12-tone scale in the key of A-Minor.
 * 
 * @author tgreen
 *
 */
public class GDiatonicIntonationAMinor extends GZWesternIntonationBase implements Externalizable {

	/**
	 * Constructs the node.
	 */
	public GDiatonicIntonationAMinor() {
	}

	@Override
	public Intonation genInton(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Intonation)( s.get(this) ) );
		}
		
		DiatonicIntonationAMinor wv = new DiatonicIntonationAMinor();
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "DiatonicIntonationAMinor" );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
