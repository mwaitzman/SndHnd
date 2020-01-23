





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

import java.util.ArrayList;

import waves.AbsValWaveform;
import waves.ZeroWaveform;
import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ConstantNonClampedCoefficient;

/**
 * Class for generating a waveform approximating turbulence as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class TurbulenceWave {
	
	
	/**
	 * Generates the turbulence waveform.
	 * @param noise  Input noise from which to generate the turbulence.  Typically this would be a lattice noise.
	 * @param minfreq The minimum frequency at which to evaluate the turbulence.
	 * @param maxfreq The maximum frequency at which to evaluate the turbulence.
	 * @return Waveform representing the turbulence.
	 */
	public static WaveForm gen( final WaveForm noise , double minfreq , double maxfreq ) {
		double f = 1.0;
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		for( f = minfreq ; f < maxfreq ; f = f * 2.0 )
		{
			coefficients.add( new AbsValWaveform( noise ) );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( 1.0 / f ) );
			parameterCoefficients.add( new ConstantNonClampedCoefficient( f ) );
		}
		
		AdditiveWaveForm awave = new AdditiveWaveForm( new ZeroWaveform() , 
				new ConstantNonClampedCoefficient(1.0) , coefficients ,
				coefficientCoefficients , parameterCoefficients );
		return( awave );
		
	}


}

