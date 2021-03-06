





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







package waves;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.Meta;
import meta.VersionBuffer;
import core.SongData;
import core.WaveForm;

/**
 * Node for switching between a rough-draft Bezier approximation waveform and a final-draft waveform.
 * 
 * @author tgreen
 *
 */
public class GAppxBezWaveform extends GWaveForm  implements Externalizable {

	/**
	 * The input waveform.
	 */
	private GWaveForm chld;

	/**
	 * The class of the constructing agent used for indexing.
	 */
	private Class clss = getClass();

	/**
	 * Gets the class of the constructing agent used for indexing.
	 * @return The class of the constructing agent used for indexing.
	 */
	public Class getClss() {
		return clss;
	}

	/**
	 * Sets the class of the constructing agent used for indexing.
	 * @param clss The class of the constructing agent used for indexing.
	 */
	public void setClss(Class clss) {
		this.clss = clss;
	}

	/**
	 * Gets the in-agent ID used for indexing.
	 * @return The in-agent ID used for indexing.
	 */
	public String getAppxId() {
		return appxId;
	}

	/**
	 * Sets the in-agent ID used for indexing.
	 * @param appxId The in-agent ID used for indexing.
	 */
	public void setAppxId(String appxId) {
		this.appxId = appxId;
	}

	/**
	 * The in-agent ID used for indexing.
	 */
	private String appxId = "A";

	/**
	 * Constructor used for persistence only.
	 */
	public GAppxBezWaveform() {
	}

	/**
	 * Constructs the node.
	 * @param _chld The input waveform.
	 * @param _appxId The in-agent ID used for indexing.
	 * @param _clss The class of the constructing agent used for indexing.
	 */
	public GAppxBezWaveform(GWaveForm _chld, String _appxId, Class _clss) {
		chld = _chld;
		appxId = _appxId;
		clss = _clss;
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if (s.get(this) != null) {
			return ((WaveForm) (s.get(this)));
		}

		s.put(this, new Integer(5));

		WaveForm w = chld.genWave(s);

		boolean useRoughDraft = false;
		if( SongData.ROUGH_DRAFT_MODE )
		{
			if (SongData.roughDraftMode == SongData.ROUGH_DRAFT_MODE_BEZ_APPROX) {
				useRoughDraft = true;
			}
		}

		WaveForm wv = null;
		try
		{
			wv = SongData.appxBezWaveform(w, clss, appxId, useRoughDraft);
			s.put(this, wv);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
			throw( new RuntimeException( "Failed" ) );
		}

		return (wv);
	}

	/**
	 * Loads new values into the node.
	 * @param in The input waveform.
	 */
	public void load(GWaveForm in) {
		chld = in;
	}

	public Object getChldNodes() {
		return (chld);
	}

	@Override
	public String getName() {
		return ("AppxBez");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return (in instanceof GWaveForm);
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
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setProperty("ClassName", clss.getName());
		myv.setProperty("AppxId",appxId);

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
			String cname = (String)( myv.getProperty("ClassName") );
			clss =
				Class.forName(cname, true, Meta.getDefaultClassLoader());
			appxId = (String)( myv.getProperty("AppxId") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

